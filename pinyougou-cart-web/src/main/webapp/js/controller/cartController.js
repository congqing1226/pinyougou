//购物车控制层
app.controller('cartController',function($scope,cartService){
	
	//查询购物车列表
	$scope.findCartList=function(){
		cartService.findCartList().success(
			function(response){
				$scope.cartList=response;
				sum();//求合计
			}
		);		
	}


	
	//添加商品到购物车(数量变化)
	$scope.add=function(itemId,num){
		
		cartService.addGoodsToCartList(itemId,num).success(
			function(response){
				if(response.success){
					$scope.findCartList();//重新查询
				}else{
					alert(response.message);
				}				
			}
		);
		
	}
	
	//求合计
	sum=function(){
		$scope.totalNum=0;//数量合计
		$scope.totalMoney=0.00;//金额合计
		
		for(var i=0;i<$scope.cartList.length;i++){
			var cart = $scope.cartList[i];			
			for(var j=0;j<cart.orderItemList.length;j++){
				var orderItem=cart.orderItemList[j];
				$scope.totalNum+=orderItem.num;
				$scope.totalMoney+=orderItem.totalFee;				
			}				
		}	
	}


	
});
