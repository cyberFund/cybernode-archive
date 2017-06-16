FROM scratch

ADD bin/* /cyberapp/
VOLUME /cyberdata

# https://github.com/btcsuite/btcd/blob/master/docs/default_ports.md
EXPOSE 8333

CMD ["/cyberapp/btcd", "--datadir=/cyberdata"]
