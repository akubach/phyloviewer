#!/bin/bash

# version: 0.1
# guilty-party: Andrew Lenards <lenards@iplantcollaborative.org>
# date: November 12, 2009

#  Define variables.  "1" etc. is the order of the passed in ones to be used.  if a var isn't passed in, it defaults to whatever is after "-".
HOST=${1-localhost}
SQL_Dir=${2-.}
USER=${3-phyloviewer}
DB=${4-phyloviewer}

# specific the scripts you want to be called
scripts=(tree-data.sql)
# note: they will be executed in the under they appear in the array

# check for the .pgpass file in $HOME
if [ ! -e $HOME'/.pgpass' ]
then
    echo -e 'ERROR: ' $HOME '/.pgpass is missing. \n A postgresql password file must be in the home directory of the user executing this script.'
    exit 1
fi

# clear the old schema from the database
psql -h $HOST -U $USER -d $DB -c "drop schema public cascade" && psql -h $HOST  -U $USER -d $DB -c "create schema public"

if [ $? -ne 0 ]
then
    echo -e 'Dropping the public schema for database ' {$DB} ' failed.'
    exit 2
fi

# change the ownership to $DB for the public schema
psql -h $HOST  -U $USER -d $DB -c "alter database $DB owner to $USER" && psql -h $HOST  -U $USER -d $DB -c "alter schema public owner to $USER"

if [ $? -ne 0 ]
then
    echo -e 'Changing ownership for database ' {$DB} ' failed.'
    exit 3
fi

# Create the new schema from the change scripts in $scripts
for script in ${scripts[@]}
  do
     psql -h $HOST  -U $USER -d $DB < $SQL_Dir/$script
done