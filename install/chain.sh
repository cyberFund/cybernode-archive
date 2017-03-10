cd ./chain
docker stop chain
docker rm -v chain
docker rmi -f chain
docker build -t chain .
docker run -dt --net host -v ~/golosnode/witness_node_data_dir:/golos --name chain chain
docker attach --sig-proxy=false chain
