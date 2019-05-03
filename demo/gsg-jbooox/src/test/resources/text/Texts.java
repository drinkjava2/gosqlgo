/* Copyright 2018-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package text;

import com.github.drinkjava2.gosqlgo.util.Txt;

/**
 * Store multiple line Strings for unit test
 */

public class Texts {

	public static class JavaPiece extends Txt {
		/*-SERV FRONT #GetAmount import java.lang.Object;
			int money=Integer.parseInt($3);
			if(money<=0) 
			  throw new SecurityException("Money<=0, IP:"+ getRequest().getRemoteAddr());
			Account a=new Account().setId($1).load();
			if(a.getAmount()<money)
			   return "No enough balance!";
			Account b=new Account().setId($2).load();
			a.setAmount(a.getAmount()-money).update();
			b.setAmount(b.getAmount()+money).update(); 
			return "Transfer Success!|"+a.getAmount()+"|"+b.getAmount();
		 */
	}

	// for DynamicCompileEntine test
	public static class JavaA extends Txt {
		/*- 
		 package com.demo.deploy;
		 public class A {
		  public String name="a"; 
		 } 
		 */
	}
	 
	 
	// for DynamicCompileEntine test
	public static class JavaB extends Txt {
		/*- 
		 package com.demo.deploy;
		 import com.demo.deploy.A;
		 public class B { 
		 
		  public B(){
		   A a=new A();   
		   System.out.println("aClass in B ="+A.class.getName());
		   System.out.println("aClass in B hashCode="+A.class.hashCode());
		   System.out.println(A.class.getClassLoader());
		   
		   System.out.println("aClass in B ="+a.getClass().getName());
		   System.out.println("aClass in B hashCode="+a.getClass().hashCode());
		   System.out.println(a.getClass().getClassLoader()); 
		  } 
		 } 
		 */
	}

}