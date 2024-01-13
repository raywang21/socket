package socket;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

//如果要发送消息给其他机器，只在发送消息的时刻建立Socket，发送之后就关闭
//发送时给出自己的地址和Server端口，便于后续Server端回复
public class ThreadedClient {

	SocketGUI gui;
	Socket s;

	public ThreadedClient(SocketGUI gui) {
		this.gui = gui;
	}

	public class SendThread extends Thread {

		public void run() {
			try {
				
				int clientPort = Integer.parseInt(gui.getClientPort());
				s = new Socket(gui.getClientIP(), clientPort);
				//System.out.println(gui.getClientIP());
				gui.appendRecv("连接到服务器:" + gui.getClientIP() + ":" + clientPort);
				
				OutputStream os = s.getOutputStream();
				DataOutputStream dos = new DataOutputStream(os);

				String str = "对方发送消息：" + gui.getSendMessage();
				InetAddress host = InetAddress.getLocalHost();
				str = str + "，回复地址是" + host.getHostAddress(); 
				System.out.println("client send bytes " + str.length());
					dos.writeUTF(str);
						
				//接受Server回复的确认消息
				BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		        String response = reader.readLine();
		        gui.appendRecv("对方响应：" + response);
		        
		        s.close();
					

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void startSendThread() {
			new SendThread().start();
	}
	

	
	public static void main(String[] args) {

		SocketGUI.setLookAndFeel();
		SocketGUI gui = new SocketGUI();
		
		gui.setFrameName("Client");
		ThreadedClient client = new ThreadedClient(gui);
		
		gui.bSend.addActionListener(new ActionListener() {

			// 当按钮被点击时，就会触发 ActionEvent事件
			// actionPerformed 方法就会被执行
			public void actionPerformed(ActionEvent e) {
					// 启动接受消息线程
				client.startSendThread();
				
			}
		});
		
		
		gui.show();

	}

}
