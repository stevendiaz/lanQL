#!/usr/bin/env bash

docker exec -ti --user postgres lanql_db_1 psql -Utexaslan_role texaslandb
