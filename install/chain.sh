cd ./chain
docker stop chain
docker rm -v chain
docker rmi -f chain
docker build -t chain .
docker run -dt --net host -v ~/golosnode:/golos --name chain chain
docker attach --sig-proxy=false chain
