
Every VM or physical server of Cybernode is based on
Ubuntu 16.04 for simplicity. It needs automatic security
updates and Docker.

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

