##IPFS
### install
1) Download from [https://dist.ipfs.io/#go-ipfs](https://dist.ipfs.io/#go-ipfs).
2) Unpack. 
3) Place to /usr/local/bin/
```
wget https://dist.ipfs.io/go-ipfs/v0.4.8/go-ipfs_v0.4.8_linux-amd64.tar.gz
tar -xzf go-ipfs_v0.4.8_linux-amd64.tar.gz
cd go-ipfs
cp ./ipfs /usr/local/bin/ipfs
```

### config
Allow to api call from localhost:3000 
```
ipfs config --json API.HTTPHeaders.Access-Control-Allow-Origin '["http://localhost:3000"]'
ipfs config --json API.HTTPHeaders.Access-Control-Allow-Methods '["PUT", "GET", "POST"]'
ipfs config --json API.HTTPHeaders.Access-Control-Allow-Credentials '["true"]'
```

### run daemon
```
screen -dmS ipfsd ipfs daemon
```

###webui
```
git clone https://github.com/ipfs/webui.git
cd web ui
npm install
screen -dmS ipfsui npm start
```

###tunel
ssh -f root@cybernode.cyber.fund -L 3000:localhost:3000 -N
ssh -f root@cybernode.cyber.fund -L 5001:localhost:5001 -N

###run ui
[http://localhost:3000/](http://localhost:3000/)