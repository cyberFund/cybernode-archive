var config = require('./config.json');
var async = require('async');
var rpc = require('json-rpc2');
var r = require('rethinkdb');

var store = rpc.Client.$create(config.ipfsStore.port, config.ipfsStore.host);
var rConnection;

/*
 * Connect to rethinkdb, create the needed tables/indexes and then start express.
 * Create tables/indexes then start express
 */
async.waterfall([
    function connect(callback) {
        r.connect(config.rethinkdb, callback);
    },
    function createDatabase(connection, callback) {
        //Create the database if needed.
        r.dbList().contains(config.rethinkdb.db).do(function (containsDb) {
            return r.branch(
                containsDb,
                {created: 0},
                r.dbCreate(config.rethinkdb.db)
            );
        }).run(connection, function (err) {
            callback(err, connection);
        });
    },
    function createTable1(connection, callback) {
        createTable('block', connection, callback);
    },
    function createTable2(connection, callback) {
        createTable('tx', connection, callback);
    },
    function createIndex1(connection, callback) {
        createIndex('block', 'createdAt',connection, callback);
    },
    function waitForIndex1(connection, callback) {
        waitForIndex('block', 'createdAt',connection, callback);
    },
    function createIndex2(connection, callback) {
        createIndex('block', 'height',connection, callback);
    },
    function waitForIndex2(connection, callback) {
        waitForIndex('block', 'height',connection, callback);
    },
    function createIndex3(connection, callback) {
        createIndex('tx', 'createdAt',connection, callback);
    },
    function waitForIndex3(connection, callback) {
        waitForIndex('tx', 'createdAt',connection, callback);
    }
], function (err, connection) {
    if (err) {
        console.error(err);
        process.exit(1);
        return;
    }

    startProcessing(connection);
    console.info("RethinkDB projector started");

});

function createTable(tableName, connection, callback) {
    r.tableList().contains(tableName).do(function (containsTable) {
        return r.branch(
            containsTable,
            {created: 0},
            r.tableCreate(tableName)
        );
    }).run(connection, function (err) {
        callback(err, connection);
    });
}

function createIndex(tableName, indexName, connection, callback) {
    //Create the index if needed.
    r.table(tableName).indexList().contains(indexName).do(function (hasIndex) {
        return r.branch(
            hasIndex,
            {created: 0},
            r.table(tableName).indexCreate(indexName)
        );
    }).run(connection, function (err) {
        callback(err, connection);
    });
}

function waitForIndex(tableName, indexName, connection, callback) {
    //Wait for the index to be ready.
    r.table(tableName).indexWait(indexName).run(connection, function (err, result) {
        callback(err, connection);
    });
}

const BTC = 'btc';
const DATA_REQUEST_DELAY = 3000;

function startProcessing(connection) {
    rConnection = connection;
    r.table('block').max('height').run(rConnection, function (err, result) {
        if (err) {
            getAndPut(0, getAndPutNext);
            return;
        }
        getAndPut(result.height + 1, getAndPutNext);
    });

}


function getAndPut(height, callback) {
    getBlock(height, function (block) {
        async.each(block.tx,
            function (txid, next) {
                if (txid == "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b") {
                    next();
                }
                getTx(txid, function (tx) {
                    insertTx(tx, next);
                });
            },
            function (txErr) {
                if (txErr) {
                    callback(txErr);
                    return;
                }
                insertBlock(block, function (err) {
                    callback(err, height);
                });

            });
    });
}

function getAndPutNext(err, height) {
    if (err) {
        console.error(err);
        return;
    }
    getAndPut(height + 1, getAndPutNext)
}


function getBlock(number, callback) {
    store.call('getBlockByHeight', [number, BTC], function (err, block) {
        if (!block) {
            setTimeout(function () {
                getBlock(number, callback);
            }, DATA_REQUEST_DELAY);
            return;
        }
        block.height = number;
        block.createdAt = r.now();
        callback(block)

    });

}


function getTx(txid, callback) {
    store.call('getTxByTxid', [txid, BTC], function (err, tx) {
        if (!tx) {
            setTimeout(function () {
                getTx(txid, callback);
            }, DATA_REQUEST_DELAY);
            return;
        }
        tx.createdAt = r.now();
        callback(tx)

    });

}

function insertBlock(block, callback) {
    r.table('block').insert(block, {returnChanges: true}).run(rConnection, callback);
}


function insertTx(tx, callback) {
    r.table('tx').insert(tx, {returnChanges: true}).run(rConnection, callback);
}

