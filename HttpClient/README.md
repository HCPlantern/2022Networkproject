## Http-Client

### 项目要求

实现基础的 HTTP 请求、响应功能，具体要求如下：

- HTTP客户端可以发送请求报文、呈现响应报文（命令行形式）

- HTTP客户端对301、302、304的状态码做相应的处理

- MIME 至少支持三种类型，包含一种非文本类型 

### Before Start

本项目使用了`log4j2`记录日志，为保证控制台日志信息**有颜色**，请先设置JVM参数：`-Dlog4j.skipJansi=false`

### 项目简介

- 编写测试文件以发起客户端的 Http 请求
- Client 类使用 Socket 通信

- Componets 内包括 Http 报文的 Header, Body, 以及 Http 请求和 Http 回复报文
- Handler 包括对请求和回复报文的相关类，其中请求时会查找永久重定向与缓存资源，得到回复后处理各种状态码
- LocalCache 内包括对重定向和请求资源的缓存相关类

### 运行测试

#### 普通请求 200

首先编写测试文件

![image-20220613233054328](https://cdn.hcplantern.cn/img/202206132330390.png)

由于未设定 UA，网站未启用 Cache，故此测试用例结果将保持状态码为 200

![image-20220613232925814](https://cdn.hcplantern.cn/img/202206132329906.png)

#### 301 Moved Permanently

请求服务器名为 "/movedIndex.html" 文件时，服务器将返回状态码301并告知新的路径

![image-20220613231616207](https://cdn.hcplantern.cn/img/202206132316259.png)

此时服务器将保存此次重定向结果，下一次请求 "/movedIndex.html" 时直接更改路径

#### 302 Found

请求服务器名为 "/movedIndex2.html" 文件时，服务器将返回状态码302并告知临时的新路径。此时客户端将重构此次请求，但不会缓存重定向结果。

![image-20220613232128035](https://cdn.hcplantern.cn/img/202206132321103.png)

#### 304 Not Modified

若请求服务器的同一个资源两次，第一次请求完毕后该资源和 `Last-Modified` 值将被保存至缓存中；第二次请求时发现本地缓存，加上 `If Modified Since` 字段后请求服务器。若服务器资源未改变，则返回 304 代码以告知客户端直接使用缓存资源。 

![image-20220613232817858](https://cdn.hcplantern.cn/img/202206132328935.png)

#### gzip

支持 `gzip` 格式，可参考第一个测试