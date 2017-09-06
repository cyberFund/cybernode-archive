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
  echo Running script '$*'
  bash "$*"
fi

# cleanup
#minikube delete

