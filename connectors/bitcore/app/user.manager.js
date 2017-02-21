var config = require('../config.json');
var i=0;

const TEST_MODE = process.argv.includes('test');

function getUser() {
    if (!TEST_MODE && config.multyAccounts) {
        if (i >= Math.min(config.multyAccounts, 1000)) {
            i = 0;
        }
        return getUserByNumber(i);
    } else {
        return config.cyberchain.nickname;
    }
}

function getUserByNumber(n) {
    var name = config.cyberchain.nickname + (("" + (1000 + n)).slice(1));
    i++;
    return name;
}

module.exports.getUser = getUser;
module.exports.getUserByNumber = getUserByNumber;
