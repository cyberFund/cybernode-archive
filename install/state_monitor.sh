cd ../state_monitor
docker rmi -f state_monitor
docker build -t state_monitor .
docker stop state_monitor
docker rm -v state_monitor
docker run -d --net host -p 3000:3000 --name state_monitor state_monitor
docker attach --sig-proxy=false state_monitor
