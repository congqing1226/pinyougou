package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author congzi
 * @Description: 品牌Controller
 * @create 2018-05-03
 * @Version 1.0
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 查询全部品牌列表
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    /**
     * 返回分页列表
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findAll(int page ,int rows){
        PageResult pageResult = brandService.findPage(page, rows);

        return  pageResult;
    }

    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand){

        try {
            brandService.add(tbBrand);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    /**
     * 修改品牌
     * @param tbBrand
     * @return
     */
    @RequestMapping("/update")
    public  Result update(@RequestBody TbBrand tbBrand){

        try {
            brandService.update(tbBrand);
            return new Result(true,"添加成功");

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    /**
     * 根据ID获取实体
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    /**
     * 删除操作
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids){

        try {
            brandService.delete(ids);
            return new Result(true,"添加成功");

        } catch (Exception e) {

            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
}
