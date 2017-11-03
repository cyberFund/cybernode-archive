`cybernode` components can be run separately with scripts
in `images/` directory, or together as a part of specific
`cybernode` **type**.

Because each component is packed into container, it can
be run on own physical servers, or in the cloud, such as
Google GKE.

Physical server setup is tested with Ubuntu 16.04, which
is configured with `/install/01bootstrap.sh` script with
automatic security updates, Docker and few more goodies.
Docker and updates:

    # automatic security updates
    apt-get -y update
    apt-get -y install unattended-upgrades
    unattended-upgrades -v
    # check /var/log/unattended-upgrades/unattended-upgrades.log

    # Docker
    apt-get -y install docker.io
    # enable command auto-completion for non-login shells
    echo >> .bashrc
    echo "source /usr/share/bash-completion/bash_completion" >> .bashrc

`cybernode` containers are run under `cyber` user. Its
home contains base mount point for container data.
For example, data from `bitcoin-btcd` container should be
mounted at `/home/cyber/cyberdata/bitcoin-btcd`.

    # create `cyber` user
    adduser cyber --disabled-password --gecos ""

[GECOS docs](https://en.wikipedia.org/wiki/Gecos_field) if
you're curious.
