app.controller("contentController",function($scope,contentService){
	
	$scope.contentList=[];//广告集合
	
	$scope.findByCategoryKey=function(key){
		contentService.findByCategoryKey(key).success(
			function(response){
				$scope.contentList[key]=response;
			}
		);		
	}
    //搜索  （传递参数）
    $scope.search=function(){
        location.href="http://localhost:9103/search.html#?keywords="+$scope.keywords;
	}

});