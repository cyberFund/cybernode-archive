var express = require('express');
var router = express.Router();
var async = require('async');

var bitcore = require('../app/bitcore.service');
var chain = require('../app/cyberchain.service');
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
            bitcore.getSync(function (err, data) {
                processHttpResponse(err, data, function(data) {
                    model.bitcore = data;
                });
                next();
            });
        },
        function (next) {
            chain.getLastApprovedBlock(function (err, block) {
                processRpcResponse(err, block, function (height) {
                    model.chain.height = height;
                }, function (data) {
                    if (!data) {
                        return 0;
                    }
                    return JSON.parse(data.json_metadata).height;
                });
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
            rethink.getLastBlockNumber(function(err, height){
                model.connector.height = err ? 'Connection error' : height + ' blocks';
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
