FROM node

MAINTAINER Andrey Lobarev <nxtpool@gmail.com>

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY ../../chain_mock/package.json /usr/src/app/
RUN npm install
COPY ../../chain_mock/ /usr/src/app

CMD [ "npm", "start" ]

EXPOSE 8092
