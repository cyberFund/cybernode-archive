echo ...Starting k8s cluster...
# [ ] detect if started
# [ ] detect if failed to start
# [ ] start on GKE
#   [ ] choose other options https://kubernetes.io/docs/setup/pick-right-solution/
# [ ] minikube - choose different virt driver on MacOS
minikube start --vm-driver=kvm

echo ...Create ReplicaSet for markets container...
echo "(running cm-app container on cluster)"
kubectl run kubermarkets --image=cybernode/cm-app --port=80

# [ ] kubectl doesn't wait for pod to appear
#     https://github.com/kubernetes/kubernetes/issues/51850
#     (alter default --pod-running-timeout for non-attached run)
#     using custom bash instead
while [[ -z `kubectl get pods -o name` ]]; do
  echo Waiting for ReplicaSet to appear and start Pod
  sleep 0.7
done

POD_NAME=$(kubectl get pods -o go-template --template '{{range .items}}{{.metadata.name}}{{"\n"}}{{end}}')
echo Now open http://127.0.0.1:8001/api/v1/proxy/namespaces/default/pods/$POD_NAME/
kubectl proxy

# for cleanup
#minikube delete

