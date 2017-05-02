FROM node:6

MAINTAINER Andrey Lobarev <nxtpool@gmail.com>

#ipfs
RUN  apt-get update \
  && apt-get install -y wget \
  && rm -rf /var/lib/apt/lists/*

RUN wget https://dist.ipfs.io/go-ipfs/v0.4.8/go-ipfs_v0.4.8_linux-amd64.tar.gz

RUN gunzip -c go-ipfs_v0.4.8_linux-amd64.tar.gz | tar xvf -
WORKDIR go-ipfs
RUN cp ipfs /usr/local/bin/ipfs
RUN ipfs init

#node
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY package.json /usr/src/app/
RUN npm install
COPY . /usr/src/app

CMD [ "npm", "start" ]

EXPOSE 8000
