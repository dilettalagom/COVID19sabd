#!/usr/bin/env bash
#sh $NIFI_HOME/bin/nifi.sh install
sudo chmod 777 $NIFI_HOME
sh $NIFI_HOME/bin/nifi.sh start
sh $NIFI_HOME/bin/nifi.sh status

exec /bin/bash -c "trap : TERM INT; sleep infinity & wait"
