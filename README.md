# PhotoSelectSdk
Android端相册和相机集成SDK，可用于头像功能。

提供三个接口调用：
（1）弹窗调用
（2）相册接口调用
（3）相机接口调用

### 使用-导入Lib
##### 由于是提供给Unity在游戏中调用，实现头像选取功能，故demo是采用了eclipse的导入方式，可将photosdk_lib作为Module导入到项目中，并添加依赖，如下：
<img src="img/xm.png" width="700">

### demo运行截图
<img src="img/main.png" width="250">

#### 弹窗调用
##### 可自定义传入弹窗title，content，显示自定义的标题和内容。
##### demo调用代码
<img src="img/dialog_code.png" width="700">

##### 运行截图
<img src="img/dialog.png" width="250">

#### 相册接口调用
##### 单独调用相册接口，获取并返回处理后的图片
##### demo调用代码
<img src="img/p_code.png" width="700">

##### 运行截图
<table><tr>
  <td><img src="img/p.png" width="250"></td>
  <td><img src="img/pb.png" width="250"></td>
</tr></table>

#### 相机接口调用
##### 单独调用相册接口，获取并返回处理后的图片
##### demo调用代码
<img src="img/c_code.png" width="700">

##### 运行截图
<table><tr>
  <td><img src="img/c.png" width="250"></td>
  <td><img src="img/cb.png" width="250"></td>
</tr></table>

#### 按需重写onActivityResult
##### demo代码块
<img src="img/a.png" width="700">

#### 动态权限
##### 如有实现动态权限，可省略示例代码中关于权限的判断及调用。
##### demo代码块
<img src="img/r.png" width="700">
