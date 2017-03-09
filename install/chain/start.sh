#!/bin/bash
screen -dmS golosd /usr/local/bin/golosd
rl=0
while [ $rl = 0 ]
do
    sleep 1s
    r=$(curl -d '{"id":"1","method":"get_dynamic_global_properties","params":[""]}' 127.0.0.1:8090 | grep result)
    rl=${#r}
done
screen -dmS cliwallet /usr/local/bin/cli_wallet --server-rpc-endpoint=ws://127.0.0.1:8090 --rpc-http-endpoint=127.0.0.1:8091 --rpc-http-allowip 127.0.0.1 -d
