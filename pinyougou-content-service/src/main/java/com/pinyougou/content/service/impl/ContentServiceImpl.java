package com.pinyougou.content.service.impl;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentCategoryMapper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentCategory;
import com.pinyougou.pojo.TbContentCategoryExample;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import com.pinyougou.content.service.ContentService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
		//清除缓存
		clearCache(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){//得到原来的内容
		TbContent content_old = contentMapper.selectByPrimaryKey(content.getId());

		contentMapper.updateByPrimaryKey(content);
		//清除原来的缓存
		clearCache(content_old.getCategoryId());
		//清除更改后  缓存
		clearCache(content.getCategoryId());

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//清除缓存
			clearCache(contentMapper.selectByPrimaryKey(id).getCategoryId()  );
			contentMapper.deleteByPrimaryKey(id);
		}
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getContent()!=null && content.getContent().length()>0){
				criteria.andContentLike("%"+content.getContent()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	
	@Autowired
	private RedisTemplate<String, TbContent> redisTemplate;
		
	@Override
	public List<TbContent> findByCategoryKey(String key) {

		//先查询缓存
		List<TbContent> contentList = (List<TbContent>)redisTemplate.boundHashOps("content").get(key);

		if(contentList == null){
			TbContentCategoryExample example = new TbContentCategoryExample();
			TbContentCategoryExample.Criteria criteria = example.createCriteria();

			criteria.andContentKeyEqualTo(key);
			//开启状态
			criteria.andStatusEqualTo("1");
			List<TbContentCategory> categoryList = contentCategoryMapper.selectByExample(example);

			if(categoryList.size()==0){
				return new ArrayList();
			}

			TbContentExample contentExample = new TbContentExample();
			Criteria criteria2 = contentExample.createCriteria();

			criteria2.andCategoryIdEqualTo(categoryList.get(0).getId());
			criteria2.andStatusEqualTo("1");
			contentList = contentMapper.selectByExample(contentExample);

			//存入缓存
			redisTemplate.boundHashOps("content").put(key,contentList);
			System.out.println("从数据库中查询数据放入缓存");

		}else{
			System.out.println("从缓存中查询数据");

		}

		return contentList;
	}
	

	
	//清除缓存
	private void clearCache(Long categoryId){
		TbContentCategory tbContentCategory = contentCategoryMapper.selectByPrimaryKey(categoryId);

		String contentKey = tbContentCategory.getContentKey();
		System.out.println("清除缓存" +  contentKey);

		redisTemplate.boundHashOps("content").delete(contentKey);
	}
	
}
