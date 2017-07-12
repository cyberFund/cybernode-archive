### fullnode-btcd image layout

* `/cyberapp/btcd`     - main process
* `/cyberapp/VERSION`  - revision in btcd repository
* `/cyberdata`         - volume with blockchain data

### btcd configuration and docs

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
port 8333 is accessible from outside. To make it accessible
from host machine, publish it:

    docker run -d -v -p 8333:8333 "$HOME"/cyberdata:/cyberdata fullnode-btcd

[glide.lock]: https://github.com/btcsuite/btcd/blob/master/glide.lock
[glide.yaml]: https://github.com/btcsuite/btcd/blob/master/glide.yaml
