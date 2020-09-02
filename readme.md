# 项目名称
XFrameCore

# API
1. `XFrameCore`核心模块  
    - 模块启动  
    ```
    java -jar 
        [module.jarInfo.path]               -- 上传的.jar绝对路径
        --server.port=[module.webPort]      -- Web服务端口
        -Dfile.encoding=UTF-8               -- Java程序文件编码
        --mid=[module.id]                   -- Module ID
        --token=[token]                     -- 本次服务token, token仅用于XFrame内部通信校验
    ```

    - 模块停止
    ```
    "http://localhost:[module.webPort]/shutdown"
       - module.webPort: web服务端口
    ```
    【注意】: 如果调用`/shutdown/{id}/{token}`失败, 
            `XFrameCore`将会采取杀死进程的方式强制停止模块,
            届时可能会造成资源回收不及时的问题.

    - 修改模块日志目录, 文件名默认[`spring.log`]即可
    ```
    "/logDir/{id}/{token}/{logDir}"
        -id: 模块ID, 模块启动时通过参数[mid]获取
        -token: 启动令牌, 模块启动时通过参数[token]获取
        -logDir: 日志文件目录使用base64编码(UTF-8)
    ```

2. `XFrameModule`模块  
    启动成功时应该立即将日志文件目录告知`XFrameCore`
    ```
    String logDir = StringUtil.base64Enc(logFileDir);
    // 请求地址
    "http://localhost/module/logDir/{id}/{token}/{logDir}"
        -logFileDir: 日志文件目录, 自定义
        -id: 模块ID, 模块启动时通过参数[mid]获取
        -token: 启动令牌, 模块启动时通过参数[token]获取
        -logDir: logFileDir的base64编码(UTF-8)
    ```

    - 模块停止API
    ```
    "/shutdown/{mid}/{token}"
       - mid: 启动模块时分配的ID
       - token: 本次服务token
    ```
    模块停止时必须执行 `mid`&`token`双重校验
    
    - 获取日志目录, 文件名默认[`spring.log`]即可
    ```
    "/logDir/{mid}/{token}"
       - mid: 启动模块时分配的ID
       - token: 本次服务token
    ```
    必须执行 `mid`&`token`双重校验

3. `XFrameService`服务模块  

4. `XFrameApplication`应用模块  
 
    - 应用入口
    ```
    "/index/{mid}/{token}"
       - mid: 启动模块时分配的ID
       - token: 本次服务token
    ```
    必须执行 `mid`&`token`双重校验

    - 应用与Desktop在前端交互  
        - 发送消息
        ```js
        let data = 'WIN_EXIT'; // 退出程序
        top.postMessage(JSON.stringify(data), '*');
        ```

    - 用户登录授权
    

# 知识要点
1. 通过`Runtime`执行命令需要加上前缀, 示例代码为查询指定端口占用情况
    ```java
    // windows
    String[] commands = {
            // 系统指令前缀
            "cmd", "/c",
            // 具体指令
            "netstat", "-aon", "|findstr", String.valueOf(port)
    };
    Process prc = rt.exec(commands);
    
    // linux
    String[] commands = {
            // 系统指令前缀
            "/bin/sh", "-c",
            // 具体指令
            "netstat", "-ntulp", "|grep ", String.valueOf(port)
    };
    Process prc = rt.exec(commands);
    ```
    在`JVM`环境中启动`SpringBoot.jar`程序没有执行到Springboot`事件`, 
    转由`.bat`文件执行
    ```shell script
    @echo off
    start javaw -jar D:\xframe\upload\jar\45E20CBFE4054892A4821DB1A823208E.jar --server.port=9000 -Dfile.encoding=UTF-8 --server.servlet.session.cookie.name=F920476681CE4E97B2988A13AD2C1B7C --mid=2 --token=290E1F1C3D1C49A09D7F058296A65E70 -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005
    @echo on
    exit
    ```
   
2. `RandomAccessFile`读取文件会存在编码问题, 需要手动转码
    ```java
    RandomAccessFile ra = new RandomAccessFile("test.txt", "rw");
    ra.seek(0);
    ra.write("Chinese 中文".getBytes());
    ra.seek(0);     
    // 需要重新转码才能正常显示
    System.out.println(new String(ra.readLine().getBytes("ISO-8859-1"),"utf-8"));
    ra.close();
    ```
3. angular中`iframe.src`的值需要`SafeResourceUrl`
    ```
    // 导入
    import { DomSanitizer } from '@angular/platform-browser';
    // 注入
    constructor(private sanitizer: DomSanitizer) {}
    // 转化为安全地址
    this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url); 
    // 引用 
    <iframe id="treeid" [src]="treeUrl" width="100%" ></iframe>
    ```
4. angular中`iframe`加载页面导致主页面阻塞  
    - 解决方案1:    
   `iframe.src`指向`xframe`的中转静态页面`/public/post.html`, 
   并通过`?uri=index/{id}/{token}&port={webPort}`将`应用模块`首页地址传递到`post.html`页面中, 
   `post.html`页面通过`location.href=url`直接跳转到目标页面. 
   `post.html`页面应该尽量的小: 
    ```html
    <!DOCTYPE html>
    <html lang="en">
    <script>
        var appUrl = location.search.substring(1);
        var port = "", uri = "";
        appUrl.split("&").forEach(function (v) {
            var sp = v.split("=");
            if ('port' === sp[0]) port = sp[1];
            else if ('uri' === sp[0]) uri = sp[1];
        });
        var url = location.protocol + "//" + location.hostname + ":" + port + "/" + uri;
        console.log('appUrl = ', appUrl);
        location.href = url;
    </script>
    </html>
    ```
    测试发现, 如果`iframe.src`指向`xx.html`会把当前页面的`jsessionid`改变,
    导致当前用户被迫下线, 通过配置sessionCookieName解决:
    ```yaml
    # 在Module添加启动参数
    server.servlet.session.cookie.name: notepadSessionID
    ```
    
5. `nz-upload`图片回填  
    - 从列表页面直接把图片地址传递到编辑页面进行回显: `OK`
    - 通过ID从后台获取图片地址回显: `FAIL`, 原因未知.

    
# 任务计划
 - [x] 用户注册登录
 