package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.entity.Users;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("此时走了get方法");
        for(Cookie cookie : req.getCookies()){
            System.out.println(cookie.getName() + ": " + cookie.getValue() );
        }

        //现在不能直接访问这个页面 需要通过 Session
        HttpSession session = req.getSession();
        Users user = (Users) session.getAttribute("user");
        if(user == null){
            resp.sendRedirect("login");
            //return的核心作用：终止方法执行，避免逻辑混乱
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String date = dateFormat.format(new Date());
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().write(date + "欢迎你" + user.getUsername());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("此时走了 post方法");
        //把post请求的内容 转交给这个类的 doGet方法
        this.doGet(req, resp);
        System.out.println(req.getAttribute("test"));
        System.out.println(getServletContext().getAttribute("test02"));
    }
}
