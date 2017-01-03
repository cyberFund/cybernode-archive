var config = require('./config.json');
var bitcore = require('./app/bitcore.service');
var cyberchain = require('./app/cyberchain.service');


cyberchain.getLastAprovedBlock(function(url, title, metadata) {
    //Determine height
});
