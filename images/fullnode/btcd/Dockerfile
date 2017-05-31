FROM golang:1.8
# $ cat /etc/os-release
# PRETTY_NAME="Debian GNU/Linux 8 (jessie)"

#RUN go version && go env GOROOT GOPATH
# GOROOT: /usr/local/go
# GOPATH: /go

COPY 01build.sh .

RUN ./01build.sh
