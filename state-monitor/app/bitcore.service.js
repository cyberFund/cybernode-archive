var config = require('../config.json');
var http = require('http');

/**
 * @typedef {Object} Sync
 * @property {String} status
 * @property {Number} blockChainHeight
 * @property {Number} syncPercentage
 * @property {Number} height
 * @property {String} error
 * @property {String} type
 */

/**
 * @callback SyncCallback
 * @param {Sync} posts - actual posts
 */

/**
 * Get bitcore sync status
 *
 * @name getSync
 * @param {SyncCallback} callback
 * @returns {void}
 */
function getSync(callback) {
    getData('/insight-api/sync/', function(data) {return data;}, callback);
}

function getData(path, projector, doneCallback) {

    var options = {
        host: config.insight.host,
        port: config.insight.port,
        method: 'GET',
        path : path
    };

    var req = http.request(options, function(response) {
        var str = '';
        response.on('data', function (chunk) {
            str += chunk;
        });

        response.on('end', function () {
            doneCallback(null, projector(JSON.parse(str)));
        });
    });

    req.on('error', function (err) {
        doneCallback(err);
    });

    req.end();
}

module.exports.getSync = getSync;
