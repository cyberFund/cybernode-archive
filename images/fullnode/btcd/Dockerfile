FROM scratch

ADD bin/* /cyberapp/
VOLUME /cyberdata

# `btcd` still tries to create its .btcd
# https://github.com/btcsuite/btcd/issues/995
ENV HOME /cyberdata

# https://github.com/btcsuite/btcd/blob/master/docs/default_ports.md
# Bitcoin P2P
EXPOSE 8333
# JSON-RPC + WebSockets
EXPOSE 8334

ENTRYPOINT ["/cyberapp/btcd", "--datadir=/cyberdata", "--logdir=/cyberdata"]
# [ ] disable logging to disk by default
#     and access with `docker logs` instead
#     https://github.com/btcsuite/btcd/issues/994
CMD []
