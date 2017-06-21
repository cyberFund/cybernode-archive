*Automation for `cybernode` images*

### TL;DR

Run `./build-*.sh` scripts. **Build images** are not
pushed. Binaries are extracted to `bin/` subdir of image
sources.

### Common operations

Check version of dockerized software (`btcd` for example):

    docker inspect --format='{{.Config.Labels.version}}' btcd

### Intro

`cybernode` is a cluster of services. Every service runs in
container to simplify administration, deployment and
automatic management (also known as `orchestration`).

Docker is the current choice for running production
containers. On the other hand image build scripts are kept
decoupled from Docker for easy migration to alternative
container providers.

Independent build scripts are named as `01build.sh`, Docker
specific files are `Dockerfile` and `Dockerfile-build`.
Main `build.sh` file currently contains docker commands.


### Building images with Docker

Build process - dependendency download, compilation and
packaging leaves a lot of garbage on the build system.
Build tools, sources and dependencies are usually not
needed for operations. That's why we use separate
**build images** to build and package things and separate
**run images** to deploy software. **run images** are what
is uploaded to public repositories.

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

1. Study some existing image (`fullnode-btcd` is okay)
2. Create the same, test that it works
3. Add build script to `.travis.yml`
4. Document the component in `install/` docs
5. Write `run-*.sh` script

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
