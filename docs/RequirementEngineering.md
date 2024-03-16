# Requirement engineering

## Elicitazione dei requisiti

Per quanto riguarda questa fase, è stato fondamentale confrontarsi con persone che hanno lavorato nel settore della logistica.  
Per riuscire ad ottenere tutte le informazioni necessarie alla realizzazione del sistema, sono state usate le seguenti tecniche di elicitazione dei requisti:

- __Intervistare/chiedere:__

  Sono state organizzate delle riunioni, nelle quali, mediante la tecnica del brainstorming, è stato possibile identificare alcuni dei requisiti     
  necessari allo sviluppo del sistema.  
  La sessione di brainstorming è stata suddivisa in due fasi principali:
  - La prima fase, il cui obiettivo principale è stato generare il maggior numero possibile di idee.
  - La seconda fase, durante la quale l’obiettivo è stato selezionare, tra le idee generate, quelle più utili per la realizzazione del sistema   
    finale.
  
- __Analisi delle attività:__
  
  Sono state analizzate le varie attività che il personale di un magazzino deve svolgere nel corso della giornata lavorativa.  
  Da questa analisi è stato possibile definire i seguenti "attori" e i corrispettivi compiti:
  
  - _Magazziniere:_
    
    Movimenta i prodotti da una locazione all’altra, preleva il prodotto, verifica la disponibilità e la locazione del prodotto mediante il suo         codice. Deve inoltre avere la possibilità di modificare le quantità di prodotti a magazzino.
    
  - _magazzinieri qualificati:_

    Oltre all'attività di magazziniere può anche accettazione (o meno) i rifornimenti.
    
  - _Responsabile ordini:_
  
    Genera gli ordini di prelievo e deposito prodotti nel magazzino. Ha modo di ottenere la lista dei prodotti con rimanenze sottosoglia.    
    Inoltre ha la possibilità di visionare le statistiche riguardanti il magazzino.
    
  - _Manager magazzino:_

    Gestione del personale: ruoli, utenti, statistiche lavorative sul personale.
    Ha accesso alle informazioni di accounting (storico). Gestisce i prodotti (aggiunta di nuovi, rimozione, etc.) e li assegna ai box. 

  - _Sistema informativo:_
  
    Se le rimanenze di prodotto scendono sotto una certa soglia, invia una notifica al responsabile ordini se non ha già provveduto al riordino.
    Gestisce l'accounting: storico degli spostamenti/modifiche prodotti. 

- __Analisi degli scenari:__
 
  È stato molto utile ascoltare i racconti dei lavoratori nel settore della logistica riguardanti alcune delle situazioni che si sono presentate 
  nel corso della loro esperienza lavorativa. Grazie a questa tecnica, infatti, è stato possibile elicitare quei requisiti che solo chi ha lavorato 
  nel settore logistico è in grado di fornire.

- __Derivazione da sistema esistente:__
  
  Poiché la fase di elicitazione dei requisiti si è basata in gran parte sull’esperienza dei lavoratori nel settore logistico, inevitabilmente   
  alcune delle funzionalità implementate nel nostro progetto derivano da sistemi già esistenti.

## Specifica dei requisiti

1. Il magazzino deve essere composto da:
    - Corsie: identificate da una o più lettere.
    - Scaffali: una corsia può contenere sia uno scaffale a sinistra che uno a destra.
    - Slot: gli scaffali sono divisi in slot. Per gli scaffali a sinistra, il numero dello slot è dispari, mentre per quelli a destra, il numero   
      dello slot è pari.
    - Piani: uno slot è suddiviso in piani, ognuno dei quali è identificato da un numero intero
    - Box: identifica la locazione di un prodotto ed è dato dall'insieme delle parti che costituiscono il magazzino elencate sopra.

   Ecco un esempio di come dovrà essere gestita una corsia all'inetrno di SmartMag:

    <img width="482" alt="Struttura magazzino" src="https://github.com/JohnnyLAmpAz/smartmag/assets/145765934/67260cbf-f99c-4a0e-a860-aaf650dff81f">
    
2. Per accedere al software è richiesto il log-in dell'utente, dove andranno inseriti Id e password.
  
3. Il manager, si dovrà occupare della gestione del personale.
   1. Deve poter creare gli utenti di tutto il personale inserendo: matricola, nome, cognome, ruolo, password.
   2. Deve poter accedere alla lista dei prodotti presenti a magazzino
   3. Deve poter aggiungere o rimuovere prodotti nel magazzino
   4. Deve poter assegnare un box ed una quantità a ciascun prodotto
   5. Deve poter modificare le quantità dei prodotti

4. Il responsabile ordini si occupa della gestione degli ordini e dei prodotti presenti a magazzino.
   1. Deve poter accedere alla lista dei prodotti presenti a magazzino
   2. Deve poter accedere alla lista degli ordini
   3. Deve poter aggiungere e rimuovere un ordine
   4. L'inserimento di un ordine manualmente
   5. L'inserimento di un ordine anche da file
   6. Deve poter modificare un ordine se è in stato di attesa e non sono ancora state generate le relative movimentazioni
   7. Può approvare o rifiutare le movimentazioni proposte dal sistema in seguito all'inserimento dell'ordine

5. Le movimentazioni devono essere generate dal sistema a seguito dell'approvazione da parte del responsabile ordini.
   1. Per ogni prodotto presente nell'ordine deve essere generata una movimentazione.
   2. La movimentazione deve specificare: l'ordine, il prodotto, l'origine, la estinazione, lo stato e il magazzniere che l'ha presa in carico.

 6. Se il magazziniere dopo aver preso in carico una movimentazione, si accorge che le quantità richieste non sono presenti nel box:
    1. Deve poter aggiornare la quantità di prodotto nel box
    2. Il sistema verifica se è presente la quantità richiesta in altri box
    3. Se la quantità richiesta è disponibile in un altro box, genera una nuova movimentazione
    4. Se la quantità richiesta non è presente a magazzino, annulla la movimentazione e riporta lo stato dell'ordine in "attesa".
    
  7. Il responsabile ordini, a fronte di disponibilità limitate di certi prodotti, può effettuare richieste di acquisto, 
     specificando le quantità di rifornimento per i vari prodotti coinvolti.

  8. Alla consegna dei rifornimenti, il magazziniere qualificato verifica che quanto ordinato corrisponde al carico ricevuto.
     1. Se i prodotti ricevuti corrispondono all'ordine, accetta la consegna e fà generare al sistema le movimentazioni.
     2. Nel caso in cui sia arrivato prodotto non ancora assegnato ad un box, il sistema ne sceglie uno in automatico.
     3. Se i prodotti ricevuti non corrispondono all'ordine, lo stato dell'ordine non cambia e rimane "in attesa".
    
  9. Quando un magazziniere prende in carico una movimentazione, lo stato di quest'ultima passa da "in attesa" a "presa in carico".
     Una volta posizionato il carico nell'opportuna destinazione, lo stato della movimentazione viene aggiornato in "completata".

## MoSCoW

| Must have                   | Should have      | Could have | Won't have |
|-----------------------------|------------------|------------|------------|
|    1                        |      3.5         |     8.2    |    4.5     |
|    2                        |      4.6 - 4.7   |            |            |
|    3.1 - 3.2 - 3.3 - 3.4    |      6.2 - 6.3   |            |            |
|    4.1 - 4.2 - 4.3 - 4.4    |      8.1 - 8.2   |            |            |
|    5.1 - 5.2                |                  |            |            |
|    6.1 - 6.4                |                  |            |            |
|    7                        |                  |            |            |
|    9                        |                  |            |            |

