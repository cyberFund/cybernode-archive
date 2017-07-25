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

./gradlew assemble

echo --- cp versioned binary into $HOME/bin/ ---
mkdir $HOME/bin
cd build/libs
ls -la
 
# bash way to list files into array
binaries=($NAME-*.jar)
binary=${binaries[0]}
cp $binary $HOME/bin/$NAME.jar
# record version
echo "$binary $HASH" > $HOME/bin/VERSION
ls -la $HOME/bin
