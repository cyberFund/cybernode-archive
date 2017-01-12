var config = require('../config.json');
var steem = require('steem');

var rpc = require('json-rpc2');
var cyberchainClient = rpc.Client.$create(config.cyberchain.port, config.cyberchain.host);

const maxStart = 4294967295;
const size = 10;

function getLastApprovedBlock(callback, start) {

    if (!start) start = maxStart;
    if (start < size) start = size;

    cyberchainClient.call('get_account_history', [config.cyberchain.nickname, start, 10], function (err, result) {
        if (!result.isArray()) {
            console.error('Account history get failed');
        }
        result.reverse();
        for (var i = 0; i < result.length; i++) {
            var record = result[i];
            if (!record.isArray()) {
                console.error('Account history element get failed');
            }
            start = record[0];
            if (!record[1].op.isArray()) {
                console.error('Account history op property get failed');
            }
            if (isMyPost(record[1].op)) {
                callback(record[1].op[1].author, record[1].op[1].permlink, record[1].op[1].title, record[1].op[1].json_metadata, record[1].block);
                return;
            }
            if (record[1].op[0] == 'vote' && record[1].op[1].voter == config.cyberchain.nickname && record[1].op[1].weight > 0) {
                callback(record[1].op[1].author, record[1].op[1].permlink);
                return;
            }
        }
        if (start > 0) {
            getLastApprovedBlock(callback, start)
        }

    });
}

function getBlockByAuthorAndPermlink(author, permlink, callback) {
    cyberchainClient.call('get_content', [author, permlink], function (err, result) {
        if (author != result.author || permlink != result.permlink) {
            console.error('Transaction content get failed');
        }
        callback(result);
    });
}

function makePost(post, callback) {
    steem.broadcast.comment(config.cyberchain.key, '', systemTag, config.cyberchain.nickname,
        post.hash, post.hash, post.body, {system: systemTag, height: post.height}, callback);
}

function getBlockByHash(hash, callback) {
    cyberchainClient.call('get_discussions_by_trending', [{
        tag: systemTag,
        limit: 10,
        start_permlink: hash
    }], function (err, result) {
        if (err) {
            console.error(err);
        }
        callback(result);
    });
}

function vote(author, permlink, weight, callback) {
    steem.broadcast.vote(config.steem.key, config.steem.nickname, author, permlink, weight, function (err, result) {
        callback();
    });
}

function isPost(operation) {
    return operation[0] == 'comment' && operation[1].parent_author == '' && operation[1].parent_permlink == systemTag;
}

function isMyPost(operation) {
    return isPost(operation) && operation[1].author == config.cyberchain.nickname;
}

function getPostsFromBlockByHeight(height, callback) {
    cyberchainClient.call('get_block', [height], function (err, result) {
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
    });
}

function checkMyVote(author, permlink, callback) {
    cyberchainClient.call('get_content', [author, permlink], function (err, result) {
        for (var i = 0; i < result.active_votes.length; i++) {
            if (result.active_votes[i].voter == config.cyberchain.nickname) {
                callback(result.active_votes[i].weight);
                return;
            }
        }
        callback(0);
    });
}

module.exports.getLastAprovedBlock = getLastApprovedBlock;
module.exports.getBlockByAuthorAndPermlink = getBlockByAuthorAndPermlink;
module.exports.getBlockByHash = getBlockByHash;
module.exports.makePost = makePost;
module.exports.vote = vote;
module.exports.getPostsFromBlockByHeight = getPostsFromBlockByHeight;
module.exports.checkMyVote = checkMyVote;
