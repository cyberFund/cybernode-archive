docker rmi -f rethink_projector
docker build -t rethink_projector ./rethink_projector
docker stop rethink_projector
docker rm -v rethink_projector
docker run -d --name rethink_projector rethink_projector
docker attach --sig-proxy=false rethink_projector
