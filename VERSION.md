### V1.0.6
1.每个账号使用自己的设备信息

### V1.0.5
1.把所有的自定义插件都分离出主项目
2.所有插件上传至中央仓库 https://repo1.maven.org/maven2/com/shr25/robot/plugin/

### V1.0.4
1.调整插件列表，添加简单模式.只展示主要命令
```
project.qq-robot.simplify-command=true
```

### V1.0.2
1.处理sqlite保存时间时未格式化，导致查询报错问题  
2.core加入了gif的操作jar  
3.新增了摸头插件。[参考目地址](https://github.com/LaoLittle/PatPat.git)

### V1.0.1
1.调整的maven的groupId,由com.shr25调整为com.shr25.robot
2.添加sqlite数据源，调整默认数据源为sqlite,本地运行可以直接运行，不再强制依赖mysql
3.可以通过配置文件配置采用那种数据源
4.新建demo项目：https://gitee.com/shr25/shr25-qq-robot-demo-plugin


### V1.0.0
上传只中央仓库，可以单独建立插件项目。
