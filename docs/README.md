# Documentazione

Qui metteremo tutta la documentazione

## Testing

Abbiamo configurato *JUnit 5* attraverso il `pom.xml` in modo tale che vengano 
eseguiti tutti gli unit test presenti nel progetto durante le build di Maven.

Configurando poi ad-hoc una *GitHub Action*
(vedasi `/.github/workflows/maven.yml`), abbiamo ottenuto un workflow di 
*Continuous Integration* il quale esegue in ambiente cloud "sterile" le build 
Maven, **test compresi**, ad ogni PR/push sul branch _main_. Questo 
costituisce un ottimo feedback sui risultati dei test indipendentemente dallo 
stato dell'ambiente di sviluppo utilizzato.
