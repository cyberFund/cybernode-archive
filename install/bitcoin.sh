docker stop bitcoin
docker rm -v bitcoin
docker run -d -p 8333:8333 -p 18333:18333 --name bitcoin -v /data/bitcoin:/data amacneil/bitcoin

docker attach --sig-proxy=false bitcoin
