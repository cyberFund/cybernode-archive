var async = require('async');
var constants = require('./app/constants');
var statusService = require('./app/status.service');
var indexer = require('./app/indexer');
var validator = require('./app/validator');

var status = {};

statusService.readStatus(function (data) {
    status = data ? {source: data.source, destination: data.destination} :
        {source: constants.SOURCE_START_HEIGHT, destination: constants.DESTINATION_START_HEIGHT};
    async.parallel([indexNext, validateNext]);
});

/**
 * Recursive method to read next source block and index it if needed.
 *
 * @name indexNext
 * @returns {void}
 */
function indexNext() {
    indexer.index(status.source, function (exist) {
        if (exist) {
            status.source += 1;
            statusService.writeStatus(status);
            indexNext();
        } else {
            setTimeout(function () {
                indexNext();
            }, constants.INDEXING_LISTEN_DELAY);
        }
    });
}

/**
 * Recursive method to read next cyberchain block and validate data corresponding to source blockchain.
 *
 * @name validateNext
 * @returns {void}
 */
function validateNext() {
    validator.validate(status.destination, function (exist) {
        if (exist) {
            status.destination += 1;
            statusService.writeStatus(status);
            validateNext();
        } else {
            setTimeout(function () {
                validateNext();
            }, constants.VALIDATING_LISTEN_DELAY);
        }
    });
}
