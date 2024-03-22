# Testing

Abbiamo creato dei *casi di test* con *JUnit 5* per esaminare la maggior parte delle funzionalità del sistema.
L'obiettivò è individuare il maggior numero possibile di *difetti* (*bug*) durante la fase di build. Ciò viene fatto in vari modi: invocando le funzionalità da testare con parametri non validi, utilizzandole in contesti in cui non dovrebbe essere permesso o semplicemente simulando scenari e sequenze di azioni che riflettono l'uso tipico dell'applicazione.

## Focus sui modelli

In particolare, abbiamo deciso di concentrare la nostra attività di testing sui *modelli* (MVC) in quanto ricoprono un ruolo chiave nel sistema sviluppato:

- implementano la *business logic*;
- espongono le *interfacce* attraverso cui è effettivamente possibile *svolgere operazioni sui dati* gestiti;
- garantiscono l'*integrità dei dati* e la *persistenza* degli stessi.

### Gestione dei record DB utilizzati dai casi di test

Dato che la maggior parte dei modelli opera su record esistenti e non abbiamo un controllo diretto sull'ordine di esecuzione dei test, abbiamo deciso di adottare i seguenti criteri:

- ogni caso di test *NON deve fare affidamento sulla presenza di record* già salvati a DB;
- ogni caso di test *deve creare TUTTI i record a lui necessari* per ricreare lo scenario sotto esame.

In sintesi, **l'esecuzione di un caso di test non deve influenzare in alcun modo quella degli altri**.

Per implementare i principi scelti, abbiamo adottato la seguente strategia: **prima di eseguire ogni caso di test, svuotiamo il database dedicato e, una volta terminati tutti i test, ne cancelliamo il file**. Questo ci assicura che ogni caso di test sia indipendente dagli altri. Ogni test ha il compito di creare i record necessari (attraverso i modelli) e di effettuare i controlli appropriati, senza preoccuparsi dell’ordine di esecuzione. **Questo approccio aumenta l’affidabilità ed l’efficacia dei nostri test**.

## Numeri

Abbiamo realizzato in totale *74 casi di test* che interessano circa l'*80% del codice* dei modelli.

![JUnit](./img/screens_tests/JUnit.jpg)

![Coverage dei test](./img/screens_tests/coverage.png)

## Maven e GitHub Action per la Continuous Integration

Abbiamo configurato *JUnit 5* attraverso il `pom.xml` in modo tale che vengano 
eseguiti tutti gli unit test presenti nel progetto durante le build di *Maven*.

Configurando poi ad-hoc una *GitHub Action*
(vedasi `/.github/workflows/maven.yml`), abbiamo ottenuto un workflow di 
*Continuous Integration* il quale esegue in ambiente cloud "sterile" le build *Maven*, **test compresi**, ad ogni PR/push sul branch _main_. Questo *si integra al meglio nella gestione delle pull request* (mostrando l'esito delle build) e costituisce un *ottimo feedback* sui risultati dei test, indipendentemente dallo stato dell'ambiente di sviluppo utilizzato.

![GitHub CI Action](./img/screens_tests/GH_action.png)

![GitHub CI Action fail](./img/screens_tests/GH_action_fail.png)
