package org.example.reggie.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import java.lang.Thread;

import org.example.reggie.common.BaseContext;
import org.example.reggie.common.R;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
检查用户是否登录
 */
@Slf4j
@WebFilter(filterName ="loginCheckFilter",urlPatterns ="/*")
public class LoginCheckFilter implements Filter{
    public static final AntPathMatcher PATH_MATCHER =new AntPathMatcher();//匹配判断器**
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response =(HttpServletResponse) servletResponse;

         //获取请求URI
        String requestURI=request.getRequestURI();
        log.info("拦截到请求:{}",requestURI);

        //定义不需要处理的路径
        String[] urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                //对用户登陆操作放行
                "/user/login",
                "/user/sendMsg"
        };

        //判断请求是否需要处理
        boolean check = check(urls, requestURI);

        //不需要处理，直接放行
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //判断后台登录状态，登录的放行
        if (request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("employee"));

            Long empId =(Long) request.getSession().getAttribute("employee");
            BaseContext.setThreadLocal(empId);

            long id=Thread.currentThread().getId();
            log.info("线程id为：{}",id);

            filterChain.doFilter(request,response);
            return;
        }

        //判断前端用户是否登录
        if(request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));
            Long userId = (Long)request.getSession().getAttribute("user");
            BaseContext.setThreadLocal(userId);
            filterChain.doFilter(request,response);
            return;
        }

        //未登录的返回登录结果
        log.info("用户未登录！");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }
    /*
    路径匹配，判断是否放行
     */
    public boolean check(String[] urels,String requestURI){
        for (String url:urels){
            boolean match=PATH_MATCHER.match(url,requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
