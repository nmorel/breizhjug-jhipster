'use strict';

breizhjugApp.factory('Event', function ($resource) {
        return $resource('app/rest/events/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    });
