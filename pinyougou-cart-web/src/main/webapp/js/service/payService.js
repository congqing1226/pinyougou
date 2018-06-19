app.service("payService",function($http){
	
	
	this.createNative=function(){
		return $http.get("pay/createNative.do");		
	}
	
	//检测支付状态
	this.checkPayStatus=function(out_trade_no){
		return $http.get("pay/checkPayStatus.do?out_trade_no="+out_trade_no);
	}
	
});