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
        if(result.length == 0) {
            //No history
            callback(null, null);
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
        callback(null, null);
        //FIXME need to reget history if not found any
        /*
        if (something) {
            start = somethig;
            getLastApprovedBlock(callback, start)
        }
        */

    });
}

function getBlockByAuthorAndPermlink(author, permlink, callback) {
    chain.call('get_content', [author, permlink], function (err, block) {
        callback(block);
    });
}

function getLastPostedBlock(callback) {
    chain.call('get_discussions_by_created', [{tag: constants.SYSTEM, limit: 1}], callback);
}

function isPost(operation) {
    return operation[0] == 'comment' && operation[1].parent_author == '' && operation[1].parent_permlink == constants.SYSTEM;
}

function isMyPost(operation) {
    return isPost(operation) && operation[1].author == config.cyberchain.nickname;
}


module.exports.getLastAprovedBlock = getLastApprovedBlock;
module.exports.getLastPostedBlock = getLastPostedBlock;
