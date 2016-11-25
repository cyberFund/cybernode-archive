docker stop chain
docker rm -v chain
docker run -d --network="host" --name chain -v /data/bitcoin:/data cf/chain
docker attach --sig-proxy=false chain
