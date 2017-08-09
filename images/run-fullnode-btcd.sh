
# fail on error
set -e

# empty if `cyber` does not exist, set to `cyber` uid otherwise
CYBER=$(id -u cyber 2>/dev/null) || true   # "|| true" ignores error
if [ -z "$CYBER"]; then
  echo "Warning: no 'cyber' user, running with docker default ('root')"
  RUNUSER=
else
  # -u $(id -u cyber)  - run container under user `cyber`, param needs uid
  RUNUSER=-u $CYBER
fi


IMAGE=fullnode-btcd
NAME=btcd
VERSION=`docker inspect --format='{{.Config.Labels.version}}' $IMAGE`

# -p 8333:8333  - host:container - expose port 8333 as 8333 from host
#   8333 (Bitcoin P2P) is accessible from outside
#   8334 (JSON-RPC/WS) only locally
PORTS="-p 8333:8333 -p 127.0.0.1:8334:8334"

echo ... starting $NAME $VERSION from $IMAGE
docker run -d --restart always $RUNUSER $PORTS --name $NAME -v /home/cyber/cyberdata/$NAME:/cyberdata $IMAGE $*
#    -d                  - run as daemon 
#    --name btcd         - just convenient name to find running container
#    -v .a.:.b.          - mount .a. in host as .b. in container
