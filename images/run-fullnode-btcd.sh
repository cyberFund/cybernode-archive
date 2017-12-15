#!/bin/bash

# fail on error
set -e
# show commands
set -x

# name of running container and image
NAME=bitcoin-btcd
IMAGE=cybernode/bitcoin-btcd


# empty if `cyber` does not exist, set to `cyber` uid otherwise
CYBER=$(id -u cyber 2>/dev/null) || true   # "|| true" ignores error
if [ -z "$CYBER" ]; then
  echo "Warning: no 'cyber' user, running with docker default ('root')"
  RUNUSER=
else
  # -u $(id -u cyber)  - run container under user `cyber`, param needs uid
  RUNUSER="-u $CYBER"
fi


# if /cyberdata does not exist
if [ -z "$CYBERDATA" ]; then
  if [ -z "$CYBER" ]; then
    echo "Error: set CYBERDATA to store 'btcd' blockchain (150Gb+)."
    echo "       Make sure to use separate partition from your root"
    echo "       to avoid filling system disk and locking your system."
    exit 1
  else
    CYBERDATA=/home/cyber/cyberdata/$NAME
  fi
fi

VOLUMEMOUNT=$CYBERDATA:/cyberdata
# RedHat/Fedora come with SELINUX, which needs workaround for Docker
if [[ `getenforce` == "Enforcing" ]]; then
  echo "SELINUX is enabled, adding :z suffix to mounts"
  # :z marks volume to be accessible by all containers
  # :Z could be used to isolate access to a single container
  VOLUMEMOUNT=$VOLUMEMOUNT:z
fi


# pull image if not exists
docker inspect $IMAGE || EXIT_CODE=$? && true;
if [[ $EXIT_CODE != 0 ]]; then
    docker pull $IMAGE
fi


VERSION=`docker inspect --format='{{.Config.Labels.version}}' $IMAGE`

# -p 8333:8333  - host:container - expose port 8333 as 8333 from host
#   8333 (Bitcoin P2P) is accessible from outside
#   8334 (JSON-RPC/WS) only locally
PORTS="-p 8333:8333 -p 127.0.0.1:8334:8334"

echo ... starting $NAME $VERSION from $IMAGE
#ARGS=-d debug
ARGS="--rpcuser=cyber --rpcpass=cyber --rpclisten=0.0.0.0:8334 --notls"
docker run -d --restart always --name $NAME $RUNUSER $PORTS -v $VOLUMEMOUNT $IMAGE $ARGS $*
#    -d                  - run as daemon
#    --restart always    - when to restart container
#    --name btcd         - just convenient name to find running container
#    -v .a.:.b.          - mount .a. in host as .b. in container

