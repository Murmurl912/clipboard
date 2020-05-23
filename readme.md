# 同步剪切板
    这是一款剪切板同步应用，应用由四个部分组成。
    分别是存储服务端，登陆认证服务端，数据库服务器和客户端。
   
+ ##配置
    1. 数据库安装  
        数据库选用的是MongoDB 4，数据库需要采用集群模式部署以支持分布式事务。
        需要创建数据库clipboard, 在数据库clipboard中创建用户clipboard_app
        配置密码并授予dbOwner角色。
        
    2. 数据源配置  
       分别为clipboard_store和clipboard_server创建数据源配置文件。  
       具体内容如下：
           
           spring.data.mongodb.host=ipaddress or domain name
           spring.data.mongodb.port=primary database port
           spring.data.mongodb.database=clipboard
           spring.data.mongodb.password= user password
           spring.data.mongodb.username= username
    3. 存储服务端配置  
       存储服务端采用webflux技术，使用Netty作为web服务器，端口和路径配置   
       参考springboot web配置  
           
           server.port= port for netty
           server.address= address bind
       
    4. 登陆认证服务器配置  
        服务器配置同存储服务端配置类似，只增加了AccessToken的签名密钥,
        默认签名算法为HMS512，密钥需要采用SecureRandom生成，长度至少为512bit。
            
            jwt.secret= hms512 secret key
        
    5. 客户端配置
       客户端需要配置的只有存储服务器的地址和端口
           
           app.base=http://domain:port/path
           
    6. 总结  
        所有配置都可以直接更改/resource/application.properties文件。
        外部配置参照springboot external configuration。
        配置完成便可按照需要打包。
        
+ ##安装
    1. 服务端安装  
       服务端打包成jar，运行环境为jdk 11。  
       打包采用mvn命令
       使用java的java -jar server.jar 便可运行。
       
    2. 客户端安装
       客户端目前使用javafx 和 springboot 开发，sdk 版本为jdk-11。  
       目前独立打包工具存在依赖问题，不兼容jdk11，由于编写过程中使用了  
       jdk11中的新特性，需要重写。
       可以利用maven打包成jar，但是运行需要jre 11的支持。  
    
+ ##使用
    1. 服务端监控
       服务端没有加入任何监控模块  
       如果需要可以将spring devtools 加入到服务端模块的maven依赖中。  
       重新打包和配置后便可以监控springboot程序。
       数据库需要设置定时任务清理过期数据。
       数据库监控可以使用mongo shell或者mongo compass
       
    2. 客户端使用
       在配置了jre环境后便可运行客户端，
       客户大启动时会在所在目录创建本地数据库缓存文件。运行时配置虚拟机参数
           
           -Dprism.verbose=true -Dprism.forceGPU=true  
       启用GPU加速渲染。
       
    3. 用户注册
    
    4. 用户登陆
    
    5. 开始使用