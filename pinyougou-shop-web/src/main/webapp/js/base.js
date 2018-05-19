var app=angular.module('pinyougou',[]);//定义品优购模块

/**
 *AngularJS模块可以在被加载和执行之前对模块自身进行配置。
 *          当你需要在AngularJS模块加载之前进行配置，就要用到config
 */
app.config([
    '$locationProvider',function ($locationProvider) {
        $locationProvider.html5Mode(true);
    }
]);