# Architettura Software
### Introduzione
Il software é stato sviluppato seguendo un'architettura modulare basata sul pattern Model-View-Controller, in cui i due moduli view e controller sono stati accorpati in quanto fortemente legati; questa andrá ad interfacciarsi, attraverso l'uilizzo di jOOQ, con un database creato usando SQlite. La connessione al database SQLite è gestita da un componente  che si trova nel package "smartmag.db", il quale fornisce un'interfaccia per stabilire e gestire la connessione (unica) con il database, garantendo l'integrità e la persistenza dei dati.


### Modello
Il modello rappresenta i dati e la business logic dell'applicazione. Nel contesto del nostro software ciò é gestito da due package:

1. **Package `smartmag.data`**: Contiene le classi che gestiscono i dati delle singole entità su cui lavoreranno i vari modelli del sistema.

2. **Package `smartmag.models`**: Contiene i modelli (Mvc) delle varie entità, uno per ciascuna classe presente nel package precedente. Ogni modello, ha due attributi principali: un'istanza di una delle suddette classi contenenti i dati dell'entità e un record corrispondente (messo a disposizione da jOOQ) che permette la sincronizzazione con il database. In queste classi sono definite tutti i metodi che vanno effettivamente a fornire le funzionalità del sistema, modificando i dati degli oggetti gestiti.

### Vista e Controller
Le Viste e i Controller sono combinati insieme nel package **`smartmag.ui`** il quale gestisce l'interfaccia utente e le interazioni dell'utente con il sistema. Questa parte del software richiama i metodi dei modelli per consentire all'utente di interagire con i dati del database. Inoltre, le viste vengono notificate dai modelli ad ogni modifica dei dati.

### Conclusioni
L'architettura del software per la gestione del magazzino è stata progettata con l'obiettivo di garantire un'organizzazione chiara e una gestione efficiente dei dati. L'implementazione del pattern MVC, con il Controller integrato insieme alla View, contribuisce a conferire modularità alla struttura del sistema, facilitando lo sviluppo e la manutenzione del software.

