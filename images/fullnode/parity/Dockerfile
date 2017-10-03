FROM scratch

ADD _build/bin/parity /cyberapp/
VOLUME /cyberdata


# https://ethereum.stackexchange.com/questions/809/which-tcp-and-udp-ports-are-required-to-run-an-ethereum-client
# Ethereum P2P
EXPOSE 30303
# JSON-RPC
EXPOSE 8545
# Parity UI
EXPOSE 8180

ENTRYPOINT ["/cyberapp/parity", "--base-path=/cyberdata"]
# [ ] check that disk logging is disabled by default
CMD []
