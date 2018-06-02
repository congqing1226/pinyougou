package com.pinyougou.user.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @Description: 用户信息认证
 * @create 2018-06-02
 * @Version 1.0
 */
public class UserDetailServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //授权
        List<GrantedAuthority> authorities = new ArrayList<>();
        //设置角色
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        //密码验证,有CAS系统完成
        return new User(username,"",authorities);
    }

}
