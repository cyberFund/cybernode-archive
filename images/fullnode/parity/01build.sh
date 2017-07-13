#!/bin/bash

echo --- checking build tools ---
rustc --version
cargo --version
echo $PATH

echo --- clone Parity sources ---
git clone https://github.com/paritytech/parity
cd parity

echo --- build in release mode to ./target/release ---
cargo build --release

# [ ] static compile for scratch image
# record version
git rev-parse HEAD > ./target/release/VERSION
ls -la ./target/release
