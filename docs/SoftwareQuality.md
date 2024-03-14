# Software quality

Per lo sviluppo del progetto sono stati seguiti alcuni dei fattori di qualità definiti da Mc Call:

- _Correttezza:_ Il software soddisfa le specifiche emerse durante la fase di ingegneria dei requisiti.
  In particolare soddisfa i requisiti con priorità più elevata (identificati mediante il processo di triage con la tecnica MoSCoW).
  Per quanto riguarda i requisiti meno rilevanti, invece, verranno poi implementati in una versione successiva del prototipo.
  
- _Affidabilità:_ Garantita dai test che sono stati fatti sul codice, grazie ad essi infatti è stato possibile accertare che il programma svolgesse le funzione richieste 
  con un certo grado di precisione.

- _Manutenibilità:_ E' stato ottenuto un certo livello di manutenibilità grazie all'utilizzo dei design patterns che hanno permesso di dare al software una struttura
  che lo rendesse semplice da comprendere.
  Pertanto in caso di guasti da risolvere, anche semplice da manutenere.  
  Quando si effettua la manutenzione di un software, è molto importante comprenderne il funzionamento.
  Per favorire ciò, è stato utilizzato Javadoc, in particolare per la descrizione delle funzioni svolte dai metodi presenti all'interno del software.
  
- _Usabilità:_ Come scritto in precedenza per la manutenibilità, l'utilizzo di Javadoc, dell'architettura MVC e dei design patterns, ha reso il programma semplice da capire
  e anche da utilizzare.

- _Testabilità:_ Il software che è stato realizzato, pur utilizzando un DB (che potrebbe introdurre difficoltà nella realizzazione dei casi di test), è semplice da testare.
 Questa semplicità è stata ottenuta grazie alla realializzazione di metodi, che invocati al termine dei test, ripuliscono il DB da eventuali modifiche apportate.
 Inoltre nel caso non servisse lavorare su dati già presenti nel DB dell'applicazione, è stata data la possibilità di usare altri DB sui quali effettuare casi di test.

- _Portabilità:_ Il software è stato realizzato nel linguaggio Java, il quale garantisce portabilità grazie alla JVM (Java Virtual Machine) e al bytecode.

- _Flessibilità:_ Il programma è stato sviluppato basandosi su un'architettura ben definita. Un programma ben strutturato è anche semplice da modificare.

 - _Riutilizzabilità:_ Il software è stato sviluppato sull'architettura MVC (Model View Controller), il che gli ha conferito un certo livello di modularità.
  Ciò permette al programma di poter essere riutilizzato in maniera semplice.
  Questa caratteristica è stata riscontrata nella realizzazione dei vari modelli all'interno del progetto. Infatti per la generazione di un nuovo modello,
  ne veniva riutilizzato uno già esistente modificato opportunamente. 
  
