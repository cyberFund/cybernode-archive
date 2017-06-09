#!/usr/bin/env bash

# Init base Ubuntu 16.04 with `cyber` user, Docker and automatic
# security updates. See [/docs/01bootstrap.md] for details.


echo [x] Enable automatic security updates
apt-get -y update
apt-get -y install unattended-upgrades
unattended-upgrades -v
# check /var/log/unattended-upgrades/unattended-upgrades.log


echo [x] Installing Docker
apt-get -y install docker.io
# enable command auto-completion for non-login shells
echo >> .bashrc
echo "source /usr/share/bash-completion/bash_completion" >> .bashrc


echo [x] Creating Cyber
# https://en.wikipedia.org/wiki/Gecos_field
adduser cyber --disabled-password --gecos ""
