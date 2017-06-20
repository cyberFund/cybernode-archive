IMAGE=fullnode-btcd
NAME=btcd
docker run -d -u $(id -u cyber) -p 8333:8333 --name $NAME -v /home/cyber/cyberdata/$NAME:/cyberdata $IMAGE
#    -d                  - run as daemon 
#    -u $(id -u cyber)   - run container under user `cyber`, param needs uid
#    -p 8333:8333        - host:container - expose port 8333 as 8333 from host
#    --name btcd         - just convenient name to find running container
#    -v .a.:.b.          - mount .a. in host as .b. in container
