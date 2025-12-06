## Springboot-Vue3_Webflux

###### ~~*Long may the Student Pack*~~
 
English / [简体中文](./README_CN.md)

---

### Tech Stack

#### Backend

- Kotlin with Gradle.kts
    - Spring Security
    - Spring Mail
    - Mybatis Plus
    - JWT
    - MySQL
    - Redis
    - RabbitMQ

#### Frontend

- Vue3 with Vite and Javascript
    - vue-router
    - axios
    - element-plus

---

#### Project Setup

##### General Requirements

- JDK 17+
- Kotlin 1.6+
- Node.js 16+
- MySQL 8+
- Redis 6+
- RabbitMQ 3.8+

##### Git clone

```bash
git clone https://github.com/SOR2171/Springboot-Vue3_Test.git
cd Springboot-Vue3_Test
```

##### Backend Setup

1. Go to backend directory

```bash
cd backend
```

2. Configure application properties

Crate `src/main/resources/application.yml`
or edit `src/main/resources/application-example.yml`
to set your own configurations.

3. Load Database

Import `src/main/resources/springboot_vue3.sql`
> default database user:
>
> username: `test`,
> password: `123456`

4. Grade build

```bash
gradle build
```

jar directory: `build/libs/backend-(ver).jar`

5. Run the backend server

```bash
java -jar build/libs/backend-(ver).jar
```

##### Frontend Setup

1. Go to frontend directory

```bash
cd frontend
```

2. Build

```bash
npm install
npm run build
```

The build files will be generated in `dist` directory.

##### Nginx Setup (Optional)

If you want to serve the frontend with Nginx, you can follow these steps:

1. Install Nginx if you don't have it.

   [download Nginx](https://nginx.org/en/download.html)

2. Copy the contents of the `dist` directory to your Nginx `html` directory.
3. Configure Nginx to serve the frontend and proxy API requests to the backend server.
4. Restart Nginx.

```bash
sudo systemctl restart nginx
```
