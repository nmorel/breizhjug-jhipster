'use strict';

breizhjugApp.controller('PersonController', function ($scope, resolvedPerson, Person) {

    $scope.persons = resolvedPerson;
    $scope.types_address = ['GITHUB', 'TWITTER', 'MAIL'];

    $scope.addAddress = function (newAddress) {
        if (newAddress === undefined || newAddress.type === undefined || newAddress.value === undefined) {
            return;
        }

        if ($scope.person.addresses === undefined) {
            $scope.person.addresses = [];
        }
        var address = {
            type: newAddress.type,
            value: newAddress.value
        };
        newAddress.type = undefined;
        newAddress.value = undefined;
        $scope.person.addresses.push(address);
    };

    $scope.removeAddress = function (index) {
        $scope.person.addresses.splice(index, 1);
    };

    $scope.create = function () {
        Person.save($scope.person,
            function () {
                $scope.persons = Person.query();
                $('#savePersonModal').modal('hide');
                $scope.clear();
            });
    };

    $scope.update = function (id) {
        $scope.person = Person.get({id: id});
        $('#savePersonModal').modal('show');
    };

    $scope.delete = function (id) {
        Person.delete({id: id},
            function () {
                $scope.persons = Person.query();
            });
    };

    $scope.clear = function () {
        $scope.person = {
            name: null,
            photo: null,
            resume: null,
            speaker: null,
            teamMember: null,
            id: null,
            addresses: []
        };
    };

    $scope.clear();
});
