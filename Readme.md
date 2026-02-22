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