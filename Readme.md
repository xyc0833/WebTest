# JavaWeb-Servlet-Learning
✨ 初心者向けJavaWeb Servletの核心知識を体系的に学ぶためのリポジトリです。環境構築から実践例まで完全なチュートリアルを含みます。

## 📚 学習内容
- Servlet基礎：ライフサイクル、核心メソッド（init/doGet/doPost/destroy）
- アノテーション設定：@WebServletアノテーションの使用とパスマッピング
- デプロイ実践：IDEA + Tomcat 11（Jakarta EE）によるServletプロジェクトのデプロイ
- トラブルシューティング：404エラー/ポート占用/デプロイ失敗などの常見問題の解決策
- 核心API：HttpServletRequest/HttpServletResponseの使用方法

## 🛠 環境設定
- JDK：17+（Jakarta EE 10に対応）
- サーバー：Apache Tomcat 11.0.18
- 開発ツール：IntelliJ IDEA
- 規格：Jakarta Servlet 6.0

## 📝 実践例
- 基礎的なHelloWorld Servlet（アノテーション/XMLの2通りの設定方式）
- リクエストパラメータの取得とレスポンスデータの返却
- Servletコンテキストとセッション管理

## ❓ 常見問題
学習過程で遭遇した典型的な問題を記録しています：
- Tomcatデプロイ後の404エラーのトラブルシューティング
- 8080ポート占用の解決策
- Jakartaと伝統的なjavax.servletの互換性対応


## 学习笔记

一个Servlet的生命周期为：

首先执行构造方法完成 Servlet 初始化
Servlet 初始化后调用 init () 方法。
Servlet 调用 service() 方法来处理客户端的请求。
Servlet 销毁前调用 destroy() 方法。
最后，Servlet 是由 JVM 的垃圾回收器进行垃圾回收的。


我们发现service方法中，还有两个参数，ServletRequest和ServletResponse，
实际上，用户发起的HTTP请求，就被Tomcat服务器封装为了一个ServletRequest对象，
我们得到是其实是Tomcat服务器帮助我们创建的一个实现类，HTTP请求报文中的所有内容，
都可以从ServletRequest对象中获取，同理，ServletResponse就是我们需要返回给浏览器的HTTP响应报文实体类封装。


## @WebServlet(value = "/test",loadOnStartup = 1) loadOnstartup 参数的作用 

你问的 `loadOnStartup = 1` 是 `@WebServlet` 注解里非常实用的配置项，核心作用是**改变 Servlet 的初始化时机**，我会用通俗易懂的方式帮你讲清楚它的作用、用法和实际价值。

### 1. 先理解「默认情况」（不加 loadOnStartup）
如果不配置这个参数，Tomcat 启动时**不会初始化你的 Servlet**，而是要等：
- 客户端第一次访问 `/test` 这个路径时，Tomcat 才会创建 Servlet 实例 → 执行 `init()` 方法 → 处理请求。
- 缺点：第一次访问会有明显延迟（因为要初始化），如果 Servlet 的 `init()` 里有耗时操作（比如加载配置、连接数据库），第一次请求会很慢甚至超时。

### 2. loadOnStartup 的核心作用
`loadOnStartup` 是一个**整数参数**，它会告诉 Tomcat：
> 「启动的时候就把这个 Servlet 初始化好，不要等第一次请求才弄！」

具体规则：
- **值 ≥ 0**：Tomcat 启动时就创建 Servlet 实例，执行 `init()` 方法，Servlet 进入「就绪状态」，等待请求；
- **值 < 0**（默认，不配置就是 -1）：按默认规则，第一次请求时才初始化；
- **多个 Servlet 有该参数**：数值越小，初始化优先级越高（比如 `loadOnStartup=1` 比 `=2` 先初始化）；
- **值相同**：初始化顺序由 Tomcat 决定（不保证）。

### 3. 代码示例 + 效果对比
#### 你的代码（加了 loadOnStartup = 1）
```java
@WebServlet(value = "/test", loadOnStartup = 1)
public class TestServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        // 模拟耗时初始化：加载配置、连接数据库等
        System.out.println("TestServlet 初始化完成！");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write("Hello Test!");
    }
}
```

#### 效果：
- 启动 Tomcat 时，控制台立刻打印 `TestServlet 初始化完成！`；
- 第一次访问 `http://localhost:8080/xxx/test` 时，直接返回结果，无初始化延迟。

#### 不加 loadOnStartup 的效果：
- 启动 Tomcat 时，控制台无打印；
- 第一次访问 `/test` 时，先打印 `TestServlet 初始化完成！`，再返回结果（有延迟）。

### 4. 实际应用场景（什么时候用）
- ✅ **Servlet 初始化有耗时操作**：比如加载配置文件、初始化数据库连接池、预加载缓存等；
- ✅ **核心业务 Servlet**：保证用户第一次访问不卡顿，提升体验；
- ❌ 非核心/极少访问的 Servlet：没必要提前初始化，浪费内存。

### 总结
1. `loadOnStartup = 1` 让 Servlet 在**Tomcat 启动时就完成初始化**，而非第一次请求时；
2. 数值 ≥ 0 生效，数值越小优先级越高，解决「第一次访问卡顿」问题；
3. 适合初始化有耗时操作的核心 Servlet，非核心 Servlet 无需配置（用默认 -1 即可）。

简单记：`loadOnStartup` 就是「启动时加载」，数值是优先级，核心 Servlet 配 1/2 即可。


## 上传文件的路径为什么不能是文件夹

你现在遇到的核心问题是：把文件上传的目标路径设成了**文件夹**（`/Users/xuyaochen/FuRiIT/project/WebTest/src/main/resources`），但 `FileOutputStream` 要求必须指定**具体的文件名称**，而不是文件夹，所以抛出了 `FileNotFoundException` 异常。

### 为什么路径不能是文件夹？
`FileOutputStream` 的核心作用是**写入「文件」**，而不是「文件夹」：
- 当你传入一个文件夹路径时，JVM 会认为你要把数据直接写入这个文件夹（而非文件夹里的某个文件），但文件夹本身是「容器」，不是可写入的文件，因此系统会报错「Is a directory」（是一个目录）。
- 举个通俗的例子：你想把文件放到抽屉里（文件夹），但必须指定「放到抽屉里的某张纸」（具体文件），而不能直接把内容写在抽屉本身上。

### 修复代码（核心：拼接「文件夹+具体文件名」）
我帮你修改代码，既指定文件夹路径，又自动获取上传文件的原始名称，确保写入到具体文件中：
```java
protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // 1. 解决中文乱码
    req.setCharacterEncoding("UTF-8");
    resp.setContentType("text/html;charset=UTF-8");

    // 2. 定义文件保存的「文件夹路径」（确保这个文件夹已存在！）
    String saveDir = "/Users/xuyaochen/FuRiIT/project/WebTest/src/main/resources/";
    // 检查文件夹是否存在，不存在则创建
    File dir = new File(saveDir);
    if (!dir.exists()) {
        dir.mkdirs(); // 递归创建多级文件夹
    }

    try {
        // 3. 获取上传的文件部件（对应前端input的name="test-file"）
        Part part = req.getPart("test-file");
        // 4. 获取上传文件的原始名称（比如 "test.png"）
        String fileName = part.getSubmittedFileName();
        // 防止空文件名（比如用户没选文件就提交）
        if (fileName == null || fileName.isEmpty()) {
            resp.getWriter().write("请选择要上传的文件！");
            return;
        }

        // 5. 拼接「文件夹路径 + 文件名」，得到完整的文件路径（关键！）
        String fullFilePath = saveDir + fileName;
        // 6. 写入到具体文件（而非文件夹）
        try (FileOutputStream stream = new FileOutputStream(fullFilePath)) {
            IOUtils.copy(part.getInputStream(), stream);
            resp.getWriter().write("文件上传成功！保存路径：" + fullFilePath);
        }
    } catch (Exception e) {
        resp.getWriter().write("文件上传失败：" + e.getMessage());
        e.printStackTrace();
    }
}
```

### 关键补充说明
1. **必须确保文件夹存在**：
   如果 `src/main/resources` 文件夹不存在，`FileOutputStream` 会报错，所以代码中加了 `dir.mkdirs()` 自动创建文件夹（`mkdirs()` 支持创建多级文件夹，比如 `a/b/c`，而 `mkdir()` 只能创建单级）。

2. **避免文件覆盖**（可选但推荐）：
   如果多个用户上传同名文件（比如都叫 `test.png`），后上传的会覆盖先上传的。可以给文件名加唯一标识（比如 UUID）：
   ```java
   // 生成唯一文件名：UUID + 原始文件名（避免覆盖）
   String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
   String fullFilePath = saveDir + uniqueFileName;
   ```

3. **为什么不建议上传到 resources 文件夹？**
   `src/main/resources` 是项目的「资源源码目录」，运行时会被打包到 WAR/JAR 中，**不适合作为文件上传的目标路径**：
    - 部署到 Tomcat 后，这个路径可能是只读的（无法写入）；
    - 重新编译/打包项目时，上传的文件会被清空。
      推荐做法：选择项目外的独立文件夹（比如 `/Users/xuyaochen/upload/`），或 Tomcat 的临时目录。

### 总结
1. `FileOutputStream` 必须指定**具体文件名称**，不能是文件夹路径，因为它的作用是写入「文件」而非「目录」；
2. 修复核心：拼接「文件夹路径 + 上传文件的原始名称」，生成完整的文件路径；
3. 额外优化：检查文件夹是否存在、处理空文件名、避免文件覆盖，且不要上传到 `resources` 源码目录。

按这个代码修改后，就能正常上传文件到指定文件夹里的具体文件了。如果还报错，大概率是文件夹权限问题（比如没有写入权限），可以告诉我，我再帮你解决~