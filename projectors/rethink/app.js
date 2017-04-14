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
    function createTable(connection, callback) {
        //Create the table if needed.
        r.tableList().contains('block').do(function (containsTable) {
            return r.branch(
                containsTable,
                {created: 0},
                r.tableCreate('block')
            );
        }).run(connection, function (err) {
            callback(err, connection);
        });
    },
    function createIndex1(connection, callback) {
        createIndex('createdAt',connection, callback);
    },
    function waitForIndex1(connection, callback) {
        waitForIndex('createdAt',connection, callback);
    },
    function createIndex2(connection, callback) {
        createIndex('height',connection, callback);
    },
    function waitForIndex2(connection, callback) {
        waitForIndex('height',connection, callback);
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

function createIndex(indexName, connection, callback) {
    //Create the index if needed.
    r.table('block').indexList().contains(indexName).do(function (hasIndex) {
        return r.branch(
            hasIndex,
            {created: 0},
            r.table('block').indexCreate(indexName)
        );
    }).run(connection, function (err) {
        callback(err, connection);
    });
}

function waitForIndex(indexName, connection, callback) {
    //Wait for the index to be ready.
    r.table('block').indexWait(indexName).run(connection, function (err, result) {
        callback(err, connection);
    });
}

const BTC = 'btc';
const DATA_REQUEST_DELAY = 3000;

function startProcessing(connection) {
    rConnection = connection;
    r.table('block').max('height').run(rConnection, function (err, result) {
        if (err) {
            getBlock(0);
            return;
        }
        getBlock(result.height + 1);
    });

}


function getBlock(number) {
    store.call('getBlockByHeight', [number, BTC], function (err, result) {
        if (!result || result.length == 0) {
            setTimeout(function () {
                getBlock(number);
            }, DATA_REQUEST_DELAY);
            return;
        }
        result[0].height = number;
        result[0].createdAt = r.now();
        insertBlock(result[0])

    });

}

function insertBlock(block) {
    r.table('block').insert(block, {returnChanges: true}).run(rConnection, function (err, result) {
        if (err) {
            console.log('Error: ' + err);
            return;
        }
        getBlock(block.height + 1);
    });
}
