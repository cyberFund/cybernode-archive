var config = require('./config.json');
var async = require('async');
var rpc = require('json-rpc2');
var r = require('rethinkdb');

var client = rpc.Client.$create(config.rpc.port, config.rpc.host);
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


function startProcessing(connection) {
    rConnection = connection;
    //FIXME get from DB
    r.table('block').max('height').run(rConnection, function (err, result) {
        if (err) {
            getBlock(1);
            return;
        }
        getBlock(result.height + 1);
    });

}

//upon to https://steemit.github.io/steemit-docs/
function getBlock(number) {
    client.call('get_block', [number], function (err, result) {
        if (!result) {
            setTimeout(function () {
                getBlock(number);
            }, 3000);
            return;
        }
        result.height = number;
        result.createdAt = r.now();
        insertBlock(result)

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
