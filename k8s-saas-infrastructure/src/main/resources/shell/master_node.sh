sudo systemctl enable kubelet
sudo kubeadm config images pull --cri-socket unix:///run/containerd/containerd.sock

kubeadm init --apiserver-advertise-address=INSTANCE_PRIVATE_IP --pod-network-cidr=192.168.0.0/16 --ignore-preflight-errors=all

mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config

kubectl create -f https://raw.githubusercontent.com/projectcalico/calico/v3.25.0/manifests/tigera-operator.yaml
kubectl create -f https://raw.githubusercontent.com/projectcalico/calico/v3.25.0/manifests/custom-resources.yaml
watch kubectl get pods --all-namespaces

kubectl get nodes -o wide
