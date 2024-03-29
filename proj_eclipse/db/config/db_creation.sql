CREATE TABLE "Box" (
	"id"	VARCHAR,
	"prodotto"	INTEGER,
	"qta"	INTEGER NOT NULL DEFAULT 0 CHECK("qta" >= 0),
	FOREIGN KEY("prodotto") REFERENCES "Prodotto"("id") ON DELETE SET NULL ON UPDATE CASCADE,
	PRIMARY KEY("id")
);

CREATE TABLE "Prodotto" (
	"id"	INTEGER,
	"nome"	VARCHAR NOT NULL,
	"descrizione"	TEXT,
	"peso"	REAL,
	"soglia"	INTEGER NOT NULL DEFAULT 0,
	PRIMARY KEY("id")
);

CREATE TABLE "Ordine" (
	"id"	INTEGER,
	"tipo"	TEXT NOT NULL CHECK("tipo" IN ("IN", "OUT")),
	"stato"	TEXT NOT NULL CHECK("stato" IN ("IN_ATTESA", "IN_SVOLGIMENTO", "COMPLETATO")),
	"dataEm"	TEXT NOT NULL DEFAULT '1970-01-01',
	"dataCo"	TEXT,
	PRIMARY KEY("id")
);

CREATE TABLE "ProdottiOrdini" (
	"prod"	INTEGER,
	"ordine"	INTEGER,
	"qta"	INTEGER NOT NULL CHECK("qta" > 0),
	FOREIGN KEY("prod") REFERENCES "Prodotto"("id") ON UPDATE CASCADE ON DELETE RESTRICT,
	FOREIGN KEY("ordine") REFERENCES "Ordine"("id") ON UPDATE CASCADE ON DELETE CASCADE,
	PRIMARY KEY("prod","ordine")
);

CREATE TABLE "Utente" (
	"matricola"	TEXT,
	"nome"	TEXT NOT NULL,
	"cognome"	TEXT NOT NULL,
	"password"	TEXT NOT NULL,
	"ruolo"	TEXT NOT NULL CHECK(ruolo IN ("MAGAZZ", "MAGAZZ_QUAL", "RESP_ORDINI", "MANAGER")),
	PRIMARY KEY("matricola")
);

CREATE TABLE "Movimentazione" (
	"ordine"	INTEGER,
	"box"	TEXT,
	"prod"	INTEGER NOT NULL,
	"qta"	INTEGER NOT NULL CHECK("qta" > 0),
	"stato"	TEXT NOT NULL DEFAULT NON_ASSEGNATA CHECK("stato" IN ("NON_ASSEGNATA", "PRESA_IN_CARICO", "PRELEVATA", "COMPLETATA", "ANNULLATA")),
	"magazziniere"	TEXT COLLATE BINARY,
	FOREIGN KEY("box") REFERENCES "Box"("id") ON UPDATE CASCADE ON DELETE NO ACTION,
	PRIMARY KEY("ordine","box"),
	FOREIGN KEY("prod") REFERENCES "Prodotto"("id") ON UPDATE CASCADE ON DELETE RESTRICT,
	FOREIGN KEY("magazziniere") REFERENCES "Utente"("matricola") ON UPDATE CASCADE ON DELETE NO ACTION,
	FOREIGN KEY("ordine") REFERENCES "Ordine"("id") ON UPDATE CASCADE ON DELETE CASCADE
);

-- Default manager user admin:admin
INSERT INTO Utente VALUES ("admin", "admin", "admin", "admin", "MANAGER");