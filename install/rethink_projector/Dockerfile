FROM node

MAINTAINER Andrey Lobarev <nxtpool@gmail.com>

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY ../../projectors/rethink/package.json /usr/src/app/
RUN npm install
COPY ../../projectors/rethink/ /usr/src/app

CMD [ "npm", "start" ]

