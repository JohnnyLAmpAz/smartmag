# Software quality

Per lo sviluppo del progetto sono stati seguiti alcuni dei fattori di qualità definiti da *McCall*:

- _Correttezza:_ Il software soddisfa le specifiche emerse durante la fase di ingegneria dei requisiti.
  In particolare soddisfa quelli con priorità più elevata (identificati mediante la tecnica *MoSCoW* durante il processo di triage).
  Per quanto riguarda i requisiti meno rilevanti, invece, verranno eventualmente implementati in una versione successiva del prototipo.
  
- _Affidabilità:_ Garantita dai test effettuati sul codice, grazie ai quali è stato possibile accertare che il programma svolgesse le funzioni richieste 
  con un certo grado di precisione.

- _Manutenibilità:_ È stato ottenuto un certo livello di manutenibilità grazie all'utilizzo dei *design patterns* che hanno permesso di dare al software una struttura
  che lo rendesse di facile comprensione e di conseguenza, in caso di guasti da risolvere, anche semplice da manutenere.  
  Difatti durante la manutenzione di un software, è molto importante comprenderne il funzionamento.
  A tal proposito è stato utilizzato intensivamente *Javadoc*, in particolare per la descrizione delle funzioni svolte dai metodi presenti all'interno del software.
  
- _Usabilità:_ L'interfaccia grafica che è stata sviluppata è intuitiva e semplice da utilizzare. Questa semplicità di utilizzo è favorita     
  dalla presenza di pulsanti autoesplicativi e da una buona suddivisione in finestre contenenti funzionalità affini.

- _Testabilità:_ Il software che è stato realizzato, pur utilizzando un DB (che potrebbe introdurre difficoltà nella realizzazione dei casi di test), è semplice da testare.
 Quest'ultima è stata ottenuta grazie alla realizzazione di metodi che, invocati al termine dei test, ripuliscono il DB da eventuali modifiche apportate.
 Inoltre nel caso non servisse lavorare su dati già presenti nel DB dell'applicazione, è stata data la possibilità di utilizzare altri DB sui quali effettuare casi di test.

- _Portabilità:_ Il software è stato realizzato nel linguaggio *Java*, il quale garantisce portabilità grazie alla *JVM* (Java Virtual Machine) e al *bytecode*.

- _Flessibilità:_ Il programma è stato sviluppato basandosi su un'architettura ben definita. Un programma ben strutturato è anche semplice da modificare.

 - _Riutilizzabilità:_ Il software è stato sviluppato sull'architettura *MVC* (Model View Controller), il che gli ha conferito un certo livello di *modularità* e che permette al programma di poter essere riutilizzato in maniera semplice. Ad esempio, si potrebbe realizzare un applicativo client/server con interfaccia web riutilizzando i modelli già sviluppati, senza preoccuparsi di business logic e persistenza dati!
  
