#!/bin/bash

# fail on error
set -e

# building Bitcoin fullnode based on btcd

BUILDDIR="$PWD"/btcd
cd $BUILDDIR
docker build -t fullnode-btcd-build -f Dockerfile . | tee build.log
docker run --rm -v "$PWD"/bin:/build fullnode-btcd-build | tee build-run.log
echo ... built btcd binaries:
ls -la $BUILDDIR/bin
echo ... creating fullnode-btcd image
# TBD
cd -
