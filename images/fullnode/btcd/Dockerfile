FROM golang:1.8

RUN go version && go env GOROOT GOPATH

# Defaults:
# GOROOT: /usr/local/go
# GOPATH: /go

RUN go get -u github.com/Masterminds/glide

RUN git clone https://github.com/btcsuite/btcd $GOPATH/src/github.com/btcsuite/btcd
WORKDIR $GOPATH/src/github.com/btcsuite/btcd
# fetch dependencies into vendor/
RUN glide install
# install all tools found in cmd/ to $GOPATH/bin
RUN go install . ./cmd/... && ls -la $GOPATH/bin
