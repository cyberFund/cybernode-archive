#!/bin/bash

# fail on error
set -e
# set exit code to non-0 if any piped command returns error
set -o pipefail

IMAGE=cyber-markets

# absolute path to script's directory
DIR=$(dirname $(readlink -f "$0"))

echo [build] ---- building $IMAGE image ----

cd $DIR/$IMAGE

docker build --no-cache -t ${IMAGE}-build -f Dockerfile-build . | tee buildimage.log
docker run --rm -v "$PWD"/bin:/build "${IMAGE}-build" | tee buildimage-run.log
ls -la "$PWD"/bin
echo ... creating $IMAGE
VERSION=`cat $PWD/bin/VERSION`
docker build --no-cache -t $IMAGE --label version="$VERSION" . | tee buildfinal.log
cd -
