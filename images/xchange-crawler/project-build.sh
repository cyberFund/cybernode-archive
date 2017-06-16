#!/usr/bin/env bash

echo --- clone latest crawler version ---
git clone https://github.com/cyberFund/xchange-crawler
cd xchange-crawler

echo --- fetch dependencies ---
git submodule init
git submodule update

echo --- build crawler .jar in crawler/target ---
mvn install
ls -la crawler/target

echo --- cp versioned binary into $HOME/bin/xchange-crawler.jar ---
mkdir $HOME/bin
cd crawler/target
# bash way to list files into array
binaries=(xchange-crawler-*.jar)
binary=${binaries[0]}
cp $binary $HOME/bin/xchange-crawler.jar
# extract config file
unzip $binary crawler.properties -d $HOME/bin/
# record version
echo $binary > $HOME/bin/VERSION
ls -la $HOME/bin
