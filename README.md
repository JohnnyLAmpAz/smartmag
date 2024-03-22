<p align="center">
  <img width="350" alt="Logo" src="./proj_eclipse/img/smartmag.png">
</p>

# SmartMag

Repo del progetto per il corso di Ingegneria del Software.

Si tratta di un sistema di gestione di un magazzino, che permette di svolgere una serie di attività, tra cui:
- Tenere traccia dei depositi e dei prelievi degli articoli, con le relative destinazioni;
- Monitorare le quantità disponibili, con la possibilità di impostare una soglia minima di scorta a magazzino;
- Tenere traccia delle caratteristiche di ogni singolo prodotto.

## Membri del gruppo

Questo progetto è stato realizzato da:
- Brambilla Davide
- Brivio Lorenzo
- Gervasoni Massimiliano

## Documentazione

Tutta la documentazione del progetto risiede nella cartella `/docs`.

Nella sezione *Projects* della repo è presente anche la *Kanban board* dove vengono gestiti gli issue/PR e le attività divise in *da fare* | *in corso* | *fatte*.

## Implementazione

Nello specifico, il risultato sarà un'applicazione desktop GUI sviluppata in Java (*Swing*) che si appoggerà ad un database *sqlite* locale.

Il progetto Eclipse con tutti i sorgenti risiede nella cartella `/proj_eclipse`.

## Getting started

Seguire i seguenti passi per importare il progetto nell'IDE Eclipse:

1. Clonare questa repo
2. In Eclipse cliccare *File* > *Importa*, poi *Generale* > *Progetto esistente nel workspace*
3. Selezionare la cartella `/proj_eclipse` che si trova **dentro** quella della repo e importare
4. **IMPORTANTE: Tasto destro sul progetto > *Maven* > *Aggiorna progetto***

Si consiglia poi l'utilizzo della *JDK 21*, la stessa utilizzata durante lo sviluppo e dalla GitHub Action *Java CI with Maven*.
