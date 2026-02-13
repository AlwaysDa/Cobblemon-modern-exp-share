# Cobblemon Modern Exp Share

一个用于 **Cobblemon (Fabric)** 的 Exp. Share（学习装置）行为“现代化”模组：当玩家背包/物品栏中持有 `cobblemon:exp_share` 时，在战斗胜利结算阶段，会把额外的经验值（EXP）和努力值（EV）分配给同一玩家队伍中 **未参战且未昏厥** 的队友。

English README: [README_EN.md](README_EN.md)

## 兼容性

- Minecraft: **1.21.1**
- Fabric Loader: **>= 0.17.2**
- Fabric API: **>= 0.116.6+1.21.1**
- Cobblemon (Fabric): **1.7.1+1.21.1**
- Java: **21**

模组信息：
- Name: Modern Exp Share
- Mod ID: `modern_exp_share`

## 行为规则

当满足以下条件时生效：
- 玩家背包/物品栏中存在 `cobblemon:exp_share`
- 战斗胜利结算时，针对该玩家队伍中的每只宝可梦：
  - **未参战**
  - **未昏厥**

对符合条件的队友，发放额外收益：
- **EXP**：`参战获得的 EXP × sharedExpMultiplier`（默认 0.5）
- **EV**：`参战获得的 EV × sharedEvMultiplier`（默认 1.0）

> 说明：倍率由服务端配置控制（单人整合服同样以“服务器侧”为准）。

## 配置

配置文件路径（服务端/单人世界通用）：
- `config/cobblemon_modern_exp_share.json`

默认配置示例：

```json
{
  "sharedExpMultiplier": 0.5,
  "sharedEvMultiplier": 1.0
}
```

字段说明：
- `sharedExpMultiplier`：额外分享 EXP 的倍率（>= 0）
- `sharedEvMultiplier`：额外分享 EV 的倍率（>= 0）

配置加载时机：服务器启动时读取；若不存在会自动生成默认配置。

## 构建

本项目已自带 Gradle Wrapper（固定 Gradle 9.2.0），推荐直接用 `gradlew` 构建。

1) 准备本地 Cobblemon Jar

本项目使用本地依赖方式引用 Cobblemon，请确保以下文件存在：
- `libs/cobblemon-fabric-1.7.1+1.21.1.jar`

2) 确保使用 Java 21

`gradle.properties` 里包含 `org.gradle.java.home`，如果你的 JDK 21 安装路径不同，请改成你本机的 Java 21 路径，或删除该行并保证系统环境能找到 Java 21。

3) 构建

在项目根目录运行：

```powershell
.\gradlew.bat build
```

产物默认输出到：
- `build/libs/cobblemon-modern-exp-share-<version>.jar`

## 安装/使用

- 将构建出的 jar 放入客户端/服务端的 `mods/` 目录
- 同时需要安装对应版本的 Cobblemon 与 Fabric API
- 在游戏内让玩家背包/物品栏持有 `cobblemon:exp_share`，即可按上述规则分享收益

## 许可证

MIT（以工程内实际 License 文件/声明为准）。
