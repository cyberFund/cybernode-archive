var async = require('async');
var bitcore = require('./app/bitcore.service');
var cyberchain = require('./app/cyberchain.service');

function validate(height, callback) {
    cyberchain.getPostsFromBlockByHeight(height, function (posts) {
        var validators = [];
        for (var i = 0; i < posts.length; i++) {
            validators.push(getPostValidator(posts[i]));
        }
        async.parallel(validators, function (err, results) {
            if (err) {
                console.error(err);
            }
            callback();
        })
    });
}

function getPostValidator(post) {
    return function(callback) {
        validatePost(post, callback);
    }
}

function validatePost(post, callback) {

    bitcore.getBlockByHash(post.title, function (sourceBlock) {
        var weight = compareBlocks(sourceBlock, post.body) ? 1000 : -1000;
        cyberchain.checkMyVote(post.author, post.permlink, function (myVoteWeight) {
            if (weight != myVoteWeight) {
                cyberchain.vote(post.author, post.permlink, weight, function () {
                    callback(null, true);
                });
            } else {
                callback(null, false);
            }
        });
    })
}

function compareBlocks(source, dest) {
    return source == dest;
}

module.exports.validate = validate;