package org.example.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.example.entity.Users;
import org.example.mapper.UserMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Log
@WebServlet(value = "/login", initParams = {
        @WebInitParam(name = "xyc001", value = "我是一个默认的初始化参数")
})
public class LoginServlet extends HttpServlet {

    SqlSessionFactory factory;
    @SneakyThrows
    @Override
    public void init() throws ServletException {
        factory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config.xml"));
        System.out.println(getInitParameter("xyc001"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();
        if(cookies != null){
            String username = null;
            String password = null;
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("username")) username = cookie.getValue();
                if(cookie.getName().equals("password")) password = cookie.getValue();
            }
            if(username != null && password != null){
                //登陆校验
                try (SqlSession sqlSession = factory.openSession(true)){
                    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
                    Users user = mapper.getUser(username, password);
                    if(user != null){
                        HttpSession session = req.getSession();
                        session.setAttribute("user",user);
                        resp.sendRedirect("time");
                        return;   //直接返回
                    }else{
                        Cookie cookie_username = new Cookie("username", username);
                        cookie_username.setMaxAge(0);
                        Cookie cookie_password = new Cookie("password", password);
                        cookie_password.setMaxAge(0);
                        resp.addCookie(cookie_username);
                        resp.addCookie(cookie_password);
                    }
                }
            }
        }
        //正常情况还是转发给默认的Servlet帮我们返回静态页面
        req.getRequestDispatcher("/").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        req.getParameterMap().forEach((k,v)->{
//            System.out.println(k + " " + Arrays.toString(v));
//        });

        //首先设置一下响应类型
        resp.setContentType("text/html;charset=UTF-8");
        //获取POST请求携带的表单数据
        Map<String, String[]> map = req.getParameterMap();
        //判断表单是否完整
        if(map.containsKey("username") && map.containsKey("password")) {
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            //权限校验（待完善）
            try(SqlSession sqlSession = factory.openSession(true)){
                UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
                Users user = userMapper.getUser(username,password);

                if(user != null ){
                    resp.getWriter().write("用户：" + username + "登录成功");
                    //登录成功之后 加入 重定向
                    //resp.sendRedirect("time");
                    //请求转发可以携带数据
                    //请求转发的好处： 它可以携带数据！
//                    req.setAttribute("test", "我是请求转发前的数据");
//                    ServletContext context = getServletContext();
//                    context.setAttribute("test02", "我是重定向之前的数据");
//                    req.getRequestDispatcher("/time").forward(req, resp);

                    //resp.sendRedirect("time");

                    //学习cookie
                    //Cookie cookie = new Cookie("test004", "yyds");
                    //resp.addCookie(cookie);
                    //resp.sendRedirect("time");

                    if(map.containsKey("remember-me")){   //若勾选了勾选框，那么会此表单信息
                        Cookie cookie_username = new Cookie("username", username);
                        cookie_username.setMaxAge(30);
                        Cookie cookie_password = new Cookie("password", password);
                        cookie_password.setMaxAge(30);
                        resp.addCookie(cookie_username);
                        resp.addCookie(cookie_password);
                    }
                    //登录之后 获取session
                    HttpSession session = req.getSession();
                    session.setAttribute("user",user);
                }else{
                    resp.getWriter().write("您登录的用户密码不正确或者此用户不存在 ");
                }
            }
        }else {
            resp.getWriter().write("错误，您的表单数据不完整！");
        }
    }
}
