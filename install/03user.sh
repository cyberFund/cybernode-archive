#!/bin/bash

echo [x] Adding $USER to 'docker' group
sudo adduser $USER docker
echo Now you can command docker daemon.

echo [x] Adding $USER to 'cyber' group
sudo adduser $USER cyber
echo Now you can run cybernode components.

