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

public class ThreadedClient {

	GUI gui;
	Socket s;

	public ThreadedClient(GUI gui) {
		this.gui = gui;
	}

	public class SendThread extends Thread {

		private Socket s;

		public SendThread(Socket s) {
			this.s = s;
		}

		public void run() {
			try {
				OutputStream os = s.getOutputStream();
				DataOutputStream dos = new DataOutputStream(os);

				while (true) {
					String str = gui.getSendMessage();
					dos.writeUTF(str);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public class RecieveThread extends Thread {

		private Socket s;

		public RecieveThread(Socket s) {
			this.s = s;
		}

		public void run() {
			try {
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


	public void startRecvThread() {
			new RecieveThread(s).start();
	}
	
	public void connectRemoteServer() {
		try {
			int clientPort = Integer.parseInt(gui.getClientPort());
			s = new Socket(gui.getClientIP(), clientPort);
			System.out.println(gui.getClientIP());
			gui.appendRecv("连接到服务器:" + gui.getClientIP() + ":" + clientPort);

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
		gui.setFrameName("Client");
		ThreadedClient client = new ThreadedClient(gui);
		
		gui.bClient.addActionListener(new ActionListener() {

			// 当按钮被点击时，就会触发 ActionEvent事件
			// actionPerformed 方法就会被执行
			public void actionPerformed(ActionEvent e) {
				client.connectRemoteServer();
				// 启动接受消息线程
				client.startRecvThread();
				
			}
		});
		
		
		gui.bSend.addActionListener(new ActionListener() {

			// 当按钮被点击时，就会触发 ActionEvent事件
			// actionPerformed 方法就会被执行
			public void actionPerformed(ActionEvent e) {
					client.sendMessage(client.getSocket(), gui.getSendMessage());
			}
		});

		gui.show();

	}

}
