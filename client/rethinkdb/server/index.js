var config = require('../config');
var express = require('express');
var http = require('http');
var server = require('rethinkdb-websocket-server');

var app = express();
app.use('/', express.static('public'));
var httpServer = http.createServer(app);

var options = config.rethink;
options.httpServer = httpServer;
options.httpPath = config.websockets.path;

server.listen(options);

httpServer.listen(config.websockets.port);
console.log(`Server started on port ${config.websockets.port}`);
