var constants = require('./app/constants');
var indexer = require('./app/indexer');
var orbit = require('./app/orbit.service');

var height;

orbit.prepareDatabase(function() {
    orbit.getHeight(function (data) {
        height = data ? data+1 : constants.SOURCE_START_HEIGHT;
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



