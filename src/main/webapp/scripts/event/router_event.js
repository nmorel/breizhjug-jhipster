'use strict';

breizhjugApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/event', {
                    templateUrl: 'views/events.html',
                    controller: 'EventController',
                    resolve:{
                        resolvedEvent: ['Event', function (Event) {
                            return Event.query().$promise;
                        }],
                        resolvedSpeakers: ['Person', function (Person) {
                            return Person.query().$promise;
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        });
