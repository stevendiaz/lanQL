#!/usr/bin/env bash

docker exec -i --user postgres lanql_db_1 createdb texaslandb

docker exec -i --user postgres lanql_db_1 psql texaslandb -a  <<__END
create user texaslan_role password 'allilengyi';
__END

cat lanql_dump.sql | docker exec -i lanql_db_1 psql -Utexaslan_role texaslandb
