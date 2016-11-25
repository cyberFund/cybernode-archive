(function () {
    'use strict';

    angular.module('cyberfund.chain')
        .filter('btc', function() {
            return function(input, args) {
                if (!input) {
                    return '';
                }
                var result = '' + input / 100000000 + ' btc';
                if (args && args.length  > 0 && args.length && input > 0) {
                    result = '+' + result;
                }
                return result;
            };
        });

}());
