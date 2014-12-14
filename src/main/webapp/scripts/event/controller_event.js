'use strict';

breizhjugApp.controller('EventController', function ($scope, resolvedEvent, resolvedSpeakers, Event) {

    $scope.events = resolvedEvent;
    $scope.speakers = resolvedSpeakers;
    $scope.linkTypes = [
        {label: 'Code', value: 'book'},
        {label: 'Slides', value: 'picture'},
        {label: 'Source', value: 'barcode'},
        {label: 'Vidéo', value: 'film'}
    ];

    $scope.addLink = function (newLink) {
        if (newLink === undefined || newLink.name === undefined || newLink.type === undefined || newLink.url === undefined) {
            return;
        }

        if ($scope.event.links === undefined || $scope.event.links === null) {
            $scope.event.links = [];
        }

        var link = {
            name: newLink.name,
            type: newLink.type,
            url: newLink.url
        };
        newLink.name = undefined;
        newLink.type = undefined;
        newLink.url = undefined;
        $scope.event.links.push(link);
    };

    $scope.removeLink = function (index) {
        $scope.event.links.splice(index, 1);
    };

    $scope.create = function () {
        Event.save($scope.event,
            function () {
                $scope.events = Event.query();
                $('#saveEventModal').modal('hide');
                $scope.clear();
            });
    };

    $scope.update = function (id) {
        $scope.event = Event.get({id: id});
        $('#saveEventModal').modal('show');
    };

    $scope.delete = function (id) {
        Event.delete({id: id},
            function () {
                $scope.events = Event.query();
            });
    };

    $scope.clear = function () {
        $scope.event = {name: null, date: null, place: null, image: null, resume: null, id: null, speakers: [], links: []};
    };
});
