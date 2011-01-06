To create a user and database:

sudo -u postgres psql

# create user phyloviewer with password 'phyloviewer';
# create database phyloviewer;
# alter database phyloviewer owner to phyloviewer;
# \c phyloviewer

# alter schema public owner to phyloviewer;

To create database:

$ ./db_init.sh

To create a template database (for testing) run command on the shell:

$ ./db_init.sh localhost . phyloviewer phyloviewer_template
