<img src="D:\chrome\鱼聪明AI绘画 (2).jpg" alt="鱼聪明AI绘画 (2)" style="zoom: 25%;" />

#                                                                                                       智汇聚合搜索平台

### 项目导航

##### 前端仓库地址：

### 项目介绍

智汇搜索主要是一个搜索中台项目，既可以用来当作企业中的内部搜索中台，又可以成为类似Bilibli的搜索中台，用户可以通过一键式的搜索，搜索到不同的内容。在我们的智汇搜索中，用户可以输入一个关键词，搜索出 **全部**，**帖子**，**图片**，**用户**，**视频** 等多种数据。当我们的平台应用到企业内部时，可以当作企业内部的搜索引擎，搜索企业内部的各种信息。

### 项目演示

#### 热搜榜（根据用户搜索实时更新当下最热搜索）

![image-20240104183759116](C:\Users\86185\AppData\Roaming\Typora\typora-user-images\image-20240104183759116.png)

搜索结果：

![image-20240104183829171](C:\Users\86185\AppData\Roaming\Typora\typora-user-images\image-20240104183829171.png)

#### 自定义搜索（用户自定义搜索内容进行搜索）

比如：搜索 **哈尔滨旅游**

**全部模块内容**

![image-20240104184037577](C:\Users\86185\AppData\Roaming\Typora\typora-user-images\image-20240104184037577.png)

**帖子模块内容**

![image-20240104184354568](C:\Users\86185\AppData\Roaming\Typora\typora-user-images\image-20240104184354568.png)

**图片模块内容**

![image-20240104184502223](C:\Users\86185\AppData\Roaming\Typora\typora-user-images\image-20240104184502223.png)

**视频模块内容**

![image-20240104184537744](C:\Users\86185\AppData\Roaming\Typora\typora-user-images\image-20240104184537744.png)

### 项目技术选型：

#### 后端技术

| 技术          | 说明                                              | 官网                                                         |
| ------------- | ------------------------------------------------- | ------------------------------------------------------------ |
| SpringBoot    | Java主流后端开发框架                              | [ https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot) |
| MyBatis       | ORM框架                                           | http://www.mybatis.org/mybatis-3/zh/index.html               |
| MyBatisPlus   | 零sql，简化数据库操作，分页插件                   | https://baomidou.com/                                        |
| Redis         | 缓存加速，多数据结构支持业务功能                  | [ https://redis.io](https://redis.io/)                       |
| Nginx         | 负载均衡，https配置，websocket升级，ip频控        | [ https://nginx.org](https://nginx.org/)                     |
| Elasticsearch | 高性能分词搜索框架                                | https://www.elastic.co                                       |
| Canal         | 阿里巴巴开源数据同步框架                          | https://github.com/alibaba/canal                             |
| Kibana        | Elasticsearch数据可视化面板，ElasticStack家族系列 | https://www.elastic.co                                       |
| Swagger-UI    | API文档生成工具                                   | [ https://github.com/swagger-api/swagger-ui](https://github.com/swagger-api/swagger-ui) |
| Lombok        | 简化代码                                          | [ https://projectlombok.org](https://projectlombok.org/)     |
| Hutool        | Java工具类库                                      | https://github.com/looly/hutool                              |

#### 前端技术

| 技术       | 说明                      | 官网                                                  |
| ---------- | ------------------------- | ----------------------------------------------------- |
| Vue3       | 前端流行开发框架          | [https://cn.vuejs.org](https://cn.vuejs.org/)         |
| Pinia      | vue3 官方推荐状态管理框架 | [https://pinia.vuejs.org](https://pinia.vuejs.org/)   |
| vue-router | Vue 的官方路由            | [https://router.vuejs.org](https://router.vuejs.org/) |
| vite       | 极速的前端打包构建工具    | [ https://cn.vitejs.dev](https://cn.vitejs.dev/)      |
| Ant Design | 阿里巴巴开源前端组件库    | https://ant .desgin                                   |