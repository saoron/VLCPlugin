var exec = require('cordova/exec');

exports.coolMethod = function(arg0, success, error) {
    exec(success, error, "VLCPlugin", "coolMethod", [arg0]);
};
exports.play = function(arg0, success, error) {
    exec(success, error, "VLCPlugin", "play", [arg0]);
};