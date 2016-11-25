docker stop bitcoin
docker rm -v bitcoin
docker run -d -p 8333:8333 -p 18333:18333 --name bitcoin -v /data/bitcoin:/data amacneil/bitcoin
#docker attach --sig-proxy=false bitcoin

docker stop postgres
docker rm postgres
docker run --name postgres -e POSTGRES_PASSWORD=gfhjkm -d -p 5432:5432 postgres

docker build -t cf/chain .
docker run -d --network="host" --name chain cf/chain


