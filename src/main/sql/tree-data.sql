
BEGIN;

create table Node (
	ID uuid primary key, 
	Label varchar
);

create table Tree (
	ID uuid primary key, 
	RootID uuid not null, 
	Name varchar, 
	foreign key(RootID) references Node(ID)
);

create table Topology (
	ID uuid primary key, 
	ParentID uuid, 
	TreeID uuid, 
	LeftNode int, 
	RightNode int, 
	Depth int, 
	Height int, 
	NumChildren int, 
	NumLeaves int, 
	NumNodes int, 
	foreign key(ID) references Node(ID), 
	foreign key(ParentID) references Node(ID), 
	foreign key(TreeID) references Tree(ID)
);
			

create index IndexParent on Topology(ParentID);
create index IndexLeft on Topology(LeftNode);
create index IndexRight on Topology(RightNode);
create index IndexDepth on Topology(Depth);
create index IndexTreeID on Topology(TreeID);

COMMIT;
