### xchange-crawler image layout

* `/cyberapp/xchange-crawler.jar`  - crawler binary
* `/cyberapp/crawler.properties`   - config file
* `/cyberapp/VERSION`              - version of crawler binary

Project site: https://github.com/cyberFund/xchange-crawler

### Building image

To keep image size minimal, we supply `Dockerfile-build`
to build binary and `Dockerfile` to run it. See
[../README.md] for details. To build the image, run:

    ./build-image.sh

This will create two images - `xchange-crawler-build` with
tools and build artifacts and `xchange-crawler` with
actual binaries, which are also extracted to `bin/`
directory in current dir for inspection.
