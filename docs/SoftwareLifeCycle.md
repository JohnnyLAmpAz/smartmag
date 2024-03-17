# Software life cycle

Come modello di sviluppo, è stato scelto di utilizzare RAD (Rapid Application Develpment)

<img width="448" alt="RAD" src="https://github.com/JohnnyLAmpAz/smartmag/assets/145765934/6c621d39-bd0d-4269-b9f4-c0e50bf7b31c">

caratterizzato da:

- _Protoyping:_  
  Lo sviluppo del progetto è stato basato sulla realizzazione di prototipi. In particolare, è stata usata la prototipazione evolutiva, ovvero che dopo
  aver completato il prototipo, quest'ultimo non viene "buttato", ma viene utilizzato come base per realizzare la versione definitiva del software.

- _Utilizzo della tecnica del time boxing:_  
  Lo sviluppo del progetto è stato suddiviso in diverse attività. A ciascuna di queste, è stato dedicato un tempo di realizzazione determinato in base alla complessità del lavoro da svolgere:  
  
  | Time box | Attività |
  |----------|----------|
  | 1 | Organizzazione e scelta del progetto da realizzare |
  | 2 | Project Plan |
  | 3 | Ingegneria dei requisiti |
  | 4 | Architettura del software |
  | 5 | Design del software |
  | 6 | Realizzazione diagrammi UML |
  | 7 | Scrittura del codice |
  | 8 | Test |

  A loro volta alcune di queste time boxes sono state suddivise in processi più piccoli (come ad esempio la scrittura del codice):
  
  | Time box | Attività|
  |----------|---------|
  | 1 | MAVEN setup |
  | 2 | JOOQ setup and JUnit setup |
  | 3 | Realizzare le classi identificate nel class diagram |
  | 4 | Realizzare le classi dei modelli |
  | 5 | Realizzare le classi delle view |
  | 6 | Integrazione delle diverse parti del codice |
  | 7 | Scrivere il codice di test |

- _Processo di triage:_  
  È stata utilizzata la tecnica MoSCoW per assegnare a ciascun requisito una priorità.  
  Grazie al processo di triage, è stato possibile concentrarsi sui requisiti più rilevanti per lo sviluppo del progetto e quindi di sfruttare al meglio il tempo a disposizione. 
    
- _Frequenti riunioni informali:_  
  Queste riunioni hanno avuto lo scopo di mantenere aggiornati i membri del team sullo stato del progetto e di poter condividere idee e opinioni in merito a ciò che si stava sviluppando.
  In particolare, si sono tenute sessioni di brainstorming, dove sono emerse molte idee che hanno contribuito allo sviluppo del progetto.
  
- _Pair programming:_  
  Grazie all'utilizzo di strumenti come "code together" è stato possibile collaborare durante lo sviluppo del codice.  
  È stata utilizzata questa tecnica soprattutto nella realizzazione delle parti di codice più complesse e più impattanti per il progetto.
  
- _Continuos improvement:_  
  Anche durante l'avanzamento del lavoro si sono sempre accettati cambiamenti dei requisiti: spesso è difficile conoscerli a priori e con precisione.
  Grazie ad un software ben strutturato, queste variazioni, non hanno generato dei grossi problemi.
