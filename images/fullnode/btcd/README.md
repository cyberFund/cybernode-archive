`btcd` is written in Go, but uses https://glide.sh/ to pin
dependencies. See [glide.lock] and [glide.yaml] for config.

To keep image size minimal, we may supply two Dockerfile's.
First is to build binary - fetch sources and dependencies.
Second is to run that binary. Target system must match,

Current build command:

    docker build -t fullnode-btcd . | tee build.log

`tee` command duplicates output stream to file.

[glide.lock]: https://github.com/btcsuite/btcd/blob/master/glide.lock
[glide.yaml]: https://github.com/btcsuite/btcd/blob/master/glide.yaml
