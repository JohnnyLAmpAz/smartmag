# Modelling
Per lo sviluppo del progetto una fase importante è stata quella della modellazione. Attraverso l'uso dei diagrammi UML siamo stati in grado di definire e strutturare diversi aspetti del sistema molto delicati in modo chiaro e organizzato. In particolare sono stati utilizzati e verranno discussi i seguenti modelli:

- Use Case Diagram
- Class Diagram
- Activity Diagram
- State Diagram

### Use Case Diagram
![Model1!UseCaseDiagram_0](https://github.com/JohnnyLAmpAz/smartmag/assets/127232421/40117929-5dcb-4867-8fde-250b93e77888)
In questo diagramma sono state rappresentate le funzionalità messe a disposizione degli attori coinvolti, in particolare possiamo individuare quattro attori umani:
1. **Manager di magazzino**:
Si occupa di inserire nel sistema i dati di nuovi utenti, prodotti e box (anche se per quest'ultimo si é poi deciso di realizzarne la creazione solo all'inserimento del prodotto nel box) potrá inoltre, nelle versioni future del sistema,  visionare le informazioni e le statistiche dei vari magazzinieri.
2. **Responsabile ordini**:
Ha la possibilitá di inserire e cambiare lo stato dei divesi ordini
3. **Magazziniere**:
Il suo unico scopo é quello di spostare i prodotti all'interno del magazzino e segnalare l'avvenuto prelievo e spostamento del prodotto
4. **Magazziniere qualificato**:
Un estensione del magazziniere, é abilitato al controllo e accettazione degli ordini in ingresso

Questi 4 attori estendono l'utente in quanto tutti devono effettuare l'accesso al sistema per poter controllare le informazioni relative ai prodotti o registarare il proprio lavoro. 
Infine l'ultimo attore é il sistema informatico stesso che analizzando le disponibilità e i box del magazzino genera le movimentazioni da effettuare.

### Class Diagram
Nel seguente diagramma delle classi sono indicate ad un livello piu specifico tutte le varie classi e le loro relazioni ed é stato infatti utilizzato come scheletro per lo sviluppo del codice. 
Sono tuttavia presenti alcune differenze, durante lo sviluppo é stato infatti deciso di non creare diverse classi per i diversi tipi di utente ma di aggiungere un attributo "*TipoUtente*" alla classe Utente che non é quindi stata realizzata come astratta. 
![Model1!ClassDiagram1_0](https://github.com/JohnnyLAmpAz/smartmag/assets/127232421/1d8c85e0-ff25-475f-915d-d0cc8e26ddba)

### Activity Diagram
![Model!Activity1!ActivityDiagram1_1](https://github.com/JohnnyLAmpAz/smartmag/assets/127232421/678f62ad-78ab-4495-91ce-b42e327c288d)

in questo diagramma viene mostrata la sequenza di operazioni che il magazziniere e il sistema svolgono per gestire una movimentazione e più in generale la lista di movimentazioni.

### State Diagram

![Model!StateMachine1!StatechartDiagram1_1](https://github.com/JohnnyLAmpAz/smartmag/assets/127232421/02273cec-7260-4b11-be29-b474a19c3feb)


Abbiamo creato un diagramma di stato per rappresentare le varie fasi della giornata lavorativa di un magazziniere. Il diagramma identifica due stati principali:

1. **Fuori turno**: Indica quando il magazziniere è in pausa o ha terminato il turno.
2. **In turno**: Rappresenta il momento in cui il magazziniere è attivo e sta svolgendo le proprie mansioni. Inizialmente si trova nello stato **senza movimentazione assegnata**.

All'interno dello stato "senza movimentazione assegnata", il magazziniere si sposta in una serie di stati:

- Controlla la disponibilità di movimentazioni da svolgere.
- Se sono presenti movimentazioni in entrata, si dirige verso la zona di carico/scarico.
- Se le movimentazioni sono in uscita, si sposta nella zona dei box.
- Segna il prodotto come prelevato e lo sposta nella zona corretta.
- Deposita il prodotto e aggiorna lo stato della movimentazione a "completato".

Dopo aver completato queste azioni, il magazziniere ritorna allo stato "senza movimentazione assegnata" per continuare con le sue attività.

 
