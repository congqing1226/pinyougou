package com.pinyougou.search.service.impl;

import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author congzi
 * @Description: 搜索服务实现
 * @create 2018-05-22
 * @Version 1.0
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {

        Map<String,Object> map = new HashMap<>();

        //1) 按照关键字 查询高亮显示
        Map hightMap = searchList(searchMap);
        map.putAll(hightMap);

        //2) 查询分类列表(分组查询)
        List categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);

        //3) 查询品牌与规格列表
        if(categoryList.size() > 0){
            map.putAll(searchBrandAndSpecList(String.valueOf(categoryList.get(0))));
        }

        return map;
    }

    /**
     * 关键字搜索-高亮显示
     * @param searchMap
     * @return
     */
    public Map searchList(Map searchMap){

        Map map = new HashMap();
        HighlightQuery highlightQuery = new SimpleHighlightQuery();

        //设置高亮的域, 商品标题
        HighlightOptions options = new HighlightOptions().addField("item_title");
        //高亮前缀
        options.setSimplePrefix("<em style='color:red'>");
        //高亮后缀
        options.setSimplePostfix("</em>");
        //设置高亮选项
        highlightQuery.setHighlightOptions(options);

        /**
         *  1) 购建查询条件-按照关键字查询
         */
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        highlightQuery.addCriteria(criteria);

        /**
         * 2) 构建筛选条件-按分类筛选
         */
        if(searchMap.get("category") != null && !searchMap.get("category").equals("")){
            FilterQuery filterQuery = new SimpleFacetQuery(new Criteria("item_category").is(searchMap.get("category")));
            highlightQuery.addFilterQuery(filterQuery);
        }

        /**
         *  3) 按品牌筛选
         */
        if(searchMap.get("brand") != null && !searchMap.get("brand").equals("")){
            FilterQuery filterQuery=new SimpleFacetQuery(new Criteria("item_brand").is(searchMap.get("brand")));
            highlightQuery.addFilterQuery(filterQuery);
        }

        //   1.4 构建筛选条件，按规格筛选
        Map<String,Object>  specMap = (Map) searchMap.get("spec");

        if(searchMap.get("spec")!=null) {
            Map<String, String> specMapOptions = (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {

                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMapOptions.get(key));
                filterQuery.addCriteria(filterCriteria);
                highlightQuery.addFilterQuery(filterQuery);

            }
        }

            //************** 获取高亮结果集 ****************
        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(highlightQuery, TbItem.class);

        //循环高亮入口集合
        for(HighlightEntry<TbItem> h: tbItems.getHighlighted()){
            //获取原实体类
            TbItem item = h.getEntity();
            if(h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size()>0){
                //设置高亮的结果
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
            }
        }
        map.put("rows",tbItems.getContent());


        return map;
    }

    /**
     *  按关键字 查询分类列表(分组查询)
     *      例如: 输入三星,三星包含的分类有: 手机 电脑 平板
     * @param searchMap
     * @return
     */
    private  List searchCategoryList(Map searchMap){
        List<String> list=new ArrayList();
        Query query=new SimpleQuery();
        //按照关键字查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for(GroupEntry<TbItem> entry:content){
            //将分组结果的名称封装到返回值中
            list.add(entry.getGroupValue());
        }
        return list;
    }

    @Autowired
    private TbSpecificationOptionMapper optionMapper;

    /**
     * 查询品牌与规格
     *      根据模板ID 到redis中查询对应的品牌与规格
     */
    public Map searchBrandAndSpecList(String category){

        Map map = new HashMap();
        /**
         *  redis 存储的分类数据格式: key(分类名称) : value(模板ID)
         *      通过分类名称 获取模板ID
         */
        String typeId =(String) redisTemplate.boundHashOps("itemCat").get(category);

        if(typeId != null){
            //查询品牌列表
            List brandList = JSON.parseArray( (String) redisTemplate.boundHashOps("brandList").get(typeId));
            //查询规格列表
            List<Map> specList = JSON.parseArray( (String)redisTemplate.boundHashOps("specList").get(typeId),Map.class);

            //根据规格ID 查询规格选项列表

            Map specMap = new HashMap();

            for(Map spec : specList){
                TbSpecificationOptionExample optionExample = new TbSpecificationOptionExample();
                TbSpecificationOptionExample.Criteria criteria = optionExample.createCriteria();
                Long specId = Long.parseLong(spec.get("id")+"");
                //设置条件
                criteria.andSpecIdEqualTo(specId);
                List<TbSpecificationOption> optionList = optionMapper.selectByExample(optionExample);

                specMap.put(spec.get("text"),optionList);
            }

            map.put("brandList",brandList);
            map.put("specMap",specMap);
        }
        return map;
    }



}
