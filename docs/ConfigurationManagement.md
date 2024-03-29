# Configuration management

Il lavoro svolto è stato supportato in tutto e per tutto dall'utilizzo di *GitHub* e *git* come version controller.  
Infatti è stata creata una repository che permette la condivisione sia della documentazione che del codice.

### Struttura della repository

La repository è composta da due cartelle principali:
- `./docs/`: contiene tutta documentazione (project plan, diagrammi...)
- `./proj_eclipse/`: contiene il codice

## Issues

Nei vari incontri periodici sono stati creati degli *issue* ognuno dei quali contiene la descrizione del compito da svolgere e l’indicazione della persona a cui è stato assegnato.
Gli issues sono stati usati sia per segnalare i problemi (*bug*) che si sono riscontrati che per le aggiunte/modifiche che andrebbero apportate al progetto.

## Utilizzo dei branch

È stata ampiamente utilizzata la funzionalità dei *branch* di *git* che ha permesso ad ognuno di *introdurre modifiche senza interferire con il lavoro altrui* e mantenendo un ramo principale più stabile sul quale, solo successivamente tramite l'approvazione di una *pull request*, andare ad applicare le modifiche introdotte sul ramo secondario.

![screen di git log](./img/screens_git_log/log1_light.png)

## Procedura adottata

La gestione delle modifiche/aggiunte all'interno del programma è stata svolta seguendo la procedura:  
1. **Apertura di un issue contente la descrizione del problema/modifica**  
   _Esempio:_
   
     <img width="452" alt="issueW" src="https://github.com/JohnnyLAmpAz/smartmag/assets/145765934/ada92fe5-8c66-4231-8a33-0bf99612d8f6">


3. **Creazione di un branch nel quale apportare le modifiche**  
  _Esempio:_

    <img width="264" alt="branchW" src="https://github.com/JohnnyLAmpAz/smartmag/assets/145765934/c153c145-deb1-4f25-b430-0e8102277893">


5. **Generazione di una Pull Request**  
  _Esempio:_


    <img width="611" alt="prW" src="https://github.com/JohnnyLAmpAz/smartmag/assets/145765934/45e48147-fdb3-4dd6-95d1-49bf95908e0f">


7. **Approvazione o richiesta di ulteriori modifiche**  
  _Esempio:_

     <img width="450" alt="Commenti" src="https://github.com/JohnnyLAmpAz/smartmag/assets/145765934/638f1cae-9870-4e55-9923-dab4211b6598">

9. **Merge nel main dal branch**

11. **Chiusura del branch**


## Kanban Board

È stata utilizzata una Kanban Board composta da 4 colonne: Todo, In progress, Pending review, Done.  
La kanban Board è stata utile per monitorare lo stato delle varie attività da svolgere, in particolare per gli issues e le Pull Request.

   <img width="715" alt="kan2W" src="https://github.com/JohnnyLAmpAz/smartmag/assets/145765934/de5a845f-1c90-46a1-b7c4-b61a99786f7e">


- _Todo:_ contiene gli issues che sono stati generati ma non ancora in elaborazione.
- _In progress:_ contiene gli issues che sono in fase di elaborazione ma non ancora completati.
- _Pending review:_ contiene gli issues che sono in fase di elaborazione ma per cui è richiesto un controllo oppure quando si genera una Pull Request.
- _Done:_ Contiene gli issues che sono stati chiusi e controllati da almeno un membro del team
