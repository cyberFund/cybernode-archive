# cd ~/dkr && docker build -t golos .
# docker run -p 0.0.0.0:2001:2001 -v $PWD/data:/golos -d -t golos

FROM ubuntu:xenial
RUN apt-get update && \
	apt-get install -y gcc-4.9 g++-4.9 cmake make libbz2-dev libdb++-dev libdb-dev && \
	apt-get install -y libssl-dev openssl libreadline-dev autoconf libtool git && \
	apt-get install -y autotools-dev build-essential g++ libbz2-dev libicu-dev python-dev wget doxygen python3 python3-dev libboost-all-dev curl screen && \
        apt-get clean -qy
# P2P (seed) port
EXPOSE 2229
# RPC ports
EXPOSE 5000
EXPOSE 8090
EXPOSE 8091

RUN cd ~ && \
	git clone https://github.com/nxtpool/golos.git && \
	cd golos && \
	git checkout mytestnet && \
	git submodule update --init --recursive \
        && cd ~/golos && \
	cmake -DCMAKE_BUILD_TYPE=Release . && \
	make -j4 && make install
RUN cp ~/golos/programs/golosd/snapshot5392323.json /usr/local/bin/snapshot5392323.json
#RUN rf -rf ~/golos

VOLUME /golos
WORKDIR /golos

ADD ./start.sh /start.sh

RUN echo "Please configure me! You need to mount a data directory onto /golos of this container to it to function correctly. (if you're using golos-in-a-box most of this is handled automatically)"
CMD ["/bin/bash", "/start.sh"]