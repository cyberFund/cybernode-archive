var config = require('../config.json');
var steem = require('steem');

var rpc = require('json-rpc2');
var cyberchainClient = rpc.Client.$create(config.cyberchain.port, config.cyberchain.host);

const maxStart = 4294967295;
const size = 10;
const systemTag = 'btc';

function getLastAprovedBlock(callback, start) {

    if (!start) start = maxStart;
    if (start<size) start = size;

    client.call('get_account_history', [config.cyberchain.nickname, start, 10], function (err, result) {
        if (!result.isArray()) {
            console.error('Account history get failed');
        }
        result.reverse();
        for (var i=0; i< result.length; i++) {
            var record = result[i];
            if (!record.isArray()) {
                console.error('Account history element get failed');
            }
            start = record[0];
            if (!record[1].op.isArray()) {
                console.error('Account history op property get failed');
            }
            if (record[1].op[0] == 'comment' &&
                record[1].op[1].author == config.cyberchain.nickname &&
                record[1].op[1].parent_author == '' &&
                record[1].op[1].parent_permlink == systemTag) {
                callback('/@' + record[1].op[1].author + '/' + record[1].op[1].permlink, record[1].op[1].title, record[1].op[1].json_metadata);
                return;
            }
            if (record[1].op[0] == 'vote' && record[1].op[1].voter == config.cyberchain.nickname && record[1].op[1].weight > 0) {
                callback('/@' + record[1].op[1].author + '/' + record[1].op[1].permlink);
                return;
            }
        }
        if (start > 0) {
            getLastComment(callback, start)
        }

    });
}

function getBlockByUrl(permlink, callback) {
    //TODO
}

function makePost(trx, callback) {
    steem.broadcast.comment(config.steem.key,
        '', systemTag,
        config.steem.nickname,
        trx['permlink'],
        trx['title'],
        trx['body'],
        trx['metadata'],
        function(err, result) {
            callback(result);
        });
}


module.exports.getLastAprovedBlock = getLastAprovedBlock;