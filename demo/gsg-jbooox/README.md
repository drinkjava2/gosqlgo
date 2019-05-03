## gsg-jbooox  
这是一个GoSqlGo在Tomcat、Jetty、Undertow等Servlet环境下运行的示范项目。    
gsg-jbooox这个命名表示它是由GoSqlGo、jWebBox、jSqlBox、jBeanBox这几个工具组合而成。  
   
## 各种Servlet环境下的运行方式:    
#### 方式1，发布war包到本机的Tomcat7或Tomcat8目录下执行：   
运行：修改run_tomcat_local.bat批处理文件中的TomcatFolder为本机Tomcat目录，并执行   

#### 方式2, 命令行方式在嵌入式Tomcat上运行，这种方式本机不需要安装Tomcat, Maven会自动下载      
运行：双击运行run_tomcat_embedded.bat批处理即可     

#### 方式3, 命令行方式在嵌入式Jetty上运行，这种方式本机不需要安装Servlet容器, Maven会自动下载Jetty      
运行：双击运行run_jetty_embedded.bat批处理即可   

#### 方式4, 命令行方式在嵌入式Undertow上运行，这种方式本机不需要安装Servlet容器, Maven会自动下载Undertow      
运行：双击运行run_undertow_embedded.bat批处理即可   

#### 方式5, 导入到Eclipse中运行或调试  
1. 运行run_tomcat_embedded.bat批处理一次  
2. 运行maven_eclipse_eclipse.bat批处理，生成eclipse配置  
3. 打开Eclipse,导入项目,并运行其中的MainApp.java的main方法  
  
查看结果：在浏览器输入 http://localhost   

### 开发和布署 | Develop & Product
开发阶段：GoSqlGo在服务端运行，它自带一个动态编译工具，前端发来的SQL和Java片段，被运态编译为Java类，并调用服务端ORM工具，最后返回文本或JSON对象。  
在Sql/Java片段里面，返回值可以分为以下类型，但客户端最终收到的都是一个字符串：  
1. 字符串类型，可以为普通字符串或JSON字符串，由客户端来自行解析。    
2. WebBox对象，可以用new WebBox().setText()方法来设定一个字符串，或用new WebBox("xxx.htm")方法来嵌入一个服务端页面。  
3. 异常抛出，服务端会捕获并在服务端控制台输出异常信息，并返回空字符串。  
4. 空值，服务端会返回"null"字符串，由客户端来自行解析。  
5. 其它Java对象，服务端会对Java对象调用JSON.toJSONString(obj)方法进行字符串化，由客户端来自行解析收到的JSON字符串。  
注意以上只是gsg-jbooox示范项目对返回值的包装，用户实际项目中可以自定义自已的服务端处理逻辑（继承于ServletTemplate类)。  

布署阶段：通过一个打包工具(DeployTool)，将前端所有的SQL和原生Java片段打包到服务端去，静态存为可调试的Java源文件，原有客户端的SQl和JAVA代码在打包后将成为类似于$gsg('C9GK90J27','A');之类的通过ID进行的调用，以实现安全性，打包工具有四个方法，在IDE里可以使用:  
```
DeployTool.goServ();    //将HTML/Javascript中的sql和java代码抽取出来，生成Java类发布到项目源码的deploy目录下, 但如果SQL/Java片段开头含有"FRONT"关键字将不抽取
DeployTool.goFront();   //逆操作，将抽取出来的SQL/Java类再塞回到HTML/Javascript中，但如果Java源码有"// GSG SERV"注释将不抽取   
DeployTool.goServForce();    //同goServ，但是忽略FRONT关键字，一律强制抽取到服务端，以实现安全性  
DeployTool.goFrontForce();    //同goFront，但是忽略"// GSG SERV"注释，一律强制抽取到前端，以实现可读性   
```
注意在打包之前必须调用GoSqlGoEnv.registerGsgTemplate()方法登记所有的自定义模板类，例如：GoSqlGoEnv.registerGsgTemplate("javaTx", JavaTxTemplate.class); 详见 com.demo.InitConfig 中的示例。注意所有模板类必须继承ServletTemplate类，并且以"方法名+Template"命名，这是一个约定。    
Windows环境下，先运行run_undertow_embedded.bat一遍后，再点击goServ.bat批处理文件即可进行打包操作，再次运行run_undertow_embedded.bat就可以发现客户端的SQL和Java消失了，取而代之的是$gsg这种调用方法。   

对于Sql/Java字符串片段，有以下可选控制字符：  
SERV，永远留在后端，一旦用DeployTool.goServ()命令抽取到服务端，就不能用goFront命令塞回到前端，必须用goFrontForce命令才能塞回到前端。  
FRONT， 永远留在前端，goServ命令对它不起作用，直到使用goServForce命令才会被强制抽取到Server端。    
FULL，定义完整的Java类，package、类声明等都必须写全，这是一种比较特殊的用法，与在服务器定义一个类等同。   
#xxxxx形式，手工指定类名，如果不定义类名, 则由工具随机自动生成，用goFront命令塞回到前端时会删除这个随机类名。    
import开头，为标准Java的import语句  
示例： 
```
$java('SERV #ReadAmount import abc.DemoUser; return new DemoUser().loadById($1).getAmount();', 'u1');   
```  
在类根目录(项目的resources目录)下，有一个名为GoSqlGo.properties的配置文件，可以进行一些配置，例如配置deploy目录、template目录、设定开发/生产阶段。 