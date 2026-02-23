package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.io.Resources;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//注意，必须添加@MultipartConfig注解来表示此Servlet用于处理文件上传请求
@MultipartConfig
@WebServlet("/file")
public class FileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("image/jpg");
        OutputStream outputStream = resp.getOutputStream();
        InputStream inputStream = Resources.getResourceAsStream("5.jpg");
        //直接使用copy方法完成转换
        IOUtils.copy(inputStream, outputStream);

    }

    //文件上传


    @Override
/**
 * 处理文件上传的POST请求
 * Servlet的doPost方法用于接收客户端通过POST方式提交的请求，这里专门处理文件上传逻辑
 * @param req  HttpServletRequest对象：封装了客户端的请求信息，包括上传的文件数据
 * @param resp HttpServletResponse对象：用于向客户端返回响应结果
 * @throws ServletException Servlet处理过程中发生的通用异常
 * @throws IOException      输入输出流操作（文件读写、网络传输）可能抛出的异常
 */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 采用try-with-resources语法自动关闭流（Java7+特性），避免手动close导致的资源泄漏
        // FileOutputStream：文件输出流，用于将上传的文件数据写入指定路径的文件
        try(FileOutputStream stream = new FileOutputStream("/Users/xuyaochen/FuRiIT/project/WebTest/src/main/resources/test-file.jpg")){
            // 1. 从请求中获取名为"test-file"的上传文件部件（对应前端<input type="file" name="test-file">的name属性）
            Part part = req.getPart("test-file");

            // 2. 借助Apache Commons IO工具类的copy方法，将上传文件的输入流数据复制到文件输出流
            // 替代手动循环读写字节的繁琐操作，且内置了缓冲区，效率更高
            IOUtils.copy(part.getInputStream(),stream);

            // 3. 设置响应的内容类型和编码，确保返回的中文提示不出现乱码
            resp.setContentType("text/html;charset=UTF-8");

            // 4. 向客户端输出上传成功的提示信息
            resp.getWriter().write("文件上传成功！");
        }
        // 注意：try-with-resources会自动处理stream的关闭，无需额外写finally块
        // 补充：此处未捕获异常，若上传失败会抛出异常，实际开发中建议添加catch块处理异常并返回错误提示
    }
}
