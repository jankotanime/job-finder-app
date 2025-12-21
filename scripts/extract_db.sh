#!/bin/bash

docker exec -t jobfinderapp-database-1 pg_dump -U admin -d jobFinderApp --data-only --column-inserts > data.sql