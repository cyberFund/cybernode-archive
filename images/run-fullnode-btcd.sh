IMAGE=fullnode-btcd
NAME=btcd
VERSION=`docker inspect --format='{{.Config.Labels.version}}' $IMAGE`

# -p 8333:8333  - host:container - expose port 8333 as 8333 from host
#   8333 (Bitcoin P2P) is accessible from outside
#   8334 (JSON-RPC/WS) only locally
PORTS="-p 8333:8333 -p localhost:8334:8334"

echo ... starting $NAME $VERSION from $IMAGE
docker run -d --restart always -u $(id -u cyber) $PORTS --name $NAME -v /home/cyber/cyberdata/$NAME:/cyberdata $IMAGE
#    -d                  - run as daemon 
#    -u $(id -u cyber)   - run container under user `cyber`, param needs uid
#    --name btcd         - just convenient name to find running container
#    -v .a.:.b.          - mount .a. in host as .b. in container
