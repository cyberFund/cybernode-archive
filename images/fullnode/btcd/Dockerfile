FROM scratch

ADD bin/* /cyberapp/
VOLUME /cyberdata

# https://github.com/btcsuite/btcd/blob/master/docs/default_ports.md
# Bitcoin P2P
EXPOSE 8333
# JSON-RPC + WebSockets
EXPOSE 8334

ENTRYPOINT ["/cyberapp/btcd"]
# disable logging to disk by default
# access with `docker logs` instead
CMD ["--datadir=/cyberdata --logir="]
