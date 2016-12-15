docker stop rethink
docker rm -v rethink
docker run -d -p 8080:8080 --name rethink -v "/data/rethinkdb:/data" rethinkdb
docker attach --sig-proxy=false rethink


