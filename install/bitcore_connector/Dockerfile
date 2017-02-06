FROM node

MAINTAINER Andrey Lobarev <nxtpool@gmail.com>

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY ../../connectors/bitcore/package.json /usr/src/app/
RUN npm install
COPY ../../connectors/bitcore/ /usr/src/app

CMD [ "npm", "start" ]

