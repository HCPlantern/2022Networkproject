# **计网大作业说明文档（Team1）**

| **组员**                 | **分工** |
| ------------------------ | -------- |
| 江楠 201250033           | Server   |
| 邓尤亮 201250035         | Client   |
| 韩陈旭 201250037         | Client   |
| 万沛沛 201250038（组长） | Server   |
| 华广松 201840309         | Server   |



## **Before Start**

本项目是基于Maven构建的，使用IntelliJ IDEA开发的Java项目。运行前，需通过maven安装相关依赖。请确保本项目存放于英文路径下。

本项目使用了log4j2记录日志，为保证控制台日志信息**有颜色**，请先设置JVM参数：`-Dlog4j.skipJansi=false`

​                 ![img](https://docimg3.docs.qq.com/image/uz65w15cncY7rsr9PSgtTg.png?w=1280&h=710.8742714404663)        服务端运行后，打开http://localhost:5000/ ，经301重定向至 http://localhost:5000/index.html ，出现一个带图片的注册登录页面，说明项目配置正常。



------

## **Http-Client**

### **项目要求**

实现基础的 HTTP 请求、响应功能，具体要求如下：

- HTTP客户端可以发送请求报文、呈现响应报文（命令行形式）
- HTTP客户端对301、302、304的状态码做相应的处理
- MIME 至少支持三种类型，包含一种非文本类型 

### **项目简介**

- 编写测试文件以发起客户端的 Http 请求
- Client 类使用 Socket 通信
- Componets 内包括 Http 报文的 Header, Body, 以及 Http 请求和 Http 回复报文
- Handler 包括对请求和回复报文的相关类，其中请求时会查找永久重定向与缓存资源，得到回复后处理各种状态码
- LocalCache 内包括对重定向和请求资源的缓存相关类

### **关键类与包的描述**

- Client类

- - 主要功能

  - - 模拟客户端发送http请求，处理对应的http响应报文

  - 类图

  - - ​                 ![img](https://docimg6.docs.qq.com/image/feYQMYEvD0-kCuxKFZ_kDQ.png?w=828&h=668)        

- SocketPool

- - 主要功能

  - - 模拟socket连接池，主要用于存储长连接时没有关闭的连接，用于复用socket连接

  - 类图

  - - ​                 ![img](https://docimg10.docs.qq.com/image/12cRGVAVwyiiO92eSnVzLw.png?w=1260&h=668)        

- RequestHandler

- - 主要功能

  - - 用于重构请求报文

  - 类图

  - - ​                 ![img](https://docimg9.docs.qq.com/image/qdTscKstpPkEkJMtzsn2kw.png?w=1280&h=668.3834048640915)        

- ResponseHandler

- - 主要功能

  - - 用于对http响应报文进行处理，重点关注对301、302、304等响应码的处理

  - 类图

  - - ​                 ![img](https://docimg9.docs.qq.com/image/qdTscKstpPkEkJMtzsn2kw.png?w=1280&h=668.3834048640915)        

- LastModifiedResourceCache

- - 主要功能

  - - 主要辅助请求报文在请求头里面添加If-Modified-Since

  - 类图

  - - ​                 ![img](https://docimg1.docs.qq.com/image/n0Z1PLSMpU7qiTTR3j9tQQ.png?w=888&h=668)        

- LocalResource

- - 主要功能

  - - 表示存储在本地缓存中的资源

  - 类图

  - - ​                 ![img](https://docimg3.docs.qq.com/image/x26tgOQVF2xywZ2k1MqMqg.png?w=1230&h=668)        

- RedirectResourceCache

- - 主要功能

  - - 存放重定向的资源

  - 类图

  - - ​                 ![img](https://docimg2.docs.qq.com/image/UAREbNxtqk2qlDRYiNAJ_g.png?w=854&h=668)        

- Utils包

- - 主要功能

  - - 开发时的工具包，辅助进行文本的读取

  - 包图

  - - ​                 ![img](https://docimg6.docs.qq.com/image/kGk97mWFIznpxgZ7QrtJNw.png?w=1280&h=732.9460580912863)        

### **运行测试**

#### **普通请求 200**

首先编写测试文件                 ![img](https://docimg9.docs.qq.com/image/k5OfaWPSN1RmBdzLHlBrNw.png?w=1469&h=582)        由于未设定 UA，网站未启用 Cache，故此测试用例结果将保持状态码为 200                 ![img](https://docimg10.docs.qq.com/image/6Oi6-yq4AydjfUYDm79nmg.png?w=2527&h=767)        

#### **301 Moved Permanently**

请求服务器名为 "/movedIndex.html" 文件时，服务器将返回状态码301并告知新的路径                 ![img](https://docimg1.docs.qq.com/image/nOP4WbIuKEZvkRRZC3OCLQ.png?w=1971&h=354)        此时服务器将保存此次重定向结果，下一次请求 "/movedIndex.html" 时直接更改路径

#### **302 Found**

请求服务器名为 "/movedIndex2.html" 文件时，服务器将返回状态码302并告知临时的新路径。此时客户端将重构此次请求，但不会缓存重定向结果。                 ![img](https://docimg2.docs.qq.com/image/sfX_SWTCowW4pAqgv1iABA.png?w=2358&h=451)        

#### **304 Not Modified**

若请求服务器的同一个资源两次，第一次请求完毕后该资源和 Last-Modified 值将被保存至缓存中；第二次请求时发现本地缓存，加上 If Modified Since 字段后请求服务器。若服务器资源未改变，则返回 304 代码以告知客户端直接使用缓存资源。                  ![img](https://docimg2.docs.qq.com/image/bYHRLAbUv298ffXktkMRtg.png?w=2425&h=583)        

#### **内容编码** **gzip** 

支持内容编码 gzip ，可参考第一个测试

#### **MIME**

在 Test302 测试文件中已覆盖超过三种 MIME 支持的测试

##### **text/html**

​                 ![img](https://docimg3.docs.qq.com/image/8LrvXDOm7ZsP5E-WiFiA1A.png?w=1280&h=459.1213114754098)        

##### **text/css**

​                 ![img](https://docimg8.docs.qq.com/image/2TbCPVq-W8vSnsiEtmAQhQ.png?w=1280&h=390.0465735196274)        

##### **image/png**

​                 ![img](https://docimg3.docs.qq.com/image/1cMzSbhJOsf8L8InDBl7uA.png?w=1280&h=467.07284768211923)        

##### **image/jpeg**

​                 ![img](https://docimg2.docs.qq.com/image/El1zNS_X-MWNlNOTk3xU3w.png?w=1280&h=466.51861528412803)        

#### **长连接**

测试的逻辑是前三次请求不使用长连接，后两次请求使用长连接，socket连接只被创建4次，最后一次的连接复用第四次连接创建的socket。

测试代码如下：

​                 ![img](https://docimg5.docs.qq.com/image/9Xkm-ZgA9WV4SP0rnmVaBg.png?w=1280&h=317.3790022338049)        

测试结果如下：

​                 ![img](https://docimg10.docs.qq.com/image/LGnOFNc19c24tr37NKmVeA.jpeg?w=1280&h=930.2228047182176)        

​                 ![img](https://docimg10.docs.qq.com/image/F9cJch_t1C7Qwh8M5uUViQ.jpeg?w=1280&h=530.7521255722694)        

从测试结果可以看到有四次"Create connection",有一次"Reuse connection"



------

## **Http-Server**

### **项目要求**

实现基础的 HTTP 请求、响应功能，具体要求如下：

- HTTP 服务器端支持 GET 和 POST 请求 
- HTTP 服务器端支持 200、301、302、304、404、405、500 的状态码 
- HTTP 服务器端实现长连接
- MIME 至少支持三种类型，包含一种非文本类型 

### **项目简介**

SimpleServer类负责启动主线程ServerHandler以监听请求。监听到连接请求，回调AcceptHandler，AcceptHandler回调RequestHandler处理Http信息。

Http包负责封装请求和响应。

Controller包负责匹配路径并分发指令（如GET静态资源等），其中RequestMapper是仿Springboot控制器风格的uri匹配器，内含的Router包是uri匹配器和路由的具体实现。

Services包负责处理静态资源和业务逻辑，生成http响应。

Common包存放状态码等枚举。StaticResouces文件夹下放了一个演示网页。

### **关键类与包的描述**

- Common包

- - 主要功能

  - - 定义状态码、请求方法、响应模板

  - 包图

  - - ​                 ![img](https://docimg4.docs.qq.com/image/Vh98vUJiQkinqH3UThcuKg.png?w=1276&h=914)        

- RequestMapper

- - 主要功能

  - - 类Springboot控制器风格的注解匹配器，将对应的请求精准匹配到相应的方法

  - 类图

  - - ​                 ![img](https://docimg9.docs.qq.com/image/AtavJhWUs5r71uAH6M9iXg.png?w=802&h=668)        

  - 匹配和路由的实现在Router包内

  - - ​                 ![img](https://docimg7.docs.qq.com/image/8s58MNNfYFAfB5qMMIu1wg.png?w=442&h=244)        

- AcceptHandler

- - 主要功能

  - - 连接成功的处理类，使服务端能够继续监听客户端连接请求，实现异步非阻塞

  - 类图

  - - ​                 ![img](https://docimg5.docs.qq.com/image/v2X4AwUKHRzoVmGRaJ5QOQ.png?w=880&h=668)        

- KeepAliveHandler

- - 主要功能

  - - 用于处理长连接

  - 类图

  - - ​                 ![img](https://docimg7.docs.qq.com/image/7V6G6irHWlWh6uueXdKIfA.png?w=1020&h=668)        

  - 实现：使用TimerTask实现channel的定时关闭，使用HashMap确保每个channel的TimerTask是唯一的、最新的。                 ![img](https://docimg8.docs.qq.com/image/eRYGQo2xOwgrHuXGK0k9AA.png?w=981&h=135)        

- ServerHandler

- - 主要功能

  - - 用于开启服务端线程

  - 类图

  - - ​                 ![img](https://docimg6.docs.qq.com/image/dHPnoD1buShctDUI80LOYg.png?w=826&h=668)        

- RequestHandler

- - 主要功能

  - - 实现channel的写，和字节流向Http报文的解码

  - 类图

  - - ​                 ![img](https://docimg5.docs.qq.com/image/GGSX0T0hSMG67AdDmmN0zg.png?w=280&h=143)        

- Service包

- - 主要功能

  - - 静态资源处理和业务逻辑处理，都实现 HttpResponse handle(HttpRequest request) throws Exception 接口

  - 类图

  - - ​                 ![img](https://docimg6.docs.qq.com/image/HebZO5V5KeF4J-qfPZApEA.png?w=889&h=257)        

  - 关键数据结构

  - - 静态资源处理时，使用HashMap进行MIME，301，302，304的映射                 ![img](https://docimg1.docs.qq.com/image/xQHIs2_KsW40Sqecw73nnw.png?w=830&h=437)                         ![img](https://docimg8.docs.qq.com/image/88lDTA5_JEnzpVJqHZXoNg.png?w=807&h=131)        
    - 注册、登录功能，使用静态HashMap保存用户名密码                 ![img](https://docimg6.docs.qq.com/image/zXjd0pB8QuwDFDy3lk8VmQ.png?w=873&h=38)        

- Http包

- - 主要功能

  - - 封装Http请求和响应，并实现和报文文本的相互转换

  - 其中Components包的类图

  - - ​                 ![img](https://docimg7.docs.qq.com/image/jaqQlO_qUgfiIoXj7sd0gA.png?w=600&h=153)        

### **功能点**

#### **HTTP 服务器端支持 GET 请求，200 状态码**

浏览器GET请求静态资源，服务端呈现请求报文：

​                 ![img](https://docimg1.docs.qq.com/image/6FAymO2DM57ifbAI2cIPqA.png?w=1280&h=565.6608811748998)        

服务器响应200状态码

​                 ![img](https://docimg6.docs.qq.com/image/51LCbrtwOoJOH9VF4-3Xvw.png?w=564&h=449)        

#### **HTTP 服务器端支持 POST 请求**

​                 ![img](https://docimg6.docs.qq.com/image/XMwI7UOqhPycLOiNsieo_w.png?w=1125&h=198)        

​                 ![img](https://docimg4.docs.qq.com/image/TFs3cnDiu0lBe2QwTuQReA.png?w=658&h=345)        

#### **HTTP 服务器端支持 404 状态码**

​                 ![img](https://docimg2.docs.qq.com/image/cFNYWFOoDCgoza67VnJQlw.png?w=1280&h=481.3079019073569)        

​                 ![img](https://docimg2.docs.qq.com/image/CCpJ3_P7jiy_d2ypWc0nQQ.png?w=656&h=342)        

#### **HTTP 服务器端支持 405 状态码**

浏览器地址栏访问 http://localhost:5000/register ，以GET方法请求注册为POST的接口

​                 ![img](https://docimg9.docs.qq.com/image/Cns1_ReflquiZHrvdxWzzA.png?w=695&h=111)        

响应405

​                 ![img](https://docimg4.docs.qq.com/image/0H9MlXStWIe0hdETQr8goQ.png?w=655&h=223)        

​                 ![img](https://docimg5.docs.qq.com/image/KnZBjPw5hBvgCGGV3FXPFQ.png?w=465&h=198)        

#### **HTTP 服务器端支持 500 状态码**

浏览器地址栏访问 http://localhost:5000/error ，服务端内方法抛出异常

​                 ![img](https://docimg4.docs.qq.com/image/6bYGKAOpwWTrxIST712bMg.png?w=668&h=117)        

​                 ![img](https://docimg8.docs.qq.com/image/D6QnIZDPsBVzCxxKHrsr8A.png?w=719&h=225)        

响应500

​                 ![img](https://docimg1.docs.qq.com/image/NInn_yYJkOepiOTU6xuQmA.png?w=654&h=317)        

​                 ![img](https://docimg4.docs.qq.com/image/OLyG_Oul_h3_TTjpDV-OEw.png?w=457&h=243)        

#### **HTTP 服务器端支持 301，302，304 状态码**

已在客户端部分给出详细测试说明。

#### **HTTP 服务器端支持长连接**

已在客户端部分给出详细测试说明。

#### **HTTP 服务器端支持至少三种 MIME 类型**

​                 ![img](https://docimg3.docs.qq.com/image/F4b8TV--m_343q5WvXT-Dg.png?w=1280&h=600.6666666666666)        

​                 ![img](https://docimg6.docs.qq.com/image/bba2cUxb4rCM748VzPaMGw.png?w=539&h=217)        
