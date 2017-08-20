#!/usr/bin/env bash

# fail on error
set -e
# show commands as they are executed 
set -x

NAME=cyber-markets
BRANCH=master

echo --- building markets ---
git clone --branch $BRANCH https://github.com/cyberFund/$NAME
cd $NAME
HASH="$(git rev-parse --short HEAD)"

# cyber-markets right now is two components:
# - connectors
# - stream-api
./gradlew installDist

echo --- cp versioned binaries into $HOME/bin/ ---
mkdir $HOME/bin
ls -laR connectors/build/install
ls -laR stream-api/build/install

# bash way to list files into array
binaries=($NAME-*.jar)
binary=${binaries[0]}
cp $binary $HOME/bin/$NAME.jar
# record version
echo "$binary $HASH" > $HOME/bin/VERSION
ls -la $HOME/bin
