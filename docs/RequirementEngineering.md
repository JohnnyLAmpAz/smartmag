# Requirement engineering

## Elicitazione dei requisiti

Per quanto riguarda questa fase, è stato fondamentale confrontarsi con persone che hanno lavorato nel settore della logistica.  
Per riuscire ad ottenere tutte le informazioni necessarie alla realizzazione del sistema, sono state usate le seguenti tecniche di elicitazione dei requisti:

- __Intervistare/chiedere:__
  
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
  
  E' stato molto utile farsi raccontare da lavoratori nel settore della logistica alcune delle possibili situazioni che si sono presentate nel        corso di una giornata lavorativa. ( esempio che va nel box e non trova le quantità giuste)

- __Derivazione da sistema esistente:__
  
  Poichè i requisiti emersi, spesso derivano da funzionalità messe a disposizione da altri software di gestione di magazzino.
  Questo perchè ci si basa sull'esperienza di persone che hanno già usufruito di sistemi simili a quello che si vuole sviluppare in questo progetto.

#### Requisiti funzionali elicitati con metodo dell'intervista e con tecnica degli scenari:

Il sistema deve gestire l’organizzazione del magazzino composta da varie corsie indicate da una lettera (o una coppia di lettere in caso di necessità), 
ogni corsia contiene uno scaffale (facciata) di sinistra e uno scaffale di destra e a loro volta gli scaffali sono suddivisi in slot. 
Gli slot degli scaffali di sinistra hanno indice dispari mentre gli slot degli scaffali di destra hanno indice pari.  
Ogni slot a sua volta è diviso in piani identificati da un numero intero.  

Per accedere al software è richiesto il log-in dei vari utenti, nel quale andrà inserito Id utente e password che vengono forniti dal manager di magazzino.
Il manager, appunto, si occupa della gestione del personale inserendone i dati a gestionale quali: matricola, nome, cognome e ruolo.
Inoltre, il manager, gestisce la lista di prodotti presenti nel magazzino.
All’occorrenza, può aggiungere nuovi prodotti, ai quali viene assegnato un box nel quale stoccarli.
Ogni box, infatti, potrà contenere una certa quantità di uno solo prodotto.

Il responsabile ordini gestisce la lista dei prodotti del magazzino (aggiunta e rimozione).
Ha modo di creare un nuovo ordine, manualmente o tramite file, che rimarrà in attesa di approvazione. 

Il sistema verifica la disponibilità dei prodotti richiesti in un ordine e in caso favorevole il responsabile può approvare
l’ordine permettendo ai magazzinieri di processarlo.
Se il responsabile non approva l’ordine, lo stato di quest’ultimo rimarrà “in attesa di rifornimenti scorte”.

All’approvazione di un ordine da parte del responsabile, 
il sistema genera in automatico una serie di richieste di movimentazione per ciascun prodotto coinvolto nell’ordine.

La lista di tutte le richiese di movimentazione legate ad un certo ordine, ciascuna delle quali specifica la quantità di prodotto, 
la locazione d’origine e quella di destinazione,
viene presentata ai magazzinieri i quali possono prenderle in carico e contrassegnarle come “in lavorazione”.
Una volta effettuato uno spostamento il magazziniere lo segna come completato.

Nel caso in cui uno spostamento relativo ad un ordine prevede lo spostamento di quantità non presente a magazzino, annulla la movimentazione, 
aggiorna le quantità presenti nel box e dunque il sistema si chiede se la quantità cercata è disponibile altrove in altri box o meno.
Se la quantità richiesta è disponibile in altri box allora genera automaticamente altre richieste di spostamento da essi, 
altrimenti il sistema riporta l’ordine allo stato iniziale di “in attesa di rifornimenti scorte”.

Il responsabile, a fronte di disponibilità limitate di certi prodotti, può effettuare richieste di acquisto, 
specificando le quantità di rifornimento per i vari prodotti coinvolti.
Anche queste richieste di rifornimento effettueranno vari passaggi di stato, sempre gestiti dal responsabile.

Alla consegna dei rifornimenti, i magazzinieri qualificati verificano che quanto sia stato ordinato corrisponda al carico arrivato.
In caso favorevole accettano la consegna, contrassegnando l'ordine di rifornimento come ricevuto, e procedono allo scarico delle merci.

A fronte dell’accettazione della consegna di rifornimento, 
il sistema informativo genera in automatico delle richieste di spostamento delle merci dalla zona di carico/scarico agli scaffali designati.
Nel caso in cui un nuovo prodotto non sia stato ancora assegnato ad un box, il sistema ne sceglie uno in automatico.

Nell’eventualità di rifiuto della consegna per non corrispondenza delle merci ricevute con quelle ordinate, 
lo stato dell’ordine di rifornimento rimane in attesa di consegna.

Movimentazione ordini in entrata: I magazzinieri controllano la lista di movimentazioni in entrata e ne prendono in carico una alla volta. 
Quando il magazziniere prende in carico la movimentazione, aggiorna il suo stato da: “in attesa” a “in trasporto”.
Una volta posizionato il carico nell’opportuna destinazione, il magazziniere aggiorna lo stato della movimentazione in “conclusa”
e la movimentazione non verrà più visualizzata nella lista delle movimentazioni da effettuare. 

## MoSCoW

