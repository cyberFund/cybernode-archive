var _ = require('lodash');
var async = require('async');
var bitcore = require('./bitcore.service');
var cyberchain = require('./cyberchain.service');

function validate(height, callback) {
    cyberchain.getPostsFromBlockByHeight(height, function (posts) {
        if (!posts) {
            callback(false);
            return
        }
        actualizePosts(posts , function(actual) {
            validatePosts(actual, callback);
        });
    });
}

/**
 * @typedef {Object} Post
 */

 /**
 * @callback ActualizePostsCallback
 * @param {Post[]} posts - actual posts
 */

/**
 * Load actual state of posts
 *
 * @name actualizePosts
 * @private
 * @param {Post[]} posts - posts actual state load needed
 * @param {ActualizePostsCallback} callback - takes one arg exists, which shows is source block with such height exists
 * @returns {void}
 */
function actualizePosts(posts , callback) {
    var actualizers = [];
    for (var i = 0; i < posts.length; i++) {
        actualizers.push(getPostActualizer(posts[i]));
    }
    async.parallel(actualizers, function (err, actualPosts) {
        if (err) {
            console.error("actualizePosts: " + err);
        }
        callback(actualPosts);
    });
}

/**
 * Load actual state of posts
 *
 * @name actualizePosts
 * @private
 * @param {Post[]} posts - posts actual state load needed
 * @param {ActualizePostsCallback} callback - takes one arg exists, which shows is source block with such height exists
 * @returns {void}
 */
function validatePosts(posts , callback) {
    var validators = [];
    for (var i = 0; i < posts.length; i++) {
        validators.push(getPostValidator(posts[i]));
    }
    async.parallel(validators, function (err, results) {
        if (err) {
            console.error("validatePosts: " + err);
        }
        callback(true);
    })
}

function getPostActualizer(post) {
    return function(callback) {
        cyberchain.getBlockByAuthorAndPermlink(post.author, post.permlink, function(block) {
            callback(null, block)
        });
    }
}

function getPostValidator(post) {
    return function(callback) {
        validatePost(post, callback);
    }
}

/**
 * Check is post equals we have and if need update vote.
 *
 * @name validatePost
 * @private
 * @param {Object} post - post to check
 * @param {Function} doneCallback - takes two args (error, changed), changed shows is vote was changed.
 * @returns {void}
 */
function validatePost(post, doneCallback) {
    if (post.permlink.length == 0) {
        //post was deleted
        //TODO need to check if we need to clear votes after post removing
        doneCallback(null, false);
        return;
    }

    async.waterfall([
            function (next) {
                bitcore.getBlockByHash(post.permlink, function (sourceBlock) {
                    next(null, sourceBlock);
                });
            },
            function (sourceBlock, next) {
                var weight = _.isEqual(sourceBlock, JSON.parse(post.body)) ? 100 : -100;
                next(null, sourceBlock, weight);
            },
            function (sourceBlock, weight, next) {
                if (weight == 100) {
                    //is post we validating is the first valid block
                    findFirstValidBlockWithHash(post.permlink, JSON.parse(post.body), function (err, firstPost) {
                        if (!_.isEqual(post, firstPost)) {
                            weight = -100;
                        }
                        next(null, weight);
                    });
                } else {
                    next(null, weight);
                }
            },
            function (weight, next) {
                cyberchain.checkMyVote(post.author, post.permlink, function (myVoteWeight) {
                    next(null, weight, myVoteWeight);
                });
            }
        ],
        function (err, weight, myVoteWeight) {
            if (err) {
                doneCallback(err);
                return;
            }
            if (weight != myVoteWeight) {
                cyberchain.vote(post.author, post.permlink, weight, function () {
                    doneCallback(null, true);
                });
            } else {
                doneCallback(null, false);
            }

        });

}

function findFirstValidBlockWithHash(hash, body, callback) {
    cyberchain.getPostedBlocksByHash(hash, function (err, posts) {
        if (posts) {
            posts.reverse();
            for (var i = 0; i < posts.length; i++) {
                if (_.isEqual(JSON.parse(posts[i].body), body)) {
                    callback(null, posts[i]);
                    return;
                }
            }
        }
        callback(null, null);
    });
}

module.exports.validate = validate;
module.exports.findFirstValidBlockWithHash = findFirstValidBlockWithHash;
