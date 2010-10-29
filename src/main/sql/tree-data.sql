
BEGIN;

CREATE SEQUENCE nodes_node_id
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

CREATE SEQUENCE trees_tree_id
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

create table node (
	node_id integer DEFAULT nextval('nodes_node_id'::regclass) primary key, 
	Label varchar
);

create table node_label_lookup (
	node_id integer primary key, 
	alt_label varchar
);

create table tree (
	tree_id integer DEFAULT nextval('trees_tree_id'::regclass) primary key, 
	root_id integer not null, 
	Name varchar, 
	foreign key(root_id) references node(node_id)
);

create table topology (
	node_id integer primary key, 
	parent_id integer, 
	tree_id integer, 
	LeftNode int, 
	RightNode int, 
	Depth int, 
	Height int, 
	NumChildren int, 
	NumLeaves int, 
	NumNodes int, 
	foreign key(node_id) references node(node_id), 
	foreign key(parent_id) references node(node_id), 
	foreign key(tree_id) references tree(tree_id)
);

create table node_layout (
	node_id integer not null,
	layout_id varchar,
	point_x double precision,
	point_y double precision,
	min_x double precision,
	min_y double precision,
	max_x double precision,
	max_y double precision,
	foreign key(node_id) references node(node_id)
);

create table overview_images (
	tree_id integer not null,
	layout_id varchar not null,
	image_width integer not null,
	image_height integer not null,
	image_path varchar not null
);

create index IndexParent on topology(parent_id);
create index IndexTreeID on topology(tree_id);
create index IndexLayout on node_layout(node_id, layout_id);
create index IndexLabel on node(lower(label::text));

COMMIT;
