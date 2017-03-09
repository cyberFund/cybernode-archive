docker stop glances
docker rm -v glances
sudo docker run -d --restart="always" -p 127.0.0.1:61208:61208 -p 127.0.0.1:61209:61209 -e GLANCES_OPT="-w" -v /var/run/docker.sock:/var/run/docker.sock:ro --name glances --pid host docker.io/nicolargo/glances