echo ... starting RethinkDB ...
docker run -d -u $(id -u cyber) --name rethink -p 127.0.0.1:8080:8080 -p 127.0.0.1:28015:28015 -v /home/cyber/cyberdata/rethink:/data rethinkdb:2.3
#    -d                      - run as daemon 
#    -u $(id -u cyber)       - run container under user `cyber`, param needs uid
#    --name btcd             - convenient name to manage container
#    -p localhost:8080:8080  - host:container - expose port 8080 as 127.0.0.1:8080 on host
#                              8080 - webui, 28015 - client connections
#    -v .a.:.b.              - mount .a. in host as .b. in container

echo ... starting crawler ...

IMAGE=xchange-crawler
NAME=crawler
docker inspect $IMAGE | grep "LABEL version"
docker run -d -u $(id -u cyber) --name $NAME $IMAGE
