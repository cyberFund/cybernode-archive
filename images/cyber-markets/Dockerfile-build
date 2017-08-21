FROM openjdk:8

# build time commands
COPY project-build.sh .
RUN ./project-build.sh

VOLUME /build

# run time commands
CMD cp --verbose $HOME/bin/* /build
