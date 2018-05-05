/**
 * AngularJS 控制层(调用 service进行页面展示)
 */
app.controller("brandController",function ($scope,$http,$controller,brandService) {

    //继承
    $controller('baseController',{$scope:$scope});

    $scope.findAll=function(){
        brandService.findAll().success(
            function(response){
                $scope.list=response;//给列表变量赋值
            }
        );
    }


    //分页方法
    $scope.findPage=function(page,rows){
        brandService.findPage(page,rows).success(
            function(response){
                $scope.paginationConf.totalItems=response.total;//总记录数
                $scope.list=response.rows;//给列表变量赋值
            }
        );
    }


    //查询实体
    $scope.findOne=function(id){
        brandService.findOne(id).success(
            function(response){
                $scope.entity=response;
            }
        );
    }

    //增加
    $scope.save=function(){
        var object=null;
        if($scope.entity.id!=null){
            object=brandService.update($scope.entity);
        }else{
            object=brandService.add($scope.entity);
        }
        object.success(
            function(response){
                if(response.success){//如果成功
                    $scope.reloadList();//刷新列表
                }else{
                    alert(response.message);//提示错误
                }
            }
        );
    }


    //批量删除
    $scope.dele=function(){

        brandService.dele($scope.selectIds).success(
            function(response){
                if(response.success){//如果成功
                    $scope.reloadList();//刷新列表
                }else{
                    alert(response.message);//提示错误
                }
            }
        );
    }

});