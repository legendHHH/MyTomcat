package com.legend.tomcatv2;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * AAServlet小程序
 * @author legend
 *
 */
public class AAServlet implements Servlet {

	@Override
	public void init() {
		// TODO Auto-generated method stub
		System.out.println("aaServlet....");
	}

	@Override
	public void Service(InputStream is, OutputStream ops) throws Exception {
		// TODO Auto-generated method stub
		ops.write("I am come from AAServlet...Service".getBytes());
		ops.flush();
	}

	@Override
	public void destory() {
		// TODO Auto-generated method stub
		System.out.println("BBServlet....destory");
	}

}
