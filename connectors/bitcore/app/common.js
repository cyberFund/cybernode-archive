module.exports.parse = function(string) {
    if (!string || string.length == 0) {
        return null;
    }
    return JSON.parse(string);
};
