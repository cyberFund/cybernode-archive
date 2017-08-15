var config = require('../config.json');
var http = require('http');
var async = require('async');


function getStatus(callback) {
    var status = {gpu: 'no data'};

    async.parallel([
        function (next) {
            getCpuPercentUse(function (err, data) {
                if (err) {
                    next(err);
                    return;
                }
                status.cpu = data + "%";
                next();
            });
        },
        function (next) {
            getMemoryPercentUse(function (err, data) {
                if (err) {
                    next(err);
                    return;
                }
                status.ram = data + "%";
                next();
            });
        },
        function (next) {
            getFileSystemPercentUse(function (err, data) {
                if (err) {
                    next(err);
                    return;
                }
                status.ssd = data + "%";
                status.hdd = data + "%";
                next();
            });
        },
        function (next) {
            getNetworkUse(function (err, data) {
                if (err) {
                    next(err);
                    return;
                }
                status.net = (data / 1024).toFixed(2) + " kb/s";
                next();
            });
        }
    ], function (err) {
        callback(err, status);
    });
}

//FIXME How to determine what hdd is needed?
function getFileSystemPercentUse(callback) {
    getData('/api/2/fs', function(data) {
        var fs = data.find(function(item) {
            return item.mnt_point.startsWith('/etc');
        });
        return fs ? fs.percent : 'no data';
    }, callback);
}

function getCpuPercentUse(callback) {
    getData('/api/2/cpu', function(data) {
        return data.total;
    }, callback);
}

function getMemoryPercentUse(callback) {
    getData('/api/2/mem', function(data) {
        return data.percent;
    }, callback);
}

function getNetworkUse(callback) {
    getData('/api/2/network', function(data) {
        var network = data.find(function(item) {
            return item.interface_name.startsWith('eth');
        });
        return network ? network.cx / network.time_since_update : 0;
    }, callback);
}

function getData(path, projector, doneCallback) {

    var options = {
        host: config.glances.host,
        port: config.glances.port,
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

    req.on('error', function(err) {
        doneCallback(err);
    });

    req.end();
}

module.exports.getStatus = getStatus;
