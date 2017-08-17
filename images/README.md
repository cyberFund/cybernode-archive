*Build scripts for `cybernode` images*

### TL;DR

Use `./build-*.sh` scripts to build and pack binaries
into Docker images. Use `./run-*.sh` scripts to execute
images.

For debugging, binaries are extracted to `bin/` subdir of
image sources. Images that end in `-build` are used for
building process itself and can be removed.

### Common operations

Check version of image software (`btcd` for example):

    docker inspect --format='{{.Config.Labels.version}}' btcd

Check image command and parameters:

    docker inspect --format='{{.Config.Entrypoint}}' btcd


### Intro

`cybernode` is a cluster of services. Every service runs in
own container to simplify "administration, deployment and
automatic management" (also known as `orchestration`).

Docker is the current choice for running production
containers. But scripts for building software are kept
decoupled from Docker for easy migration to alternative
container providers.

Software build scripts are named as `01build.sh`, Docker
specific files are `Dockerfile` and `Dockerfile-build`.
Main `build.sh` file currently contains docker commands.


### Building images with Docker

Software for image is built from sources. Build process
takes place inside special **build image** to isolate
build artifacts, tools, sources and dependencies from host
system and target image. The target image with compiled
binary is called **run image** and it is the one uploaded
to container repositories.

##### Build images

By containing compilers, sources and development tools,
**build image** keeps host system clean. Instructions for
build images are provided in `Dockerfile-build` files.

A quick review about Docker images. Docker flow includes
two steps - *build* and *run*. During build step Docker
copies files from host to container, sets environment
variables, etc. This prepares image to be run right away
without any more additional initialization steps.
Everything that happens in `Dockerfile` is about build step
and only few commands, such as CMD, ENTRYPOINT and VOLUME
are executed during run step.

During build step Docker is only able to COPY files from
host to container, but there is no standard way to get
these files back - `docker build` command doesn't support
mounting volumes (see
[this issue](https://github.com/moby/moby/issues/17745) for
details). To get built binaries, a **build image** should
be executed with `docker run`. CMD entry in
`Dockerfile-build` then copies produced binaries to
`/build` VOLUME, which can be mounted from host to retrieve
binaries.

A typical command run by `build image`:

    CMD cp --verbose $GOPATH/bin/* /build


##### Run images

**run image** is built from `Dockerfile`. It contains
application binaries in `/cyberapp` and stores data
`/cyberdata` dir. Thanks to this convention, `/cyberdata`
can be mounted as external storage in uniform way accross
containers.

To run docker images on cybernode, make sure you're present
in `docker` group (add yourself with `adduser $USER docker`
command). Then run (example for btcd fullnode):

    $ docker run -d -u $(id -u cyber) -p 8333:8333 --name btcd -v /home/cyber/cyberdata:/cyberdata fullnode-btcd

You should see the image in `docker ps` output. You can also
check it with `docker logs btcd`. Parameters explained:

    -d                  - run as daemon 
    -u $(id -u cyber)   - run container under user `cyber`, param needs uid
    -p 8333:8333        - host:container - expose port 8333 as 8333 from host
    --name btcd         - just convenient name to find running container
    -v /aaa:/bbb        - mount /aaa in host as /bbb in container

To get more info about `-u $(id -u cyber)` read Docker
security chapter below.

### Adding new image

It helps to study existing image such as `fullnode/btcd` to
get into the process faster.

1. Create directory for the image, blockchain nodes go into
   `fullnode/` dir, images are prefixed with `fullnode`,
   runtime containers use abbreviated names such as `btcd`.
2. Create `Dockerfile-build` to get tools, build project
   and extract binaries.
3. Create `Dockerfile` with minimal footprint and default
   settings. Binaries go into `/cyberapp`, data (if any)
   into `/cyberdata` VOLUME.
4. Write `build-*.sh` script for images (adapt existing),
   add it to `.travis.yml` for continuous testing
5. Write `run-*.sh` script, specify if it should be
   restarted automatically, `fullnode` chains should.

### Docker security

By default, Docker containers are run as `root`:

    $ ps faux
    root ...  \_ containerd -l unix:///var/run/docker/libco
    root ...     \_ containerd-shim 3cd12f4b19d2615f06db041
    root ...         \_ /cyberapp/btcd --datadir=/cyberdata 


This doesn't mean container app has access to host system,
but bugs in Docker code or in container isolation on kernel
level may make it possible. As a workaround, it is possible
to specify user for containers process in `Dockerfile`:

    USER cyber

or run `docker` with explicit user command:

    docker run -d -u $(id -u cyber) <image>

`-u` or `--user` options doesn't accept user name and
requires numeric UID passed as parameter, for details, see
https://github.com/moby/moby/issues/22323#issuecomment-215449380


### Troubleshooting

We pack binaries in read-only docker images. They become
read-only when we run them under user `cyber` who is
different from `root` and don't have write access to
anything except mounted volumes.

For example, if `docker logs` shows:

    loadConfig: Failed to create home directory: mkdir /.btcd: permission denied

That means that `btcd` can not create `/.btcd` because
there is no rights for `cyber` user to create dir in
container root, and all processes run in root dir by
default. Make sure that application writes all its files
in `/cyberdata` volume.

To discover commands that shell scrips run, launch them
with bash `-x`:

    bash -x run-fullnode-btcd.sh

It also helps to run container as root (remove -u UID param
from `docker run`) and explore its filesystem afterwards
with:

    docker export btcd | tar -tv

