# Software Design

## Pattern MVC

Al fine di fornire una certa *modularità* al sistema sviluppato, abbiamo deciso di implementare il *pattern MVC* per lo sviluppo dell'applicazione.
L'applicazione infatti è stata scomposta in tre componenti principali: *Model*, *View* e *Controller*.
A livello di codice, *view e controller sono stati accorpati* nelle stesse classi, questo a causa della forte correlazione tra i due.
- *Model*: contiene i metodi che permettono l'accesso ai dati nel database e implementa la businnes logic del software.
- *View*: si occupa di visualizzare i dati e gestisce le interazioni con l'utente.
- *Controller*: si occupa di ricevere i comandi dell'utente nella view e tradurle in azioni attraverso le funzionalità messe a disposizione dal modello.

### Ruolo chiave dei modelli

I modelli costituiscono la parte più importante del sistema, effettuando i dovuti controlli sulle azioni intraprese dall'utente e assicurando l'integrità dei dati che vengono poi resi persistenti sincronizzandoli nel database. Per questo abbiamo scelto di realizzare i casi di test su di essi.


## Altri design pattern utilizzati

### Singleton pattern

Questo pattern è stato molto utile per tutte le classi in cui si voleva avere un'unica istanza.  
Nel progetto è stato utilizzato per:

- _Modelli:_ le istanze dei modelli sono uniche. Questo perchè più view potrebbero necessitare di uno stesso modello, pertanto se esiste una sola
  istanza di quel modello, tutte le view si riferiscono ad essa.
  Avere più istanze dei modelli porterebbe dei problemi a livello di gestione dei dati e di aggiornamento delle viste.
  
- _DB:_ la connessione è unica, non possono essere create più istanze di connessioni con esso. In questo modo tutti i modelli lavorano sulla stessa istanza di connessione con il database.

### Observer pattern
È stato utilizzato questo pattern per ridurre l'interconnessione tra i modelli e la view.
Facendo in questo modo, è stato possibile svincolare i modelli dalle view, così in futuro se si vorrà aggiornare la UI del sistema sarà possibile farlo mantenendo inalterati i modelli.
Ciascun modello (Observable) contiene una lista di observer, i quali ricevono una notifica ad ogni modifica dei dati.
Ogni view (Observer) è stata registrata al relativo modello per ricevere le notifiche in caso di aggiornamenti.

## Misurazione del codice
### JDepend:
Per misurare le metriche indicate nella tabella seguente e valutare così la qualità della progettazione del software, abbiamo fatto ricorso a *JDepend*.  
Dalla tabella si può notare che il livello di astrazione del software è generalmente basso. Per aumentare questo valore, si dovrà effettuare del refactoring in fase di manutenzione.

| Package            | CC | AC | Ca(afferente) | Ce(efferente)  | Astrazione | Instabilità |
|--------------------|----|----|---------------|----------------|------------|-------------|
| Smartmag.data      | 10 | 0  | 4             | 2              | 0.00       | 0.33        |
| Smartmag.db        | 1  | 0  | 2             | 3              | 0.00       | 0.60        |            
| Smartmag.db.utils  | 0  | 1  | 0             | 2              | 1.00       | 1.00        |
| Smartmag.models    | 6  | 1  | 4             | 9              | 0.14       | 0.69        |
| Smartmag.models.ui | 6  | 0  | 1             | 2              | 0.00       | 0.66        |
| Smartmag.ui        | 59 | 0  | 1             | 7              | 0.00       | 0.87        |
| Smartmag.utils     | 1  | 2  | 2             | 3              | 0.66       | 0.60        |

Dove:
- CC: classi concrete
- AC: classi astratte
- Ca: accoppiamento afferente
- Ce: accoppiamento efferente

### Structure101:
Per avere un'ulteriore valutazione sulle dipendenze delle classi e dei pacchetti, abbiamo utilizzato *Structure101*.  
Principalmente sono state valutate le classi contenute nei pacchetti e quindi il loro livello di coesione e la presenza di loop.

- Package "data":  
  Non sono emersi loop all'interno di questo pacchetto e la coesione tra le classi sembra essere molto elevata:

  ![St101_data](https://github.com/JohnnyLAmpAz/smartmag/assets/145765934/bdb2f44b-9a5c-4a67-ac87-08f7d60d57df)

- Package "models":  
  E' emersa la presenza di un loop all'intermo di questo pacchetto:

  <img width="170" alt="St101_models_cattivo" src="https://github.com/JohnnyLAmpAz/smartmag/assets/145765934/0b605d8a-dbcd-4e19-85ff-74e9a44c3845">

- Package "ui":  
  E' emersa la presenza di un loop (molto semplice da correggere) all'intermo di questo pacchetto:
  
  <img width="515" alt="image" src="https://github.com/JohnnyLAmpAz/smartmag/assets/145765934/573e0152-5110-4b68-8029-62930bf4977d">


Riuscire a identificare questi loop è molto importante per poter sistemare il codice in modo tale da renderne la struttura più solida e facile da manutenere.


