# 为避免误解，在此声明：
## 本项目架构上没有什么安全问题，有质疑安全、注入问题的，在吐槽之前请将本说明完整看完。

### GoSqlGo简介 | Description
天下武功，唯快不破，程序无非就是接收用户输入、存到数据库。GoSqlGo能让前端直接存取数据库，独立完成项目开发。

### 缘起 | Origin
一直认为，开发效率最高的方式不是让MVC架构极简(SpringBoot/jFinal)，而是彻底省略掉MVC架构和后端程序员，直接由前端搞定一切，由多层架构变成两层，在前端直接写SQL，缩短界面和数据库之间的距离，才是最快的开发途径。基于此理念，在2011年本人在[这里](https://blog.csdn.net/drinkjava/article/details/6935539)写了一句预言，没想到技术的发展如此之慢，现在要自己亲手去实现它了，这就是GoSqlGo项目，如果名字翻译成中文，可以翻成"Sql冲冲冲冲冲"，这个比较形象，它表达了SQL为王，一路狂奔，冲到了前端的意思。界面和逻辑混合是一种肮脏的开发模式，但是就象臭豆腐一样闻着臭，吃着吃着就觉得香了。  

GoSqlGo是一个运行于后端的开发工具，它的最大特点就是在运行期动态编译客户端Java代码，所有SQL和Java代码都可以在前端Html页面完成，可以彻底甩掉后端。开发完成后再利用打包工具将SQL和Java从前端移到后端，以实现安全。忘掉MVC吧，因为现在架构变成MV两层了；忘掉FreeMaker之类模板吧，因为Java内嵌到HTML里去了；忘掉后端程序员吧，因为前端把后端的活给干了(这叫大前端)；忘掉前端校验吧，因为后端校验这活也归前端了，前端校验能偷懒就偷吧。  

### 简介 | Features
用一个例子来说明直接在前端Javascript里写SQL和Java代码，以下html示例，实测通过，完整文件位于[这里](https://gitee.com/drinkjava2/gosqlgo/blob/master/demo/gsg-jbooox/src/main/webapp/page/demo1.html)。  
这是一个转账业务的演示，开发阶段把所有的SQL和业务逻辑都写在html里面，在布署阶段再由打包工具抽取到服务端：     
```
<!DOCTYPE html>
<html>
<head>
<style>...略...</style>
<script src="/js/jquery-1.11.3.min.js"></script>
<script src="/js/jquery-ajax-ext.js"></script>
<script src="/js/gosqlgo.js"></script>
</head>
<body>
    <script> 
	  document.write($java(`return new WebBox("/page/menu.html").setAttribute("title", $1);`, "Transaction demo, use jQuery")); 
	  function getUserListHtml(){ 
		  var users=JSON.parse($qryMapList(`select * from account where amount>=? order by id`,0));
		  var html="User List:<br/>";
		  for(var i=0;i<users.length;i++) 
			  html+="User ID:" +  users[i].ID+", AMOUNT:"+ users[i].AMOUNT+"<br/>"; 
	      return html;		   
	  } 
	</script>   
	<div id="msgid" class="msg"></div> 
	<p id="Users">
	    <script>document.write(getUserListHtml());</script>   
	</p>
	
	<section>
		<header>Account A</header>
		<div id="A" class="amount">
			<script>
				document.write($qry(`select amount from account where id=? and amount>=?`, 'A',0));
			</script>
		</div>
	</section>
	<section>
		<header>Account B</header>
		<div id="B" class="amount">
			<script>
				document.write($java(`return new Account($1,$2).load().getAmount();`, 'B',0));
			</script>
		</div>
	</section>
	<script>
	  function transfer(from, to, money){ 
		 var rst = $java(`#TransferMoney 
						int money=Integer.parseInt($3);
						if(money<=0) 
						  throw new SecurityException("Money<=0, IP:"+ getRequest().getRemoteAddr());
						Account a=new Account().setId($1).load();
						if(a.getAmount()<money)
						   return "Error:No enough balance!";
						Account b=new Account().setId($2).load();
						a.setAmount(a.getAmount()-money).update();
						b.setAmount(b.getAmount()+money).update(); 
						return "Transfer Success!|"+a.getAmount()+"|"+b.getAmount();
						`,	from,to,money);   
		  if(rst.startsWith("Transfer Success!")) { 
			  var words=rst.split('|');
	 	      $("#msgid").text(words[0]); 
	 	      $("#"+from).text(words[1]);
	 	      $("#"+to).text(words[2]);
	 	      $("#msgid").css("background", "#dfb");
	 	      $("#Users").html(getUserListHtml());
		  }
		  else  if(rst.startsWith("Error:")) { 
			     $("#msgid").text(rst.substring(6));
		         $("#msgid").css("background", "#ffbeb8");
		  } 
		}
	</script>
	<section>
		<header>Transfer</header>
		<form onsubmit="return false" action="##" method="post">
			<input name="amount" value="100" class="amount">
			<button name="btnA2B" value="true" onclick="transfer('A','B',100)">From
				account A to account B</button>
			<button name="btnB2A" value="true" onclick="transfer('B','A',100)">From
				account B to account A</button>
		</form>
	</section>
</body>
</html>
```
另外还有两个演示:  
[演示2](https://gitee.com/drinkjava2/gosqlgo/blob/master/demo/gsg-jbooox/src/main/webapp/page/demo2.html)：GoSqlGo结合Vue的使用。  
[演示3](https://gitee.com/drinkjava2/gosqlgo/blob/master/demo/gsg-jbooox/src/main/webapp/page/demo3.html): 在前端定义一个完整的实体类并进行DDL生成、建表、表单输入检查、表单提交和存盘。  

查看演示:在windows下点击demo\gsg-jbooox\run_undertow_embedded.bat批处理即可。  

GoSqlGo运行在服务端，只需要进行简单的数据源设定、ActiveRecord实体类定义就可以了（也可以定义在html里，见例3)。配置详见com.demo.InitConfig类内容。  
GoSqlGo是一个工具，而不是一个框架，它是通过在Web容器里添加一个Servlet过滤器，处理.gsg访问，也就是说，它通常寄生在其它后端框架里，这样的优点是可以直接共享其它框架的Session和工具类。 

GoSqlGo使用需要在客户端添加gosqlgo.js，业务逻辑和SQL直接写在客户端的Javascript里，示范项目里主要有以下方法：
```
$java(String, Object...) 执行多行Java语句。第一个参数是Java本体，后面是参数，在Java里可以用$1,$2...来访问。  
$javaTx(String, Object...) 执行多行Java语句并开启事务，如果有异常发生，事务回滚。
$qry(String, Object...) 将SQL查询结果的第一行第一列作为字符串值返回，第一个参数是SQL，后面是SQL参数    
$qryArray(String, Object...)  返回SQL查询的第一行数据，格式为Object[]的JSON字符串  
$qryArrayList(String, Object...)  返回多行查询结果为List<数组>的JSON格式    
$qryTitleArrayList(String, Object...)  返回多行查询结果，为List<数组>的JSON格式，但第一行内容是各个列的标题  
$qryMap(String, Object...) 返回SQL查询的第一行数据，为Map的JSON格式  
$qryMapList(String, Object...)  返回SQL查询的多行数据，为List<Map>的JSON格式  
$qryEntityList(String, Object...)  返回多行数据为List<实体>的JSON格式，SQL写法是实体类名+逗号+SQL, 示例:$qryEntityList(`a.b.Demo, select * from demo`);  
```
开发阶段，以上方法中第一个字符串参数，必须用键盘ESC下方的单引号括起来(这是Javascript的特殊单引号，支持多行文本)，而不能用普通的双引号或单引号。  

### 依赖和运行 | Dependency and Run
GoSqlGO使用需要在服务端项目里加入以下依赖：
```
    <dependency>
      <groupId>com.github.drinkjava2</groupId>
      <artifactId>gosqlgo</artifactId>
      <version>1.1.0</version>
    </dependency>
```
它主要有两个功能：  
1.动态编译客户端传来的SQL/Java，生成Java类，例如以下调用会动态地根据Java源码生成Java类实例：   
DynamicCompileEngine.instance.javaCodeToClass(className, classSrc);  
这个功能前端程序员不需要了解，因为通常前端拿到手的是后端已经精心装配好的如gsg-jbooox这样的开箱即用的整合包。  
  
2.打包工具，将前端的SQL/Java抽出到后端，或反之。详见"开发和布署"一节。   

原则上GoSqlGo可以使用在任意Servlet容器内，并可搭配不同的DAO工具使用，但是学习它最简单的方法是运行它的示例项目"gsg-jbooox"，这是个微型的开箱即用的服务端整合包。  
"gsg-jbooox"项目需Java8或以上，用git clone下载后, 有几种运行方式:    

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

### 常见问题 | FAQ
* 安全上有没有问题?  
没有安全问题，但有信息泄露问题。在java方法里，可以手工进行参数合法性检查，不存在安全漏洞。但是在qry这类SQL方法里，没有进行参数校验的语句，如果遇到客户端恶意传入非法SQL参数，例如想要调入用户A的账户，却传入了用户C的账户ID，这时候就会出现非法存取，甚至出现扫表情况，整张表的信息都会泄露。qry系列方法可以防止SQL注入攻击，但不能防止扫表造成信息泄露。  
为什么有这么大的信息泄露漏洞还要保留SQL类方法?  
因为作者认为方便性要大于安全性，这是故意这样设计的。例如对于github、FB、OSC这种社交类网站，除了用户密码外，大多数表格内容都是可以公开的，即使出现非法参数或扫表，也不是件大事。当然扫表太历害就成了DDOS攻击了。对于不允许扫表攻击的表格，可以在后台qry模板上加拦截器登录、防扫表检查，或强制只能用java方法，先对输入参数进行合法性检查。  

* 为什么示例项目gsg-jBooox采用jSqlBox这么小众的DAO工具?  
因为jSqlBox是本人写的DAO工具，打广告用的，它的DAO功能很全，可以号称SQL写法百科全书。如果前端对jSqlBox不感冒，可以对公司的后端提要求，搭配不同的DAO工具即可。GoSqlGo的所有方法命名和后台模板是可以自行定制的。目前只有gsg-jbooox示例，今后可能发行SpringBoot、jFinal等多个示范项目。  
GoSqlGo与其说是个产品，不如说是个概念，稍有经验的后端都可以在它的基础上开发出支持其它ORM、MVC框架的后端平台, GoSqlGo核心是一个编译、打包工具，它不是一个框架。  

* (小鹏提出)Java写在HTML/Javascript里没有错误检查、语法提示，及重构功能。  
这个将来可以通过IDE插件解决，但目前只能运行goServ.bat批处理将Sql/Java抽取成Java源码类，在IDE里找错、更正后再用goFront.bat批处理塞回到HTML里去，也可以干脆就不塞回去了，后者就对应传统的前后端分离开发情形。   

* 与Node.js相比
GoSqlGo与Node.js本质上类似，是运行在服务端的，但它区别在:1)可以将后端代码直接写在前端 2)采用Java语言，适合于对Java有偏好的人群，事实上Java也是后端的主流语言。  

* 与GraphQL相比
1)GraphQL相当于一种特殊的查询语法,相比于GoSqlGo直接使用SQL和Java，在使用体验上还是有差距的。2)GraphQL有安全性问题，因为它允许根据用户传入的动态结构在服务器上拉取数据，这就造成可攻击的漏洞太多了，非常难以防范，这是架构设计问题，这个缺陷无法根除。所以GraphQL通常只能用于内容发布类网站如Facebook等，或仅充当简单的通信接口。GoSqlGo在发布后它的SQL是不可见的，只允许客户端改变输入参数，而不能改变SQL本体或业务逻辑，所以更安全。  

* 为什么没有https配置的演示
这个是运维人员和运维阶段要考虑的事，难度不大，没有演示的必要。  

## 相关开源项目 | Related Projects
- [ORM数据库工具 jSqlBox](https://gitee.com/drinkjava2/jSqlBox)  
- [IOC/AOP工具 jBeanBox](https://gitee.com/drinkjava2/jBeanBox)   
- [服务端布局工具 jWebBox](https://gitee.com/drinkjava2/jWebBox)  

## 期望 | Futures
GoSqlGo已发布，对它感兴趣的请加关注，或发issue提出完善意见。也欢迎同学们提交GoSqlGo演示示例，GoSqlGo如果用好了，结合前端的可视化组件，可以实现类似Delphi一样的开发效率。  

## 版权 | License
[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

## 关注我 | About Me
[Github](https://github.com/drinkjava2)  
[码云](https://gitee.com/drinkjava2)  

## 点赞 | Star
点赞很重要，必须的