### Bootstrapping `cybernode`

Before you can run `cybernode` components, you need to
prepare your system.

`cybernode` components are built and started with scripts
in `images/` directory. Muliple components can be bundled
together as a part of specific `cybernode` **type**.

Component are packed into containers. This way they can be
run on own physical servers, or in the cloud, such as
Google GKE or OpenShift. We use Kubernetes to manage
containers for cloud installations.

#### Bootstrapping your own physical server

Server setup is tested with Ubuntu 16.04. To install
necessary prerequisites on Ubuntu, run:

    ./install/01bootstrap.sh

It sets up automatic security updates, Docker and few more
goodies, like shell completion and version control for
`/etc` contents.

`cybernode` containers are run under `cyber` user, and
`/home/cyber/cyberdata`  dir provides root location for
component data. For example, data from `bitcoin-btcd`
container will be mounted at `/home/cyber/cyberdata/bitcoin-btcd`.

`01bootstrap.sh` script creates `cyber` user for running
containers and `cyberbuilder` user for building images and
pushing them to DockerHub.

See the full scripts here:
https://github.com/cyberFund/cybernode/blob/master/install/01bootstrap.sh
