var config = require('./../config.json');
var r = require('rethinkdb');

var rConnection;
r.connect(config.rethinkdb, function (err, connection) {
    rConnection = connection;
});

const TABLE_NAME = 'block';
const BLOCK_FIELD_NAME = 'height';

function getLastBlockNumber(callback) {
    r.table(TABLE_NAME).count().run(rConnection, function (err, count) {
        if (err) {
            console.error(err);
        }
        if (count == 0) {
            callback(0);
            return;
        }
        r.table(TABLE_NAME).max(BLOCK_FIELD_NAME).run(rConnection, function (err, result) {
            if (err) {
                console.error(err);
            }
            callback(result ? result.height : 1);
        });
    });
}

module.exports.getLastBlockNumber = getLastBlockNumber;
