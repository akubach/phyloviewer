
Postgis (www.postgis.org) is now required to store tree geometry.  Installing the postgis 
extensions to postgres requires superuser privileges.  The db_init script now has to be run as 
superuser and the script assumes that user the server uses is phyloviewer.

-----------------------------------------------------------

To create a user and database:

sudo -u postgres psql

# create user phyloviewer with password 'phyloviewer';
# create database phyloviewer;
# alter database phyloviewer owner to phyloviewer;
# \c phyloviewer

# alter schema public owner to phyloviewer;

Exit shell

-----------------------------------------------------------

Create langauge required by postgis (replace postgres with another account with superuser privileges if needed):

$ sudo -u postgres createlang plpgsql phyloviewer
$ sudo -u postgres createlang plpgsql phyloviewer_template

-----------------------------------------------------------

To create database:

$ ./db_init.sh localhost . superuser

To create a template database (for testing) run command on the shell:

$ ./db_init.sh localhost . superuser phyloviewer_template
