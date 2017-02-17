docker stop rethink_projector
docker rm -v rethink_projector
docker run -d --net host --name rethink_projector rethink_projector
docker attach --sig-proxy=false rethink_projector
