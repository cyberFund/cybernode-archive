const config = require('./../config.json');
const async = require('async');
const IpfsDaemon = require('ipfs-daemon/src/ipfs-node-daemon');
const OrbitDB = require('orbit-db');

var orbitdb;
var docstore;

////////////////////
// Database creation
////////////////////

function prepareDatabase(doneCallback) {
    var ipfs = new IpfsDaemon();
    ipfs.on('error',
        function (err) {
            console.error(err);
        }
    );

    ipfs.on('ready',
        function () {
            const orbit = new OrbitDB(ipfs, 'benchmark');
            docstore = orbit.docstore('cybernode-test', { indexBy: 'key' });
            doneCallback();
        }
    );

}

////////////
// Save data
////////////

function insertBlock(block, callback) {
    block.key='block-'+block.hash;
    docstore.put(block).then(callback); 
}

function insertTx(tx, callback) {
    tx.key='tx-'+tx.txid;
    docstore.put(tx).then(callback); 
}


///////////
// Get data
///////////

function getBlockByHash(hash, callback) {
    var result = docstore.get('block-' + hash);
    callback(result.length > 0 ? result[0] : null);
}

function getTxByTxid(txid, callback) {
    var result = docstore.get('tx-' + txid);
    callback(result.length > 0 ? result[0] : null);
}

/////////////
// Agregation
/////////////
function hasHeight(height, callback) {
    var result = docstore.query(function (e) {
        return e.height == height;
    });
    callback(result.length > 0);
}

function getHeight(doneCallback) {
    hasHeight(0, function (exists) {
        if (!exists) {
            doneCallback(-1);
            return;
        }

        var q = {min: 0, max: 1, limited: false};
        async.until(
            function () {
                return q.limited && q.min + 1 == q.max;
            },
            function (callback) {
                searchForBorder(q, function (next) {
                    q = next;
                    callback();
                })
            },
            function (err) {
                doneCallback(q.min)
            });
    });
}

function searchForBorder(search, callback) {
    if (!search.limited) {
        hasHeight(search.max * 2, function (has) {
            callback({min: search.max, max: 2 * search.max, limited: !has})
        });
    } else {
        hasHeight((search.min + search.max) / 2, function (has) {
            callback({
                min: has ? (search.min + search.max) / 2 : search.min,
                max: has ? search.max : (search.min + search.max) / 2,
                limited: true
            })
        });
    }
}

module.exports.prepareDatabase = prepareDatabase;
module.exports.insertBlock = insertBlock;
module.exports.insertTx = insertTx;
module.exports.getBlockByHash = getBlockByHash;
module.exports.getTxByTxid = getTxByTxid;
module.exports.getHeight = getHeight;
