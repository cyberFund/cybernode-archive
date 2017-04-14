cd ../ipfs
docker stop ipfs_store
docker rm -v ipfs_store
docker rmi -f ipfs_store
docker build -t ipfs_store .
docker run -d --net host --restart="always" -v /data/ipfs:/data/ipfs --name ipfs_store ipfs_store
docker attach --sig-proxy=false ipfs_store
