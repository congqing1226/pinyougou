app.controller('searchController',function($scope,searchService){
	
	//搜索
	$scope.search=function(){
		searchService.search($scope.searchMap).success(
			function(response){
				$scope.resultMap=response;
			}
		);		
	}	

	//定义搜索对象模型
	$scope.searchMap = {"keywords":"","category":"","brand":"","spec":{}};

	//添加搜索项
	$scope.addSearchItem = function(key,value){

		//判断是否点击选择的是 分类 或 品牌
		if(key == 'categoey' || key == 'brand'){
				//生成的格式 : {category:value}
				$scope.searchMap[key] = value;
		}else{
			//点击的是规格
			$scope.searchMap.spec[key] = value;
		}
		$scope.search();
    }


    //删除搜索项
	$scope.removeSearchItem = function (key) {

		if(key == 'categoey' || key == 'brand'){
            $scope.searchMap[key] = "";
		}else{
            delete $scope.searchMap.spec[key];	//移除map中的元素

        }
        $scope.search();
    }

});