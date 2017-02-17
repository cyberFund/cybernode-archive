var config = require('../config.json');
var fs = require('fs');

function readStatus(callback) {
    fs.readFile(config.statusfile, 'utf8', function(err, contents) {
        callback(!contents ? null : JSON.parse(contents));
    });
}

function writeStatus(status) {
    fs.writeFile(config.statusfile, JSON.stringify(status), function(err) {
        if(err) {
            return console.log(err);
        }
    });
}

module.exports.readStatus = readStatus;
module.exports.writeStatus = writeStatus;