/**
 * 父控制器中 编写公用函数
 */
app.controller('baseController',function($scope){

    //分页控件配置   currentPage:当前页   totalItems:总记录数
    //itemsPerPage:每页显示记录数   perPageOptions：分页选项   onChange :当你更改页码的时候出发的事件
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.reloadList();
        }
    };


    //刷新列表
    $scope.reloadList=function(){
//      $scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        $scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }


    //定义ID集合
    $scope.selectIds = [];

    //更新复选框
    $scope.updateSelection = function ($event,id) {
        if($event.target.checked){

            $scope.selectIds.push(id);
        }else{
            //获取选中ID在数组中的位置
            var idx = $scope.selectIds.indexOf(id);

            //取消勾选
            $scope.selectIds.splice(idx,1);
        }
    }

    //转换json字符串，提取某key的值为字符串列表
    $scope.jsonToString=function(jsonString,key){

        var list= JSON.parse(jsonString);
        var str="";

        for(var i=0;i<list.length;i++){
            if(i>0){
                str+=",";
            }
            str+=list[i][key];
        }
        return str;
    }



});