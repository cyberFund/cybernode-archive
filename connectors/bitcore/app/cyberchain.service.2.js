var async = require('async');

var config = require('../config.json');
var constants = require('./constants');

var rpc = require('json-rpc2');


///// 
//
// cli wallet
//

var wallet = rpc.Client.$create(config.wallet.port, config.wallet.host);

function transfer(nickname, callback) {
    isWalletLocked(function(state) {
       if (state) {
           unlockWallet(function() {
               transferInternal(nickname, callback);
           });
       } else {
           transferInternal(nickname, callback);
       }
    });
}

function create(nickname, callback) {
    isWalletLocked(function(state) {
        if (state) {
            unlockWallet(function() {
                createInternal(nickname, callback);
            });
        } else {
            createInternal(nickname, callback);
        }
    });
}

function isWalletLocked(callback) {
    console.info('is_locked');
    wallet.call('is_locked', [], function (err, result) {
        if (err) {
            console.error(err);
        }
        callback(result);
    });
}

function unlockWallet(callback) {
    console.info('unlock');
    wallet.call('unlock', [config.wallet.password], function (err, result) {
        if (err) {
            console.error(err);
        }
        callback();
    });
}

function transferInternal(nickname, callback) {
    console.info('transfer_to_vesting');
    wallet.call('transfer_to_vesting', [config.cyberchain.nickname, nickname, "1.000 GOLOS", true], function (err, result) {
        if (err) {
            console.error(err);
        }
        callback(err);
    });
}

function createInternal(nickname, callback) {
    console.info('create_account');
    wallet.call('create_account', [config.cyberchain.nickname, nickname, '', true], function (err, result) {
        if (err) {
            console.error(err);
        }
        callback(err);
    });
}


//
//
/////


module.exports.create = create;
module.exports.transfer = transfer;
