COVID19 è un sistema distribuito per l’analisi dei dati relativi ai contagi sia su scala nazionale che globale. L’obiettivo è quello di fornire un sistema altamente disponibili e persistente (AP) per garantire una risposta tempestiva agli utenti. 
L’architettura è costituita da ?? 
Un nodo di pre-processamento e pull dei raw-data costituito dall’engine Nifi
Un cluster di processamento costituito da 3 nodi ospitanti il framework Spark e l’Hadoop Distributed FileSystem secondo l’architettura master-worker (1 master e 2 workers)
Un cluster di storage costituito da 3 nodi ospitanti il datastorage Cassandra secondo l’architettura peer-to-peee (2 workers e 1 nodo seeder)
Un nodo server ospitante Flask, con il ruolo di connettore per lo scambio di flussi di dati processati 
Un nodo ???? ospitante il framework Grafana per l’ interpretazione visiva dei dati
###Build dell’architettura
Per istanziare il sistema è necessario:
Avere istallato il supporto docker in locale
Aver fornito almeno 3GB di RAM al docker-hub (???)
e eseguire il comando $docker-compose up - - build per effettuare la pull e build delle immagini. 
###Esecuzione delle query
Per invocare l’esecuzione delle varie query è necessario:
Spostarsi sul nodo master di Spark eseguendo: $docker exec -it master bash
Richiedere la query specifica eseguendo: $ sh execute-query.sh -q <num_query> [-t <kmeans_algorithm_type>]. Il <num_query> corrisponde al numero della query che si vuole eseguire (1,2,3), mentre il <kmeans_algoritm_type> è un parametro obbligatorio solo nel caso della query 3, per specificare quale implementazione utilizzare per l’esecuzione dell’algoritmo di clustering (naive, mllib, ml) 
Visualizzare su grafana (?) 
Altri entrypoints interessanti sono:
-interfaccia grafica di nifi, accessibile dal browser collegandosi all’indirizzo http://0.0.0.0:4040/nifi
-interfaccia grafica di hadoop, accessibile dal browser collegandosi all’indirizzo http://0.0.0.0:9860 (??)
-interfaccia grafica di cassandra (???) e lo stato delle partizioni dei dati sul cluster eseguendo $docker exec -it  <hostname_cluster_cassandra> nodetool status
<hostname_cluster_cassandra> può essere il nome di uno dei nodi tra cassandra1, cassandra2, cassandra-node-seed
-interfaccia grafica di grafana http://localhost:3000/.

