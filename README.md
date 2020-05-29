##SABD:COVID19

*COVID19* è un sistema distribuito per l’analisi dei dati relativi ai contagi sia su scala nazionale che globale. 

L’architettura è costituita da:
- un nodo di pull dei dataset e preprocessamento costituito dall’engine **Nifi**;
- un cluster di processamento costituito da 4 nodi ospitanti il framework **Spark** e l’**Hadoop Distributed File System** secondo l’architettura master-worker (1 master e 3 workers);
- un cluster di storage per il salvataggio delle query costituito dal datastore **Cassandra**;
- Un nodo connettore ospitante **Flask** per lo scambio dei dati tra Cassandra e Grafana;
- Un nodo ospitante la piattaforma **Grafana** per la visualizzazione dei dati.

###Build dell’architettura
Per istanziare il sistema è necessario:
- aver istallato il supporto docker in locale
- eseguire il comando ```$docker-compose up -- build ``` 
per effettuare la pull e la build delle immagini.

###Esecuzione delle query
Per invocare l’esecuzione delle varie query è necessario:
- avviare il *pre-processing processor* all'interno dell'interfaccia grafica di Nifi, accedendo all'URL http://localhost:4040/nifi 
- spostarsi sul nodo master di Spark eseguendo: 
    ``` $docker exec -it master bash```
- richiedere la query specifica eseguendo: 
```$ sh execute-query.sh -q <num_query> [-t <kmeans_algorithm_type>]```
 dove:
  1. ```<num_query>: [1,2,3]``` corrisponde al numero della query che si vuole eseguire (1,2,3),
  2. ```<kmeans_algoritm_type>: [naive, mllib, ml]``` è il parametro obbligatorio della query 3, utilizzato per specificare l'esecuzione di una delle possibili implementazioni dell’algoritmo di clustering

###Store dei risultati 
Per visualizzare i risultati delle query all'interno del datastore Cassandra è necessario:
- spostarsi su uno dei nodi del cluster Cassandra eseguendo: 
    ``` $docker exec -it <hostname_cluster_cassandra> bash```
 dove:
 ```<hostname_cluster_cassandra>``` può essere il nome di uno dei nodi del cluster: ```[cassandra1, cassandra2]```
- caricare lo schema Covid19 eseguendo:
     ``` $cqlsh -f COVID19schema.cql```
- avviare il *result-query processors* (eventualmente avviando il processore all'interno dell'interfaccia grafica di Nifi relativo ai risultati della specifica query)
- richiedere i valori desiderati interagendo con il terminale cqlsh di Cassadra (ES: ``` SELECT * FROM covid19.query1_results```)


###Visualizzazione dei risultati
Per visualizzare graficamente i risultati delle query salvati in Cassandra all'interno della dashboard di Grafana è necessario:
- collegarsi attraverso un browser all'indirizzo http://localhost:3000/ per accedere alla dashboard di Grafana
- inserire i valori di username e password (admin-admin)
- selezionare una delle query disponibili nelle dashboard


####Entrypoints aggiuntivi
Altri entrypoints interessanti sono:
- interfaccia grafica di *Nifi*, accessibile dal browser collegandosi all’indirizzo http://localhost:4040/nifi
- interfaccia grafica di *HDFS*, accessibile dal browser collegandosi all’indirizzo http://localhost:9870
- lo stato delle partizioni dei dati sul cluster eseguendo ```$docker exec -it  <hostname_cluster_cassandra> nodetool status```
```<hostname_cluster_cassandra>``` può essere il nome di uno dei nodi del cluster: ```[cassandra1, cassandra2, cassandra-node-seed]```
- interfaccia grafica di *Grafana* http://localhost:3000/
