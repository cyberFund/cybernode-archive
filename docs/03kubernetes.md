While it is possible to run every **cybernode component**
separately, this is only useful for **blockchain nodes**.
Most other components heavily rely on each other. To manage
components and their connections as a whole, we use
[Kubernetes](https://kubernetes.io/).

### Installing `minikube` and `kubectl`

[minikube](https://github.com/kubernetes/minikube)
is a self-contained Kubernetes cluster that runs
independently on your own machine. You can then **depoy**
cybernode components there by **adding** them **as resources**.

[kubectl](https://kubernetes.io/docs/reference/kubectl/overview/)
is a tool for interacting with Kubernetes clusters.

Both tools can be installed with `cybernode` script:

    ./install/02kubernetes.sh
