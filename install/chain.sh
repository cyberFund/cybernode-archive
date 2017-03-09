cd ./chain
docker stop chain
docker rm -v chain
docker rmi -f chain
docker build -t chain .
docker run-d --net host -v ~/golosnode:/golos --name chain
