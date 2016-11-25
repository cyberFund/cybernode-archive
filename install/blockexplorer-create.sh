docker stop chain
docker rm -v chain
docker rmi -f cf/chain
docker build -t cf/chain .
