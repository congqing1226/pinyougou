// 定义模块:
var app = angular.module("pinyougou",[]);
/*$sce服务写成过滤器*/
app.filter('trustHtml',['$sce',function($sce){
    return function(data){
        return $sce.trustAsHtml(data);
    }
}]);

/**
 *AngularJS模块可以在被加载和执行之前对模块自身进行配置。
 *          当你需要在AngularJS模块加载之前进行配置，就要用到config
 */
app.config([
    '$locationProvider',function ($locationProvider) {
        $locationProvider.html5Mode(true);
    }
]);