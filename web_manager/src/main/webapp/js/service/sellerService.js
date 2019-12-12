//服务层
app.service('sellerService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../seller1/findAll.do');
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../seller1/findPage.do?page='+page+'&rows='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../seller1/findOne.do?id='+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../seller1/add.do',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../seller1/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../seller1/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../seller1/search.do?page='+page+"&rows="+rows, searchEntity);
	}    
	
	this.updateStatus = function(sellerId,status){
		return $http.get('../seller1/updateStatus.do?sellerId='+sellerId+"&status="+status);
	}
});
