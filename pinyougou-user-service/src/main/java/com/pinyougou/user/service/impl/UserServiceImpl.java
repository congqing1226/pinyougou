package com.pinyougou.user.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.pojo.TbUserExample;
import com.pinyougou.pojo.TbUserExample.Criteria;
import com.pinyougou.user.service.UserService;

import entity.PageResult;


/**
 * @author congzi
 * @Description: userService 服务
 * @create 2018-06-01
 * @Version 1.0
 */
@Service
public class UserServiceImpl implements UserService{

    private final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private TbUserMapper userMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbUser> findAll() {
        return userMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbUser> page=   (Page<TbUser>) userMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbUser user) {

        /**
         * 用户创建时间 与 修改时间
         */
        user.setCreated(new Date());
        user.setUpdated(new Date());

        /**
         * 设置用户可用
         */
        user.setStatus("1");

        /**
         * 密码加密
         */
        String md5Hex = DigestUtils.md5Hex(user.getPassword());
        user.setPassword(md5Hex);

        userMapper.insert(user);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbUser user){
        userMapper.updateByPrimaryKey(user);
    }

    /**
     * 根据ID获取实体
     * @param id
     * @return
     */
    @Override
    public TbUser findOne(Long id){
        return userMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for(Long id:ids){
            userMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbUser user, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbUserExample example=new TbUserExample();
        Criteria criteria = example.createCriteria();

        if(user!=null){
            if(user.getUsername()!=null && user.getUsername().length()>0){
                criteria.andUsernameLike("%"+user.getUsername()+"%");
            }
            if(user.getPassword()!=null && user.getPassword().length()>0){
                criteria.andPasswordLike("%"+user.getPassword()+"%");
            }
            if(user.getPhone()!=null && user.getPhone().length()>0){
                criteria.andPhoneLike("%"+user.getPhone()+"%");
            }
            if(user.getEmail()!=null && user.getEmail().length()>0){
                criteria.andEmailLike("%"+user.getEmail()+"%");
            }
            if(user.getSourceType()!=null && user.getSourceType().length()>0){
                criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
            }
            if(user.getNickName()!=null && user.getNickName().length()>0){
                criteria.andNickNameLike("%"+user.getNickName()+"%");
            }
            if(user.getName()!=null && user.getName().length()>0){
                criteria.andNameLike("%"+user.getName()+"%");
            }
            if(user.getStatus()!=null && user.getStatus().length()>0){
                criteria.andStatusLike("%"+user.getStatus()+"%");
            }
            if(user.getHeadPic()!=null && user.getHeadPic().length()>0){
                criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
            }
            if(user.getQq()!=null && user.getQq().length()>0){
                criteria.andQqLike("%"+user.getQq()+"%");
            }
            if(user.getIsMobileCheck()!=null && user.getIsMobileCheck().length()>0){
                criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
            }
            if(user.getIsEmailCheck()!=null && user.getIsEmailCheck().length()>0){
                criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
            }
            if(user.getSex()!=null && user.getSex().length()>0){
                criteria.andSexLike("%"+user.getSex()+"%");
            }

        }

        Page<TbUser> page= (Page<TbUser>)userMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    private RedisTemplate<String ,Object> redisTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination smsDestination;

    /**
     * 短信模板 ID
     */
    @Value("${templateCode}")
    private String templateCode;

    /**
     * 短信签名
     */
    @Value("${sign_name}")
    private String sign_name;

    /**
     *  生成验证码,保存到redis中
     *  使用MQ, 调用短信微服务 发送短信
     * @param phone 手机号
     */
    @Override
    public void createSmsCode(final String phone) {

        //生成随机数
        final String smscode = (long) (Math.random() * 1000000)+"";
        logger.info("生成用户注册验证码: "+smscode);

        //保存到redis 使用hash key:用户手机号 value:验证码
        redisTemplate.boundHashOps("smscode").put(phone,smscode);

        //发送MQ消息
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {

                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("phone",phone);
                mapMessage.setString("templateCode",templateCode);
                mapMessage.setString("sign_name",sign_name);

                //验证码
                Map map = new HashMap();
                map.put("code",smscode);
                mapMessage.setString("param",JSON.toJSONString(map));

                logger.info("用户电话:"+phone+" - 短信模板ID:"+
                        templateCode+" - 短信签名:"+sign_name+" - 验证码:"+smscode);

                return (Message) mapMessage;
            }
        });

    }

    @Override
    public boolean checkSmsCode(String phone, String code) {

        logger.info("用户输入的code:"+code);
        String  sysCode = (String) redisTemplate.boundHashOps("smscode").get(phone);

        if(sysCode==null){
            return false;
        }

        logger.info("redis中的code:"+code);
        if(!sysCode.equals(code)){
            return false;
        }
        return true;
    }
}
