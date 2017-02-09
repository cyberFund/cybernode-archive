cd ../
cd chain_mock
docker rmi -f chain_mock
docker build -t chain_mock .
docker stop chain_mock
docker rm -v chain_mock
docker run -d --net host -p 8092:8092 --name chain_mock chain_mock
cd ../

cd connectors/bitcore
docker rmi -f bitcore_connector
docker build -t bitcore_connector .
docker stop bitcore_connector
docker rm -v bitcore_connector
docker run -d --net host --name bitcore_connector bitcore_connector
cd ../../

cd projectors/rethink
docker rmi -f rethink_projector
docker build -t rethink_projector .
docker stop rethink_projector
docker rm -v rethink_projector
docker run -d --net host --name rethink_projector rethink_projector
cd ../../

cd state_monitor
docker rmi -f state_monitor
docker build -t state_monitor .
docker stop state_monitor
docker rm -v state_monitor
docker run -d --net host -p 3000:3000 --name state_monitor state_monitor
cd ../
