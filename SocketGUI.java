package socket;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class SocketGUI {

	JFrame f = new JFrame("SocketTest");

	{
		f.setSize(1000, 600);
		f.setLocation(200, 200);
		f.setLayout(null);
	}

	JPanel pServer = new JPanel();

	JLabel lIP = new JLabel("Local IP:");
	JLabel lPort = new JLabel("Port:");
	JTextField tIP = new JTextField(16);
	JTextField tPort = new JTextField(8);
	JButton bServerStart = new JButton("Start Server");
	JButton bServerStop = new JButton("Stop Server");
	
	{
		pServer.setLayout(new FlowLayout());
		tIP.setText("127.0.0.1");
		tPort.setText("8888");
		pServer.add(lIP);
		pServer.add(tIP);
		pServer.add(lPort);
		pServer.add(tPort);
		pServer.add(bServerStart);
		pServer.add(bServerStop);
	}

	JPanel pClient = new JPanel();

	JLabel lIP1 = new JLabel("Remote IP:");
	JLabel lPort1= new JLabel("Port:");
	JTextField tIP1 = new JTextField(16);
	JTextField tPort1 = new JTextField(8);
	JButton bSend = new JButton("SendMessage");

	{
		pClient.setLayout(new FlowLayout());
		tPort1.setText("8888");
		pClient.add(lIP1);
		pClient.add(tIP1);
		pClient.add(lPort1);
		pClient.add(tPort1);
		pClient.add(bSend);
	}

	JPanel pRecv = new JPanel();
	JPanel pSend = new JPanel();
	
	JLabel lRecv = new JLabel("Message Received:");
	JTextArea taRecv = new JTextArea();
	JScrollPane scRecv = new JScrollPane(taRecv);

	{
		// taLog.setPreferredSize(new Dimension(200, 150));
		taRecv.setLineWrap(true);
	}

	JLabel lSend = new JLabel("Message Sent:");
	JTextArea taSend = new JTextArea();
	JScrollPane scSend = new JScrollPane(taSend);

	{
		//taRecv.setPreferredSize(new Dimension(150, 150));
		taRecv.setLineWrap(true);
	}


	{
		pRecv.setLayout(new BorderLayout());
		pRecv.add(lRecv, BorderLayout.NORTH);
		pRecv.add(scRecv, BorderLayout.CENTER);
		
		pSend.setLayout(new BorderLayout());
		pSend.add(lSend, BorderLayout.NORTH);
		pSend.add(scSend, BorderLayout.CENTER);
	
	}

	JSplitPane spLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pServer, pClient);
	{// 设置分割条的位置
		spLeft.setResizeWeight(0.5);
	}
	
	JSplitPane spRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pRecv, pSend);
	{
		//spRight.setDividerLocation(300);
		// 设置分割条的位置
		spRight.setResizeWeight(0.5);
	}
	
	JSplitPane spMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spLeft, spRight);

	{
		spMain.setDividerLocation(400);
		// 设置分割条的位置，Left/top获得新增部分的20%
		spMain.setResizeWeight(0.2);
	}

	// 把sp当作ContentPane
	{
		f.setContentPane(spMain);
	}

	public void show() {
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

	public void setFrameName(String str) {
		f.setTitle(str);
	}
	public void setIP(String ip) {
	
		tIP.setText(ip);
	
	}

	
	public void  appendRecv(String str) {
		taRecv.append(str+"\n");
		taRecv.setCaretPosition(taRecv.getText().length());
	}
	
	public void  appendSend(String str) {
		taSend.append(str+"\n\n");
		//将光标移动到文本区域最后
		taSend.setCaretPosition(taSend.getText().length());
	}

	public String getSendMessage() {
		String msg = taSend.getText();
		taSend.setText("");
		return msg;
	}
	

	public String getServerPort() {
		return tPort.getText();
	}
	
	public String getClientIP() {
		return tIP1.getText();
	}
	
	public String getClientPort() {
		return tPort1.getText();
	}
	
	public static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			// 也可以用 javax.swing.plaf.metal.MetalLookAndFeel
			// com.sun.java.swing.plaf.motif.MotifLookAndFeel
			// com.sun.java.swing.plaf.windows.WindowsLookAndFeel
			// com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel等

		} catch (Exception e) {
			// ignore error
		}
	}

	//每个机器都启动自己的一个Server，以Thread方式不断运行，直到StopFlag为True为止
	//如果要发送消息给其他机器，只在发送消息的时刻建立Socket，发送之后就关闭
	//发送时给出自己的地址和Server端口，便于后续Server端回复
	
	//发送之后Server会给一个回复确认，让发送方知道Server已经收到。
	//如果要回复消息，就连接Client机器的Server进程
	
	public static void main(String[] args) {

		setLookAndFeel();
		SocketGUI gui = new SocketGUI();
	
		gui.setFrameName("Server&Client");
		
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
		
		ThreadedClient client = new ThreadedClient(gui);
		
		gui.bSend.addActionListener(new ActionListener() {

			ThreadedClient client = new ThreadedClient(gui);
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
