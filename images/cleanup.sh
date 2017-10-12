#!/bin/bash

echo "...remove dangling images (unused <none>:<none>)"
docker rmi $(docker images --quiet --filter "dangling=true")

echo "...remove exited containers"
docker rm $(docker ps -q -f status=exited)

echo "...unused volumes"
VOLUMES=$(docker volume ls -q -f dangling=true)
#docker volume inspect -f "{{.Mountpoint}}" $VOLUMES
docker volume rm $VOLUMES

