var async = require('async');

var config = require('../config.json');
var constants = require('./constants');

var rpc = require('json-rpc2');
var chain = rpc.Client.$create(config.cyberchain.port, config.cyberchain.host);

const maxStart = 4294967295;
const maxLimit = 100;
const size = 10;

function getLastApprovedBlock(callback, start) {

    if (!start) start = maxStart;
    if (start < size) start = size;

    chain.call('get_account_history', [config.cyberchain.nickname, start, 10], function (err, result) {
        //FIXME should not fall on no result
        if (!Array.isArray(result)) {
            console.error('Account history get failed');
        }
        result.reverse();
        for (var i = 0; i < result.length; i++) {
            var record = result[i];
            if (!Array.isArray(record)) {
                console.error('Account history element get failed');
            }
            start = record[0];
            if (!Array.isArray(record[1].op)) {
                console.error('Account history op property get failed');
            }
            if (isMyPost(record[1].op)) {
                getBlockByAuthorAndPermlink(record[1].op[1].author, record[1].op[1].permlink, callback);
                return;
            }
            if (record[1].op[0] == 'vote' && record[1].op[1].voter == config.cyberchain.nickname && record[1].op[1].weight > 0) {
                getBlockByAuthorAndPermlink(record[1].op[1].author, record[1].op[1].permlink, callback);
                return;
            }
        }
        if (start > 0) {
            getLastApprovedBlock(callback, start)
        }

    });
}

function getBlockByAuthorAndPermlink(author, permlink, callback) {
    chain.call('get_content', [author, permlink], function (err, result) {
        callback(result);
    });
}

//FIXME need to fix blockchain to allow return data reverse orderred
function getPostedBlocksByHash(hash, callback) {
    var start_author = null;
    var start_permlink = null;
    var posts = [];
    var resultLength = Number.MAX_VALUE;
    async.until(
        function () {return resultLength < maxLimit;},
        function (next) {getPostedBlocksByHashInternal(hash, start_author, start_permlink, function(err, result) {
            if (err) {
                next(err);
            }
            //FIXME maybe need to remove first one
            posts = posts.concat(result);
            resultLength = result.length;
            if (resultLength == maxLimit) {
                var last = posts[posts.length-1];
                start_author = last.start_author;
                start_permlink = last.start_permlink;
            }
            next(null)
        })},
        function (err) {
            callback(err, posts);
        }
    );
}

function getPostedBlocksByHashInternal(hash, start_author, start_permlink, callback) {
    chain.call('get_discussions_by_created', [{tag: hash, start_author:start_author, start_permlink: start_permlink, limit: maxLimit}], callback);
}


function isPost(operation) {
    return operation[0] == 'comment' && operation[1].parent_author == '' && operation[1].parent_permlink == constants.SYSTEM;
}

function isMyPost(operation) {
    return isPost(operation) && operation[1].author == config.cyberchain.nickname;
}

function getPostsFromBlockByHeight(height, callback) {
    chain.call('get_block', [height], function (err, data) {
        if (!data) {
            callback(null);
            return;
        }
        var posts = [];
        var txs = data.transactions;
        for (var i = 0; i < txs.length; i++) {
            var ops = txs[i].operations;
            for (var j = 0; j < ops.length; j++) {
                if (isPost(ops[j])) {
                    posts.push(ops[j][1]);
                }
            }
        }
        callback(posts);
    }, callback);
}

function checkMyVote(author, permlink, callback) {
    chain.call('get_content', [author, permlink], function (err, result) {
        for (var i = 0; i < result.active_votes.length; i++) {
            if (result.active_votes[i].voter == config.cyberchain.nickname) {
                callback(result.active_votes[i].weight);
                return;
            }
        }
        callback(0);
    });
}




///// 
//
// cli wallet
//

var wallet = rpc.Client.$create(config.wallet.port, config.wallet.host);

function vote(author, permlink, weight, callback) {
    isWalletLocked(function(state) {
       if (state) {
           unlockWallet(function() {
               voteInternal(author, permlink, weight, callback);
           });
       } else {
           voteInternal(author, permlink, weight, callback);
       }
    });
}

function makePost(post, callback) {
    isWalletLocked(function(state) {
        if (state) {
            unlockWallet(function() {
                makePostInternal(post, callback);
            });
        } else {
            makePostInternal(post, callback);
        }
    });
}

function isWalletLocked(callback) {
    wallet.call('is_locked', [], function (err, result) {
        if (err) {
            console.error(err);
        }
        callback(result);
    });
}

function unlockWallet(callback) {
    wallet.call('unlock', [config.wallet.password], function (err, result) {
        if (err) {
            console.error(err);
        }
        callback();
    });
}

function voteInternal(author, permlink, weight, callback) {
    wallet.call('vote', [config.cyberchain.nickname, author, permlink, weight, true], function (err, result) {
        if (err) {
            console.error(err);
        }
        callback();
    });
}

function makePostInternal(post, callback) {
    wallet.call('post_comment', [config.cyberchain.nickname, post.hash, '', constants.SYSTEM, post.hash, post.body,
        JSON.stringify({tags:[constants.SYSTEM, post.hash], system: constants.SYSTEM, height: post.height}), true], function (err, result) {
        if (err) {
            console.error(err);
        }
        callback(err);
    });
}


//
//
/////


module.exports.getLastAprovedBlock = getLastApprovedBlock;
module.exports.getBlockByAuthorAndPermlink = getBlockByAuthorAndPermlink;
module.exports.getPostedBlocksByHash = getPostedBlocksByHash;
module.exports.makePost = makePost;
module.exports.vote = vote;
module.exports.getPostsFromBlockByHeight = getPostsFromBlockByHeight;
module.exports.checkMyVote = checkMyVote;
