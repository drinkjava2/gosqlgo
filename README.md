### 声明
## 本项目架构上没有什么安全问题，有质疑安全、注入问题的，在吐槽之前请将本说明完整看完

### GoSqlGo简介 | Description
天下武功，唯快不破，程序无非就是接收用户输入、存到数据库。GoSqlGo能让前端直接存取数据库，独立完成项目开发。

### 缘起 | Origin
一直认为，开发效率最高的方式不是让MVC架构极简(SpringBoot/jFinal)，而是彻底省略掉MVC架构和后端程序员，直接由前端搞定一切，由多层架构变成两层，在前端直接写SQL，缩短界面和数据库之间的距离，才是最快的开发途径。基于此理念，在2011年本人在[这里](https://blog.csdn.net/drinkjava/article/details/6935539)写了一句预言，没想到技术的发展如此之慢，现在要自己亲手去实现它了，这就是GoSqlGo项目，如果名字翻译成中文，可以翻成"Sql冲冲冲冲冲"，这个比较形象，它表达了SQL为王，一路狂奔，冲到了前端的意思。  

GoSqlGo是一个运行于后端的软件，它的最大特点就是在运行期动态编译客户端Java代码，所有SQL和Java代码都可以在前端Html页面完成，可以彻底甩掉后端。开发完成后再利用打包工具将SQL和Java从前端移到后端，以实现安全。  

GoSqlGo的竞品是GraphQL之类在可以在前端进行业务操作的工具，但区别在于：1.GoSqlGo支持直接在前端写SQL，而GraphQL不支持 2.GoSqlGo支持在前端写Java语句, GraphQL不支持。3.GraphQL之类工具是基于API，而GoSqlGo是不存在API的(它也可以生成API，只是没必要)，没有API可以消除前后端勾通成本，尤其是查询，只要开启了GoSqlGo服务，所有的查询操作都可以在前端独立完成，不需要后端程序员参与。  

### 适用场合 | Applications
最适用于快速、原型开发、业务逻辑简单、业务与页面高度绑定的场合。对于复杂的业务、需要考虑业务重用、以及实现特殊功能（如文件上传等)的场合不适用。GoSqlGo是独立的服务，通常使用token进行签权，所以可以与任意项目混搭使用，可以开启一个GoSqlGo服务实现与页面绑定的、简单的CRUD工作，而用传统的开发模式去实现复杂的、需要重用的业务。理论上只要开启了GoSqlGo服务，所有的查询操作都可以在前端独立完成，不需要后端程序员参与。  

### 简介 | Features
用一个例子来说明直接在前端写SQL和Java代码，以下示例实测通过，文件位于[这里](https://gitee.com/drinkjava2/gosqlgo/blob/master/demo/gsg-jbooox/src/main/webapp/page/demo1.html)。  
这是一个转账业务的演示，开发阶段把所有的SQL和业务逻辑都写在html里面，在布署阶段再由打包工具抽取到服务端：     
```
<!DOCTYPE html>
<html>
 <head>
 <script src="/js/jquery-1.11.3.min.js"></script>
 <script src="/js/jquery-ajax-ext.js"></script>
 <script src="/js/gosqlgo.js"></script>
 </head>
 <body>
      
    <script>document.write($java(`return new WebBox("/WEB-INF/menu.html");`)); </script>
    <h2>Transaction demo, use jQuery</h2>
    
    <script> 
	  function getUserListHtml(){
		  var users=$qryMapList(`select * from account where amount>=? order by id`,0);
		  var html="User List:<br/>";
		  for(var i=0;i<users.length;i++) 
			  html+="User ID:" +  users[i].ID+", AMOUNT:"+ users[i].AMOUNT+"<br/>"; 
	      return html;		   
	  }
	</script>
	   
	<div id="msgid" class="msg"></div> 
	
	<section>
		<header>Account A</header>
		<div id="A" class="amount">
			<script>
				document.write($qryObject(`select amount from account where id=? and amount>=?`, 'A',0));
			</script>
		</div>
	</section>
	<section>
		<header>Account B</header>
		<div id="B" class="amount">
			<script>
			    account=$qryEntity(`com.demo.entity.Account, select * from account where id=?`, 'B');
				document.write(account.amount);
			</script>
		</div>
	</section>
	<script>
	  function transfer(from, to, money){ 
			var rst = $$javaTx(`
					int money = Integer.parseInt($3);
					if (money <= 0)
					     return new JsonResult(0, "Error: Illegal input.");
					Account a = new Account().setId($1).load();
					if (a.getAmount() < money)
					     return new JsonResult(0, "Error:No enough balance!");
					Account b = new Account().setId($2).load();
					a.setAmount(a.getAmount() - money).update();
					b.setAmount(b.getAmount() + money).update();
					    return new JsonResult(200, "Transfer success!").setDataArray(a.getAmount(), b.getAmount());
			        `, from,to,money); 
		  $("#msgid").text(rst.msg);	
		  if(rst.code==200) { 
	 	      $("#"+from).text(rst.data[0]);
	 	      $("#"+to).text(rst.data[1]);
	 	      $("#msgid").css("background", "#dfb");
		  }
		  else $("#msgid").css("background", "#ffbeb8");		  
		}
	</script>
	<section>
		<header>Transfer</header>
		<form onsubmit="return false" action="##" method="post">
			<input id="amount" name="amount" value="100" class="amount">
			<button name="btnA2B" value="true" onclick="transfer('A','B', $('#amount').val())">From account A to account B</button>
			<button name="btnB2A" value="true" onclick="transfer('B','A',$('#amount').val())">From account B to account A</button>
		</form>
	</section>
 </body>
</html>
```
另外还有两个演示:  
[演示2](https://gitee.com/drinkjava2/gosqlgo/blob/master/demo/gsg-jbooox/src/main/webapp/page/demo2.html)：GoSqlGo结合Vue的使用。  
[演示3](https://gitee.com/drinkjava2/gosqlgo/blob/master/demo/gsg-jbooox/src/main/webapp/page/demo3.html): 演示直接在前端进行表单的输入检查并保存到数据库  

### 运行 | Dependency and Run
GoSqlGo项目分为两个目录，core目录为GoSqlGo内核包，用户不需要关心, server目录是一个示范项目，使用时用户只需要将内容作一些修改即可以运行，如更改数据库连接和签权方式的自定义。  
在windows下点击server目录下的\start_server.bat批处理，并用用户名demo、密码123登录即可运行。  

第一次运行后，双击goServer.bat，即可将前端的java和Sql方法抽取到后端，再次运行start_server.bat就可以看到前端HTML里已不存在java和sql源码了，这就实现了安全性。  

示范项目里主要有以下方法：
```
$java(String, Object...) 执行多行服务端Java语句。第一个参数是Java本体，后面是参数，在Java里可以用$1,$2...来访问。  
$javaTx(String, Object...) 执行多行服务端Java语句并开启事务，如果有异常发生，事务回滚。  
$qryObject(String, Object...) 将SQL查询结果的第一行第一列对象返回，第一个参数是SQL，后面是SQL参数，下同  
$qryArray(String, Object...)  返回SQL查询的第一行数据  
$qryArrayList(String, Object...)  返回多行查询结果    
$qryTitleArrayList(String, Object...)  返回多行查询结果，第一行内容是各个列的标题  
$qryMap(String, Object...) 返回SQL查询的第一行数据，为Map 格式  
$qryMapList(String, Object...)  返回SQL查询的多行数据，为List<Map>格式  
$qryEntity(String, Object...)  返回第一行数据为实体对象，SQL写法是实体类名+逗号+SQL, 示例:$qryEntity(`a.b.Demo, select * from demo`); 
$qryEntityList(String, Object...)  返回多行数据为List<实体>对象，SQL写法是实体类名+逗号+SQL, 示例:$qryEntityList(`a.b.Demo, select * from demo`);   
```
注意以上方法都是自定义的，用户也可以自定义自忆的方法。以上方法还可以用$$开头返回JSON对象。JSON对象有{code, msg, data, debugInfo} 4个字段，但debugInfo字段仅当服务端配置为debug_info=true时才有值。  
GoSqlGo方法可以添加以下两类特殊语句：
 1. #xxxxx 形式的ID，用来自定义方法ID，如没有这个ID，方法缺省ID为"Default"
 2. import开头的Java包引入语句。  
示例：   
$java('#ReadAmount import abc.DemoUser; return new DemoUser().loadById($1).getAmount();', 'u1');   

在类根目录(项目的resources目录)下，有一个名为GoSqlGo.properties的配置文件，可以进行一些配置，例如配置deploy目录、设定开发/生产阶段、设定develop_token和debug_inifo等
注意以上方法必须用键盘ESC下方的单引号括起来，这是Javascript的特殊单引号，支持多行文本，不能用普通的单引号。 

开发阶段：GoSqlGo示范项目在服务端运行，它自带一个动态编译工具，前端发来的SQL和Java片段，被动态编译为Java类，并调用服务端DAO工具，最后返回JSON对象。  
GoSqlGo如果方法前是两个$符号，如$$java，则返回一个JSON对象，它的data字段保存了返回结果。如果方法前只有一个$符号，如$java，返回的值直接就是javascript对象了，也就是Json的data字段。
 
### 布署 | Deploy
布署阶段：双击运行打包工具goServer.bat，将前端所有的SQL和原生Java片段抽取到服务端去，转变为可调试的Java源文件，原有客户端的SQl和JAVA代码在打包后将成为类似于$gsg('Xxxx_C9GK90J27','A');之类的通过ID进行的调用，以实现安全性。 server目录下还有一个文件名为goFront.bat，这个是打包的逆操作，可以将后端的Java代码移回到前端。

### 安全 | Security
在示范项目的gosqlgo.properties文件里，有以下关于安全的设定：  
1.stage设定，可以设定为develop或product，当设定为product阶段时，不再接收前端传来的SQL和Java片段  
2.deveop_token设定，这个设定仅在develop阶段生效，开发阶段前端传来的develop_token必须与服务器的设定一致，否则拒绝访问，这样可以排除非法访问。  
3.token_security设定，这是用来登记用户自定义的登录检查和token验证逻辑类，使用详见示范项目，GoSqlGo并没有集成第三方权限框架，所以需要用户根据自已的项目实现自定义的登录和token验证类。通常是根据token和方法ID来判定是否允许执行GoSqlGo方法。

### 常见问题 | FAQ
* 安全上有没有问题?  
架构上没有安全问题，GoSqlGO可以通过token和方法ID，结合自定义方法来检查每一个GoSqlGo调用是否有执行权限。当然java代码有可能出现安全漏洞，但这个与GoSqlGo无关了。  

* 为什么示例项目gsg-jBooox采用jSqlBox这么小众的DAO工具?  
因为jSqlBox是本人写的DAO工具，打广告用的，它的DAO功能很全，可以号称SQL写法百科全书。如果前端对jSqlBox不感冒，可以仿照示例改用不同的DAO工具。  

* (小鹏提出)Java写在HTML/Javascript里没有错误检查、语法提示，及重构功能。  
这个将来可以通过IDE插件解决，但目前只能运行goServ.bat批处理将Sql/Java抽取成Java源码类，在IDE里找错、更正后再用goFront.bat批处理塞回到HTML里去，也可以干脆就不塞回去了，后者就对应传统的前后端分离开发情形。  

## 相关开源项目 | Related Projects
- [ORM数据库工具 jSqlBox](https://gitee.com/drinkjava2/jSqlBox)  
- [IOC/AOP工具 jBeanBox](https://gitee.com/drinkjava2/jBeanBox)   
- [服务端布局工具 jWebBox](https://gitee.com/drinkjava2/jWebBox)  

## 期望 | Futures
GoSqlGo已发布，对它感兴趣的请加关注，或发issue提出完善意见。也欢迎同学们提交GoSqlGo演示示例。

## 版权 | License
[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

## 关注我 | About Me
[Github](https://github.com/drinkjava2)  
[码云](https://gitee.com/drinkjava2)   
