var config = require('../config');
var client = require('rethinkdb-websocket-client');
var r = client.rethinkdb;

var connPromise = client.connect(config.websockets);

var appDiv = document.getElementById('app');
appDiv.innerHTML = 'Loading...';

var query = r.table('block').changes();

connPromise.then(function(conn) {
  return query.run(conn).then(function(cursor) {
    return cursor.each(function(err, results) {
      appDiv.innerHTML += JSON.stringify(results, null, 2);
    });
  });
}).catch(function(error) {
  appDiv.innerHTML = JSON.stringify(error, null, 2);
});
