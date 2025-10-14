#!/bin/bash

docker exec -t database pg_dump -U admin -d jobFinderApp --data-only --column-inserts > data.sql