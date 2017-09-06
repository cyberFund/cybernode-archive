#!/usr/bin/env bash

echo ...Starting k8s cluster...
# [ ] detect if started
# [ ] detect if failed to start
# [ ] start on GKE
#   [ ] choose other options https://kubernetes.io/docs/setup/pick-right-solution/
# [ ] minikube - choose different virt driver on MacOS
minikube start --vm-driver=kvm

# ability to run as ./script.sh markets.sh
if [ $# -ne 0 ]; then
  if [ ${1: -3} == ".sh" ]; then
    echo Running bash script "$*"
    bash "$*"
  else
    if [ ${1: -5} == ".yaml" ]; then
      echo Creating resources from kubectl config "$*"
      kubectl create -f $*
    fi
  fi
fi

# cleanup
#minikube delete

