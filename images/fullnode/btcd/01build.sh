
echo --- detecting defaults ---
go version && go env GOROOT GOPATH

echo --- get dependency manager ---
go get -u github.com/Masterminds/glide

echo --- clone btcd sources ---
git clone https://github.com/btcsuite/btcd $GOPATH/src/github.com/btcsuite/btcd
cd $GOPATH/src/github.com/btcsuite/btcd

echo --- fetch dependencies into vendor/ ---
glide install

echo --- build all tools found in cmd/ to $GOPATH/bin ---
go install . ./cmd/...
ls -la $GOPATH/bin
