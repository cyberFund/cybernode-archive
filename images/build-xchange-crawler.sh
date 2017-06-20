#!/bin/bash

# fail on error
set -e

IMAGE=xchange-crawler

# absolute path to script's directory
DIR=$(dirname $(readlink -f "$0"))

echo [build] ---- building $IMAGE image ----

cd $DIR/$IMAGE

docker build --no-cache -t ${IMAGE}-build -f Dockerfile-build . | tee build.log
docker run --rm -v "$PWD"/bin:/build "${IMAGE}-build" | tee build-run.log
ls -la "$PWD"/bin
echo ... creating $IMAGE
VERSION=`cat $BINDIR/VERSION`
docker build -t $IMAGE --label version="$VERSION" . | tee build-$IMAGE.log
cd -
