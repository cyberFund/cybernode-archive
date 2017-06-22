echo ... starting RethinkDB ...
docker run -d -u $(id -u cyber) --name rethink -p 127.0.0.1:8080:8080 -v /home/cyber/cyberdata/rethink:/data rethinkdb:2.3
#    -d                      - run as daemon 
#    -u $(id -u cyber)       - run container under user `cyber`, param needs uid
#    --name btcd             - convenient name to manage container
#    -p localhost:8080:8080  - host:container - expose port 8080 as 127.0.0.1:8080 on host
#                              8080 - webui, 28015 - client connections
#    -v .a.:.b.              - mount .a. in host as .b. in container

RETHINK_IP=`docker inspect --format='{{.NetworkSettings.IPAddress}}' rethink`
while ! nc -z $RETHINK_IP 28015; do
  echo waiting for RethinkDB to start on $RETHINK_IP:28015
  sleep 0.7
done


echo ... starting crawler ...
IMAGE=xchange-crawler
NAME=crawler
VERSION=`docker inspect --format='{{.Config.Labels.version}}' $IMAGE`
echo Running $NAME $VERSION from $IMAGE

# ---hack until https://github.com/cyberFund/xchange-crawler/issues/45 is resolved
IMAGES=$(dirname $(readlink -f "$0"))
cp $IMAGES/xchange-crawler/crawler.properties .
sed --in-place "s/rethink.host=localhost/rethink.host=$RETHINK_IP/" $PWD/crawler.properties
HACK="-v $PWD/crawler.properties:/cyberapp/crawler.properties"

docker run -d -u $(id -u cyber) $HACK --name $NAME $IMAGE
