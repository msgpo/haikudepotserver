/*
 * Copyright 2015, Andrew Lindesay
 * Distributed under the terms of the MIT License.
 */

/**
 * <p>This directive will allow the user to choose from a list of repositories.</p>
 */

angular.module('haikudepotserver').directive(
    'repositoriesChooser',
    function() {
        return {
            restrict: 'E',
            templateUrl: '/js/app/directive/repositorieschooser.html',
            replace: true,
            scope: {
                repositories: '=',
                min: '@'
            },
            controller: [
                '$scope', '$log',
                'referenceData','errorHandling','messageSource','userState','repositoryService',
                function(
                    $scope,$log,
                    referenceData,errorHandling,messageSource,userState,repositoryService) {

                    var min = !!min && min.length ? parseInt('' + $scope.min) : 0;
                    $scope.showChooser = false;
                    $scope.possibleRepositoryOptions = undefined;

                    function reset() {
                        $scope.possibleRepositoryOptions = undefined;
                    }

                    function refreshRepositoryOptions() {
                        repositoryService.getRepositories().then(
                            function (repos) {
                                $scope.possibleRepositoryOptions = _.map(
                                    repos,
                                    function(r) {
                                        return {
                                            repository : r,
                                            selected : !!_.findWhere($scope.repositories, { code : r.code })
                                        }
                                    }
                                );
                            },
                            function () {
                                $log.warning('unable to obtain the list of repositories');
                                errorHandling.navigateToError();
                            }
                        );
                    }

                    $scope.goHideChooser = function() {
                        reset();
                        $scope.showChooser = false;
                    };

                    $scope.goShowChooser = function () {
                        reset();
                        refreshRepositoryOptions();

                        // this will open up the modal dialog-box which shows all of the languages that the user
                        // is then able to choose from.

                        $scope.showChooser = true;

                    };

                    function getSelectedRepositoryOptions() {
                        return _.filter(
                            $scope.possibleRepositoryOptions,
                            function(ro) {
                                return ro.selected;
                            }
                        );
                    }

                    $scope.isValid = function() {
                        return getSelectedRepositoryOptions().length > min;
                    };

                    /**
                     * This function is hit when the user confirms their repository selection.
                     */

                    $scope.goConfirm = function() {
                        $scope.repositories = _.map(
                            getSelectedRepositoryOptions(),
                            function(ro) {
                                return ro.repository;
                            }
                        );

                        reset();
                        $scope.showChooser = false;
                    }

                }
            ]
        };
    }
);