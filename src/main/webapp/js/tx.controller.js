(function () {
    'use strict';

    angular.module('cyberfund.chain')
        .controller('txCtrl', ['$scope', '$http', '$routeParams', function ($scope, $http, $routeParams) {
            $scope.prefix = prefix;
            var tx = $routeParams.tx;
            $http.get('api/tx/'+ tx).success(function (response) {
                $scope.tx = response;
            });
        }])

}());
