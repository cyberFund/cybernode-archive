var async = require('async');
var indexer = require('./indexer');
var cyberchainService2 = require('./cyberchain.service.2');
var userManager = require('./user.manager');


var processing = 0;
var next = [];
function nonStop(j) {
    var i = j;
    while (next.length < 450) {
        next.push(i);
        i++;
    }
    while (i < 500000) {
        if (processing > 450) {
            setTimeout(function () { nonStop(i); }, 1000);
            return;
        }
        if (next.length < 450) {
            next.push(i);
            i++;
            console.info("From " + Math.min.apply(Math, next) +" to " + i);
        }
        processing++;
        const n = next[0];
        next.splice(0,1);
        indexer.indexNoCheck(n, function (exist) {
            if (!exist) {
                next.push(n);
            }
            processing--;
        });
    }
}

function prepareAccounts(next) {
    var nicknames = [];
    for (var i = 0; i < 1000; i++) {
        nicknames.push(userManager.getUserByNumber(i));
    }
    async.until(
        function () {
            return nicknames.length == 0;
        },
        function (test) {
            cyberchainService2.getAccounts(nicknames, function (result) {
                if (result) {
                    var exist = result.map(function (acc) {
                        return acc.name
                    });
                    nicknames = nicknames.filter(function (el) {
                        return exist.indexOf(el) < 0;
                    });
                }
                async.forEach(nicknames, function (nick, callback) {
                    cyberchainService2.createAccount(nick, function(err) {
                        console.error(err);
                        callback()
                    });
                }, function (err) {
                    test();
                });
            })
        },
        function() {
            transferVestingShare(next);
        });
}

function transferVestingShare(callback) {
    var nicknames = [];
    for (var i = 0; i < 1000; i++) {
        nicknames.push(userManager.getUserByNumber(i));
    }
    async.forEach(nicknames, function (nick, callback) {
        cyberchainService2.transfer(nick, callback);
    }, function (err) {
        console.error(err);
        if (callback) {
            callback();
        }
    });
}
function run() {
    prepareAccounts(function () {
        console.info("Bitcore connector started");
        nonStop(0);
    });
}

module.exports.run = run;