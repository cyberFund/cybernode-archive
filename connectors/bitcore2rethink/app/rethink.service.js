var config = require('./../config.json');
var async = require('async');
var r = require('rethinkdb');

var rConnection;

const BLOCK_TABLE = 'block';
const TX_TABLE = 'tx';

const BLOCK_HEIGHT_COLUMN = 'height';
const BLOCK_HASH_COLUMN = 'hash';
const BLOCK_TX_COLUMN = 'tx';

const TX_ID_COLUMN = 'txid';

const TX_INPUTS_COLUMN = 'vin';
const TX_INPUTS_ADDRESS_COLUMN = 'addr';

const TX_OUTPUTS_COLUMN = 'vout';
const TX_OUTPUTS_ADDRESS_COLUMN = 'addresses';

////////////////////
// Database creation
////////////////////

function prepareDatabase(doneCallback) {
    async.waterfall([
        function connect(callback) {
            r.connect(config.rethinkdb, callback);
        },
        function (connection, callback) {
            createDatabase(config.rethinkdb.db, connection, callback);
        },
        function (connection, callback) {
            createTable(BLOCK_TABLE, connection, callback);
        },
        function (connection, callback) {
            createIndex(BLOCK_TABLE, BLOCK_HEIGHT_COLUMN, connection, callback);
        },
        function (connection, callback) {
            createIndex(BLOCK_TABLE, BLOCK_HASH_COLUMN, connection, callback);
        },
        function (connection, callback) {
            createIndex(BLOCK_TABLE, BLOCK_TX_COLUMN, connection, callback, true);
        },
        function (connection, callback) {
            createTable(TX_TABLE, connection, callback);
        },
        function (connection, callback) {
            createIndex(TX_TABLE, TX_ID_COLUMN, connection, callback);
        },
        function (connection, callback) {
            createIndex2(TX_TABLE, TX_INPUTS_COLUMN, TX_INPUTS_ADDRESS_COLUMN, connection, callback);
        },
        function (connection, callback) {
            createIndex2(TX_TABLE, TX_OUTPUTS_COLUMN, TX_OUTPUTS_ADDRESS_COLUMN, connection, callback);
        }
    ], function (err, connection) {
        if (err) {
            console.error('rethinkService.prepareDatabase: ' + err);
            process.exit(1);
            return;
        }
        rConnection = connection;
        doneCallback(connection);
    });
}

function createDatabase(databaseName, connection, callback) {
    r.dbList().contains(databaseName).do(function (containsDb) {
        return r.branch(containsDb, {created: 0}, r.dbCreate(config.rethinkdb.db));
    }).run(connection, function (err) {
        callback(err, connection);
    });
}

function createTable(tableName, connection, callback) {
    r.tableList().contains(tableName).do(function (containsTable) {
        return r.branch(containsTable, {created: 0}, r.tableCreate(tableName));
    }).run(connection, function (err) {
        callback(err, connection);
    });
}

function createIndex(table, field, connection, doneCallback, multy) {
    async.waterfall([
        function (callback) {
            r.table(table).indexList().contains(field).run(connection, function (err, hasIndex) {
                callback(err, hasIndex, connection);
            });
        },
        function (hasIndex, connection, callback) {
            if (!hasIndex) {
                var create = multy ? r.table(table).indexCreate(field, {multi: true}) : r.table(table).indexCreate(field);
                    create.run(connection, function (err) {
                    callback(err, connection);
                });
            } else {
                callback(null, connection);
            }
        },
        function (connection, callback) {
            r.table(table).indexWait(field).run(connection, function (err) {
                callback(err, connection);
            });
        }], doneCallback);
}

function createIndex2(table, field1, field2, connection, doneCallback) {
    async.waterfall([
        function (callback) {
            r.table(table).indexList().contains(indexName([field1, field2])).run(connection, function (err, hasIndex) {
                callback(err, hasIndex, connection);
            });
        },
        function (hasIndex, connection, callback) {
            if (!hasIndex) {
                r.table(table).indexCreate(indexName([field1, field2]), r.row(field1)(field2), {multi: true})
                    .run(connection, function (err) {
                    callback(err, connection);
                });
            } else {
                callback(null, connection);
            }
        },
        function (connection, callback) {
            r.table(table).indexWait(indexName([field1, field2])).run(connection, function (err) {
                callback(err, connection);
            });
        }], doneCallback);
}

/////////////
// Agregation
/////////////
function getHeight(callback) {
    r.table(BLOCK_TABLE).max({index: BLOCK_HEIGHT_COLUMN}).getField(BLOCK_HEIGHT_COLUMN).run(rConnection, function (err, result) {
        if (err) {
            if(err.name != "ReqlQueryLogicError") {
                console.log('Error: ' + err);
            }
            callback(null);
            return;
        }
        callback(result);
    });
}

///////////
// Get data
///////////

function getBlockByHash(hash, callback) {
    r.table(BLOCK_TABLE).getAll(hash, {index: BLOCK_HASH_COLUMN}).run(rConnection, function (err, result) {
        if (err) {
            console.log('Error: ' + err);
            return;
        }
        callback(result.length > 0 ? result[0] : null);
    });
}

function getTxByTxid(hash, callback) {
    r.table(TX_TABLE).getAll(hash, {index: TX_ID_COLUMN}).run(rConnection, function (err, result) {
        if (err) {
            console.log('Error: ' + err);
            return;
        }
        callback(result.length > 0 ? result[0] : null);
    });
}

////////////
// Save data
////////////

function insertBlock(block, callback) {
    r.table(BLOCK_TABLE).insert(block, {returnChanges: true}).run(rConnection, callback);
}

function insertTx(tx, callback) {
    r.table(TX_TABLE).insert(tx, {returnChanges: true}).run(rConnection, callback);
}

///////////
// Internal
///////////

function indexName(fields) {
    if (!Array.isArray(fields)) {
        return fields;
    }
    var name = fields.map(function (value) {
        return value.charAt(0).toUpperCase() + value.slice(1);
    }).join('And');
    name = name.charAt(0).toLowerCase() + name.slice(1);
    return name;
}


module.exports.prepareDatabase = prepareDatabase;
module.exports.getHeight = getHeight;
module.exports.getBlockByHash = getBlockByHash;
module.exports.insertBlock = insertBlock;
module.exports.insertTx = insertTx;
module.exports.getTxByTxid = getTxByTxid;
