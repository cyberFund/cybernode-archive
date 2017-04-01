cd ./chain
docker stop chain
docker rm -v chain
docker rmi -f chain
docker build -t chain .
docker run -dt --restart="always" --net host -v ~/golosnode:/golos --name chain chain
docker attach --sig-proxy=false chain
