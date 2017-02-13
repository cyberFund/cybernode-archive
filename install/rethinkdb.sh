docker stop rethink
docker rm -v rethink
docker run -d -p 127.0.0.1:8080:8080 -p 127.0.0.1:28015:28015 --name rethink -v "/data/rethinkdb:/data" rethinkdb
docker attach --sig-proxy=false rethink


