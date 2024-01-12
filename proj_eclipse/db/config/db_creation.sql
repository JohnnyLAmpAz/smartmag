CREATE TABLE IF NOT EXISTS Corsia (id INTEGER PRIMARY KEY);

CREATE TABLE IF NOT EXISTS Scaffale (
    id INTEGER,
    corsia INTEGER REFERENCES Corsia(id),
    PRIMARY KEY(id, corsia)
);

CREATE TABLE IF NOT EXISTS Ripiano (
    id INTEGER,
    scaffale INTEGER REFERENCES Scaffale(id),
    corsia INTEGER REFERENCES Corsia(id),
    PRIMARY KEY(id, scaffale, corsia)
);