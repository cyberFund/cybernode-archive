#!/bin/bash

# fail on error
set -e
# set exit code to non-0 if any piped command returns error
set -o pipefail

IMAGE=cybernode/bitcoin-btcd
BUILDIMAGE=build-$IMAGE

# absolute path to script's directory
DIR=$(dirname $(readlink -f "$0"))

echo [build] ---- building Bitcoin fullnode based on btcd ----

BUILDDIR="$DIR"/fullnode/btcd
BINDIR=$BUILDDIR/bin
BUILDMOUNT=$BINDIR:/build

# SELINUX on RedHat/Fedora needs workaround for Docker volumes
if [[ `getenforce` == "Enforcing" ]]; then
  echo "SELINUX is enabled, adding :z suffix to mounts"
  # :z marks volume to be accessible by all containers
  # :Z could be used to isolate access to a single container
  BUILDMOUNT=$BUILDMOUNT:z
fi


echo "Build Dir:   $BUILDDIR"

cd $BUILDDIR
docker build --no-cache -t ${BUILDIMAGE} -f Dockerfile-build . | tee buildimage.log
docker run --rm -v $BUILDMOUNT ${BUILDIMAGE} | tee buildimage-run.log
echo ... built btcd binaries:
ls -la $BINDIR
echo ... creating ${IMAGE} image
VERSION=`cat $BINDIR/VERSION`
docker build --no-cache -t ${IMAGE} --label version="$VERSION" . | tee buildfinal.log
cd -
