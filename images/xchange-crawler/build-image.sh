IMAGE=xchange-crawler
docker build -t ${IMAGE}-build -f Dockerfile-build . | tee build.log
docker run --rm -v "$PWD"/bin:/build "${IMAGE}-build" | tee build-run.log
