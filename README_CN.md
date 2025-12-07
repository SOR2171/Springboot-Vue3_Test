## Springboot-Vue3

###### ~~*赞美学生包*~~

[English](./README.md) / 简体中文

---

### 技术栈

#### 后端

- Kotlin + Gradle.kts
    - Spring Security
    - Spring Mail
    - Mybatis Plus
    - JWT
    - MySQL
    - Redis
    - RabbitMQ

#### 前端

- Vue3 + Vite + Javascript
    - vue-router
    - axios
    - element-plus

---

### 项目搭建

#### 基础要求

- JDK 17+
- Kotlin 1.6+
- Node.js 16+
- MySQL 8+
- Redis 6+
- RabbitMQ 3.8+

#### Git 克隆项目

```bash
git clone https://github.com/SOR2171/Springboot-Vue3_Test.git
cd Springboot-Vue3_Test
```

#### 后端配置

1. 进入后端目录

```bash
cd backend
```

2. 配置应用属性

创建 `src/main/resources/application.yml`
或编辑 `src/main/resources/application-example.yml`
以设置你自己的配置。

3. 导入数据库

导入 `src/main/resources/springboot_vue3.sql`

> 默认数据库用户：
>
> 用户名：`test`
> 密码：`123456`

4. Gradle 构建

```bash
gradle build
```

jar 文件目录：`build/libs/backend-(ver).jar`

5. 启动后端服务器

```bash
java -jar build/libs/backend-(ver).jar
```

---

#### 前端配置

1. 进入前端目录

```bash
cd frontend
```

2.  准备一些图片

你需要一张 `frontend/src/assets/welcome-image.png` 用作登陆界面的背景

3. 构建前端

```bash
npm install
npm run build
```

构建结果会生成在 `dist` 目录中。

---

#### Nginx 部署（可选）

如果你想使用 Nginx 来部署前端，可以按以下步骤操作：

1. 若未安装 Nginx，请先安装。

   [下载 Nginx](https://nginx.org/en/download.html)

2. 将 `dist` 目录中的内容复制到 Nginx 的 `html` 目录。

3. 配置 Nginx，使其能够提供前端页面，并将 API 请求代理到后端服务器。

4. 重启 Nginx。

```bash
sudo systemctl restart nginx
```
