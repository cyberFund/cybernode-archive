#!/bin/bash

# fail on error
set -e

BTCDHASH=master

echo --- detecting defaults ---
go version && go env GOROOT GOPATH

echo --- get dependency manager ---
go get -u github.com/Masterminds/glide

echo --- clone btcd sources ---
git clone https://github.com/btcsuite/btcd $GOPATH/src/github.com/btcsuite/btcd
cd $GOPATH/src/github.com/btcsuite/btcd
git checkout $BTCDHASH

echo --- fetch dependencies into vendor/ ---
glide install
ls -la $GOPATH/bin

echo --- build all tools found in cmd/ to $GOPATH/bin ---
# static compile to work on any Linux
CGO_ENABLED=0 go install . ./cmd/...
# record version
git rev-parse HEAD > $GOPATH/bin/VERSION
ls -la $GOPATH/bin
