#!/bin/bash

# fail on error
set -e
# set exit code to non-0 if any piped command returns error
set -o pipefail
# show commands as they are executed
set -x


IMAGE=ethereum-parity
REPO=https://github.com/paritytech/parity

# absolute path to script's directory
DIR=$(dirname $(readlink -f "$0"))

echo [build] ---- building Ethereum fullnode based on Parity ----

BUILDDIR="$DIR"/parity/_build
CLONEDIR=$BUILDDIR/parity
# caching build dependencies to speed up builds
CACHE=$BUILDDIR/.cache

echo "\
Build Dir:   $BUILDDIR
Build Cache: $CACHE"

mkdir -p $BUILDDIR $CACHE
cd $BUILDDIR

# cloning sources
CLONE=yes
if [ -d $CLONEDIR ] ; then
  echo "old checkout detected"
  read -p "remove (y/n)?" choice
  case "$choice" in
    y|Y ) rm -rf $CLONEDIR;;
    n|N ) CLONE=no;;
    * ) exit 1;;
  esac
fi

if [ "$CLONE" == "yes" ]; then
  git clone --depth=100 https://github.com/paritytech/parity $CLONEDIR
fi
REVISION=`git -C $CLONEDIR rev-parse --short=8 HEAD`
TAG="v`date +%Y%m%d`-$REVISION"
echo $TAG > VERSION

echo Patch Rust toolchain version for repeatable builds
# https://github.com/rust-lang-nursery/docker-rust/issues/8
echo 1.20.0 > $CLONEDIR/rust-toolchain

# Back to dir with Dockerfile, because Docker is unable to use
# it from subdirectory
cd ..


# Build new build image with libudev-dev, which is not included upstream
# https://github.com/rust-lang-nursery/docker-rust/blob/master/1.20.0/stretch/Dockerfile
# https://github.com/docker-library/buildpack-deps/pull/67
docker build --no-cache -t build-${IMAGE} -f Dockerfile-build .

#docker run --name build-$IMAGE-tmp rust:1.20-stretch bash -c "apt-get update && apt-get -y install libudev-dev"
#docker commit build-$IMAGE-tmp build-$IMAGE
#docker rm build-$IMAGE-tmp

CACHEVOL="-v $CACHE:/build/.cargo"
COMMAND="CARGO_HOME=/build/.cargo cargo build --release"
docker run --rm --user "$(id -u)":"$(id -g)" $CACHEVOL -v $CLONEDIR:/build -w /build build-$IMAGE /bin/bash -c "$COMMAND" | tee buildimage-run.log


echo ... built $IMAGE binaries:
ls -la $BUILDDIR
ls -la $CLONEDIR
ls -la $CLONEDIR/target
ls -la $CLONEDIR/target/release
mkdir -p $BUILDDIR/bin
cp -r $CLONEDIR/target/release/* $BUILDDIR/bin
echo ... creating ${IMAGE} image
VERSION=`cat $BUILDDIR/VERSION`
docker build --no-cache -t ${IMAGE} --label version="$VERSION" . | tee buildfinal.log

