var async = require('async');

var config = require('../config.json');
var constants = require('./constants');
var userManager = require('./user.manager');

var rpc = require('json-rpc2');
var chain = rpc.Client.$create(config.cyberchain.port, config.cyberchain.host);

/////
//
// d
//

function getAccounts(accounts, callback) {
    chain.call('get_accounts', [accounts], function (err, result) {
        callback(result);
    });
}

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

function createAccount(nickname, callback) {
    isWalletLocked(function(state) {
        if (state) {
            unlockWallet(function() {
                createAccountInternal(nickname, callback);
            });
        } else {
            createAccountInternal(nickname, callback);
        }
    });
}

function isWalletLocked(callback) {
    wallet.call('is_locked', [], function (err, result) {
        if (err) {
            if (err.message == "Have no response object") {
                isWalletLocked(callback);
                console.error('is_locked: Have no response object');
                return;
            }
            console.error('is_locked:' + err);
        }
        callback(result);
    });
}

function unlockWallet(callback) {
    wallet.call('unlock', [config.wallet.password], function (err, result) {
        if (err) {
            console.error('unlock: ' + err);
        }
        callback();
    });
}

function transferInternal(nickname, callback) {
    wallet.call('transfer_to_vesting', [config.cyberchain.nickname, nickname, "10000.000 GOLOS", true], function (err, result) {
        if (err) {
            console.error('transfer_to_vesting: ' + err);
        }
        callback(err);
    });
}

function createAccountInternal(nickname, callback) {
    wallet.call('create_account', [config.cyberchain.nickname, nickname, '', true], function (err, result) {
        if (err) {
            console.error('create_account: ' + err);
        }
        callback(err);
    });
}


//
//
/////


module.exports.createAccount = createAccount;
module.exports.transfer = transfer;
module.exports.getAccounts = getAccounts;
