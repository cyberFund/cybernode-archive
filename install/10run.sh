IMAGE=fullnode-btcd
NAME=btcd
docker run -d -u $(id -u cyber) -p 8333:8333 --name $NAME -v /home/cyber/cyberdata/$IMAGE:/cyberdata $IMAGE
