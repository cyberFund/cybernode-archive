echo ...Starting k8s cluster...
# [ ] detect if started
# [ ] detect if failed to start
# [ ] start on GKE
#   [ ] choose other options https://kubernetes.io/docs/setup/pick-right-solution/
# [ ] minikube - choose different virt driver on MacOS
minikube start --vm-driver=kvm

echo ...Creating deployment for markets...
echo "(running cm-app container on cluster)"
# [ ] kubectl doesn't wait for pod to appear, so getting pods fails
#     https://github.com/kubernetes/kubernetes/issues/51850
kubectl run kubermarkets --image=cybernode/cm-app --port=80 --pod-running-timeout=1m0s
kubectl get pods

echo Sleeping for 5 seconds to wait for pod to appear..
sleep 5
POD_NAME=$(kubectl get pods -o go-template --template '{{range .items}}{{.metadata.name}}{{"\n"}}{{end}}')
echo Now open http://127.0.0.1:8001/api/v1/proxy/namespaces/default/pods/$POD_NAME/
kubectl proxy

# for cleanup
#minikube delete

