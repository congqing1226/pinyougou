app.controller('searchController',function($scope,$location,searchService){

    //定义搜索对象模型
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'' ,'pageNo':1,'pageSize':10,'sortField':'','sort':''};


	//搜索
	$scope.search=function(){

		//将当前页码转为int类型
		$scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);

		searchService.search($scope.searchMap).success(
			function(response){
				$scope.resultMap=response;

                buildPageLabel();
			}
		);		
	}	



	//添加搜索项
	$scope.addSearchItem = function(key,value){

		//判断是否点击选择的是 分类 或 品牌
		if(key == 'categoey' || key == 'brand' || key == 'price'){
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

		if(key == 'categoey' || key == 'brand' || key == 'price'){
            $scope.searchMap[key] = "";
		}else{
            delete $scope.searchMap.spec[key];	//移除map中的元素

        }
        $scope.search();
    }

    /**
	 * 构建分页标签
     */
    buildPageLabe = function(){
		//分页栏
		$scope.pageLabel = [];
		//最后一页
		var maxPageNo = $scope.resultMap.totalPages;
		//起始页
		var firstPage = 1;
		//结束页
        var lastPage=maxPageNo;

        //设置省略 ......
        $scope.firstDot = true;
        $scope.lastDot = true;


		//如果总页数大于5 显示部分页码
		if($scope.resultMap.totalPages > 5){

			if($scope.searchMap.pageNo <= 3){
				//当前页小于3
				lastPage = 5;

                $scope.firstDot=false;//前面没点
			}else if($scope.resultMap.pageNo >= lastPage-2){
				firstPage = maxPageNo - 4;

                $scope.lastDot=false;//后边没点
			}else{
                firstPage=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
            }
		}else{
            $scope.firstDot=false;//前面无点
            $scope.lastDot=false;//后边无点

        }

		for(var i = firstPage; i <= lastPage; i++){
			$scope.pageLabel.push(i);
		}
    }

    //根据页码查询
	$scope.queryByPage = function (pageNo) {
		//页码验证
		if(pageNo < 1 || pageNo > $scope.searchMap.totalPages){
			return;
		}

		$scope.searchMap.pageNo = pageNo;
		$scope.search();
    }

    //判断当前页是否为第一页
	$scope.isTopPage = function () {
		if($scope.searchMap.pageNo == 1){
			return true;
		}else{
			return false;
		}
    }

    //判断当前页是否未最后一页
    $scope.isEndPage=function(){
        if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
            return true;
        }else{
            return false;
        }
    }

	//设置排序规则
	$scope.sortSearch = function (sortField,sort) {
		$scope.searchMap.sortField = sortField;
		$scope.searchMap.sort=sort;

		$scope.search();
    }

    //判断关键字是不是品牌
	$scope.keywordsIsBrand = function () {
		for(var i=0; i<$scope.resultMap.brandList.length; i++){
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
				return true ;
			}
			return false;
		}
    }

    //加载关键字
    $scope.loadkeywords=function(){
        $scope.searchMap.keywords= $location.search()['keywords'];
        $scope.search();//查询
    }


});