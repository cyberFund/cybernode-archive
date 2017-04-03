var constants = require('./app/constants');
var indexer = require('./app/indexer');
var rethink = require('./app/rethink.service');

var height;

rethink.prepareDatabase(function() {
    rethink.getHeight(function (data) {
        height = data ? data : constants.SOURCE_START_HEIGHT;
        indexNext();
    });
});

/**
 * Recursive method to read next source block and index it if needed.
 *
 * @name indexNext
 * @returns {void}
 */
function indexNext() {
    indexer.index(height, function (exist) {
        if (exist) {
            height += 1;
            indexNext();
        } else {
            setTimeout(function () {
                indexNext();
            }, constants.INDEXING_LISTEN_DELAY);
        }
    });
}



