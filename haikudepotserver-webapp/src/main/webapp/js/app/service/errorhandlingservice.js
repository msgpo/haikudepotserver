/*
 * Copyright 2014, Andrew Lindesay
 * Distributed under the terms of the MIT License.
 */

/**
 * <p>This service is used to centralize the handling of errors; providing consistent handling and error feedback.
 */

angular.module('haikudepotserver').factory('errorHandling',
    [ '$log','$location',
        function($log,$location) {

            var ErrorHandlingService = {

                /**
                 * <p>When a JSON-RPC failure occurs, this method can be invoked to provide uniform logging and
                 * handling.</p>
                 */

                handleJsonRpcError : function(jsonRpcErrorEnvelope) {
                    if(null==jsonRpcErrorEnvelope) {
                        $log.error('json-rpc error; cause is unknown as no error envelope was available');
                    }
                    else {
                        var code = jsonRpcErrorEnvelope.code ? jsonRpcErrorEnvelope.code : '?';
                        var message = jsonRpcErrorEnvelope.message ? jsonRpcErrorEnvelope.message : '?';
                        $log.error('json-rpc error; code:'+code+", msg:"+message);
                    }

                    $location.path("/error").search({});
                }
            };

            return ErrorHandlingService;

        }
    ]
);

// note the use of the injector service here is used to avoid cyclic dependencies
// with directly injecting $location.

angular.module('haikudepotserver').config([
    '$provide',
    function($provide) {
        $provide.decorator('$exceptionHandler', [
            '$delegate','$injector',
            function($delegate, $injector) {
                return function(exception, cause) {
                    var $location = $injector.get('$location');
                    $delegate(exception,cause);
                    $location.path("/error").search({});
                }
            }
        ]);

    }
]);