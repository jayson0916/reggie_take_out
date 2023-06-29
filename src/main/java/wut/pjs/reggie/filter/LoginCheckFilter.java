package wut.pjs.reggie.filter;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import wut.pjs.reggie.common.BaseContext;
import wut.pjs.reggie.common.R;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @Author: Jayson_P
 * @Date: 2023/06/04/13:31
 */
/*
* 检查用户是否已经完成登录
* */
@WebFilter(filterName = "LoginCheckFilter" ,urlPatterns = "/*")
@Slf4j
@Component
public class LoginCheckFilter implements Filter {
    //路经匹配器，支持通配符写法
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request =(HttpServletRequest) servletRequest;
        HttpServletResponse response =(HttpServletResponse) servletResponse;

        //1.获取本次请求URI
        String  requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);

        //定义不需要拦截的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //2.判断本次请求是否需要被处理
        boolean filterCheck = check(urls,requestURI);

        //3.如果不需要处理，则直接放行
        if (filterCheck) {
            log.info("本次请求不需要处理{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4.判断登录状态，如果已经登录，则直接放行，从session获取
        if (request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("employee"));

            //将当前登录用户Id存入到线程副本中
            Long empId =(Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        //4.判断登录状态，如果已经登录，则直接放行，从session获取
        if (request.getSession().getAttribute("user")!=null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));

            //将当前登录用户Id存入到线程副本中
            Long userId =(Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
        //5.如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }




    /*
    * 检查本次请求是否需要放行
    * */
    public boolean check(String[] urls , String requestUri) {
        for (String url: urls) {
            boolean match = PATH_MATCHER.match(url,requestUri);
            if (match) {
                return true;
            }
        }
        //没有检测到对应的方向路径，不放行
        return false;
    }

}
