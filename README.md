# aliyun-ddns
Dynamically update the DNS cloud resolution

# 使用说明
##  1. 编译jar包
```shell
mvn package
```
假设编译出来的jar包名字为： target/ddns-1.0-SNAPSHOT-jar-with-dependencies.jar
 
##  2. 安装：
1. 把jar包复制到你喜欢的目录。
2. 在jar包同级目录存放一份`private/ddns.properties`文件， 并按照自己的情况修改其中变量。 主要是账号信息，以及白名单。

## 3. 在该目录下运行:
```
java -jar ddns-1.0-SNAPSHOT-jar-with-dependencies.jar
```
## 4. 添加到cron job 
```java
(略)
```

# Motivation
1. 用阿里云做域名解析，然而IP是电信宽带动态分配的，经常莫名发生改变
2. 类似的工具有好几个，python2, python3, .net的，一开始我也想捡便宜用用别人现成的工具的。然而由于总总原因，我觉得还是自己从新写一个可行一点。
3. 参考：https://blog.csdn.net/woaijaycs/article/details/96489285

#Todo
1. To support complicated resolution table. e.g. Just to update specific lines, rather than all. 

# 经验教训
## 1. 一个别人愿意用的工具，首先应该是一个容易使用的工具。
吐槽：

1.1. python的工程，最好加上requirement.txt

1.2. 如果有必须的配置项，那么每个配置项解释一下。千万别留下某些用户完全不知道该填什么配置项。

## 2. 再吐槽一下Aliyun的SDK
1. 没有注释真不知道怎么用。 看名字猜，但是好几个包里面都有domain的字样，到底哪个是DNS解析啊！
   - 我是最后把java sdk的源码全拖下来，对照HTTP API的Action 名字去代码里面找，才总算找到了该用哪个包。 
   
2. 最好给写点sample

## 3. 工具开发完了，我发现我也不想写使用说明……
