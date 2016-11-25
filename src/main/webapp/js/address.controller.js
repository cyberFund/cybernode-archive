(function () {
    'use strict';

    angular.module('cyberfund.chain')
        .controller('addressCtrl', ['$scope', '$http', '$routeParams', function ($scope, $http, $routeParams) {
            $scope.prefix = prefix;
            var address = $routeParams.address;
            $http.get('api/address/'+ address).success(function (response) {
                $scope.address = response;
            });
        }])

}());
