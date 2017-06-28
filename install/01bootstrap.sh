#!/usr/bin/env bash

# Init base Ubuntu 16.04 with `cyber` user, Docker and automatic
# security updates. See [/docs/01bootstrap.md] for details.

# This script is not idempotent and may leave some duplicate
# settings if run twice.


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


echo [x] Creating Cyber and CyberBuilder
# cyber runs containers and owns cyberdata
if id "cyber" >/dev/null 2>&1; then
  echo 'cyber' user already exists
else
  # https://en.wikipedia.org/wiki/Gecos_field
  adduser cyber --disabled-password --gecos ""
fi

if id "cyberbuilder" >/dev/null 2>&1; then
# cyberbuilder (re)builds containers and pushes them to DockerHub
  echo 'cyberbuilder' user already exists
else
  adduser cyberbuilder --disabled-password --gecos ""
  adduser cyberbuilder docker
fi


echo [x] Start using swap when memory usage is near 90%
cat /proc/sys/vm/swappiness
echo "vm.swappiness = 10" > /etc/sysctl.d/60-cybernode-swappiness.conf
sysctl -p /etc/sysctl.d/60-cybernode-swappiness.conf
cat /proc/sys/vm/swappiness


# mount separate disk for cyberdata
#blkid
#echo "UUID=f9698264-4655-4998-a045-fb2392f0976c  /home/cyber/cyberdata  ext4  defaults  0  2" > /etc/fstab

