#!/bin/bash

# fail on error
set -e

# path to script directory
DIR=$(dirname $(readlink -f "$0"))

echo [build] ---- building xchange-crawler image ----

cd $DIR

IMAGE=xchange-crawler
docker build -t ${IMAGE}-build -f Dockerfile-build . | tee build.log
docker run --rm -v "$PWD"/bin:/build "${IMAGE}-build" | tee build-run.log
ls -la "$PWD"/bin
echo ... creating $IMAGE
docker build -t $IMAGE . | tee build-$IMAGE.log
cd -
