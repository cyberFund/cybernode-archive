var config = require('./../config.json');
var async = require('async');
var r = require('rethinkdb');

var rConnection;

const TABLE_NAME = 'comment';
const BLOCK_FIELD_NAME = 'block';
const AUTHOR_FIELD_NAME = 'author';
const PERMLINK_FIELD_NAME = 'permlink';
const TITLE_FIELD_NAME = 'title';

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
            createTable(TABLE_NAME, connection, callback);
        },
        function (connection, callback) {
            createIndex(BLOCK_FIELD_NAME, connection, callback);
        },
        function (connection, callback) {
            createIndex([AUTHOR_FIELD_NAME, PERMLINK_FIELD_NAME], connection, callback);
        },
        function (connection, callback) {
            createIndex(TITLE_FIELD_NAME, connection, callback);
        }
    ], function (err, connection) {
        if (err) {
            console.error(err);
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
    r.tableList().contains(TABLE_NAME).do(function (containsTable) {
        return r.branch(containsTable, {created: 0}, r.tableCreate(tableName));
    }).run(connection, function (err) {
        callback(err, connection);
    });
}

function createIndex(fields, connection, doneCallback) {
    async.waterfall([
        function (callback) {
            r.table(TABLE_NAME).indexList().contains(indexName(fields)).run(connection, function (err, hasIndex) {
                callback(err, hasIndex, connection);
            });
        },
        function (hasIndex, connection, callback) {
            if (!hasIndex) {
                if (!Array.isArray(fields)) {
                    r.table(TABLE_NAME).indexCreate(indexName(fields)).run(connection, function (err) {
                        callback(err, connection);
                    });
                } else {
                    r.table(TABLE_NAME).indexCreate(indexName(fields), rows(fields)).run(connection, function (err) {
                        callback(err, connection);
                    });
                }
            } else {
                callback(null, connection);
            }
        },
        function (connection, callback) {
            r.table(TABLE_NAME).indexWait(indexName(fields)).run(connection, function (err) {
                callback(err, connection);
            });
        }], doneCallback);
}

///////////
// Get data
///////////

function getCommentsByAuthor(author, limit, callback) {
    r.table(TABLE_NAME).filter({author: author}).orderBy(r.desc(BLOCK_FIELD_NAME)).limit(limit).run(rConnection, function (err, result) {
        if (err) {
            console.log('Error: ' + err);
            return;
        }
        callback(err, result);
    });
}

function getVotesByVoter(voter, limit, callback) {
    r.table(TABLE_NAME).filter(
        function(c){
            return c("votes").contains(
                function (vote) {
                    return vote("voter").eq(voter);
                }
            )
        }
    ).concatMap(function(c) {return c("votes").filter({"voter":  voter}).map(
        function(vote) {return {
            id:c("id"),
            author: c("author"),
            permlink: c("permlink"),
            block: vote("block"),
            voter: vote("voter"),
            weight: vote("weight")
        }
        })
    }).orderBy(r.desc(BLOCK_FIELD_NAME)).limit(limit).run(rConnection, function (err, result) {
        if (err) {
            console.log('Error: ' + err);
            return;
        }
        callback(err, result);
    });
}

function getCommentsByHeight(height, callback) {
    r.table(TABLE_NAME).getAll(height, {index: BLOCK_FIELD_NAME}).run(rConnection, function (err, result) {
        if (err) {
            if (err.msg.startWith('Table') && err.msg.endsWith('does not exist.')) {
                callback(null, []);
            }
            console.log('Error: ' + err);
            callback(err);
        } else {
            result.toArray(callback);
        }
    });
}


function getCommentByAuthorAndPermLink(author, permlink, callback) {
    //FIXME need to use getAll and index
    r.table(TABLE_NAME).filter({author: author, permlink: permlink}).run(rConnection, function (err, result) {
        if (err) {
            console.log('Error: ' + err);
            callback(err);
        } else {
            //we need first one only
            result.next(callback);
        }
    });
}

function getCommentByTitle(title, callback) {
    //FIXME use index for filter
    //FIXME use index for order
    r.table(TABLE_NAME).filter({title: title}).orderBy('created').run(rConnection, callback);
}

function getCommentsByTag(tag, limit, callback) {
    //FIXME use index for filter
    //FIXME use index for order
    //FIXME why 100
    r.table(TABLE_NAME).filter(function(comment) {
            return comment("json")("tags").contains(tag)
        }
    ).orderBy(r.desc('block')).limit(limit).run(rConnection, callback);
}

function getLastBlockNumber(callback) {
    r.table(TABLE_NAME).count().run(rConnection, function (err, count) {
        if (err) {
            console.error(err);
        }
        if (count == 0) {
            callback(0);
            return;
        }
        r.table(TABLE_NAME).max(BLOCK_FIELD_NAME)(BLOCK_FIELD_NAME).run(rConnection, function (err, max1) {
            if (err) {
                console.error(err);
            }
            r.table(TABLE_NAME).concatMap(function(c) {return c("votes")}).max(BLOCK_FIELD_NAME)(BLOCK_FIELD_NAME).run(rConnection, function (err, max2) {
                if (err && !err.message.startsWith("Cannot take the max of an empty stream.")) {
                    console.error(err);
                }
                callback(max1 ? (max2 ? (Math.max(max1, max2)) : max1) : (max2 ? max2 : 1));
            })
        });
    });
}

////////////
// Save data
////////////

function insertComment(comment, callback) {
    r.table(TABLE_NAME).insert(comment, {returnChanges: true}).run(rConnection, function (err, result) {
        if (err) {
            console.log('Error: ' + err);
            return;
        }
        callback();
    });
}

function addVoteToComment(author, permlink, vote, callback) {
    //FIXME use index and getAll
    r.table(TABLE_NAME).filter({author: author, permlink: permlink})
        .update({votes: r.row('votes').union([vote])})
        .run(rConnection, function (err, result) {
        if (err) {
            console.log('Error: ' + err);
        }
        callback(err, result && result.replaced > 0);
    });
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

function rows(fields) {
    return fields.map(function (value) {
        return r.row(value);
    });
}

module.exports.prepareDatabase = prepareDatabase;
module.exports.insertComment = insertComment;
module.exports.getLastBlockNumber = getLastBlockNumber;
module.exports.getCommentsByAuthor = getCommentsByAuthor;
module.exports.getVotesByVoter = getVotesByVoter;
module.exports.getCommentByAuthorAndPermLink = getCommentByAuthorAndPermLink;
module.exports.addVoteToComment = addVoteToComment;
module.exports.getCommentByTitle = getCommentByTitle;
module.exports.getCommentsByHeight = getCommentsByHeight;
module.exports.getCommentsByTag = getCommentsByTag;