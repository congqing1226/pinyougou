package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;
import entity.Result;

import java.util.List;

/**
 * @author congzi
 * @Description: 品牌服务接口
 * @create 2018-05-03
 * @Version 1.0
 */
public interface BrandService {

    /**
     * 返回全部列表
     * @return
     */
    public List<TbBrand> findAll();

    /**
     * 分页查询
     * @param pageNum  当前页
     * @param pageSize 每页显示记录数
     * @return
     */
    public PageResult findPage(int pageNum , int pageSize);

    /**
     * 添加品牌
     * @param tbBrand
     * @return
     */
    public Result add(TbBrand tbBrand);

    /**
     * 修改操作
     * @param tbBrand
     */
    public void update(TbBrand tbBrand);

    /**
     * 根据ID查询单条数据
     * @param id
     * @return
     */
    public TbBrand findOne(Long id);

    /**
     * 根据ID删除
     * @param ids
     */
    public void delete(Long[] ids);
}
