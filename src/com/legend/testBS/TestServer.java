package com.legend.testBS;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {
	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = null;
		Socket socket = null;
		OutputStream ops = null;
		try {
			//1.创建ServerSocket对象 监听本机8080端口
			serverSocket = new ServerSocket(8080);
			
			while(true) {
				//2.等待来自客户端的请求获取 和客户端对应的socket对象
				socket = serverSocket.accept();
				
				//3.通过获取Socket对象获取到对应输出流对象
				ops = socket.getOutputStream();
				//4.通过获取的输出流对象将HTTP协议的响应部分发送给客户端
				ops.write("HTTP/1.1 200 OK\n".getBytes());
				ops.write("Content-Type:text/html; charset=UTF-8\n".getBytes());
				ops.write("Server:Tengine\n".getBytes());
				ops.write("\n\n".getBytes());
				
				StringBuffer buf = new StringBuffer();
				buf.append("<html>");
				buf.append("<title>传智播客</title>");
				buf.append("<body>");
				buf.append("<h1>I am Legend</h1>");
				buf.append("<a href='http://www.baidu.com'>百度</a>");
				buf.append("</body>");
				buf.append("</html>");
				
				ops.write(buf.toString().getBytes());
				ops.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			//5.释放资源
			if(ops!=null) {
				ops.close();
				ops = null;
			}
			if(socket!=null) {
				socket.close();
				socket = null;
			}
		}
	}

}
