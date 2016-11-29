docker stop bitcore
docker rm -v bitcore
docker run -d -p 8333:8333 -p 3001:3001 --name bitcore -v /data/bitcoin:/root/.bitcore sena/bitcore
docker attach --sig-proxy=false bitcore
