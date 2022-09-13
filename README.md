# shr25-qq-robot

#### 介绍
qq机器人，使用基础框架：https://github.com/mamoe/mirai  
文档地址：https://docs.mirai.mamoe.net  
推荐一个spring boot启动脚本：https://gitee.com/billdowney/spring-boot-script

想要 管理后台"脚手架" 的可以看另一个项目：[https://gitee.com/shr25/shr25](https://gitee.com/shr25/shr25)

采用插件式处理消息，可无限扩展插件，需要jdk1.8以上
默认采用的sqlite数据库存储数据,可以切换其他数据源,如：mysql。 
用户和群隔离开，每个群单独的配置。
系统管理相关命令都在【系统管理插件】里，部分命令必须配置了root管理员才能使用，对应配置：project.qq-robot.root-manage-qq（例如开关群管理权限）
#### 软件架构
```
shr25-qq-robot
 ├── cache -- qq信息、文件缓存目录
 ├── db -- 数据库文件目录
 ├── shr25-qq-robot-core -- 核心公共部分
 |    └── com.shr25.robot.StartUpApplication -- 调试启动类
 └── shr25-qq-robot-plugin -- 自定义插件项目
```

### 联系方式
QQ群：786310882

### start转移声明
为防止项目中出现多个启动类，导致新的开发者产生疑惑，把自定义插件和start部分独立转移出项目
自定义插件创建： [跳转wiki，创建您的自定义插件](https://gitee.com/shr25/shr25-qq-robot/wikis/%E6%8F%92%E4%BB%B6/%E9%80%9A%E8%BF%87%20maven%20%20archetype%20%E5%88%9B%E5%BB%BA)
start启动器： [shr-qq-robot-start](https://gitee.com/shr25/shr-qq-robot-start)


#### 安装教程
idea建议使用2022.X，低版本 会报红，找不到类（虽然可以正常启动， 但使用不方便，无法跟进源码）
1. 下载源码，更新maven包
2. 安装mysql数据库，导入表结构与数据
3. 调整配置 application-dev.yml， 配置 需要登录的QQ 和管理员 
4. 启动类：StartUpApplication 直接启动核心

更多使用说明见wiki,点击[shr25-qq-robot](https://gitee.com/shr25/shr25-qq-robot/wikis/%E5%BF%AB%E9%80%9F%E4%BD%BF%E7%94%A8)进入wiki页面。
![输入图片说明](https://foruda.gitee.com/images/1662267199054688012/9374aed8_1911860.png "屏幕截图")

#### 使用说明
1. 所有内置插件在com.shr25.robot.qq.plugins包里
2. 管理员qq执行命令：#简介、#介绍、#管理  可以查看所以功能
3. 命令：#简介 可以查看机器人管理命令
4. 命令：#插件列表，可查看当前所有内置插件状态
5. 命令：#{插件名称}，可查看对应插件简介以及使用方式
6. 如果出现找不到类的情况，请编译整个项目。

#### 已知问题

1.  [常见问题](https://docs.mirai.mamoe.net/Bots.html#%E5%B8%B8%E8%A7%81%E7%99%BB%E5%BD%95%E5%A4%B1%E8%B4%A5%E5%8E%9F%E5%9B%A0)  
2.  假如出现能发送好友消息，发送不了群消息，可以尝试删除cache/qq/[登录的qq号]文件夹，怀疑是缓存的服务器列表发送不了

本项目仅用于学习用途，不可用于其他用途，任何法律责任由用户自行承担。 其他分支或者仓库可能不包含最新声明，若无特殊声明，以github仓库默认分支为准。

### Mirai 官方声明片段
mirai 是一个在全平台下运行，提供 QQ Android 协议支持的高效率机器人库

### 声明
一切开发旨在学习，请勿用于非法用途
mirai 是完全免费且开放源代码的软件，仅供学习和娱乐用途使用
mirai 不会通过任何方式强制收取费用，或对使用者提出物质条件
mirai 由整个开源社区维护，并不是属于某个个体的作品，所有贡献者都享有其作品的著作权。
除本页的 Gitter 讨论组外，Mirai 在各个平台均没有任何所谓官方交流群或论坛, 请不要轻信任何所谓学习, 交流群, 不造谣不传谣不信谣从我做起

mirai 采用 AGPLv3 协议开源。为了整个社区的良性发展，我们强烈建议您做到以下几点：

间接接触（包括但不限于使用 Http API 或 跨进程技术）到 mirai 的软件使用 AGPLv3 开源
不鼓励，不支持一切商业使用
鉴于项目的特殊性，开发团队可能在任何时间停止更新或删除项目。
