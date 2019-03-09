package com.legend.tomcatv2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


public class TestServer {
	
	//定义一个变量,存放服务端WebContent目录的绝对路径
	public static String WEB_ROOT = System.getProperty("user.dir")+"\\"+"WebContent"+"";
	
	//定义一个变量，用于存放本次请求的静态页面名称
	private static String url="";
	
	//定义一个静态类型map,存储服务端conf.properties中的配置信息
	private static Map<String,String> map = new HashMap<String,String>();

	//服务器启动之前将配置参数中的信息加载到MAP中
	static {
		//创建一个Properties对象
		Properties prop = new Properties();
		try {
			//加载WebContent目录下的conf.properties文件
			//prop.load(new FileInputStream(WEB_ROOT+"\\conf.properties"));
			prop.load(new FileInputStream("src\\conf.properties"));
			//将配置文件中的数据读取到map对象中
			Set set = prop.keySet();
			Iterator iterator = set.iterator();
			while(iterator.hasNext()) {
				String key = (String) iterator.next();
				String value = prop.getProperty(key);
				 map.put(key, value);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//启动服务器
	public static void main(String[] args) throws Exception {
		//System.out.println(WEB_ROOT);
		System.out.println(map);
		
		//创建ServerSocket对象
		ServerSocket serverSocket = null;
		
		//创建Socket对象
		Socket socket = null;
		
		//输入输出流
		InputStream is = null;
		OutputStream ops = null;
		
		try {
			//创建ServerSocket,监听本机的8080端口,等待来自客户端的请求
			serverSocket = new ServerSocket(8080);
			
			//让所有的客户端请求都能过来
			while(true) {
				//获取客户端对应的socket
				socket = serverSocket.accept();
				
				//获取输入流对象
				is = socket.getInputStream();
				
				//获取到输出流对象
				ops = socket.getOutputStream();
				//获取HTTP协议的请求部分,截取客户端要访问的资源名称,将这个资源名称赋值给url
				parse(is);
				
				//判断本次请求的是静态demo.html 页面还是运行在服务端一段Java小程序
				if(url.indexOf(".")!=-1) {
						//发送的是静态资源文件   有.
						sendStaticResource(ops);
					}else {
						//发送动态资源
						sendDynamicResource(is,ops);
					}
				}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			//释放资源
			if(null!=is) {
				is.close();
				is = null;
			}
			if(null!=ops) {
				ops.close();
				ops = null;
			}
			if(null!=socket) {
				socket.close();
				socket = null;
			}
		}
	}
	

	//发送动态资源
	private static void sendDynamicResource(InputStream is, OutputStream ops) throws Exception, ClassNotFoundException {
		//将HTTP协议的响应头和响应行发送到客户端
		ops.write("HTTP/1.1 200 OK\n".getBytes());
		ops.write("Server:Apache\n".getBytes());
		ops.write("Content-type:text/html;charset=utf-8\n".getBytes());
		ops.write("\n".getBytes());
		
		//判断map中是否存在一个key,这个key上是否和本次带请求的资源路径一致
		if(map.containsKey(url)) {
			//如果包含指定的key，获取到map中key对应的value
			String value = map.get(url);
			
			//通过执行反射将对应的Java程序加载到内存
			Class clazz = Class.forName(value);//这里不能写泛型就是   Class<?>
			Servlet servlet = (Servlet)clazz.newInstance();
			
			//执行init
			servlet.init();
			
			//执行service
			servlet.Service(is, ops);
		}
		
	}

	//发送静态资源
	private static void sendStaticResource(OutputStream ops) throws IOException {
		// 定义一个字节数组,用于存放本次请求的静态资源test.html
		byte[] bytes = new byte[2048];
		
		//定义一个文件输入流,用户获取静态资源文件中的内容
		FileInputStream fis = null;
		
		try {
			//创建文件对象File,代表本次要求请求的资源test.html
			File file = new File(WEB_ROOT,url);
			//如果文件存在
			if(file.exists()) {
				//向客户端输出Http协议的响应行/响应头
				ops.write("HTTP/1.1 200 OK\n".getBytes());
				ops.write("Server:apache-Coyote/1.1\n".getBytes());
				ops.write("Content-Type:text/html;charset=utf-8\n".getBytes());
				ops.write("\n".getBytes());
				
				//获取到文件输入流对象
				fis = new FileInputStream(file);
				
				//读取静态资源test.html
				int ch = fis.read(bytes);
				
				while (ch != -1) {
					// 将读取到数组中的内容通过输出流发送到客户端
					ops.write(bytes, 0, ch);
					ch = fis.read(bytes);
				}
			} else {
				// 如果文件不存在
				ops.write("HTTP/1.1 404 not found\n".getBytes());
				ops.write("Server:apache-Coyote/1.1\n".getBytes());
				ops.write("Content-Type:text/html;charset=utf-8\n".getBytes());
				ops.write("\n".getBytes());
				
				// 向客户端响应不存在信息
				String errorMessager = "File Not Found";
				ops.write(errorMessager.getBytes());
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			// 释放文件输入流对象
			if(null!=fis) {
				fis.close();
				fis = null;
			}
		}
	}
	
	//获取HTTP协议的请求部分,截取客户端要访问的资源名称,将这个资源名称赋值给url
	public static void parse(InputStream is) throws Exception {
		//定义一个变量,存放HTTP协议请求部分数据
		StringBuffer content = new StringBuffer(2048);
		
		//定义一个数组,存放HTTP协议请求部分数据
		byte[] buffer = new byte[2048];
		
		//定义一个变量i,代表读取数据到数组中之后,数据量的大小
		int i = -1;
		
		//读取客户端发送过来的数据,将数据读取到字节数组buffer中,i代表读取数据量的大小 311字节
		i = is.read(buffer);
		
		//遍历字节数组,将数组中的数据追加到content变量中
		for (int j = 0; j < i; j++) {
			//要转换成字符再追加
			content.append((char)buffer[j]);
		}
		//打印HTTP协议请求部分数据
		System.out.println(content);
		
		//截取客户端要请求的资源路径  /test.html 复制给url
		parseUrl(content.toString());
	}

	
	
	/**
	 * GET /test2.html HTTP/1.1
		Host: localhost:8080
		
		截取两个空格之间的字符
	 * @param content
	 */
	private static void parseUrl(String content) {
		//定义两个变量,存放请求的2个空格
		int index1,index2;
		
		//获取http请求部分第1个空格的位置
		index1 = content.indexOf(" ");
		
		
		//获取到了第一个空格
		if(index1!=-1) {
			//从指定的位置开始查找后面空格出现的位置
			index2 = content.indexOf(" ",index1+1);
			
			//获取http请求部分 第2个空格的位置
			if(index1<index2) {
				//截取字符串获取到本次请求资源的名称
				url = content.substring(index1+2, index2);
			}
		}
		//打印本次请求静态资源的名称
		System.out.println(url); 
	}
	
}
