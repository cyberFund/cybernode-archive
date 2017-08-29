#!/bin/bash

# fail on error
set -e
# set exit code to non-0 if any piped command returns error
set -o pipefail
# show commands as they are executed 
set -x


IMAGE=cm-app

# absolute path to script's directory
DIR=$(dirname $(readlink -f "$0"))

echo [build] ---- building $IMAGE image ----

mkdir -p $DIR/markets-app/_build
cd $DIR/markets-app/_build

# cloning sources
CLONE=yes
if [ -d cyber-markets ] ; then
  echo "old checkout detected"
  read -p "remove (y/n)?" choice
  case "$choice" in 
    y|Y ) rm -rf cyber-markets;;
    n|N ) CLONE=no;;
    * ) exit 1;;
  esac
fi

if [ "$CLONE" == "yes" ]; then
  git clone https://github.com/cyberFund/cyber-markets
fi
git -C cyber-markets rev-parse --short=8 HEAD > VERSION

# extracting static webapp pages
cp cyber-markets/stream-api/src/main/resources/*.html .
echo "<div><a href='trades-test.html'>Trades</a></div>" > index.html
echo "<div><a href='orders-test.html'>Orders</a></div>" >> index.html

#docker build --no-cache -t ${IMAGE}-build -f Dockerfile-build . | tee buildimage.log
#docker run --rm -v "$PWD"/bin:/build "${IMAGE}-build" | tee buildimage-run.log
#ls -la "$PWD"/bin
#echo ... creating $IMAGE

# creating versioned image
VERSION=`cat $PWD/VERSION`

docker build --no-cache -t cybernode/$IMAGE --label version="$VERSION" .. | tee buildfinal.log
cd -

