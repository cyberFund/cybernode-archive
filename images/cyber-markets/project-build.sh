#!/usr/bin/env bash

# fail on error
set -e
# show commands as they are executed 
set -x

NAME=cyber-markets
BRANCH=development

echo --- building markets ---
git clone --branch $BRANCH https://github.com/cyberFund/$NAME
cd $NAME
HASH="$(git rev-parse --short HEAD)"

# cyber-markets right now is two components:
# - connectors
# - stream-api
#./gradlew installDist
./gradlew assemble

echo --- cp binaries into $HOME/bin/ ---
mkdir $HOME/bin
ls -laR connectors/build/libs
ls -laR stream-api/build/libs


# this was used to get versioned file name
# of Java package, such as cyber-markets-1.0.jar
#
#function first_file {
#  # return first found file from the mask
#
#  # bash way to list files into array
#  list=($1)
#  first=${list[0]}
#  echo $first
#}
#
#first_file "$NAME-*.jar"
#binary=$(first_file "$NAME-*.jar")


cp connectors/build/libs/connectors.jar $HOME/bin/
cp stream-api/build/libs/stream-api.jar $HOME/bin/

# record version
echo "$binary $HASH" > $HOME/bin/VERSION
ls -la $HOME/bin

