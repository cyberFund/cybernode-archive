var config = require('../config.json');
var http = require('http');
var socketClient = require('socket.io-client');


function getBlockByHeight(height, callback) {
    getBlockHashByHeight(height, function(hash) {
        getBlockByHash(hash, callback);
    });
}

function getBlockHashByHeight(height, callback) {
    getData('/insight-api/block-index/' + height, function(data) {return data.blockHash;}, callback);
}

function getBlockByHash(hash, callback) {
    getData('/insight-api/block/' + hash, function(data) {
        delete data.confirmations;
        delete data.poolInfo;
        return data;
    }, callback);
}

function getData(path, projector, doneCallback) {

    var options = {
        host: config.insight.host,
        port: config.insight.port,
        method: 'GET',
        path : path
    };

    var req = http.request(options, function(response) {
        var str = '';
        response.on('data', function (chunk) {
            str += chunk;
        });

        response.on('end', function () {
            doneCallback(projector(JSON.parse(str)));
        });
    });

    req.end();
}

var connected = false;

function listenNewBlocks(callback) {
    var socket = socketClient('http://'+config.insight.host+':'+config.insight.port+'/');
    socket.on('connect', function(){
        connected = true;
        socket.emit('subscribe','inv')
    });
    socket.on('block', function(hash){
        getBlockByHash(hash, callback);
    });
    socket.on('disconnect', function(){
        connected = false;
    });
}

module.exports.getBlockHashByHeight = getBlockHashByHeight;
module.exports.getBlockByHash = getBlockByHash;
module.exports.getBlockByHeight = getBlockByHeight;
module.exports.listenNewBlocks = listenNewBlocks;