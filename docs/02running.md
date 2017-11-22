### Running `cybernode` components

Make sure you've bootstrapped you system as explained
in [01bootstrap.md](docs/01bootstrap.md) so that your
system contains `cyber` user and installed `docker`.

Then change directory to `images/` and use scripts that
start with run. Example running `bitcoin-btcd`:

    cd images && ./run-fullnode-btcd.sh

You need to be in `docker` unix group to be able to
run docker images.
