var config = require('../config.json');
var bitcore = require('./app/bitcore.service');
var cyberchain = require('../app/cyberchain.service');

function index(height, callback) {
    bitcore.getBlockByHeight(height, function (sourceBlock) {
        if (!sourceBlock) {
            callback(false);
        }
        cyberchain.getBlockByAuthorAndPermlink(config.cyberchain.nickname, sourceBlock.hash, function(result) {
            if (!result) {
                indexBlock(sourceBlock, callback);
            } else if (result != sourceBlock) {
                //This means that indexer works incorrect now or have been before
                if (config.indexerAllowOverride) {
                    updateBlockIndex(sourceBlock, callback);
                } else {
                    console.error("Fatal error. Indexer works incorrect or something. Please investigate.")
                }
            }
        });
    });
}

function indexBlock(sourceBlock, callback) {
    var post = {
        hash: sourceBlock.hash,
        body: sourceBlock,
        height: sourceBlock.height
    };
    cyberchain.makePost(post, function() {
        if (err) {
            console.error(err);
        }
        callback(true);
    });
}


function updateBlockIndex(sourceBlock, callback) {
    indexBlock(sourceBlock, callback);
}


module.exports.index = index;