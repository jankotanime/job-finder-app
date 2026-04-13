#!/bin/sh
set -eu

python3 -m pip install --no-cache-dir psycopg2-binary >/dev/null

deadline=$(( $(date +%s) + 60 ))

until python3 -c 'import socket; socket.create_connection(("spring-boot-service", 8080), timeout=2).close()' >/dev/null 2>&1; do
    if [ "$(date +%s)" -gt "$deadline" ]; then
        echo "Timed out waiting for spring-boot-service:8080" >&2
        exit 1
    fi
    sleep 1
done

python3 /seed_data.py
