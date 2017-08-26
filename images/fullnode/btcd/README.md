### tl;dr

From `images/` subdir in repository run:

    CYBERDATA=/datadir ./run-fullnode-btcd.sh

 * executes `cybernode/bitcoin-btcd`
 * JSON-RPC at http://cyber:cyber@127.0.0.1:8334
 * blockchain data stored in `/datadir`

Building `cybernode/bitcoin-btcd`:

    ./build-fullnode-btcd.sh


### btcd setup

`btcd` lacks single BTCDHOME setting to root its data
under a single directory, such as `/cyberdata`. It uses
$HOME/.btcd location by default and the following options
to set certain paths:

    --configfile=     Path to configuration file ($HOME/.btcd/btcd.conf)
    --datadir=        Directory to store data ($HOME/.btcd/data)
    --logdir=         Directory to log output. ($HOME/.btcd/logs)
    --rpccert=        File containing the certificate file ($HOME/.btcd/rpc.cert)
    --rpckey=         File containing the certificate key ($HOME/.btcd/rpc.key)

Even when these params are explicitly set to be out of
`.btcd` the app still tries to create this directory at
$HOME which is unset for containers, and this attemp makes
container fail. https://github.com/btcsuite/btcd/issues/995

So we explicitly set chain locations:

    $CYBERDATA/<netname>           - blockchain data
    $CYBERDATA/<netname>/btcd.log  - chain logs

But for everything else we set HOME environment variable to
/cyberdata to allow `btcd` create `/cyberdata/.btcd` and be
happy. This set location for the following files:

    $CYBERDATA/.btcd/btcd.conf
    $CYBERDATA/.btcd/rpc.cert
    $CYBERDATA/.btcd/rpc.key


### fullnode-btcd image layout

* `/cyberapp/btcd`     - main process
* `/cyberapp/VERSION`  - revision in btcd repository
* `/cyberdata`         - volume for btcd/blockchain data

### btcd config docs

See `btcd --help` ([online](https://godoc.org/github.com/btcsuite/btcd))
and [sample-btcd.conf](https://github.com/btcsuite/btcd/blob/master/sample-btcd.conf)
for configuration description and the rest of the docs at
https://github.com/btcsuite/btcd/tree/master/docs#table-of-contents

### Building image

`btcd` is written in Go, but uses https://glide.sh/ to pin
dependencies. See [glide.lock] and [glide.yaml] for config.

To keep image size minimal, we supply `Dockerfile-build`
and `Dockerfile`. First is to build `btcd` binary. Second
is to run that binary. See [../../README.md] for details.

Current build command:

    docker build -t fullnode-btcd-build -f Dockerfile-build . | tee build.log
    docker run --rm -v "$PWD"/bin:/build fullnode-btcd-build | tee build-run.log
    docker build -t fullnode-btcd . | tee fullnode-build.log

`tee` command duplicates output stream to a file. Binaries
will be present in `bin/` directory.

### Running image

Run container and store blockchain in `$HOME/cyberdata`
directory:

    docker run -d -v "$HOME"/cyberdata:/cyberdata fullnode-btcd

To make image visible at https://bitnodes.21.co/ make sure
port 8333 is accessible from host machine by publishing it
for all hosts in the same network, and then make the port
available from outside. Publishing:

    docker run -d -v -p 8333:8333 "$HOME"/cyberdata:/cyberdata fullnode-btcd

You can pass all configuration parameters on `docker run`.
For example, checking `btcd` version:

    docker run --rm fullnode-btcd --version

Checking `btcd` version from hash (we record it in label):

    docker inspect -f "{{.Config.Labels.version}}" fullnode-btcd


[glide.lock]: https://github.com/btcsuite/btcd/blob/master/glide.lock
[glide.yaml]: https://github.com/btcsuite/btcd/blob/master/glide.yaml
