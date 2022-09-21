package com.teamwork.takeout.filter;

import com.alibaba.fastjson.JSON;
import com.teamwork.takeout.common.BaseContext;
import com.teamwork.takeout.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    public boolean check(String[] urls, String reqURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url,reqURI);
            if (match) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String reqURI = req.getRequestURI();

        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };

        if (check(urls, reqURI)){
            filterChain.doFilter(servletRequest, servletResponse);
            return ;
        }

        Long employeeId = (Long) req.getSession().getAttribute("employee");

        if (employeeId != null) {
            log.info("用户已登录, ID为 {}, 可放行...", employeeId);
            BaseContext.setCurrentId(employeeId);
            filterChain.doFilter(servletRequest, servletResponse);
            return ;
        }

        Long userId = (Long) req.getSession().getAttribute("user");

        if (userId != null) {
            log.info("用户已登录, ID为 {}, 可放行...", userId);
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(servletRequest, servletResponse);
            return ;
        }

        log.info("用户未登录...");
        resp.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

}
