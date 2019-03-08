package com.legend.testBS;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TestClient {

	public static void main(String[] args) throws Exception {
		Socket socket = null;
		
		InputStream is = null;
		OutputStream ops = null;
		try {
			//1.建立一个Socket对象,连接itcastcn域名的80端口
			socket = new Socket("www.itcast.cn",80);
			
			//2.获取输出流对象
			is = socket.getInputStream();
			
			//3.获取输入流对象
			ops = socket.getOutputStream();
			
			//4.将HTTP协议的请求部分发送到服务端 /subject/about/index.html
			ops.write("GET /subject/about/index.html HTTP/1.1\n".getBytes());
			ops.write("HOST:www.itcast.cn\n".getBytes());
			ops.write("Content-Type:text/html; charset=UTF-8\n".getBytes());
			ops.write("\n".getBytes());
			
			//5.读取来自服务端的数据打印控制台
			int i = is.read();
			while(i!=-1) {
				System.out.print((char)i);
				i = is.read();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			//6.释放资源
			if(is!=null) {
				is.close();
				is=null;
			}
			if(ops==null) {
				ops.close();
				ops=null;
			}
			if(socket==null) {
				socket.close();
				socket=null;
			}
		}
		
	}
}
