#!/bin/bash
export KUBECONFIG=k8s.config
kubectl config set-context --current --namespace=labhunter
. labhunter-fill-configs.sh
kubectl apply -f generated-configs