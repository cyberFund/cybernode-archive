var config = require('./config.json');
var rpc = require('json-rpc2');
var rethinkService = require('./app/rethink.service');
var server = rpc.Server.$create();

server.expose('status', status);
server.expose('get_account_history', getAccountHistory);
server.expose('get_content', getContent);
server.expose('get_discussions_by_created', getDiscussionsByCreated);
server.expose('get_block', getBlock);
server.expose('is_locked', isLocked);
server.expose('unlock', unlock);
server.expose('vote', vote);
server.expose('post_comment', postComment);

function status(args, opt, callback) {
    callback(null, "ok");
}

function getAccountHistory(params, opt, callback) {
    logRequest('get_account_history', params);
    processRequest(params, 3, function () {
        var author = params[0], start = params[1], length = params[2];
        rethinkService.getCommentsByAuthor(author, function (error, result) {
            //TODO prepare data
            callback(error, result);
        });
    }, callback);
}

function getContent(params, opt, callback) {
    logRequest('get_content', params);
    processRequest(params, 2, function () {
        var author = params[0], permlink = params[1];
        rethinkService.getCommentByAuthorAndPermLink(author, permlink, function (error, result) {
            if (result) {
                result = prepareComment(result);
            }
            callback(error, result);
        });
    }, callback);
}

function prepareComment(comment) {
    comment.body = JSON.stringify(comment.body);
    comment.active_votes = constructActiveVotes(comment.votes);
    delete comment.votes;
    return comment;
}

function getDiscussionsByCreated(params, opt, callback) {
    logRequest('get_discussions_by_created', params);
    processRequest(params, 1, function () {
        var filter = params[0];
        rethinkService.getCommentByTitle(filter.tag, function (error, comments) {
            comments = comments.map(prepareComment);
            callback(error, comments);
        });
    }, callback);
}

function getBlock (params, opt, callback) {
    logRequest('get_block', params);
    processRequest(params, 1, function () {
        var height = params[0];
        rethinkService.getCommentsByHeight(height, function (error, result) {
            callback(error, constructBlock(result));
        });
    }, callback);
}

function isLocked(params, opt, callback) {
    //Allways unlocked
    logRequest('is_locked', params);
    callback(false);
}

function unlock(params, opt, callback) {
    //Do nothing
    logRequest('unlock', params);
    callback();
}

function vote(params, opt, callback) {
    var error, result;

    logRequest('vote', params);

    processRequest(params, 5, function () {

        var broadcast = params[4];
        if (!broadcast) {
            callback(null);
            return;
        }
        rethinkService.getLastBlockNumber(function (block) {

            var author = params[1], permlink = params[2];
            var vote = {
                voter: params[0],
                weight: params[3],
                block: block
            };

            rethinkService.addVoteToComment(author, permlink, vote, callback);

        });

    }, callback);

}

function postComment(params, opt, callback) {
    var error, result;

    logRequest('post_comment', params);

    processRequest(params, 8, function () {

        var broadcast = params[7];
        if (!broadcast) {
            callback(null);
            return;
        }
        rethinkService.getLastBlockNumber(function (block) {


            var comment = {
                author: params[0],
                permlink: params[1],
                parent_author: params[2],
                parent_permlink: params[3],
                title: params[4],
                body: JSON.parse(params[5]),
                json: params[6],
                block: block + 1,
                votes: []
            };

            rethinkService.insertComment(comment, function () {
                callback(error, result);
            });

        });

    }, callback);

}

function constructBlock(comments) {
    if(comments.length == 0) {
        return null;
    }
    var operations = [];
    for (var i = 0; i< comments.length; i++) {
        comments[i].body = JSON.stringify(comments[i].body);
        /*
        comments[i].active_votes = comments[i].votes;
        delete comments[i].votes;
        */
        operations.push(['comment', comments[i]]);
    }
    return {
        transactions: [{
            operations : operations
        }]
    };
}

function constructActiveVotes(votes) {
    return votes;
}

var logRequest = function (name, params) {
    if (params && params.length) {
        console.log('Requested ' + name + ' with params:' + params.join(', '));
    } else {
        console.log('Requested ' + name + ' with no params.');
    }

};

var processRequest = function (params, n, process, callback) {
    if (params.length != n) {
        callback({code: -32602, message: "Invalid params"});
    } else {
        process();
    }
};

rethinkService.prepareDatabase(function (connection) {
    server.listen(config.rpc.port, config.rpc.host);
});
