package com.legend.tomcatv2;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 所有服务端的Java小程序要实现的接口
 * @author legend
 *
 */
public interface Servlet {
	//初始化
	public void init();
	
	//服务
	public void Service(InputStream is,OutputStream ops) throws Exception;
	
	//销毁
	public void destory();
}
