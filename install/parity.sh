docker stop parity
docker rm -v parity
sudo docker run -d --net host  --restart="always" --name parity -v /data/parity:/root/.parity ethcore/parity -d /root/.parity --jsonrpc-interface local --jsonrpc-hosts local --dapps-port 9080
docker attach --sig-proxy=false parity
