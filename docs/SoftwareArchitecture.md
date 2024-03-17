# Architettura Software
### Introduzione
Il software é stato sviluppato seguendo un'architettura modulare basata sul pattern Model-View-Controller, in cui i due moduli view e controller sono stati integrati per semplificare la struttura del sistema; questa andrá ad interfacciarsi, attraverso l'uilizzo di JOOQ, con un database creato usando SQlite. La connessione al database SQLite è gestita da un componente  che si trova nel package "smartmag.db", il quale fornisce un'interfaccia per stabilire e gestire la connessione con il database, garantendo l'integrità e la coerenza dei dati.


### Modello
Il modello rappresenta i dati e la logica dell'applicazione, nel contesto del nostro software ció é rappresentato da due package 

1. **Package "smartmag.data"**: Contiene le classi che forniscono una rappresentazione astratta dei dati e costituiscono la base per i modelli del sistema. Questo contribuisce alla modularitá del sistema.

2. **Package "smartmag.models"**: Contiene i modelli degli oggetti presenti nel magazzino, uno per ciascuna classe presente nel package precedente. Ogni modello, ha due attributi principali: la classe .data corrispondente e un record che permette e rappresenta l'interazione con il database. In queste classi sono definite tutte le funzioni che vanno effettivamente a modificare i dati degli oggetti e utenti del magazzino o ne creano di nuovi.

### Vista e Controller
La Vista e il Controller sono combinati insieme nel package **"smartmag.ui"** il quale gestisce l'interfaccia utente e le interazioni dell'utente con il sistema. Questa parte del software richiama i metodi dei modelli per consentire all'utente di interagire con i dati del database.

### Conclusioni
L'architettura del software per la gestione del magazzino è stata progettata con l'obiettivo di garantire un'organizzazione chiara e una gestione efficiente dei dati. L'implementazione del pattern MVC, con il Controller integrato insieme alla View, contribuisce a semplificare la struttura complessiva del sistema, facilitando lo sviluppo e la manutenzione del software.

