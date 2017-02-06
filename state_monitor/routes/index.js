var express = require('express');
var router = express.Router();
var bitcore = require('../app/bitcore.service');
var chain = require('../app/cyberchain.service');
var rethink = require('../app/rethink.service');

router.get('/', function (req, res, next) {
    var model = {title: 'State monitor', bitcore: {}};
    bitcore.getSync(function (data) {
        model.bitcore = data;
        chain.getLastAprovedBlock(function(block) {
            model.chain = {height:JSON.parse(block.json_metadata).height};
            chain.getLastPostedBlock(function (err, last) {
                model.chain.totalHeight = JSON.parse(last[0].json_metadata).height;
                rethink.getLastBlockNumber(function(height){
                    model.connector = {height: height};
                    res.render('index', model);
                });
            });
        });
    });
});

module.exports = router;
