FROM golang:1.8
# $ cat /etc/os-release
# PRETTY_NAME="Debian GNU/Linux 8 (jessie)"

#RUN go version && go env GOROOT GOPATH
# GOROOT: /usr/local/go
# GOPATH: /go

# build time commands
COPY [0-9]* ./
RUN ./01build.sh

VOLUME /build

# run time commands
CMD rm $GOPATH/bin/glide && cp --verbose $GOPATH/bin/* /build
