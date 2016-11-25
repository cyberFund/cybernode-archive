(function () {
    'use strict';

    angular.module('cyberfund.chain')
        .controller('statisticCtrl', ['$scope', '$http', function ($scope, $http) {
            $scope.prefix = prefix;
            $http.get('api/').success(function (response) {
                $scope.stat = response;
            });
        }])

}());
