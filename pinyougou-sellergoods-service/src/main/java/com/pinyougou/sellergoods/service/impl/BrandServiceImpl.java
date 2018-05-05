package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author congzi
 * @Description: 品牌服务接口实现
 * @create 2018-05-03
 * @Version 1.0
 */
@Service
public class BrandServiceImpl implements BrandService{

    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {

        //使用分页插件设置分页信息
        PageHelper.startPage(pageNum,pageSize);

        Page<TbBrand> page =  (Page<TbBrand>)tbBrandMapper.selectByExample(null);

        PageResult pageResult = new PageResult(page.getTotal(),page.getResult());

        return pageResult;

    }

    @Override
    public Result add(TbBrand tbBrand) {

        tbBrandMapper.insert(tbBrand);
        return null;
    }

    @Override
    public void update(TbBrand tbBrand) {
        tbBrandMapper.updateByPrimaryKey(tbBrand);
    }

    @Override
    public TbBrand findOne(Long id) {
        return tbBrandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void delete(Long[] ids) {

        for (Long id : ids) {
            tbBrandMapper.deleteByPrimaryKey(id);
        }
    }
}
