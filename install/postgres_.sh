docker stop postgres
docker rm postgres
docker run --name postgres -e POSTGRES_PASSWORD=gfhjkm -e POSTGRES_DB=chain -d -p 5432:5432 postgres

