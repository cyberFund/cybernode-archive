#!/usr/bin/env bash

# Install minikube, kvm driver and kubectl globally to /usr/local/bin

echo [x] Install kubectl, minikube and kvm driver to /usr/local/bin

curl -Lo minikube https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64 && chmod +x minikube && sudo mv minikube /usr/local/bin/

curl -LO https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl && chmod +x kubectl && sudo mv kubectl /usr/local/bin/

# detect OS
. /etc/os-release

if [ $ID = "ubuntu" ]; then
    BIN=docker-machine-driver-kvm
    echo ..installing $BIN for $NAME
    sudo apt -y install libvirt-bin qemu-kvm
    curl -Lo $BIN https://github.com/dhiltgen/docker-machine-kvm/releases/download/v0.10.0/docker-machine-driver-kvm-ubuntu16.04 && chmod +x $BIN && sudo mv $BIN /usr/local/bin/
    echo ..adding $USER to group 'libvirt'
    sudo usermod -a -G libvirt $(whoami)
fi

