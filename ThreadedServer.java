package socket;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class ThreadedServer {

	GUI gui;
	ServerSocket ss;
	Socket s;

	public ThreadedServer(GUI gui) {
		this.gui = gui;
	}

	
	public class RecieveThread extends Thread {

		public void run() {
			try {
				s =  ss.accept();
				InputStream is = s.getInputStream();

				DataInputStream dis = new DataInputStream(is);
				while (true) {
					String msg = dis.readUTF();
					gui.appendRecv(msg);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void startServer() {
		int serverPort = Integer.parseInt(gui.getServerPort());
		System.out.println(serverPort);
		gui.appendRecv("监听在端口号:" + serverPort);
		try {
			ss = new ServerSocket(serverPort);
	
			// 启动接受消息线程
			new RecieveThread().start();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopServer() {
		try {
			ss.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Socket getSocket() {
		return s;
	}

	public void sendMessage(Socket s, String str) {
		try {
			if (s != null) {
				OutputStream os = s.getOutputStream();
				DataOutputStream dos = new DataOutputStream(os);

				dos.writeUTF(str);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		GUI.setLookAndFeel();
		GUI gui = new GUI();
		gui.setFrameName("Server");
		
		ThreadedServer server = new ThreadedServer(gui);
		
		gui.bServer.addActionListener(new ActionListener() {

			// 当按钮被点击时，就会触发 ActionEvent事件
			// actionPerformed 方法就会被执行
			public void actionPerformed(ActionEvent e) {
				server.startServer();
			}
		});

		gui.bSend.addActionListener(new ActionListener() {

			// 当按钮被点击时，就会触发 ActionEvent事件
			// actionPerformed 方法就会被执行
			public void actionPerformed(ActionEvent e) {
					server.sendMessage(server.getSocket(), gui.getSendMessage());
			}
		});

		gui.show();

	}

}
