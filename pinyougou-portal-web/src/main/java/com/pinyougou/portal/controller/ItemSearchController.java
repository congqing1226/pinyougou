package com.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author congzi
 * @Description: 搜索接口
 * @create 2018-05-22
 * @Version 1.0
 */
@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSearchService;

    @RequestMapping("search")
    public Map<String,Object> search(@RequestBody Map<String,Object> searchMap){

        try {
            Map<String, Object> search = itemSearchService.search(searchMap);
            return search;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
