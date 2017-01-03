var config = require('./config.json');
var bitcore = require('./app/bitcore.service');
var cyberchain = require('./app/cyberchain.service');

//FIXME duplicate code
const systemTag = 'btc';

cyberchain.getLastAprovedBlock(function (author, permlink, title, metadata) {
    if (metadata) {
        indexingForHeight(block.json_metadata.height + 1);
        return;
    }
    cyberchain.getBlockByAuthorAndPermlink(author, permlink, function (block) {
        indexingForHeight(block.json_metadata.height + 1);
    })
});

function indexingForHeight(height) {
    bitcore.getBlockByHeight(height, function (sourceBlock) {
        cyberchain.getBlockByHash(sourceBlock.hash, function (block) {
            if (!block) {
                //need to add block
                var post = {
                    permlink: sourceBlock.hash,
                    title: hash,
                    body: sourceBlock,
                    metadata: {system: systemTag, height: height}
                };
                cyberchain.makePost(post, function (err, result) {
                    if (err) {
                        console.error(err);
                    }
                    indexingForHeight(height + 1);
                })
            } else {
                //need to check and vote
                var voteValue = (block.body == sourceBlock) ? 1000 : -1000;
                    cyberchain.vote(block.author, block.permlink, voteValue, function(err, result) {
                        if (err) {
                            console.error(err);
                        }
                        indexingForHeight(height + 1);
                    });
            }

        })
    });
}
