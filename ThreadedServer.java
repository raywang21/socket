package socket;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;

//每个机器都启动自己的一个Server，以Thread方式不断运行，直到StopFlag为True为止
//发送之后Server会给一个回复确认，让发送方知道Server已经收到。
public class ThreadedServer {

	SocketGUI gui;
	ServerSocket ss;
	boolean stopServerFlag;

	public ThreadedServer(SocketGUI gui) {
		this.gui = gui;
	}

	public class RecieveThread extends Thread {

		public void run() {

			while (stopServerFlag == false) {
				try {
					Socket s = ss.accept();
					
					//此处如果处理时间较长（比如传输文件的操作），就创立一个进程来执行
					//这种父进程套用子进程的形式，在连接数较多的情况下，有可能造成进程的创立和销毁占用大量时间
					//那时也可以采用进程池的方式来处理
					InputStream is = s.getInputStream();

					DataInputStream dis = new DataInputStream(is);
					String msg = dis.readUTF();
					System.out.println("server get" + msg.length());
					if (msg.length() > 0 )
					   gui.appendRecv(msg);

					// 回复说消息已经收到
					PrintWriter writer = new PrintWriter(s.getOutputStream());
					writer.println("已收到消息");
					writer.flush();

					s.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} // while

			try {
				if (ss != null)
					ss.close();
				//gui.appendRecv("Server Stopped");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} // run

	}

	public void startServer() {
		int serverPort = Integer.parseInt(gui.getServerPort());
		System.out.println(serverPort);
		gui.appendRecv("监听在端口号:" + serverPort);
		try {
			ss = new ServerSocket(serverPort);
			stopServerFlag = false;
			// 启动接受消息线程
			new RecieveThread().start();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopServer() {
		stopServerFlag = true;
		//由于ServerSocket.accept方法一直阻塞在那里
		//此处由Server向自己发一个消息来触发accept向下执行
		int serverPort = Integer.parseInt(gui.getServerPort());
		try {
			Socket s = new Socket(ss.getInetAddress(), serverPort);
			OutputStream os = s.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);

			String str = "Server Stop";
				dos.writeUTF(str);
				
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public static void main(String[] args) {

		SocketGUI.setLookAndFeel();
		SocketGUI gui = new SocketGUI();
		gui.setFrameName("Server");

		ThreadedServer server = new ThreadedServer(gui);

		gui.bServerStart.addActionListener(new ActionListener() {

			// 当按钮被点击时，就会触发 ActionEvent事件
			// actionPerformed 方法就会被执行
			public void actionPerformed(ActionEvent e) {
				server.startServer();
			}
		});

		gui.bServerStop.addActionListener(new ActionListener() {

			// 当按钮被点击时，就会触发 ActionEvent事件
			// actionPerformed 方法就会被执行
			public void actionPerformed(ActionEvent e) {
				server.stopServer();
			}
		});
		
		gui.show();

	}

}
