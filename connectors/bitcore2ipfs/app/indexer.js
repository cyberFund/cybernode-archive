var async = require('async');
var bitcore = require('./bitcore.service');
var rethink = require('./orbit.service');

/**
 * Get source block by height and index it if needed
 *
 * @name index
 * @private
 * @param {Number} height - source block height
 * @param {Function} callback - takes one arg exists, which shows is source block with such height exists
 * @returns {void}
 */

function index(height, callback) {
    async.waterfall([
        function (next) {
            bitcore.getBlockByHeight(height, function (block) {
                next(null, block);
            });
        },
        function (block, next) {
            if (!block) {
                next({finished: true, exist: false});
                return;
            }
            rethink.getBlockByHash(block.hash, function (stored) {
                next(null, block, stored);
            });
        },
        function (block, stored, next) {
            if (!stored) {
                next(null, block);
            } else{
                next({finished: true, exist: true});
            }
        },
        function (block, next) {
            async.map(block.tx, function(txid, callback) {
                indexTx(txid, function () {
                    callback();
                })
            }, function() {
                rethink.insertBlock(block, function (err) {
                    next(err);
                });
            });
        }
    ], function (err) {
        if (err) {
            if (!err.finished) {
                console.log("index: " + err);
                callback(false);
            } else {
                callback(err.exist);
            }
            return;
        }
        callback(true);
    });
}

function indexTx(txid, callback) {

    //Bitcore fail to load very first transaction
    if (txid == "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b") {
        callback(true);
        return;
    }

    async.waterfall([
        function (next) {
            bitcore.getTxById(txid, function (tx) {
                next(null, tx);
            });
        },
        function (tx, next) {
            rethink.getTxByTxid(tx.txid, function (stored) {
                next(null, tx, stored);
            });
        },
        function (tx, stored, next) {
            if (!stored) {
                next(null, tx);
            } else{
                next({finished: true, exist: true});
            }
        },
        function (tx, next) {
            rethink.insertTx(tx, function (err) {
                next(err);
            });
        }
    ], function (err) {
        if (err) {
            if (!err.finished) {
                console.log("indexTx: " + err);
                callback(false);
            } else {
                callback(err.exist);
            }
            return;
        }
        callback(true);
    });
}


module.exports.index = index;
