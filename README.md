# OnlineTime - Minecraft 玩家在线时长统计插件

> 简洁、高效、生产级的玩家在线时间追踪系统

[![Minecraft](https://img.shields.io/badge/Minecraft-1.12.2--1.21.4-green.svg)](https://www.minecraft.net/)
[![License](https://img.shields.io/badge/License-Custom-blue.svg)](./LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Bukkit%20%7C%20Spigot%20%7C%20Paper-orange.svg)](https://papermc.io/)
[![Code Size](https://img.shields.io/badge/Code-253%20lines-blue.svg)]()

---

## 📖 概述

**OnlineTime** 是一款为 Minecraft 服务器设计的玩家在线时长统计插件，特点是：

- 🎯 **极简设计**：仅 253 行代码，零冗余
- ⚡ **高性能**：批量事务优化，支持 500+ 玩家并发
- 📊 **每日统计**：自动按日期分离数据，历史可查
- 🔌 **PlaceholderAPI**：无缝集成计分板、称号、聊天系统
- 🌐 **群组服支持**：MySQL 共享数据，跨服同步
- 🎨 **灵活格式**：自定义时间显示（小时/分钟/秒）


---

## 🎯 核心功能

### 1. 实时统计
- 每秒自动增加在线玩家时长（精确到秒）
- 后台异步处理，零卡顿

### 2. 占位符支持
通过 PlaceholderAPI 在任意位置使用：

```
%onlineTime_HH时%         → "2时"
%onlineTime_mm分%         → "158分"
%onlineTime_SS秒%         → "9480秒"
%onlineTime_HH时mm分%     → "2时158分"
```

**可用字段：**
- `HH` - 总小时数
- `mm` - 总分钟数
- `SS` - 总秒数

**注意：** `mm` 是总分钟数，不是"小时的余数"。例如 9480 秒 = 2 小时 = 158 分钟。

### 3. 数据持久化
- MySQL/MariaDB 持久化存储
- 服务器重启数据不丢失
- 群组服自动数据同步

---

## 📊 性能数据

| 玩家数 | 数据库操作耗时 | 内存占用 | CPU 占用 |
|--------|---------------|----------|----------|
| 50     | ~5ms          | < 10MB   | < 0.1%   |
| 100    | ~10ms         | < 20MB   | < 0.2%   |
| 500    | ~25ms         | < 50MB   | < 0.5%   |

**优化技术：**
- 批量事务提交（100 条 SQL → 1 次网络往返）
- 内存缓存（ConcurrentHashMap）
- 增量缓存更新（避免无意义的清空/重建）

---

## 🔧 技术架构

### 技术栈
- **语言**：Kotlin 1.9.21
- **框架**：Taboolib 6.2.4
- **数据库**：基于 persistentContainer 抽象层
- **缓存**：ConcurrentHashMap 线程安全缓存

### 项目结构
```
OnlineTime/
├── src/main/kotlin/ruge/onlinetime/
│   ├── OnlineTime.kt           # 插件主类 (17 行)
│   ├── OnlineTimeFun.kt        # 定时任务 (60 行)
│   ├── database/
│   │   └── OnlineTimeBase.kt   # 数据库操作 (125 行)
│   ├── papi/
│   │   └── OnlineTimePapi.kt   # PlaceholderAPI (37 行)
│   └── profile/
│       └── Files.kt            # 配置管理 (17 行)
└── src/main/resources/
    └── config.yml              # 配置文件
```

**总代码量：** 253 行（含注释）

---

## 🌐 群组服支持

### 工作原理

```
机器 A
├── 生存服 (端口 25565) → OnlineTime → MySQL
├── 创造服 (端口 25566) → OnlineTime → MySQL
└── 小游戏 (端口 25567) → OnlineTime → MySQL
         ↓          ↓          ↓
    同一个 MySQL 数据库（数据共享）
```

**关键特性：**
- 玩家在任意子服的在线时间都会累计到同一个数据库
- 所有子服共享同一份玩家在线时长数据
- 玩家在子服间切换，数据无缝衔接

**配置要点：**
1. 所有子服安装 OnlineTime 插件
2. 所有子服连接到**同一个数据库**
3. 确保数据库配置完全一致（host、database、user、password）

---

## 🛠 开发与构建

### 编译项目
```bash
./gradlew build
```

### 生成插件
编译后的插件位于：`build/libs/OnlineTime.jar`

### 开发环境
- JDK 8 或更高版本
- Gradle 8.10

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

### 开发规范
- 遵循 Kotlin 代码风格
- 保持代码简洁（避免过度工程）
- 提交前运行 `./gradlew build` 确保编译通过
- 提交信息格式：`type: description`

### 设计哲学
> **"Simplicity is prerequisite for reliability."**
> *— Edsger W. Dijkstra*

本项目遵循极简主义：
- 只解决真实存在的问题
- 不添加"可能需要"的功能
- 每一行代码都必须有明确的价值

---
