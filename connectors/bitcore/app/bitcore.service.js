var config = require('../config.json');
var async = require('async');
var http = require('http');
var socketClient = require('socket.io-client');


function getBlockByHeight(height, callback) {
    getBlockHashByHeight(height, function(hash) {
        getBlockByHash(hash, callback);
    });
}

function getBlockHashByHeight(height, callback) {
    getDataUntilSuccess('/insight-api/block-index/' + height, function(data) {return data.blockHash;}, callback);
}

function getBlockByHash(hash, callback) {
    getDataUntilSuccess('/insight-api/block/' + hash, function(data) {
        delete data.confirmations;
        delete data.poolInfo;
        return data;
    }, callback);
}

function getDataUntilSuccess(path, projector, doneCallback) {
    var completed = false;

    async.until(
        function() {
            return completed;
        },
        function (next) {
            getData(path, projector, function(err, data) {
                if (!err) {
                    completed = true;
                    doneCallback(data);
                }
                next();
            });
        },
        function() {});
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
            if (str == "Not found") {
                doneCallback("Not found");
                console.warn("Not found. Path: " + path);
                return;
            }
            doneCallback(null, projector(JSON.parse(str)));
        });

    });

    req.on('error', function(err) {
        if (err.message.code == 'ETIMEDOUT') {
            console.warn("timeout");
            getData(path, projector, doneCallback);
        }
        if (err.message.code == "ECONNRESET") {
            console.warn("connection reset");
            getData(path, projector, doneCallback);
        }

    });

    req.end();
}

var connected = false;
/*
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
 module.exports.listenNewBlocks = listenNewBlocks;
*/
module.exports.getBlockHashByHeight = getBlockHashByHeight;
module.exports.getBlockByHash = getBlockByHash;
module.exports.getBlockByHeight = getBlockByHeight;
