'use strict';

angular.module('breizhjugApp')
    .directive('activeMenu', function ($translate, $locale, tmhDynamicLocale) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs, controller) {
                var language = attrs.activeMenu;

                scope.$watch(function () {
                    return $translate.use();
                }, function (selectedLanguage) {
                    if (language === selectedLanguage) {
                        tmhDynamicLocale.set(language);
                        element.addClass('active');
                    } else {
                        element.removeClass('active');
                    }
                });
            }
        };
    })
    .directive('activeLink', function (location) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs, controller) {
                var clazz = attrs.activeLink;
                var path = attrs.href;
                path = path.substring(1); //hack because path does bot return including hashbang
                scope.location = location;
                scope.$watch('location.path()', function (newPath) {
                    if (path === newPath) {
                        element.addClass(clazz);
                    } else {
                        element.removeClass(clazz);
                    }
                });
            }
        };
    }).directive('passwordStrengthBar', function () {
        return {
            replace: true,
            restrict: 'E',
            template: '<div id="strength">' +
            '<small translate="global.messages.validate.newpassword.strength">Password strength:</small>' +
            '<ul id="strengthBar">' +
            '<li class="point"></li><li class="point"></li><li class="point"></li><li class="point"></li><li class="point"></li>' +
            '</ul>' +
            '</div>',
            link: function (scope, iElement, attr) {
                var strength = {
                    colors: ['#F00', '#F90', '#FF0', '#9F0', '#0F0'],
                    mesureStrength: function (p) {

                        var _force = 0;
                        var _regex = /[$-/:-?{-~!"^_`\[\]]/g; // "

                        var _lowerLetters = /[a-z]+/.test(p);
                        var _upperLetters = /[A-Z]+/.test(p);
                        var _numbers = /[0-9]+/.test(p);
                        var _symbols = _regex.test(p);

                        var _flags = [_lowerLetters, _upperLetters, _numbers, _symbols];
                        var _passedMatches = $.grep(_flags, function (el) {
                            return el === true;
                        }).length;

                        _force += 2 * p.length + ((p.length >= 10) ? 1 : 0);
                        _force += _passedMatches * 10;

                        // penality (short password)
                        _force = (p.length <= 6) ? Math.min(_force, 10) : _force;

                        // penality (poor variety of characters)
                        _force = (_passedMatches == 1) ? Math.min(_force, 10) : _force;
                        _force = (_passedMatches == 2) ? Math.min(_force, 20) : _force;
                        _force = (_passedMatches == 3) ? Math.min(_force, 40) : _force;

                        return _force;

                    },
                    getColor: function (s) {

                        var idx = 0;
                        if (s <= 10) {
                            idx = 0;
                        }
                        else if (s <= 20) {
                            idx = 1;
                        }
                        else if (s <= 30) {
                            idx = 2;
                        }
                        else if (s <= 40) {
                            idx = 3;
                        }
                        else {
                            idx = 4;
                        }

                        return {idx: idx + 1, col: this.colors[idx]};
                    }
                };
                scope.$watch(attr.passwordToCheck, function (password) {
                    if (password) {
                        var c = strength.getColor(strength.mesureStrength(password));
                        iElement.removeClass('ng-hide');
                        iElement.find('ul').children('li')
                            .css({"background": "#DDD"})
                            .slice(0, c.idx)
                            .css({"background": c.col});
                    }
                });
            }
        }
    })
    .directive('showValidation', function () {
        return {
            restrict: "A",
            require: 'form',
            link: function (scope, element, attrs, formCtrl) {
                element.find('.form-group').each(function () {
                    var $formGroup = $(this);
                    var $inputs = $formGroup.find('input[ng-model],textarea[ng-model],select[ng-model]');

                    if ($inputs.length > 0) {
                        $inputs.each(function () {
                            var $input = $(this);
                            scope.$watch(function () {
                                return $input.hasClass('ng-invalid') && $input.hasClass('ng-dirty');
                            }, function (isInvalid) {
                                $formGroup.toggleClass('has-error', isInvalid);
                            });
                        });
                    }
                });
            }
        };
    })
    .directive('upload', function () {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                image: '=image',
                maxWidth: '@maxWidth',
                maxHeight: '@maxHeight',
                defaultImage: '@defaultImage'
            },
            template: '<div style="width: {{maxWidth}}px; height: {{maxHeight}}px; border: 2px dotted #ccc;">' +
            '<input type="file" accept="image/*" style="opacity: 0; width: {{maxWidth}}px; height: {{maxHeight}}px;"/>' +
            '<div style="margin-top: -{{maxHeight}}px;">' +
            '<img ng-src="{{image || defaultImage}}" style="max-width: {{maxWidth}}px; max-height: {{maxHeight}}px;" />' +
            '</div>' +
            '</div>',
            link: function (scope, el, attrs) {
                var input = el.find('input');
                input.bind('change', function (event) {
                    var files = event.target.files;
                    if (files && files[0]) {
                        var reader = new FileReader();
                        reader.onload = function (e) {
                            var tempImg = new Image();
                            tempImg.src = e.target.result;
                            tempImg.onload = function () {
                                var tempW = tempImg.width;
                                var tempH = tempImg.height;
                                if (tempW > tempH) {
                                    if (tempW > scope.maxWidth) {
                                        tempH *= scope.maxWidth / tempW;
                                        tempW = scope.maxWidth;
                                    }
                                } else {
                                    if (tempH > scope.maxHeight) {
                                        tempW *= scope.maxHeight / tempH;
                                        tempH = scope.maxHeight;
                                    }
                                }

                                var canvas = document.createElement('canvas');
                                canvas.width = tempW;
                                canvas.height = tempH;
                                var ctx = canvas.getContext("2d");
                                ctx.drawImage(this, 0, 0, tempW, tempH);
                                scope.image = canvas.toDataURL(files[0].type);
                                scope.$apply();
                            };
                        };
                        reader.readAsDataURL(files[0]);
                    }
                });
            }
        };
    });
