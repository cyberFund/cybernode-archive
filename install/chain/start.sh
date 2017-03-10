#!/bin/bash
cd /usr/local/bin
screen -dmS golosd ./golosd -d /golos
rl=0
while [ $rl = 0 ]
do
    sleep 1s
    r=$(curl -d '{"id":"1","method":"get_dynamic_global_properties","params":[""]}' 127.0.0.1:8090 | grep result)
    rl=${#r}
done
screen -dmS cliwallet ./cli_wallet --server-rpc-endpoint=ws://127.0.0.1:8090 --rpc-http-endpoint=127.0.0.1:8091 --rpc-http-allowip 127.0.0.1 -d
screen -r golosd