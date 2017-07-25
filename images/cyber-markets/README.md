### cyber-markets image layout

* `/cyberapp/cyber-markets.jar`  - markets binary
* `/cyberapp/VERSION`            - version of markets binary

Project site: https://github.com/cyberFund/cyber-markets

### Building image

To keep image size minimal, we supply `Dockerfile-build`
to build binary and `Dockerfile` to run it. See
[../README.md] for details. To build the image, run:

    ./build-image.sh

This will create two images - `cyber-markets-build` with
tools and build artifacts and `cyber-markets` with
actual binaries, which are also extracted to `bin/`
directory in current dir for inspection.

### Running image

Running `markets` with default settings:

    docker run -d -u $(id -u cyber) --name markets cyber-markets

Overriding configuration is done through environment
variables. For example, `DEEPSTREAMURL` with default value
`localhost:6020`.

    docker run -d -u $(id -u cyber) --name markets cyber-markets

See [../run-cyber-markets.sh] for explanation of Docker
parameters.
