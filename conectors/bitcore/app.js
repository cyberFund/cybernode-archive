var async = require('async');
var constants = require('./app/constants');
var statusService = require('./app/status.service');
var indexer = require('./app/indexer');
var validator = require('./app/validator');

var status = {};

statusService.readStatus(function (data) {
    if (!data) {
        status.source = constants.SOURCE_START_HEIGHT;
        status.destination = constants.DESTINATION_START_HEIGHT;
    } else {
        status.source = data.source;
        status.destination = data.destination;
    }
    async.parallel([indexNext, validating], function () {

    });
});

function indexNext(callback) {
    indexer.index(status.source, function (exist) {
        if (exist) {
            status.source += 1;
            statusService.writeStatus(status);
            indexNext();
        } else {
            setTimeout(function () {
                indexNext(callback);
            }, constants.INDEXING_LISTEN_DELAY);
        }
    });
}

function validateNext(callback) {
    validator.validate(status.destination, function (exist) {
        if (exist) {
            status.destination += 1;
            statusService.writeStatus(status);
            validateNext(callback);
        } else {
            setTimeout(function () {
                validateNext(callback);
            }, constants.VALIDATING_LISTEN_DELAY);
        }
    });
}
