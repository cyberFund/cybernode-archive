const config = require('./../config.json');
const async = require('async');
const IPFS = require('ipfs');
const OrbitDB = require('orbit-db');

var ipfs;
var orbitdb;
var docstore;

////////////////////
// Database creation
////////////////////

function prepareDatabase(doneCallback) {
    ipfs = new IPFS()
    orbitdb = new OrbitDB(ipfs)
    docstore = orbitdb.docstore('cybernode-test')
    doneCallback(connection, { indexBy: 'key' });
}

////////////
// Save data
////////////

function insertBlock(block, callback) {
    block.key='block'+block.hash;
    docstore.put(block).then(callback); 
}

function insertTx(tx, callback) {
    tx.key='tx'+tx.txid;
    docstore.put(tx).then(callback); 
}


///////////
// Get data
///////////

function getBlockByHash(hash, callback) {
    docstore.get('block'+hash).then((result) => callback(result.length > 0 ? result[0] : null));
}

function getBlockByHash(hash, callback) {
    docstore.get('tx'+hash).then((result) => callback(result.length > 0 ? result[0] : null));
}

/////////////
// Agregation
/////////////
function hasHeight(height, callback) {
    docstore.query((e)=> e.height == height).then((result) => callback(result.length > 0));
}

function getHeight(doneCallback) {
    var q = {min:0, max:1, limited: false};
    async.until(() => return q.limited && q.min + 1 == q.max,
	(callback) => searchForBorder(q, (next) => {q = next; callback();}),
	(err) => doneCallback(q.min));
}

function searchForBorder(search, callback) {
    if (!search.limited) {
	hasHeight(search.max * 2, (has) => callback({min: search.max, max: 2 * search.max, limited:!has}));
    } else {
	hasHeight((search.min + search.max) / 2, (has) => callback({
	    min: has ? (search.min + search.max) / 2 : search.min, 
	    max: has ? search.max : (search.min + search.max) / 2, 
	    limited: true}));
    }
}

module.exports.prepareDatabase = prepareDatabase;
module.exports.insertBlock = insertBlock;
module.exports.insertTx = insertTx;
module.exports.getBlockByHash = getBlockByHash;
module.exports.getTxByTxid = getTxByTxid;
module.exports.getHeight = getHeight;
