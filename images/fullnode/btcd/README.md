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

Run container and store blockchain in `$HOME/cyberdata`
directory:

    docker run -d -v "$HOME"/cyberdata:/cyberdata fullnode-btcd

### fullnode-btcd image layout

* `/cyberapp/btcd`     - main process
* `/cyberapp/VERSION`  - revision in btcd repository
* `/cyberdata`         - volume with blockchain data

[glide.lock]: https://github.com/btcsuite/btcd/blob/master/glide.lock
[glide.yaml]: https://github.com/btcsuite/btcd/blob/master/glide.yaml
