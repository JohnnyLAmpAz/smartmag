# Requirement engineering

## Elicitazione dei requisiti

In questa fase è stato fondamentale confrontarsi con persone che hanno lavorato nel settore della logistica.
Per riuscire ad ottenere tutte le informazioni necessarie alla realizzazione del sistema sono state usate le seguenti tecniche di elicitazione dei requisiti:

- __Intervistare/chiedere:__

  Sono state organizzate delle riunioni, nelle quali, mediante la tecnica del brainstorming, è stato possibile identificare alcuni dei requisiti     
  necessari allo sviluppo del sistema.  
  La sessione di brainstorming è stata suddivisa in due fasi principali:
  - La prima aveva l'obiettivo di generare il maggior numero possibile di idee.
  - La seconda,invece, di selezionare tra le idee generate quelle più utili alla realizzazione del sistema finale.
  
- __Analisi delle attività:__
  
  Sono state analizzate le varie attività che il personale di un magazzino deve svolgere nel corso della giornata lavorativa.  
  Da questa analisi è stato possibile definire i seguenti "attori" e i relativi compiti:
  
  - _Magazziniere:_
    
    Movimenta i prodotti da una locazione all’altra, preleva il prodotto, verifica la disponibilità e la locazione del prodotto mediante il suo codice.
    Altresì deve avere la possibilità di modificare le quantità di prodotti a magazzino.
    
  - _Magazzinieri qualificati:_

    Oltre all'attività di magazziniere può anche accettare o rifiutare i rifornimenti.
    
  - _Responsabile ordini:_
  
    Genera gli ordini di prelievo e deposito prodotti nel magazzino. Ha modo di ottenere la lista dei prodotti con rimanenze sottosoglia.    
    Inoltre ha la possibilità di visionare le statistiche riguardanti il magazzino.
    
  - _Manager magazzino:_

    Gestisce il personale: stabilisce ruoli, definisce gli utenti e genera le statistiche lavorative sul personale. Ha accesso alle informazioni di accounting (storico).
    Gestisce i prodotti (aggiunta di nuovi, rimozione, etc.) e li assegna ai box.

  - _Sistema informativo:_
  
    Quando le rimanenze di prodotto scendono sotto una certa soglia, invia una notifica al responsabile ordini se non ha già provveduto al riordino.
    Gestisce l'accounting: storico degli spostamenti/modifiche prodotti. 

- __Analisi degli scenari:__
 
  È stato molto utile ascoltare i racconti dei lavoratori nel settore della logistica riguardanti alcune delle situazioni che si sono presentate 
  nel corso della loro esperienza lavorativa. Grazie a queste testimonianze è stato possibile elicitare quei requisiti che solo chi ha lavorato nel settore logistico è in grado di fornire.

- __Derivazione da sistema esistente:__
  
  Poiché la fase di elicitazione dei requisiti si è basata in gran parte sull’esperienza dei lavoratori nel settore logistico, inevitabilmente   
  alcune delle funzionalità implementate nel progetto derivano da sistemi già esistenti.

## Specifica dei requisiti

1. Il magazzino deve essere composto da:
    - Corsie: identificate da una o più lettere.
    - Scaffali: una corsia può contenere sia uno scaffale a sinistra che uno a destra.
    - Slot: gli scaffali sono divisi in slot. Per gli scaffali a sinistra, il numero dello slot è dispari, mentre per quelli a destra, il numero   
      dello slot è pari.
    - Piani: uno slot è suddiviso in piani, ognuno dei quali è identificato da un numero intero
    - Box: identifica la locazione di un prodotto ed è dato dall'insieme delle parti che costituiscono il magazzino elencate sopra.

   Ecco un esempio di come dovrà essere gestita una corsia all'interno di SmartMag:

    <img width="482" alt="Struttura magazzino" src="https://github.com/JohnnyLAmpAz/smartmag/assets/145765934/67260cbf-f99c-4a0e-a860-aaf650dff81f">
    
2. Per accedere al software è richiesto il log-in dell'utente, dove andranno inseriti Matricola e password.
  
3. Il manager, si dovrà occupare della gestione del personale.Egli dovrà poter:
   1. Creare gli utenti di tutto il personale inserendo: matricola, nome, cognome, ruolo, password.
   2. Accedere alla lista dei prodotti presenti a magazzino
   3. Aggiungere o rimuovere prodotti nel magazzino
   4. Assegnare un box ed una quantità a ciascun prodotto
   5. Modificare le quantità dei prodotti

4. Il responsabile ordini si occupa della gestione degli ordini. Egli dovrà poter:
   1. Accedere alla lista dei prodotti presenti a magazzino
   2. Accedere alla lista degli ordini
   3. Aggiungere e rimuovere un ordine
   4. Inserire un ordine manualmente
   5. Inserire un ordine da file
   6. Modificare un ordine se è in stato di attesa e non sono ancora state generate le relative movimentazioni
   7. Approvare le movimentazioni proposte dal sistema in seguito all'inserimento dell'ordine

5. Le movimentazioni devono essere generate dal sistema a seguito dell'approvazione da parte del responsabile ordini.
   1. Per ogni prodotto presente nell'ordine deve essere generata una o più movimentazione.
   2. La movimentazione deve specificare: l'ordine, il prodotto, l'origine, la destinazione, lo stato e il magazziniere che l'ha presa in carico.
   3. La movimentazione deve contenere anche la data di completamento per l'accounting.

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
     Quando un magazziniere preleva il prodotto dal box, lo stato della movimentazione passa da "presa in carico" a "prelevata".
     Una volta posizionato il carico nell'opportuna destinazione, lo stato della movimentazione viene aggiornato in "completata".

## MoSCoW - negoziazione dei requisiti

I requisiti del progetto, sono stati inseriti all'interno della seguente tabella in base alla loro priorità applicando la tecnica MoSCoW:

| Must have                   | Should have      | Could have | Won't have |
|-----------------------------|------------------|------------|------------|
|    1                        |      3.5         |     8.2    |    4.5     |
|    2                        |      4.6         |  6.2 - 6.3 - 6.4 |   4.7      |
|    3.1 - 3.2 - 3.3 - 3.4    |      8.1 - 8.2   |            |    5.3     |
|    4.1 - 4.2 - 4.3 - 4.4    |                  |            |            |
|    5.1 - 5.2                |                  |            |            |
|    6.1                      |                  |            |            |
|    7                        |                  |            |            |
|    9                        |                  |            |            |

Durante la fase di negoziazione è stata decisa l'assegnazione dei requisiti nella tabella mostrata sopra.
