docker rmi -f cf/postgres
docker build -t cf/postgres ./postgres

docker stop postgres
docker rm postgres
docker run --name postgres -d -p 5432:5432 cf/postgres

