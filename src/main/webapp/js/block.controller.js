(function () {
    'use strict';

    angular.module('cyberfund.chain')
        .controller('blockCtrl', ['$scope', '$http', '$routeParams', function ($scope, $http, $routeParams) {
            $scope.prefix = prefix;
            var block = $routeParams.block;
            $http.get('api/block/'+ block).success(function (response) {
                $scope.block = response;
            });
        }])

}());
