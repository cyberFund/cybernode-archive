(function () {
    'use strict';

    angular.module('cyberfund.chain', ['ng', 'ngRoute']);

    angular.module('cyberfund.chain')
        .config(['$routeProvider', function ($routeProvider) {
            $routeProvider
                .when('/', {
                    templateUrl: 'templates/statistic.html',
                    controller: 'statisticCtrl'
                })
                .when('/address/:address', {
                    templateUrl: 'templates/address.html',
                    controller: 'addressCtrl'
                })
                .when('/block/:block', {
                    templateUrl: 'templates/block.html',
                    controller: 'blockCtrl'
                })
                .when('/tx/:tx', {
                    templateUrl: 'templates/tx.html',
                    controller: 'txCtrl'
                })
        }])

}());

