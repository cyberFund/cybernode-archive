#!/bin/bash

# fail on error
set -e

IMAGE=fullnode-btcd

# absolute path to script's directory
DIR=$(dirname $(readlink -f "$0"))

echo [build] ---- building Bitcoin fullnode based on btcd ----

BUILDDIR="$DIR"/fullnode/btcd
BINDIR=$BUILDDIR/bin

echo "Build Dir:   $BUILDDIR"

cd $BUILDDIR
docker build --no-cache -t ${IMAGE}-build -f Dockerfile-build . | tee buildimage.log
docker run --rm -v $BINDIR:/build ${IMAGE}-build | tee buildimage-run.log
echo ... built btcd binaries:
ls -la $BINDIR
echo ... creating ${IMAGE} image
VERSION=`cat $BINDIR/VERSION`
docker build --no-cache -t ${IMAGE} --label version="$VERSION" . | tee buildfinal.log
cd -
