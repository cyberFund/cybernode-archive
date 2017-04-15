const config = require('./../config.json');
var rpc = require('json-rpc2');

const BTC = 'btc';
var client = rpc.Client.$create(config.ipfsStore.port, config.ipfsStore.host);

function insertBlock(block, callback) {
    client.call('insertBlock', [block, BTC], callback);
}

function insertTx(tx, callback) {
    client.call('insertTx', [tx, BTC], callback);
}

function getBlockByHash(hash, callback) {
    client.call('getBlockByHash', [hash, BTC], callback);
}

function getTxByTxid(txid, callback) {
    client.call('getTxByTxid', [txid, BTC], callback);
}

function getHeight(callback) {
    client.call('getHeight', [BTC], callback);
}

module.exports.insertBlock = insertBlock;
module.exports.insertTx = insertTx;
module.exports.getBlockByHash = getBlockByHash;
module.exports.getTxByTxid = getTxByTxid;
module.exports.getHeight = getHeight;
