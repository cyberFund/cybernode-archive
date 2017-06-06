*Sources for building `cybernode` images.*

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

