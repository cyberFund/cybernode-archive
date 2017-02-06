var cyberchain = require('./cyberchain.service.js');

function discoverSourceStartHeight(callback) {
    cyberchain.getLastAprovedBlock(function (author, permlink, title, metadata, blockHeight) {
        if (metadata) {
            callback(block.json_metadata.height);
            return;
        }
        cyberchain.getBlockByAuthorAndPermlink(author, permlink, function (block) {
            callback(block.json_metadata.height + 1);
        });
    });

}
module.exports.discoverSourceStartHeight = discoverSourceStartHeight;
