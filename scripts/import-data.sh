#!/bin/sh
set -euo pipefail

# Configurable input
DATA_FILE="/data.sql"
FILTERED="/tmp/data_filtered.sql"
WRAPPED="/tmp/data_wrapped.sql"

# Check input
if [ ! -f "${DATA_FILE}" ]; then
  echo "ERROR: ${DATA_FILE} not found"
  exit 1
fi

# Remove psql metacommands (lines starting with backslash)
awk '!/^[[:space:]]*\\/' "${DATA_FILE}" > "${FILTERED}"

# Build wrapped file (requires superuser to set session_replication_role)
{
  echo "SET session_replication_role = replica;"
  echo "BEGIN;"
  cat "${FILTERED}"
  echo "COMMIT;"
  echo "SET session_replication_role = DEFAULT;"
} > "${WRAPPED}"

# Required env: PGHOST, PGUSER, PGDATABASE, PGPASSWORD (PGPASSWORD can be passed via env)
: "${PGHOST:?PGHOST required}"
: "${PGUSER:?PGUSER required}"
: "${PGDATABASE:?PGDATABASE required}"
if [ -z "${PGPASSWORD:-}" ]; then
  echo "WARNING: PGPASSWORD is empty, psql may prompt for password"
fi

# Run import (stop on first error)
psql -h "${PGHOST}" -U "${PGUSER}" -d "${PGDATABASE}" --set=ON_ERROR_STOP=on -f "${WRAPPED}"

echo "Import finished successfully."
