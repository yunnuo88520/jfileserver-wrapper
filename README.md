# jfileserver-wrapper

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/projects/jdk/17/)
[![jFileServer](https://img.shields.io/badge/jFileServer-1.4.0-blue.svg)](https://github.com/hierynomus/smbj)

一个基于 Spring Boot 的 jFileServer 封装服务，提供开箱即用的 SMB/CIFS 文件共享解决方案。通过 REST API 即可轻松管理和控制文件服务器，无需复杂的命令行操作。

## 📋 目录

- [项目背景](#项目背景)
- [核心功能](#核心功能)
- [技术架构](#技术架构)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [客户端挂载](#客户端挂载)
- [API 接口](#api-接口)
- [常见问题](#常见问题)
- [开发指南](#开发指南)

## 🎯 项目背景

SMB/CIFS 协议是企业环境中广泛使用的文件共享协议，但传统配置方式复杂且不够灵活。jfileserver-wrapper 项目旨在：

- **简化部署**：通过 Spring Boot 自动化配置，减少手动配置工作量
- **统一管理**：提供 RESTful API 接口，便于集成到现有系统
- **跨平台支持**：基于 Java 实现，支持 Windows、Linux、macOS 等多种平台
- **灵活控制**：支持运行时动态启动、停止、重启服务器
- **生产就绪**：包含完整的线程管理、资源清理和错误处理机制

本项目适用于以下场景：

- 企业内部文件共享服务器
- 开发测试环境的文件服务
- 需要编程方式控制的 SMB 服务
- 跨平台文件共享解决方案

## ✨ 核心功能

### 服务器管理
- ✅ **一键启动**：应用启动时自动启动 SMB 服务器
- ✅ **REST API 控制**：通过 HTTP 接口控制服务器的启动、停止、重启
- ✅ **状态监控**：实时查看服务器运行状态和配置信息
- ✅ **健康检查**：提供服务健康检查接口

### SMB/CIFS 支持
- ✅ **多协议支持**：支持 SMB 1.0、SMB 2.0 等多种协议版本
- ✅ **跨平台兼容**：禁用 NetBIOS，使用纯 TCP/IP，确保跨平台兼容性
- ✅ **NTLM 认证**：支持 NTLMv1/v2 认证机制
- ✅ **用户权限管理**：可配置用户名、密码和域信息

### 性能与安全
- ✅ **线程池管理**：可配置的线程池大小，支持并发连接
- ✅ **资源管理**：完善的资源清理和优雅关闭机制
- ✅ **密码加密**：使用 BouncyCastle 进行 MD4 密码哈希
- ✅ **日志管理**：支持灵活的日志配置，可输出到控制台或文件
- ✅ **连接稳定**：可配置禁用会话超时，避免长时间操作中断

## 🏗️ 技术架构

```
┌─────────────────────────────────────────────────────┐
│              Client (SMB/CIFS)                      │
│         Windows/Linux/macOS/Network Devices         │
└──────────────────┬──────────────────────────────────┘
                   │ SMB Protocol (Port 47531)
                   ▼
┌─────────────────────────────────────────────────────┐
│         jfileserver-wrapper Application             │
│  ┌──────────────────────────────────────────────┐  │
│  │      Spring Boot Web Server (Port 8088)       │  │
│  │  ┌────────────────────────────────────────┐  │  │
│  │  │  REST API Controller                   │  │  │
│  │  │  - Start/Stop/Restart                 │  │  │
│  │  │  - Status/Config/Health               │  │  │
│  │  └────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────┐  │
│  │         JFileServer Service                  │  │
│  │  - Server Lifecycle Management              │  │
│  │  - Configuration Management                 │  │
│  │  - Thread Pool Management                  │  │
│  └──────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────┐  │
│  │        jFileServer Core (1.4.0)              │  │
│  │  - SMB Protocol Implementation               │  │
│  │  - NTLM Authentication                      │  │
│  │  - Java NIO Disk Driver                     │  │
│  └──────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────┐  │
│  │        File System                          │  │
│  │   Shared: ./jfileserver/test/               │  │
│  └──────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

### 技术栈

- **框架**: Spring Boot 3.5.10
- **核心库**: jFileServer 1.4.0
- **JDK**: Java 17
- **加密库**: BouncyCastle 1.70
- **构建工具**: Maven
- **语言**: Java 17

## 📦 环境要求

- **JDK**: 17 或更高版本
- **Maven**: 3.6+ （用于构建）
- **操作系统**:
  - Linux (推荐)
  - macOS
  - Windows
- **内存**: 至少 512MB 可用内存
- **网络**: 确保配置的端口（默认 47531）未被占用

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/yunnuo88520/jfileserver-wrapper.git
cd jfileserver-wrapper
```

### 2. 确认依赖

确保 `lib/jfileserver-1.4.0.jar` 文件存在。如果缺失，需要：

```bash
mkdir -p lib
# 将 jfileserver-1.4.0.jar 复制到 lib 目录
```

### 3. 构建项目

```bash
mvn clean package
```

### 4. 运行服务

```bash
java -jar target/jfileserver-wrapper-1.0.0.jar
```

或者使用 Maven 直接运行：

```bash
mvn spring-boot:run
```

### 5. 验证服务

**检查服务状态**：
```bash
curl http://localhost:8088/api/jfileserver/status
```

**预期响应**：
```json
{
  "running": true,
  "port": 47531,
  "shareName": "JFILESHARE",
  "sharePath": "./jfileserver/test"
}
```

## ⚙️ 配置说明

### 配置文件位置

配置文件位于：`src/main/resources/application.yml`

### 基础配置项

```yaml
server:
  port: 8088  # Spring Boot Web 服务端口

jfileserver:
  # 自动启动开关（默认：true）
  auto-start: true

  # SMB 服务端口（默认：47531）
  port: 47531

  # 共享目录路径
  # 支持绝对路径和相对路径
  # 如果目录不存在，程序会自动创建
  share-path: ./jfileserver/test

  # 共享名称（客户端挂载时显示的名称）
  share-name: JFILESHARE

  # 服务器名称（在网络中显示的名称）
  server-name: JFILESERVER

  # 工作组或域名
  domain: FILESRV

  # 认证用户名
  username: admin

  # 认证密码
  password: jfilesrv

  # 最小线程数（默认：5）
  min-threads: 5

  # 最大线程数（默认：100）
  max-threads: 100

  # Socket 超时时间（毫秒）
  # 0 或负数表示禁用超时，连接不会被自动关闭
  # 推荐设置为 0，原因：
  #   1. 禁用后 IdleSessionReaper（空闲会话清理器）不会启动
  #   2. 连接将保持稳定，不会因为空闲被自动关闭
  #   3. 避免长时间操作（如挂载ISO安装系统、大文件传输）时连接断开
  # 如果设置为正值（如 900000 = 15分钟）：
  #   - 空闲会话将在 timeout/2 时间后被自动清理
  #   - 例如：设置 900000，空闲 7.5 分钟后连接会被关闭
  socket-timeout: 0

  # jFileServer 日志文件路径
  # 支持相对路径和绝对路径
  # - 如果为 null 或空字符串，则输出到控制台
  # - 相对路径相对于项目根目录
  # 示例：
  #   - logs/jfileserver.log（相对路径，推荐）
  #   - /var/log/jfileserver/jfileserver.log（绝对路径）
  #   - ""（空字符串，输出到控制台）
  log-file-path: logs/jfileserver.log

  # 日志是否追加
  # true(默认) 表示追加模式，false 表示覆盖模式
  # - true: 每次启动日志追加到文件末尾，保留历史日志
  # - false: 每次启动覆盖原有日志文件
  log-append: true
```

### 高级配置场景

#### 场景 1：多实例部署

如果需要运行多个 SMB 服务器实例：

```yaml
# 实例 1 配置
server:
  port: 8088
jfileserver:
  port: 47531
  share-name: SHARE1
  share-path: /data/share1

# 实例 2 配置（需要修改端口）
# server:
#   port: 8089
# jfileserver:
#   port: 47532
#   share-name: SHARE2
#   share-path: /data/share2
```

#### 场景 2：生产环境配置

```yaml
jfileserver:
  auto-start: true
  port: 445  # 使用标准 SMB 端口（需要 root 权限）
  share-path: /srv/smb/shared
  server-name: PROD-FILESERVER
  domain: CORP
  min-threads: 10
  max-threads: 200
```

#### 场景 3：开发测试环境

```yaml
jfileserver:
  auto-start: false  # 手动控制启动
  port: 47531
  share-path: ./dev-test-share
  username: dev
  password: dev123
  min-threads: 2
  max-threads: 10
```

#### 场景 4：日志配置

**控制台输出日志**（开发调试）：
```yaml
jfileserver:
  log-file-path: ""  # 空字符串，日志输出到控制台
```

**文件输出日志**（生产环境推荐）：
```yaml
jfileserver:
  log-file-path: logs/jfileserver.log  # 相对路径
  log-append: true  # 追加模式，保留历史日志
```

**每次启动清空日志**（每次重启都需要全新日志）：
```yaml
jfileserver:
  log-file-path: /var/log/jfileserver/jfileserver.log  # 绝对路径
  log-append: false  # 覆盖模式
```

**生产环境日志配置**（配合 logrotate）：
```yaml
jfileserver:
  log-file-path: /var/log/jfileserver/jfileserver.log
  log-append: true  # 追加模式，由 logrotate 负责日志轮转
  socket-timeout: 0  # 禁用超时，保持连接稳定
```

### 配置优先级

配置参数的优先级从高到低：

1. 命令行参数
2. 环境变量
3. application.yml 配置文件
4. 默认值

**使用环境变量示例**：

```bash
export JFILESERVER_PORT=445
export JFILESERVER_SHARE_PATH=/data/shared
java -jar jfileserver-wrapper.jar
```

## 🖥️ 客户端挂载

### Linux/macOS 挂载

#### 使用 mount 命令

**基础挂载**：
```bash
# 创建挂载点
sudo mkdir -p /mnt/jfileserver

# 挂载 SMB 共享
sudo mount -t cifs //10.2.44.113/JFILESHARE /mnt/jfileserver \
  -o port=47531,username=admin,password=jfilesrv
```

**完整挂载示例**（推荐）：
```bash
sudo mount -t cifs //10.2.44.113/JFILESHARE /mnt/jfileserver \
  -o port=47531, \
  -o username=admin, \
  -o password=jfilesrv, \
  -o domain=FILESRV, \
  -o vers=2.0, \
  -o rw, \
  -o file_mode=0755, \
  -o dir_mode=0755
```

**详细参数说明**：
- `port=47531`: SMB 服务端口
- `username=admin`: 认证用户名
- `password=jfilesrv`: 认证密码
- `domain=FILESRV`: 工作组或域名
- `vers=2.0`: 使用 SMB 2.0 协议
- `rw`: 读写模式
- `file_mode=0755`: 文件权限
- `dir_mode=0755`: 目录权限

#### 使用 fstab 自动挂载

编辑 `/etc/fstab` 文件：

```bash
//10.2.44.113/JFILESHARE /mnt/jfileserver cifs
  port=47531,
  username=admin,
  password=jfilesrv,
  domain=FILESRV,
  vers=2.0,
  rw,
  file_mode=0755,
  dir_mode=0755,
  _netdev 0 0
```

然后执行：
```bash
sudo mount -a
```

#### 使用 smbclient 测试连接

```bash
# 安装 smbclient
sudo apt-get install smbclient  # Ubuntu/Debian
sudo yum install samba-client   # CentOS/RHEL

# 连接测试
smbclient //10.2.44.113/JFILESHARE \
  -p 47531 \
  -U admin%jfilesrv \
  -W FILESRV

# 常用命令
# ls: 列出文件
# get <filename>: 下载文件
# put <filename>: 上传文件
# exit: 退出
```

### Windows 挂载

#### 方法 1：使用资源管理器

1. 打开"此电脑"或"文件资源管理器"
2. 在地址栏输入：`\\10.2.44.113\JFILESHARE`
3. 在弹出的对话框中输入：
   - 用户名：`admin`
   - 密码：`jfilesrv`
4. 勾选"记住我的凭据"
5. 点击"确定"

#### 方法 2：使用 net use 命令

```cmd
# 查看现有连接
net use

# 映射网络驱动器
net use Z: \\10.2.44.113\JFILESHARE /user:admin jfilesrv

# 删除映射
net use Z: /delete

# 使用指定端口（需要先配置端口转发）
net use Z: \\10.2.44.113\JFILESHARE /user:admin jfilesrv
```

#### 方法 3：使用 PowerShell

```powershell
# 创建凭据对象
$cred = New-Object System.Management.Automation.PSCredential(
    "admin",
    (ConvertTo-SecureString "jfilesrv" -AsPlainText -Force)
)

# 映射网络驱动器
New-PSDrive -Name Z -PSProvider FileSystem -Root "\\10.2.44.113\JFILESHARE" -Credential $cred

# 移除映射
Remove-PSDrive -Name Z
```

### 常见挂载问题排查

#### 问题 1：Connection refused

**原因**：服务器未启动或端口错误

**解决**：
```bash
# 检查服务器状态
curl http://localhost:8088/api/jfileserver/status

# 检查端口是否开放
netstat -tuln | grep 47531  # Linux
lsof -i :47531              # macOS
```

#### 问题 2：Authentication failed

**原因**：用户名或密码错误

**解决**：
```bash
# 检查配置
curl http://localhost:8088/api/jfileserver/config

# 确认认证信息
smbclient //10.2.44.113/JFILESHARE -p 47531 -U admin%jfilesrv
```

#### 问题 3：Permission denied

**原因**：共享目录权限不足

**解决**：
```bash
# 检查目录权限
ls -la ./jfileserver/test

# 修改权限
chmod 755 ./jfileserver/test
chown -R $USER:$USER ./jfileserver/test
```

#### 问题 4：Host is down

**原因**：协议版本不匹配或网络问题

**解决**：
```bash
# 尝试指定协议版本
sudo mount -t cifs //10.2.44.113/JFILESHARE /mnt/jfileserver \
  -o port=47531,username=admin,password=jfilesrv,vers=1.0

# 或使用 vers=2.0
sudo mount -t cifs //10.2.44.113/JFILESHARE /mnt/jfileserver \
  -o port=47531,username=admin,password=jfilesrv,vers=2.0
```

## 🔌 API 接口

### 基础 URL

```
http://localhost:8088/api/jfileserver
```

### 接口列表

#### 1. 启动服务器

**请求**：
```http
POST /api/jfileserver/start
```

**响应示例**：
```json
{
  "success": true,
  "message": "jFileServer started successfully on port 47531"
}
```

**Curl 示例**：
```bash
curl -X POST http://localhost:8088/api/jfileserver/start
```

#### 2. 停止服务器

**请求**：
```http
POST /api/jfileserver/stop
```

**响应示例**：
```json
{
  "success": true,
  "message": "jFileServer stopped successfully"
}
```

**Curl 示例**：
```bash
curl -X POST http://localhost:8088/api/jfileserver/stop
```

#### 3. 重启服务器

**请求**：
```http
POST /api/jfileserver/restart
```

**响应示例**：
```json
{
  "success": true,
  "message": "jFileServer restarted successfully"
}
```

**Curl 示例**：
```bash
curl -X POST http://localhost:8088/api/jfileserver/restart
```

#### 4. 查询服务器状态

**请求**：
```http
GET /api/jfileserver/status
```

**响应示例**：
```json
{
  "running": true,
  "port": 47531,
  "shareName": "JFILESHARE",
  "sharePath": "./jfileserver/test",
  "serverName": "JFILESERVER",
  "domain": "FILESRV"
}
```

**Curl 示例**：
```bash
curl http://localhost:8088/api/jfileserver/status
```

#### 5. 查询配置信息

**请求**：
```http
GET /api/jfileserver/config
```

**响应示例**：
```json
{
  "port": 47531,
  "sharePath": "./jfileserver/test",
  "shareName": "JFILESHARE",
  "serverName": "JFILESERVER",
  "domain": "FILESRV",
  "username": "admin",
  "minThreads": 5,
  "maxThreads": 100,
  "autoStart": true
}
```

**Curl 示例**：
```bash
curl http://localhost:8088/api/jfileserver/config
```

#### 6. 健康检查

**请求**：
```http
GET /api/jfileserver/health
```

**响应示例**：
```json
{
  "status": "UP",
  "serverRunning": true
}
```

**Curl 示例**：
```bash
curl http://localhost:8088/api/jfileserver/health
```

### API 使用示例

#### Bash 脚本示例

```bash
#!/bin/bash

BASE_URL="http://localhost:8088/api/jfileserver"

# 启动服务器
echo "Starting jFileServer..."
curl -X POST "$BASE_URL/start"

# 等待启动
sleep 3

# 检查状态
echo "Checking status..."
curl "$BASE_URL/status"

# 挂载共享
echo "Mounting share..."
sudo mkdir -p /mnt/jfileserver
sudo mount -t cifs //10.2.44.113/JFILESHARE /mnt/jfileserver \
  -o port=47531,username=admin,password=jfilesrv

# 查看挂载
df -h | grep jfileserver
```

#### Python 示例

```python
import requests

BASE_URL = "http://localhost:8088/api/jfileserver"

# 启动服务器
response = requests.post(f"{BASE_URL}/start")
print(response.json())

# 检查状态
response = requests.get(f"{BASE_URL}/status")
status = response.json()
print(f"Server running: {status['running']}")
print(f"Port: {status['port']}")
```

#### Java 示例

```java
import org.springframework.web.client.RestTemplate;

RestTemplate restTemplate = new RestTemplate();
String baseUrl = "http://localhost:8088/api/jfileserver";

// 启动服务器
String startResult = restTemplate.postForObject(baseUrl + "/start", null, String.class);
System.out.println(startResult);

// 获取状态
Map<String, Object> status = restTemplate.getForObject(baseUrl + "/status", Map.class);
System.out.println("Server running: " + status.get("running"));
```

## 🔧 常见问题

### Q1: 为什么连接时提示 "Connection refused"?

**可能原因**：
1. jFileServer 未启动
2. 端口号配置错误
3. 防火墙阻止了连接

**解决方案**：
```bash
# 1. 检查服务器状态
curl http://localhost:8088/api/jfileserver/status

# 2. 检查端口监听
netstat -tuln | grep 47531

# 3. 检查防火墙（Linux）
sudo ufw status
sudo ufw allow 47531/tcp

# 4. 手动启动服务器
curl -X POST http://localhost:8088/api/jfileserver/start
```

### Q2: 为什么认证失败？

**可能原因**：
1. 用户名或密码错误
2. 配置文件中的认证信息不一致
3. 使用了不支持的认证方式

**解决方案**：
```bash
# 1. 检查当前配置
curl http://localhost:8088/api/jfileserver/config

# 2. 确认 application.yml 中的用户名密码
cat src/main/resources/application.yml | grep -A 2 "username:"

# 3. 使用 smbclient 测试
smbclient //10.2.44.113/JFILESHARE -p 47531 -U admin%jfilesrv
```

### Q3: 如何修改共享目录？

**方法 1：修改配置文件**

编辑 `application.yml`：
```yaml
jfileserver:
  share-path: /your/new/share/path
```

**方法 2：使用环境变量**
```bash
export JFILESERVER_SHARE_PATH=/your/new/share/path
java -jar jfileserver-wrapper.jar
```

**方法 3：重启服务**
```bash
# 1. 停止服务
curl -X POST http://localhost:8088/api/jfileserver/stop

# 2. 修改配置
# 编辑 application.yml

# 3. 重启应用
java -jar jfileserver-wrapper.jar
```

### Q4: 如何查看日志？

本项目有两种日志：

#### 1. Spring Boot 应用日志

包括 jfileserver-wrapper 项目的日志，如启动信息、API 请求等。

**配置文件日志**：
```yaml
logging:
  level:
    vip.ebox.jfiledemo: DEBUG
  file:
    name: logs/jfileserver-wrapper.log
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

#### 2. jFileServer 内部日志

包括 SMB 协议日志、连接日志、认证日志等，以 `[SMB]` 开头。

**配置 jFileServer 日志**：
```yaml
jfileserver:
  # 日志文件路径
  log-file-path: logs/jfileserver.log

  # 是否追加模式
  log-append: true  # true=追加, false=覆盖
```

**查看 jFileServer 日志**：
```bash
# 实时查看日志
tail -f logs/jfileserver.log

# 查看最后 100 行
tail -n 100 logs/jfileserver.log

# 搜索特定内容
grep "Session" logs/jfileserver.log
```

**输出到控制台**：
```yaml
jfileserver:
  log-file-path: ""  # 空字符串，输出到控制台
```

### Q5: 如何更改 SMB 端口？

**方法 1：修改配置文件**

编辑 `application.yml`：
```yaml
jfileserver:
  port: 445  # 使用标准 SMB 端口
```

**注意**：使用 445 端口需要 root 权限

**方法 2：使用环境变量**
```bash
export JFILESERVER_PORT=445
sudo java -jar jfileserver-wrapper.jar  # 需要 sudo
```

### Q6: 支持多用户吗？

当前版本仅支持单用户配置。如需多用户支持，需要：

1. **扩展方案 1**：运行多个实例，每个实例不同端口和用户
2. **扩展方案 2**：修改代码实现用户管理器

### Q7: 性能如何优化？

**线程池调优**：
```yaml
jfileserver:
  min-threads: 10   # 根据并发连接数调整
  max-threads: 200  # 根据服务器资源调整
```

**JVM 参数优化**：
```bash
java -Xms512m -Xmx2g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar jfileserver-wrapper.jar
```

**系统资源监控**：
```bash
# 监控 Java 进程
jps -l | grep jfileserver-wrapper
jstat -gcutil <pid> 1000

# 监控网络连接
netstat -an | grep 47531 | wc -l
```

### Q8: 如何进行故障排查？

**1. 检查服务状态**
```bash
curl http://localhost:8088/api/jfileserver/health
curl http://localhost:8088/api/jfileserver/status
```

**2. 查看详细日志**
```bash
# 启动时添加调试参数
java -Dlogging.level.vip.ebox.jfiledemo=DEBUG \
     -jar jfileserver-wrapper.jar

# 查看 jFileServer 日志
tail -f logs/jfileserver.log

# 搜索错误信息
grep -i "error\|exception" logs/jfileserver.log
```

**3. 测试网络连接**
```bash
# 检查端口
telnet 10.2.44.113 47531

# 测试 SMB 连接
smbclient -L //10.2.44.113 -p 47531 -U admin%jfilesrv
```

**4. 检查系统资源**
```bash
# CPU 和内存
top -p $(pgrep -f jfileserver-wrapper)

# 文件描述符
lsof -p $(pgrep -f jfileserver-wrapper) | wc -l

# 磁盘空间
df -h ./jfileserver/test
```

## 📚 开发指南

### 项目结构

```
jfileserver-wrapper/
├── src/
│   ├── main/
│   │   ├── java/vip/ebox/jfiledemo/
│   │   │   ├── JFileWrapperApplication.java          # 主程序入口
│   │   │   ├── controller/
│   │   │   │   └── JFileServerController.java       # REST API 控制器
│   │   │   ├── service/
│   │   │   │   └── JFileServerService.java          # 核心服务类
│   │   │   ├── config/
│   │   │   │   └── JFileServerProperties.java       # 配置属性类
│   │   │   └── listener/
│   │   │       └── JFileServerStartupListener.java  # 启动监听器
│   │   └── resources/
│   │       └── application.yml                       # 配置文件
│   └── test/
│       └── java/vip/ebox/jfiledemo/
│           └── JfileDemoApplicationTests.java       # 单元测试
├── lib/
│   └── jfileserver-1.4.0.jar                        # jFileServer 核心库
├── share/
│   ├── test.txt                                     # 测试文件
│   └── test2.txt
├── pom.xml                                          # Maven 配置
├── .gitignore
└── README.md
```

### 核心类说明

#### 1. JFileWrapperApplication

Spring Boot 应用入口类。

```java
@SpringBootApplication
public class JFileWrapperApplication {
    public static void main(String[] args) {
        SpringApplication.run(JFileWrapperApplication.class, args);
    }
}
```

#### 2. JFileServerService

核心服务类，负责 jFileServer 的生命周期管理。

**主要方法**：
- `start()`: 启动 SMB 服务器
- `stop()`: 停止 SMB 服务器
- `restart()`: 重启 SMB 服务器
- `isRunning()`: 检查运行状态
- `getStatus()`: 获取服务器状态信息

#### 3. JFileServerController

REST API 控制器，提供 HTTP 接口。

**接口端点**：
- `POST /api/jfileserver/start`: 启动服务器
- `POST /api/jfileserver/stop`: 停止服务器
- `POST /api/jfileserver/restart`: 重启服务器
- `GET /api/jfileserver/status`: 获取状态
- `GET /api/jfileserver/config`: 获取配置
- `GET /api/jfileserver/health`: 健康检查

#### 4. JFileServerProperties

配置属性类，绑定 `application.yml` 中的配置。

**配置映射**：
```java
@ConfigurationProperties(prefix = "jfileserver")
public class JFileServerProperties {
    private boolean autoStart = true;
    private int port = 47531;
    private String sharePath = "./jfileserver/test";
    // ... 其他配置项
}
```

### 添加新功能示例

#### 示例：添加用户管理功能

**步骤 1**：创建 `UserManager` 类

```java
@Component
public class UserManager {
    private Map<String, String> users = new HashMap<>();

    public void addUser(String username, String password) {
        users.put(username, password);
    }

    public boolean authenticate(String username, String password) {
        return users.containsKey(username) &&
               users.get(username).equals(password);
    }
}
```

**步骤 2**：在 `JFileServerService` 中注入使用

```java
@Service
public class JFileServerService {
    @Autowired
    private UserManager userManager;

    public void configureUsers() {
        // 配置多个用户
        userManager.addUser("admin", "admin123");
        userManager.addUser("guest", "guest123");
    }
}
```

#### 示例：添加监控指标

**步骤 1**：添加依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**步骤 2**：配置监控

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=JfileDemoApplicationTests

# 运行并生成覆盖率报告
mvn test jacoco:report
```

### 打包部署

**打包**：
```bash
mvn clean package
```

**运行**：
```bash
java -jar target/jfileserver-wrapper-1.0.0.jar
```

**后台运行**：
```bash
nohup java -jar target/jfileserver-wrapper-1.0.0.jar > app.log 2>&1 &
```

**创建系统服务（systemd）**：

创建 `/etc/systemd/system/jfileserver.service`：

```ini
[Unit]
Description=jfileserver-wrapper Service
After=network.target

[Service]
Type=simple
User=your-user
WorkingDirectory=/opt/jfileserver-wrapper
ExecStart=/usr/bin/java -jar /opt/jfileserver-wrapper/jfileserver-wrapper-1.0.0.jar
Restart=on-failure
RestartSec=10

# 环境变量 - 可选：配置日志路径
Environment="JFILESERVER_LOGFILE_PATH=/var/log/jfileserver/jfileserver.log"
Environment="JFILESERVER_LOGAPPEND=true"

[Install]
WantedBy=multi-user.target
```

**配置 logrotate**（推荐生产环境）：

创建 `/etc/logrotate.d/jfileserver`：

```
/var/log/jfileserver/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 0640 your-user your-group
    sharedscripts
    postrotate
        systemctl reload jfileserver > /dev/null 2>&1 || true
    endscript
}
```

启用服务：
```bash
sudo systemctl daemon-reload
sudo systemctl enable jfileserver
sudo systemctl start jfileserver
sudo systemctl status jfileserver

# 查看 jFileServer 日志
sudo tail -f /var/log/jfileserver/jfileserver.log
```

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

**开发流程**：
1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

**代码规范**：
- 遵循 Java 代码规范
- 添加适当的注释和文档
- 编写单元测试
- 确保 `mvn test` 通过

## 📄 许可证

本项目采用 MIT 许可证 - 详见 LICENSE 文件

## 📞 联系方式

- 项目主页：[https://blog.ebox.vip]
- 问题反馈：[Issues]
- 邮箱：[ymz@ebox.vip]

## 🙏 致谢

- [jFileServer](https://github.com/FileSysOrg/jfileserver) - 提供了强大的 SMB/CIFS 协议实现
- [Spring Boot](https://spring.io/projects/spring-boot) - 简化了应用开发和部署
- 所有贡献者 - 感谢对本项目的贡献

---

**如果这个项目对你有帮助，请给个 ⭐️ Star！**
