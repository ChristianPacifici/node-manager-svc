-- Creating Edge table and relationships
CREATE TABLE edge (
    from_id INTEGER NOT NULL,
    to_id INTEGER NOT NULL,
    PRIMARY KEY (from_id, to_id)
);