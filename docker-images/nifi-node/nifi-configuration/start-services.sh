#!/usr/bin/env bash
#sh $NIFI_HOME/bin/nifi.sh install
sudo chmod 777 $NIFI_HOME
sh $NIFI_HOME/bin/nifi.sh start
sh $NIFI_HOME/bin/nifi.sh status

wget $URL_DATASET_ITA; mv dpc-covid19-ita-andamento-nazionale.csv $NIFI_HOME/data/covid19_national.csv;
wget $URL_DATASET_GLOBAL; mv time_series_covid19_confirmed_global.csv $NIFI_HOME/data/covid19_global.csv;

exec /bin/bash -c "trap : TERM INT; sleep infinity & wait"
