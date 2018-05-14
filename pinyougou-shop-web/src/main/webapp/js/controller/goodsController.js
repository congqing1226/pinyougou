 //控制层 
app.controller('goodsController' ,function($scope,$controller,
					   goodsService,itemCatService,typeTemplateService,uploadService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){

        /**
		 * 设置富文本 编辑器的内容
         */
        $scope.entity.goodsDesc.introduction=editor.html();
		debugger;
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{

			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){

					alert("保存成功!!")
					$scope.entity={};

					//清空富文本编译器
					editor.html('');
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}


	//查询一级分类
	// $scope.selectItemCat1List = function(){
	// 	debugger;
	// 	itemCatService.findByParentId(0).success(
	// 		function (response) {
	// 			$scope.itemCat1List = response;
     //        }
	// 	);
    // }
    $scope.selectItemCat1List=function(){

        itemCatService.findByParentId(0).success(
            function(response){
                $scope.itemCat1List=response;
            }
        );
    }

    /**
	 * 查询二级分类 (监控一级分类)
	 * 	使用AngularJS $watch 用于监控某个变量的值，当被监控的值发生变化，就自动执行相应的函数
	 * 	watch函数其实是有三个变量的，
	 * 	第一个参数是需要监视的对象，
	 * 	第二个参数是在监视对象发生变化时需要调用的函数，
	 * 	第三个参数，它在默认情况下是false 其实watch函数监视的是数组的地址。
     */
    $scope.$watch('entity.goods.category1Id',function(newValue, oldValue){


		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.itemCat2List = response;
            }
		)

    })

    /**
	 * 查询三级分类
     */
    $scope.$watch('entity.goods.category2Id',function(newValue,oldValue){

    	itemCatService.findByParentId(newValue).success(
    		function (response) {
				$scope.itemCat3List = response;
            }
		)

    })

    /**
	 * 查询模板ID
     */
    $scope.$watch('entity.goods.category3Id',function(newValue,oldValue){
        itemCatService.findOne(newValue).success(
            function(response){
                $scope.entity.goods.typeTemplateId=response.typeId;
            }
        );
    });

    // //监控模板ID ，读取品牌列表
    // $scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {
    //
		// 	//查询品牌列表 和 扩展属性
		// 	typeTemplateService.findOne(newValue).success(
		// 		function(response){
		// 			//获取模板数据
		// 			$scope.typeTemplate = response;
    //
		// 			//品牌列表 json数据转换为对象类型
    //                 $scope.typeTemplate.brandIds= JSON.parse($scope.typeTemplate.brandIds);
    //
    //                 //扩展属性
    //                 $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
    //             }
		// 	);
    //
    //     //读取规格列表
    //     typeTemplateService.findSpecList(newValue).success(
    //         function(response){
    //             $scope.specList=response; //规格列表
    //         }
    //     );
    //
    // })

    //上传文件
    $scope.uploadFile=function(){

        uploadService.uploadFile().success(
            function(response){
                if(response.success){
                    $scope.imageEntity.url=response.message;
                }else{
                    alert(response.message);
                }
            }
        ).error(
            function(){
                alert("上传出错！");
            }
        );
    }

    /**
	 *  定义实体结构(JS中的对象创建)
	 *	goods
	 *  goodsDesc 表中的itemImages字段,是一个数组,存放的是 图片的地址
     */
	$scope.entity={goods:{},goodsDesc:{itemImages:[]}};

    /**
	 * 向图片列表添加图片
     */
    $scope.addImageEntity = function(){
		$scope.entity.goodsDesc.itemImages.push($scope.imageEntity);
    }

    /**
	 * 从列表中移除图片
     * @param index
     */
    $scope.removeImageEntity = function (index) {
		$scope.entity.goodsDesc.itemImages.splice(index,1);
    }






});	
