#!/usr/bin/env bash

# fail on error
set -e
# show commands as they are executed 
set -x

echo --- building markets ---
git clone https://github.com/cyberFund/cyber-markets
cd cyber-markets
./gradlew assemble

echo --- cp versioned binary into $HOME/bin/ ---
mkdir $HOME/bin
ls -la 
ls -la build
ls -la build/libs
 
: 'cd crawler/target
# bash way to list files into array
binaries=(xchange-crawler-*.jar)
binary=${binaries[0]}
cp $binary $HOME/bin/xchange-crawler.jar
# extract config file
unzip $binary crawler.properties -d $HOME/bin/
# record version
echo $binary > $HOME/bin/VERSION
ls -la $HOME/bin
'
