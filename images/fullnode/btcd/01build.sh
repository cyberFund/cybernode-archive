#!/bin/bash

# fail on error
set -e

# absolute path to script's directory
DIR=$(pwd)

BTCDHASH=master

echo --- detecting defaults ---
go version && go env GOROOT GOPATH
GOPATH=`go env GOPATH`

echo --- get dependency manager ---
go get -u github.com/Masterminds/glide

echo --- clone btcd sources ---
if [ ! -d $GOPATH/src/github.com/btcsuite/btcd ] ; then
    git clone https://github.com/btcsuite/btcd $GOPATH/src/github.com/btcsuite/btcd
else
    echo --- detected btcd, cleaning ---
    rm -R $GOPATH/src/github.com/btcsuite/btcd
    git clone https://github.com/btcsuite/btcd $GOPATH/src/github.com/btcsuite/btcd
fi
cd $GOPATH/src/github.com/btcsuite/btcd
git checkout $BTCDHASH
# record revision
REVISION=`git rev-parse --short=8 HEAD`
echo "v`date +%Y%m%d`-$REVISION" >> $GOPATH/bin/TAG
echo "btcd-revision: $REVISION" >> $GOPATH/bin/VERSION

echo --- applying patches ---
git -c user.name='cyber' -c user.email='cyber@build' am $DIR/02notls.patch
echo "patch-revision: `git rev-parse --short=8 HEAD`" >> $GOPATH/bin/VERSION

git -c user.name='cyber' -c user.email='cyber@build' am $DIR/03getblockbynumber.patch
echo "patch-revision: `git rev-parse --short=8 HEAD`" >> $GOPATH/bin/VERSION

echo --- fetch dependencies into vendor/ ---
$GOPATH/bin/glide install
ls -la $GOPATH/bin

echo --- build all tools found in cmd/ to $GOPATH/bin ---
# static compile to work on any Linux
CGO_ENABLED=0 go install . ./cmd/...
ls -la $GOPATH/bin
