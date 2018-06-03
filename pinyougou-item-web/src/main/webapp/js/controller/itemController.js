//商品详细页（控制层）
app.controller('itemController',function($scope,$http){
    //数量操作
    $scope.addNum=function(x){

        $scope.num=$scope.num+x;

        if($scope.num<1){
            $scope.num=1;
        }
    }

    $scope.specificationItems={};//记录用户选择的规格


    //判断某规格选项是否被用户选中
    $scope.isSelect=function(name,value){
        if($scope.specificationItems[name]==value){
            return true;
        }else{
            return false;
        }
    }

    //加载默认的SKU
    $scope.loadDefaultSku=function(){
        $scope.sku=skuList[0];
        $scope.specificationItems= JSON.parse(JSON.stringify($scope.sku.spec)) ;
    }

    //用户选择规格
    $scope.selectSpecification=function(name,value){
        $scope.specificationItems[name]=value;
        //读取sku
        searchSku();
    }

    //根据用户选择规格,显示SKU的价格 与 标题
    searchSku=function(){
        for(var i=0;i<skuList.length;i++ ){
            if( matchObject(skuList[i].spec ,$scope.specificationItems ) ){
                $scope.sku=skuList[i];
                return ;
            }
        }
        $scope.sku={id:0,title:'--------',price:0};//如果没有匹配的
    }

    //匹配两个对象
    matchObject=function(map1,map2){
        for(var k in map1){
            if(map1[k]!=map2[k]){
                return false;
            }
        }
        for(var k in map2){
            if(map2[k]!=map1[k]){
                return false;
            }
        }
        return true;
    }

    //添加商品到购物车
    $scope.addToCart=function(){
        alert('skuid:'+$scope.sku.id);
    }


    /**
     * 跨域调用
     *  将商品添加到购物车
     */
    $scope.addGoodsToCart = function () {
        $http.get('http://localhost:9106/cart/addGoodsToCartList.do?itemId='
        +$scope.sku.id+'&num='+$scope.num,{'withCredentials':true}).success(
            function (response) {
                alert(response);
            }
        );
    }

});
