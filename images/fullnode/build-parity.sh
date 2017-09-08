#!/bin/bash

# fail on error
set -e
# set exit code to non-0 if any piped command returns error
set -o pipefail

IMAGE=fullnode-parity

# absolute path to script's directory
DIR=$(dirname $(readlink -f "$0"))

echo [build] ---- building Ethereum fullnode based on Parity ----

BUILDDIR="$DIR"/parity
BINDIR=$BUILDDIR/bin
# caching build dependencies to speed up builds
CACHE=$BUILDDIR/.cache

echo "\
Build Dir:   $BUILDDIR
Binaries:    $BINDIR
Build Cache: $CACHE"

mkdir -p $BINDIR $CACHE

cd $BUILDDIR
CACHEVOL="-v $CACHE:/root/.cargo"

docker build --no-cache $CACHEVOL -t ${IMAGE}-build -f Dockerfile-build . | tee buildimage.log
#docker run --rm -v $BINDIR:/build ${IMAGE}-build | tee buildimage-run.log
#echo ... built btcd binaries:
#ls -la $BINDIR
#echo ... creating ${IMAGE} image
#VERSION=`cat $BINDIR/VERSION`
#docker build --no-cache -t ${IMAGE} --label version="$VERSION" . | tee buildfinal.log
#cd -
