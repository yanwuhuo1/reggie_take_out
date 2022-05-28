package com.it.filter;


import com.alibaba.fastjson.JSON;
import com.it.common.BaseContext;
import com.it.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//检查用户是否登入
//所有请求都拦截
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取本次请求的url
        String requestURI = request.getRequestURI();

        log.info("拦截到请求：{}",requestURI);
        //不处理的
        String[] strings = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",

        };
        //判断本次请求是否需要处理
        boolean check = check(strings, requestURI);
        //如果不需要处理，则直接放行
        if (check) {
            filterChain.doFilter(request, response);
            log.info("不需要处理，则直接放行");
            return;
        }
        //判断登入状态，如果已经登入，则直接放行
        if (request.getSession().getAttribute("employee") != null) {

            //  log.info("已经登入，则直接放行:{}",request.getSession().getAttribute("employee"));

            Long employee = (Long) request.getSession().getAttribute("employee");

            BaseContext.setCurrentId(employee);

            filterChain.doFilter(request, response);
            return;
        }
        //判断移动端登入状态，如果已经登入，则直接放行
        if (request.getSession().getAttribute("user") != null) {

              log.info("已经登入，则直接放行:{}",request.getSession().getAttribute("user"));

            Long userid = (Long) request.getSession().getAttribute("user");

            BaseContext.setCurrentId(userid);

            filterChain.doFilter(request, response);
            return;
        }
        //如果未登入则返回未登入的结果，通过输出流的方式向客户端响应数据
        log.info("用户未登入");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

        return;

    }


    //路径匹配，检查本次请求是否需要放行
    public boolean check(String[] strings, String requestURI) {
        for (String url : strings
        ) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;

    }


}
