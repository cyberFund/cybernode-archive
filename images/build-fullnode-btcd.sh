#!/bin/bash

# fail on error
set -e

# path to script directory
DIR=$(dirname $(readlink -f "$0"))

echo [build] ---- building Bitcoin fullnode based on btcd ----

BUILDDIR="$DIR"/fullnode/btcd
BINDIR=$BUILDIR/bin

echo "Build Dir:   $BUILDDIR"

cd $BUILDDIR
docker build -t fullnode-btcd-build -f Dockerfile-build . | tee build.log
docker run --rm -v $BINDIR:/build fullnode-btcd-build | tee build-run.log
echo ... built btcd binaries:
ls -la $BINDIR
echo ... creating fullnode-btcd image
docker build -t fullnode-btcd . | tee fullnode-build.log
cd -
