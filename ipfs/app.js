var config = require('./config.json');
var orbit = require('./app/orbit.service');
var rpc = require('json-rpc2');

var height;

orbit.prepareDatabase(function() {
    var server = rpc.Server.$create({
        'websocket': true, // is true by default
        'headers': { // allow custom headers is empty by default
            'Access-Control-Allow-Origin': '*'
        }
    });

    function insertBlock(args, opt, callback) {
        orbit.insertBlock(args[0], args[1], callback);
    }

    function insertTx(args, opt, callback) {
        orbit.insertBlock(args[0], args[1], callback);
    }

    function getHeight(args, opt, callback) {
        orbit.getHeight(args[0], callback);
    }

    function getBlockByHeight(args, opt, callback) {
        orbit.getBlockByHeight(args[0], args[1], callback);
    }

    function getBlockByHash(args, opt, callback) {
        orbit.getBlockByHash(args[0], args[1], callback);
    }

    function getTxByTxid(args, opt, callback) {
        orbit.getTxByTxid(args[0], args[1], callback);
    }

    server.expose('insertBlock', insertBlock);
    server.expose('insertTx', insertTx);
    server.expose('getHeight', getHeight);
    server.expose('getBlockByHeight', getBlockByHeight);
    server.expose('getBlockByHash', getBlockByHash);
    server.expose('getTxByTxid', getTxByTxid);

    server.listen(config.rpc.port, 'localhost');

    console.log("Started at: " + config.rpc.port);
});




