package com.pinyougou.search.service;

import java.util.Map;

/**
 * @author congzi
 * @Description: 搜索服务接口
 * @create 2018-05-22
 * @Version 1.0
 */
public interface ItemSearchService {

    public Map<String,Object> search(Map<String,Object> searchMap);

    /**
     *   保存商品信息到索引库
     */
    public void insertProductToSolr(Long id);
}
