 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,
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
	$scope.findOne=function(){

		var id = $location.search()["id"];

		if(typeof(id) == 'undefined'){
			return;
		}

		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//商品介绍
                editor.html($scope.entity.goodsDesc.introduction);
                //图片列表
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
				//扩展属性
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				//规格
                $scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				//创建表格
				$scope.createSKUTable();
            }
		);				
	}
	
	//保存 
	$scope.save=function(){

        /**
		 * 设置富文本 编辑器的内容
         */
        $scope.entity.goodsDesc.introduction=editor.html();

		var serviceObject;//服务层对象  				
		if(typeof( $location.search()['id'] )!='undefined'){//如果有ID
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
                    // 跳转到列表页
                    $scope.goListPage();
				}else{
					alert(response.message);
				}
			}		
		);				
	}


	//跳转到商品 列表页面
	$scope.goListPage = function () {
		location.href = 'goods.html';
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

    /**
	 * 监控模板ID, 读取品牌列表
     */
    $scope.$watch('entity.goods.typeTemplateId', function(newValue, oldValue){

    		//根据模板ID,查询品牌列表
    		typeTemplateService.findOne(newValue).success(

    			function(response){
					$scope.typeTemplate = response;
					$scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);

					//判断是否有ID, 有ID证明是跳转到修改页面
					if(typeof($location.search()["id"]) == 'undefined'){

					}
                    //扩展属性 读取模板中的扩展属性赋给商品的扩展属性。
                    $scope.entity.goodsDesc.customAttributeItems=JSON.parse( $scope.typeTemplate.customAttributeItems);
                }
			);

    		// 读取规格列表

			typeTemplateService.findSpecList(newValue).success(

				function(response){
					$scope.specList = response;
                }
			)

        }
	);



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
    $scope.entity={ goods:{},goodsDesc:{itemImages:[],specificationItems:[]} };//定义实体结构

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


    /**
	 * 更新规格选项
	 * 		$event: 监控复选框的 勾选状态
	 *		name: 规格 值
	 *		value : 规格下所属 规格选项的值
     */

	$scope.updateSpecAttribute = function($event, name , value){

		//判断当前操作的规格名称, 是否在 $scope.entity.goodsDesc.specificationItems 这个集合变量中
        var specList=  $scope.entity.goodsDesc.specificationItems;
        /**
		 * 首先要定义好JSON
		 * 		[{"attributeName":规格名称,"attributeValue":["规格选项 1","规格选项 2"]}]
         */
		for(var i=0; i<specList.length; i++){
			//如果规格存在
			if(specList[i].attributeName == name){

				//并且选中了 规格中的规格选项,需要保存规格选项
				if($event.target.checked){
                    specList[i].attributeValue.push(value);
				}else{
                    //如果是取消了选项,就移除
					specList[i].attributeValue.splice(specList[i].attributeValue.indexOf(value),1);
				}
				// 结束循环
				return ;
			}
		}

        //如果规格不存在,直接保存到 列表
        specList.push({attributeName:name,attributeValue:[value]});


    }

    /**
	 * 构建SKU 表格
     */
	$scope.createSKUTable = function(){
		//初始化一个集合
		var list = [{spec:{},price:0,stockCount:99999}];

		//取出该商品之前的规格结果集
        var specList=  $scope.entity.goodsDesc.specificationItems;

        /**
		 * 循环集合
		 * 	[{"attributeName":规格名称,"attributeValue":["规格选项 1","规格选项 2"]}]
         */

        for(var i=0;i< specList.length;i++ ){//循环规格
            if(specList[i].attributeValue.length>0){
                list=addColumns(list, specList[i].attributeName, specList[i].attributeValue );
            }
        }
		//生成SKU 列表
		$scope.entity.skuList = list;
    }

    /**
	 * 深克隆,克隆一个全新的集合,完成动态添加的效果
	 * 		参数1: list集合 参数2:规格名 参数3:规格选项
     * @param list
     */
    addColumns = function(list,columnName,columnValues){

		/**
		 * 初始化一个新集合(深克隆产生的新的集合对象)
		 * 		原纪录 * 规格选项个数 = 新的列表
		 */
		var newList = [];

		for(var i=0; i<list.length; i++){
			for(var j=0; j<columnValues.length; j++){

				/**
				 * 深克隆
				 * JSON.parse() 方法用于将一个 JSON 字符串转换为对象。
				 * JSON.stringify() 方法用于将 JavaScript 值转换为 JSON 字符串。
				 */
				var newRow = JSON.parse(JSON.stringify(list[i]));
				newRow.spec[columnName] = columnValues[j];
				newList.push(newRow);
			}
		}
		return newList;
    }

	//定义商品 状态
	$scope.state = ["未审核","已审核","已驳回","已关闭"];

    /**
	 * 异步查询商品分类名称
     */
    //商品分类数据
    $scope.itemCatList = [];

    //查询商品分类列表
	$scope.findItemCatList = function(){
		itemCatService.findAll().success(
			function (response) {
				for(var i=0; i < response.length; i++){
					//需要通过ID 获取分类名称, 所以需要将返回的数据再次以数组的形式进行封装
					$scope.itemCatList[response[i].id] = response[i].name;
				}
            }
		)
    }

    /**
	 * 验证规格 与 规格选项是否被勾选
	 * 		根据findOne方法返回的数据 进行验证
     */
	$scope.checkAttributeValue = function (specName,optionsName) {

		//用户选择的规格列表
        var specList= $scope.entity.goodsDesc.specificationItems;

        for(var i=0; i < specList.length; i++){
        	//判断是否有数据(能够查询到规格选项)
        	if(specList[i].attributeValue.indexOf(optionsName) >= 0){
					return true;
			}
		}
		return false;
    }


});	
