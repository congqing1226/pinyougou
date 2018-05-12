package com.pinyougou.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author congzi
 * @Description: 自定义认证类
 *      实现 security 的 UserDetailsService
 * @create 2018-05-10
 * @Version 1.0
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * spring 配置文件形式 注入bean
     */
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    /**
     *
     * @param
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        /**
         * 构建角色集合
         */
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        /**
         * 校验用户姓名
         */
        TbSeller seller = sellerService.findOne(username);
        if(seller == null){
            return null;
        }else if(!seller.getStatus().equals("1")){
            //状态不是 已审核
            return null;
        }

        return new User(username,seller.getPassword(),authorities);
    }
}
