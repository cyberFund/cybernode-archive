var express = require('express');
var router = express.Router();
var async = require('async');

var bitcore = require('../app/bitcore.service');
var ipfs = require('../app/ipfs.service');
var rethink = require('../app/rethink.service');
var glances = require('../app/glances.service');


function processRpcResponse(err, data, callback, projector) {
    if (err) {
        callback(err.message == "Have no response object" ? 'Connection error' : 'Unknown error');
    } else {
        if (projector) {
            callback(projector(data));
        } else {
            callback(data);
        }
    }
}

function processHttpResponse(err, data, callback, projector) {
    if (err) {
        callback(err.code == "ECONNREFUSED" ? 'Connection error' : 'Unknown error');
    } else {
        if (projector) {
            callback(projector(data));
        } else {
            callback(data);
        }
    }
}

router.get('/', function (req, res, theNext) {
    var model = {title: 'State monitor', bitcore: {}, chain: {}, connector: {}};
    async.parallel([
        function(next) {
            console.log('bitcore get sync status started');
            bitcore.getSync(function (err, data) {
                processHttpResponse(err, data, function(data) {
                    model.bitcore = data;
                });
                console.log('bitcore get sync status ended');
                next();
            });
        },
        function (next) {
            console.log('chain get height started');
            ipfs.getHeight("btc", function (err, block) {
                processRpcResponse(err, block, function (height) {
                    model.chain.height = height;
                });
                console.log('chain get height ended');
                next();
            });
        },

        /*
        function (next) {
            chain.getLastPostedBlock(function (err, block) {
                processRpcResponse(err, block, function (height) {
                    model.chain.totalHeight = height;
                }, function (data) {
                    return JSON.parse(data.json_metadata).height;
                });
                next();
            });
        },
        */
        function(next) {
            console.log('rethink get status started');
            rethink.getLastBlockNumber(function(err, height){
                model.connector.height = err ? 'Connection error' : height + ' blocks';
                console.log('rethink get status ended');
                next();
            });
        },
        function(next) {
            glances.getStatus(function (err, status) {
                processHttpResponse(err, status, function(data) {
                    model.glances = data;
                });
                next();
            });
        }
    ], function() {
        res.render('index', model);
    });
 });

module.exports = router;
