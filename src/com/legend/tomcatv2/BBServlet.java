package com.legend.tomcatv2;

import java.io.InputStream;
import java.io.OutputStream;
/**
 * BBServlet小程序
 * @author legend
 *
 */
public class BBServlet implements Servlet {

	@Override
	public void init() {
		// TODO Auto-generated method stub
		System.out.println("bbServlet....");
	}

	@Override
	public void Service(InputStream is, OutputStream ops) throws Exception {
		// TODO Auto-generated method stub
		ops.write("I am come from BBServlet...Service".getBytes());
		ops.flush();
	}

	@Override
	public void destory() {
		// TODO Auto-generated method stub
		System.out.println("BBServlet destory");
	}

}
