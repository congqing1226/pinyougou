package com.pinyougou.user.service;

import com.pinyougou.pojo.TbUser;
import entity.PageResult;

import java.util.List;

/**
 * @author congzi
 * @Description: 用户短信验证
 * @create 2018-06-01
 * @Version 1.0
 */
public interface UserService {

    /**
     * 返回全部列表
     * @return
     */
    public List<TbUser> findAll();


    /**
     * 返回分页列表
     * @return
     */
    public PageResult findPage(int pageNum,int pageSize);


    /**
     * 增加
     */
    public void add(TbUser user);


    /**
     * 修改
     */
    public void update(TbUser user);


    /**
     * 根据ID获取实体
     * @param id
     * @return
     */
    public TbUser findOne(Long id);


    /**
     * 批量删除
     * @param ids
     */
    public void delete(Long [] ids);

    /**
     * 分页
     * @param pageNum 当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    public PageResult findPage(TbUser user, int pageNum, int pageSize);


    /**
     * 生成短信验证码
     * @param phone 手机号
     */
    public void createSmsCode(String phone);



    /**
     * 验证短信验证码
     * @param phone 手机号
     * @param code 用户输入的短信验证码
     * @return 是否通过验证
     */
    public boolean checkSmsCode(String phone,String code);

}
