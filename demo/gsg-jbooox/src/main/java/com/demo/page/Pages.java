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
package com.demo.page;

import com.github.drinkjava2.jwebbox.WebBox;

/**
 * Pages change /xxx.htm calls to xxx WebBox instance
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
@SuppressWarnings("all")
public class Pages {

	public static class homepage extends WebBox {
		{
			this.setPage("/page/home.html");
		}
	}

	public static class page404 extends WebBox {
		{
			this.setPage("/page/404.html");
		}

	}

	public static class errorPage extends WebBox {
		{
			this.setText("Oops, Error found.");
		}

		public errorPage() {
		}

		public errorPage(String msg) {
			this.setText(msg);
		}
	}

	public static class emptyPage extends WebBox {
		{
			this.setText("");
		}
	}

}
