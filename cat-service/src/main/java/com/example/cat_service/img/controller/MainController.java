package com.example.cat_service.img.controller;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Iterator;

@Controller
@ResponseBody
public class MainController {


    /*
    이 실습의 목적은 내가 로그인을 하면서 만든 jwt에는 내 로그인 정보가 잇다.
    그리고 이 때 stateless상태의 세션을 임시로 만들고 이 세션을 통해 내가 정보에 접근할 수 있다는 점이다.
     */
    @GetMapping("/")
    public String mainP() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();
        return "Hello World " + name + role;
    }
}
