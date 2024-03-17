# Software Design

## Diagramma dei componenti

## Design pattern utilizzati

## Misurazione del codice
### JDepend:
Per misurare le metriche indicate nella tabella seguente e valutare così la qualità della progettazione del software, abbiamo fatto ricorso a JDepend.  
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
Per avere un'ulteriore valutazione sulle dipendenze delle classi e dei pacchetti, abbiamo utilizzato Structure101.  
Principalmente sono state valutate le classi contenute nei pacchetti e quindi il loro livello di coesione e la presenza di loop.

- Package "Data":  
  Non sono emersi loop all'interno di questo pacchetto e la coesione tra le classi sembra essere molto elevata:

  ![St101_data](https://github.com/JohnnyLAmpAz/smartmag/assets/145765934/bdb2f44b-9a5c-4a67-ac87-08f7d60d57df)

- Package "Models":  
  E' emersa la presenza di un loop all'intermo di questo pacchetto:

  <img width="170" alt="St101_models_cattivo" src="https://github.com/JohnnyLAmpAz/smartmag/assets/145765934/0b605d8a-dbcd-4e19-85ff-74e9a44c3845">

- Package "ui":  
  E' emersa la presenza di un loop (molto semplice da correggere) all'intermo di questo pacchetto:
  
  <img width="515" alt="image" src="https://github.com/JohnnyLAmpAz/smartmag/assets/145765934/573e0152-5110-4b68-8029-62930bf4977d">


Riuscire a identificare questi loop è molto importante per poter sistemare il codice in modo tale da renderne la struttura più solida e facile da manutenere.


