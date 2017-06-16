#!/bin/bash

# fail on error
set -e

# path to script directory
DIR=$(dirname "$0")

# building Bitcoin fullnode based on btcd

BUILDDIR="$DIR"/btcd
cd $BUILDDIR
docker build -t fullnode-btcd-build -f Dockerfile-build . | tee build.log
docker run --rm -v "$PWD"/bin:/build fullnode-btcd-build | tee build-run.log
echo ... built btcd binaries:
ls -la $BUILDDIR/bin
echo ... creating fullnode-btcd image
docker build -t fullnode-btcd . | tee fullnode-build.log
cd -
