var config = require('../config.json');
var rpc = require('json-rpc2');
var store = rpc.Client.$create(config.ipfsStore.port, config.ipfsStore.host);

const BTC = 'btc';

function getHeight(callback) {
    store.call('getHeight', [BTC], function (data) {
        if (data instanceof Error) {
            console.warn('IPFS Get Heigh response:' + data);
            callback(undefined);
            return;
        }
        callback(data);
    });
}

module.exports.getHeight = getHeight;