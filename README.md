##SABD:COVID19

*COVID19* è un sistema distribuito per l’analisi dei dati relativi ai contagi sia su scala nazionale che globale. 

L’architettura è costituita da:
- un nodo di pre-processamento e pull dei raw-data costituito dall’engine **Nifi**;
- un cluster di processamento costituito da 4 nodi ospitanti il framework **Spark** e l’**Hadoop Distributed File System** secondo l’architettura master-worker (1 master e 3 workers);
- un cluster di storage costituito da 4 nodi ospitanti il datastorage **Cassandra** secondo l’architettura peer-to-peer (3 peers e 1 nodo seeder);
- Un nodo connettore ospitante **Flask** per lo scambio di flussi di dati tra Cassandra e Grafana;
- Un nodo ospitante la piattaforma **Grafana** per l’interpretazione visiva dei dati.

###Build dell’architettura
Per istanziare il sistema è necessario:
- aver istallato il supporto docker in locale
- eseguire il comando ```$docker-compose up -- build ``` 
per effettuare la pull e la build delle immagini.

###Esecuzione delle query
Per invocare l’esecuzione delle varie query è necessario:
- spostarsi sul nodo master di Spark eseguendo: 
    ``` $docker exec -it master bash```
- richiedere la query specifica eseguendo: 
```$ sh execute-query.sh -q <num_query> [-t <kmeans_algorithm_type>]```
 dove:
  1. ```<num_query>: [1,2,3]``` corrisponde al numero della query che si vuole eseguire (1,2,3),
  2. ```<kmeans_algoritm_type>: [naive, mllib, ml]``` è il parametro obbligatorio della query 3, utilizzato per specificare l'esecuzione di una delle possibili implementazioni dell’algoritmo di clustering

###Visualizzazione dei risultati
????????????????????'

####Entrypoints aggiuntivi
Altri entrypoints interessanti sono:
- interfaccia grafica di *Nifi*, accessibile dal browser collegandosi all’indirizzo http://0.0.0.0:4040/nifi
- interfaccia grafica di *HDFS*, accessibile dal browser collegandosi all’indirizzo http://0.0.0.0:9870
- lo stato delle partizioni dei dati sul cluster eseguendo ```$docker exec -it  <hostname_cluster_cassandra> nodetool status```
```<hostname_cluster_cassandra>``` può essere il nome di uno dei nodi del cluster: ```[cassandra1, cassandra2, cassandra3, cassandra-node-seed]```
- interfaccia grafica di *Grafana* http://0.0.0.0:3000/
