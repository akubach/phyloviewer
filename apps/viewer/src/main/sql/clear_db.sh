#!/bin/bash

#  Define variables.  "1" etc. is the order of the passed in ones to be used.  if a var isn't passed in, it defaults to whatever is after "-".
HOST=${1-localhost}
SQL_Dir=${2-.}
USER=${3-phyloviewer}
DB=${4-phyloviewer}

tables=(overview_images node_layout topology tree node node_label_lookup)

for table in ${tables[@]}
  do
    psql -h $HOST -U $USER -d $DB -c 'DELETE FROM '${table}';'
done

# reset the sequences back to 1.
psql -h $HOST -U $USER -d $DB -c "SELECT setval('nodes_node_id', 1);"
psql -h $HOST -U $USER -d $DB -c "SELECT setval('trees_tree_id', 1);"
