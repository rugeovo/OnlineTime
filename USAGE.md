## 目录

1. [安装指南](#安装指南)
2. [配置说明](#配置说明)
3. [占位符使用](#占位符使用)
4. [群组服配置](#群组服配置)
5. [常见问题](#常见问题)
6. [故障排查](#故障排查)

---

## 安装指南

### 1.1 前置要求

**必需：**
- Minecraft 服务端：Bukkit / Spigot / Paper (1.12.2 - 1.21.4)
- Java 版本：Java 8 或更高
- PlaceholderAPI：[下载链接](https://www.spigotmc.org/resources/placeholderapi.6245/)
- 理论支持全核心服务端（未测试）

**推荐（用于数据持久化）：**
- MySQL 5.7+

### 1.2 安装步骤

#### 步骤 1：下载前置插件
1. 下载 [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
2. 将 `PlaceholderAPI.jar` 放入 `plugins/` 目录

#### 步骤 2：安装 OnlineTime
1. 下载 `OnlineTime.jar`
2. 将插件放入 `plugins/` 目录
3. 启动服务器

#### 步骤 3：配置数据库
1. 关闭服务器
2. 编辑 `plugins/OnlineTime/config.yml`
3. 配置数据库连接信息（见下文）
4. 重启服务器

---

## 配置说明

### 2.1 配置文件位置

```
plugins/OnlineTime/config.yml
```

### 2.2 配置文件详解

```yaml
# 数据库基本配置
database:
  # 是否启用数据库
  # 强烈推荐启用，否则数据在服务器重启后会丢失
  enable: true

  # 数据库主机地址
  # 本地数据库：localhost
  # 远程数据库：如 192.168.1.100
  host: localhost

  # 数据库端口（MySQL 默认 3306）
  port: 3306

  # 数据库用户名
  user: root

  # 数据库密码
  # 建议使用配置模板 (config.example.yml) + .gitignore
  password: your_password_here

  # 数据库名称
  # 建议使用独立数据库，如 minecraft
  database: minecraft
```

---

## 占位符使用

### 3.1 基本语法

占位符格式：`%onlineTime_<格式字符串>%`

**可用字段：**
- `HH` - 总小时数
- `mm` - 总分钟数
- `SS` - 总秒数（原始值）

### 3.2 示例

| 占位符 | 玩家在线 9480 秒时的输出 | 说明 |
|--------|------------------------|------|
| `%onlineTime_HH时%` | `2时` | 只显示小时数 |
| `%onlineTime_mm分%` | `158分` | 只显示分钟数 |
| `%onlineTime_SS秒%` | `9480秒` | 显示原始秒数 |
| `%onlineTime_HH时mm分%` | `2时158分` | 组合显示 |
| `%onlineTime_在线HH小时%` | `在线2小时` | 自定义文本混合 |

### 3.3 重要说明

**关于 `mm` 字段：**

`mm` 返回的是**总分钟数**，不是"小时的余数分钟"。

**示例计算：**
- 玩家在线 9480 秒
- 9480 秒 ÷ 3600 = 2.633 小时 → `HH` = 2
- 9480 秒 ÷ 60 = 158 分钟 → `mm` = 158（不是 38）
- 9480 秒 = 9480 秒 → `SS` = 9480

**如果需要 "2 小时 38 分钟" 格式：**

当前版本不支持余数计算。如需此功能，需要修改 `OnlineTimePapi.kt` 的格式化逻辑：

```kotlin
// 需要添加的字段：
val remainMinutes = (totalSeconds % 3600) / 60  // 余数分钟
val remainSeconds = totalSeconds % 60           // 余数秒
```

---

## 群组服配置

### 4.1 什么是群组服

群组服是指在同一台（或多台）机器上运行多个 Minecraft 服务器，通过不同端口区分：

```
机器 A (IP: 192.168.1.100)
├── 生存服 → 端口 25565
├── 创造服 → 端口 25566
└── 小游戏 → 端口 25567
```

玩家可以在这些子服之间切换，但**同一时间只能在一个子服在线**。

### 4.2 群组服数据同步原理

```
玩家 A 的游戏流程：
09:00 - 登录生存服 (25565)
09:30 - 切换到创造服 (25566)
10:00 - 切换到小游戏 (25567)
10:30 - 下线

结果：
- 生存服统计：30 分钟
- 创造服统计：30 分钟
- 小游戏统计：30 分钟
- 总计：90 分钟（1.5 小时）
```

**关键点：**
- 所有子服连接同一个 MySQL 数据库
- 每个子服只统计在**本服**在线的玩家
- 数据库中的 `second` 字段累计所有子服的在线时间

### 4.3 群组服配置步骤

#### 步骤 1：准备数据库

```sql
-- 创建独立数据库（推荐）
CREATE DATABASE minecraft_online DEFAULT CHARSET=utf8mb4;

-- 创建用户并授权
CREATE USER 'minecraft'@'%' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON minecraft_online.* TO 'minecraft'@'%';
FLUSH PRIVILEGES;
```

#### 步骤 2：配置所有子服

**生存服 (25565) 配置：**
```yaml
# plugins/OnlineTime/config.yml
database:
  enable: true
  host: 192.168.1.100  # 数据库服务器地址
  port: 3306
  user: minecraft
  password: secure_password
  database: minecraft_online  # 所有子服使用同一个数据库！
```

**创造服 (25566) 配置：**
```yaml
# plugins/OnlineTime/config.yml
database:
  enable: true
  host: 192.168.1.100  # 与生存服完全一致
  port: 3306
  user: minecraft
  password: secure_password
  database: minecraft_online  # 与生存服完全一致
```

**小游戏 (25567) 配置：**
```yaml
# plugins/OnlineTime/config.yml
database:
  enable: true
  host: 192.168.1.100  # 与生存服完全一致
  port: 3306
  user: minecraft
  password: secure_password
  database: minecraft_online  # 与生存服完全一致
```

#### 步骤 3：验证配置

```bash
# 在每个子服的控制台执行
/plugins

# 确认 OnlineTime 显示为绿色
# 查看日志确认数据库连接成功
```

### 4.4 群组服常见误区

❌ **错误理解：** "玩家可能同时在多个子服在线，会导致数据冲突"
✅ **正确理解：** 玩家只能同时在一个子服在线，不存在并发写入冲突

❌ **错误配置：** 每个子服使用不同的数据库名
✅ **正确配置：** 所有子服使用相同的数据库名（如 `minecraft_online`）

---

## 常见问题

### Q1：占位符显示为 "null"

**原因：**
- 玩家从未登录过服务器
- 缓存未加载
- 数据库连接失败

**解决方案：**
1. 让玩家重新登录服务器
2. 检查数据库连接：
   ```bash
   mysql -h localhost -u root -p
   USE minecraft;
   SHOW TABLES;  # 应该看到 onlineTime 表
   ```
3. 查看服务器日志：`logs/latest.log`

---

### Q2：数据在服务器重启后丢失

**原因：**
- 数据库未启用（`enable: false`）
- 数据库连接失败

**解决方案：**
1. 检查 `config.yml` 中 `database.enable` 是否为 `true`
2. 测试数据库连接：
   ```bash
   mysql -h localhost -u root -p
   # 输入密码后，如果成功登录说明配置正确
   ```
3. 检查插件日志是否有数据库错误

---

### Q3：群组服数据不同步

**原因：**
- 不同子服连接到不同的数据库
- 时区配置不一致（当前版本已修复）

**解决方案：**
1. 确保所有子服的 `config.yml` 中数据库配置**完全一致**：
    - `host` 地址相同
    - `database` 名称相同
    - `user` 和 `password` 相同
2. 重启所有子服
3. 验证数据库中只有一个 `onlineTime` 表

---

### Q4：时间显示不准确

**原因：**
- 系统时区不一致（当前版本已修复，使用 Asia/Shanghai）

**解决方案：**
- 使用最新版本插件（自动使用 Asia/Shanghai 时区）
- 如需自定义时区，修改 `OnlineTimeFun.kt:55` 中的时区设置

---

### Q5：高并发服务器卡顿

**原因：**
- 数据库性能不足
- 网络延迟过高

**解决方案：**
1. 优化数据库（见下文"高级配置"）
2. 使用本地数据库（减少网络延迟）
3. 升级数据库服务器硬件

---

### 5.3 调试占位符

**使用 PlaceholderAPI 调试命令：**
```bash
/papi parse me %onlineTime_HH%
```

**正常输出：**
```
0        # 玩家今天刚登录
2        # 玩家今天在线 2+ 小时
```

**异常输出：**
```
null                    → 缓存未加载，重新登录
%onlineTime_HH%         → 占位符未注册，重启插件
```

---

## 高级配置

### 6.1 数据库性能优化

**创建索引（提升查询速度）：**
```sql
USE minecraft;

-- 为常用查询字段创建索引
CREATE INDEX idx_time ON onlineTime(time);

-- 查看索引是否生效
SHOW INDEX FROM onlineTime;
```

**调整 MySQL 配置（my.cnf / my.ini）：**
```ini
[mysqld]
# 增加连接池大小
max_connections = 200

# 优化 InnoDB 缓冲池
innodb_buffer_pool_size = 256M

# 优化查询缓存
query_cache_size = 64M
query_cache_type = 1
```

### 6.2 数据备份

**定时备份脚本：**
```bash
#!/bin/bash
# 每日凌晨 3 点备份
mysqldump -u minecraft -p'password' minecraft_online onlineTime > /backup/onlineTime_$(date +\%Y\%m\%d).sql
```

**添加到 crontab：**
```bash
crontab -e
# 添加以下行：
0 3 * * * /path/to/backup.sh
```

---

## 支持与反馈

**遇到问题？**
1. 查看 [常见问题](#6-常见问题)
2. 查看 [故障排查](#7-故障排查)
3. 提交 Issue：[GitHub Issues](https://github.com/rugeovo/OnlineTime/issues)

**提交 Issue 时请提供：**
- 服务端类型和版本（如 Paper 1.20.4）
- 插件版本
- 错误日志（`logs/latest.log`）
- 配置文件（去除敏感信息）
- 是否为群组服环境

---