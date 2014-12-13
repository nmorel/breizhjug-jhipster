'use strict';

breizhjugApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/person', {
                    templateUrl: 'views/persons.html',
                    controller: 'PersonController',
                    resolve:{
                        resolvedPerson: ['Person', function (Person) {
                            return Person.query().$promise;
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        });
