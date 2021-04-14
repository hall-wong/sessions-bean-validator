## 欢迎您的到来

在本项目中，我将会结合一份简单的RESTFul服务代码，逐步讲解Java Bean Validator的应用。如果有错误和不足，希望海涵，欢迎指正。

## 一个比较常见的例子

Hi，你刚刚接手了这个项目，这个是一个关于资产（**Asset**）管理的后端程序，它对外提供RESTFul风格的接口，目前有两个接口：

1. `GET /assets?key={要查询的key}` 列表查询接口
2. `POST /assets` 创建接口



## 思考

在通读代码前，思考如下的问题：

1. 在翻到Service代码前，你是否可以猜测出各个字段的校验逻辑？
2. 在通读一遍后，你是否可以很清晰的说出每个字段的校验逻辑？
3. 你是否可以在Service中区分哪些是业务代码，哪些是校验的代码？



请带着思考，切换到`01_a_better_case`分支。