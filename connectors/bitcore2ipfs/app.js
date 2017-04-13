var constants = require('./app/constants');
var indexer = require('./app/indexer');
var ipfs = require('./app/ipfs.service.js');

var height;

ipfs.getHeight(function (data) {
    if (data instanceof Error) {
        log.error(data)
        return;
    }
    height = data ? data+1 : constants.SOURCE_START_HEIGHT;
    indexNext();
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



