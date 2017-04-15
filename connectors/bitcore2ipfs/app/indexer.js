var async = require('async');
var bitcore = require('./bitcore.service');
var ipfs = require('./ipfs.service.js');

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
            ipfs.getBlockByHash(block.hash, function (stored) {
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
                ipfs.insertBlock(block, function (hash) {
                    //FIXME
                    next(null, hash);
                });
            });
        }
    ], function (err, hash) {
        if (err) {
            if (!err.finished) {
                console.error("index: " + err);
                callback(false);
            } else {
                callback(err.exist);
            }
            return;
        }
        console.log("index: " + hash.message);
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
            ipfs.getTxByTxid(tx.txid, function (stored) {
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
            ipfs.insertTx(tx, function (hash) {
                //FIXME
                next(null, hash);
            });
        }
    ], function (err, hash) {
        if (err) {
            if (!err.finished) {
                console.err("indexTx: " + err);
                callback(false);
            } else {
                callback(err.exist);
            }
            return;
        }
        console.log("indexTx: " + hash.message);
        callback(true);
    });
}

function indexData(data, callback) {
    async.map(data.blocks, function (block, next) {
        ipfs.insertBlock(block, function (hash) {
            next(null, hash);
        });
    }, function (err, hashes) {
        if (hashes) {
            console.log("index: " + hashes.join(', '));
        }
        async.map(data.blocks, function (tx, next) {
            ipfs.insertTx(tx, function (hash) {
                //FIXME
                next(hash);
            });
        }, function (err, hashes) {
            if (hashes) {
                console.log("indexTx: " + hashes.join(', '));
            }
            callback();
        });
    });
}

function getDataForIndex(startHeight, endHeight, callback) {
    var heights = Array.apply(null, {length: endHeight - startHeight}).map(Number.call, Number).map(function (i) {
        return i + startHeight;
    });

    async.reduce(heights,{blocks: [], txs: []},
        function(wholeData, height, next) {
            getDataForIndexByHeight(height, function(data) {
                wholeData.blocks = wholeData.blocks.concat(data.blocks);
                wholeData.txs = wholeData.txs.concat(data.txs);
                next(null, wholeData);
            });
        },
        callback);
}

function getDataForIndexByHeight(height, doneCallback) {
    var data = {blocks: [], txs: []};
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
            ipfs.getBlockByHash(block.hash, function (stored) {
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
                getTxForIndex(txid, function (tx) {
                    if (tx) {
                        data.txs.push(tx);
                    }
                    callback();
                })
            }, function() {
                data.blocks.push(block);
                next();
            });
        }
    ], function (err) {
        if (err && !err.finished) {
            console.error("index: " + err);
        }
        doneCallback(data);
    });
}

function getTxForIndex(txid, callback) {

    //Bitcore fail to load very first transaction
    if (txid == "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b") {
        callback(undefined);
        return;
    }

    async.waterfall([
        function (next) {
            bitcore.getTxById(txid, function (tx) {
                next(null, tx);
            });
        },
        function (tx, next) {
            ipfs.getTxByTxid(tx.txid, function (stored) {
                next(null, tx, stored);
            });
        }
    ], function (tx, stored, err) {
        if (err && !err.finished) {
            console.error("indexTx: " + err);
            callback(undefined);
        } else if (!stored) {
            callback(tx);
        } else {
            callback(undefined);
        }
    });
}

module.exports.index = index;
module.exports.indexData  = indexData;
module.exports.getDataForIndex = getDataForIndex;