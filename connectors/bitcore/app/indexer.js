var _ = require('lodash');
var async = require('async');
var config = require('../config.json');
var bitcore = require('./bitcore.service');
var cyberchain = require('./cyberchain.service');
var validator = require('./validator');

/**
 * Get source block by height and index it if needed
 *
 * @name index
 * @private
 * @param {Number} height - source block height
 * @param {Function} callback - takes one arg exists, which shows is source block with such height exists
 * @returns {void}
 */

function index(height, callback) {
    async.waterfall([
        function (next) {
            bitcore.getBlockByHeight(height, function (sourceBlock) {
                next(null, sourceBlock);
            });
        },
        function (sourceBlock, next) {
            if (!sourceBlock) {
                next({finished: true, exist: false});
                return;
            }
            cyberchain.getBlockByAuthorAndPermlink(config.cyberchain.nickname, sourceBlock.hash, function (block) {
                next(null, sourceBlock, block);
            });
        },
        function (sourceBlock, block, next) {
            if (!block) {
                //user have not already post it
                //check if somebody already has posted valid block
                validator.findFirstValidBlockWithHash(sourceBlock.hash, sourceBlock, function (err, first) {
                    if (!first) {
                        next(null, sourceBlock);
                    } else {
                        next({finished: true, exist: true});
                    }
                });
                //FIXME need to make safe parse
            } else if (!_.isEqual(JSON.parse(block.body), sourceBlock)) {
                //This means that indexer works incorrect now or have been before
                if (config.indexerAllowOverride) {
                    next(null, sourceBlock);
                } else {
                    next("Fatal error. Indexer works incorrect or something. Please investigate. Current:" + block.body + " Source:" + JSON.stringify(sourceBlock));
                }
            }
        },
        function (sourceBlock, next) {
            var post = {
                hash: sourceBlock.hash,
                body: JSON.stringify(sourceBlock),
                height: sourceBlock.height
            };
            cyberchain.makePost(post, function () {
                next(null);
            });
        }
    ], function (err) {
        if (err) {
            if (!err.finished) {
                console.log(err);
                callback(false);
            } else {
                callback(err.exist);
            }
            return;
        }
        callback(true);
    });
}

module.exports.index = index;