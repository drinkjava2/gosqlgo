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
package com.demo;

import java.io.File;

import io.undertow.Undertow;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;

/**
 * The application
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
public class MainApp {

	// see
	// http://undertow.io/undertow-docs/undertow-docs-2.0.0/index.html#creating-a-servlet-deployment
	public static void main(String[] args) throws Exception {
		String projectFolder = new File("").getAbsolutePath();
		String webAppFolder = projectFolder + "/src/main/webapp";
		System.out.println("projectFolder=" + projectFolder);
		System.out.println("classAbsPath=" + Class.class.getClass().getResource("/").getPath());
		System.out.println("webAppFolder=" + webAppFolder);

		DeploymentInfo info = Servlets.deployment().setClassLoader(MainApp.class.getClassLoader()).setContextPath("/")
				.setDeploymentName("gsgdemo");
		info.addServlet(Servlets.servlet("initConfig", InitConfig.class).setLoadOnStartup(0));
		info.addServlet(Servlets.servlet("dispatch", GsgDispatch.class).addMapping("*.gsg"));
		info.setResourceManager(new FileResourceManager(new File(webAppFolder), 0)).addWelcomePage("/page/demo1.html");

		DeploymentManager manager = Servlets.defaultContainer().addDeployment(info);
		manager.deploy();

		Undertow server = Undertow.builder().addHttpListener(80, "localhost").setHandler(manager.start()).build();
		server.start();
		Runtime.getRuntime().exec("cmd /c start http://127.0.0.1");
		System.out.println("Undertow server started!");
	}

}
