#!/bin/bash

# fail on error
set -e

# path to script directory
DIR=$(dirname $(readlink -f "$0"))

echo [build] ---- building Bitcoin fullnode based on btcd ----

BUILDDIR="$DIR"/fullnode/btcd
BINDIR=$BUILDDIR/bin

echo "Build Dir:   $BUILDDIR"

cd $BUILDDIR
docker build --no-cache -t fullnode-btcd-build -f Dockerfile-build . | tee buildimage.log
docker run --rm -v $BINDIR:/build fullnode-btcd-build | tee buildimage-run.log
echo ... built btcd binaries:
ls -la $BINDIR
echo ... creating fullnode-btcd image
VERSION=`cat $BINDIR/VERSION`
docker build --no-cache -t fullnode-btcd --label version="$VERSION" . | tee buildfinal.log
cd -
