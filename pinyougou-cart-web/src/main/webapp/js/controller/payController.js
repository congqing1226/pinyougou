app.controller('payController',function($scope,payService){
	
	$scope.createNative=function(){
		payService.createNative().success(
			function(response){
				if(response.return_code=='SUCCESS'){//成功
					var ewm=qrcode(10,'H');
                    //生成二维码数据,将http链接封装到二维码中
					ewm.addData( response.code_url);
					ewm.make();
					//生成二维码图片
					document.getElementById('qr').innerHTML=ewm.createImgTag();

					//获取订单号,及金额
					$scope.out_trade_no=response.out_trade_no;
					$scope.total_fee=(response.total_fee/100).toFixed(2);
					
					checkPayStatus();//开始检测
				}else{
					alert("生成二维码发生错误");					
				}
				
			}				
		);		
	}
	
	//检测支付状态
	checkPayStatus=function(){
		payService.checkPayStatus($scope.out_trade_no).success(
			function(response){
				if(response=="SUCCESS"){
					location.href="paysuccess.html";
				}else{
					location.href="payfail.html";
				}				
			}
		
		);
		
	}
	
	
});