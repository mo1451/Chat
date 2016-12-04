import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 * 聊天程序客户端
 * @author 默1451
 *
 */
public class ChatClient extends Frame {
	//输入框
	TextField tf = new TextField();
	//显示框
	TextArea ta = new TextArea();
	//Socket
	Socket s = null;
	//输出流
	DataOutputStream dos = null;

	/**
	 * 客户端主函数
	 * @param args 未使用
	 */
	public static void main(String[] args) {
		new ChatClient().launchFrame();
	}
	
	/**
	 * 窗口建立函数
	 */
	private void launchFrame() {
		//设置初始位置
		this.setLocation(400, 200);
		//设置初始大小
		this.setSize(400, 400);
		//在下方加入输入框
		this.add(tf, BorderLayout.SOUTH);
		//在上方加入显示框
		this.add(ta, BorderLayout.NORTH);
		//加入Window监听
		this.addWindowListener(new WindowE());
		//加入文本输入监听
		tf.addActionListener(new TFEvent());
		this.pack();	
		//设置窗口可见
		this.setVisible(true);
		//连接服务器
		this.connect();
	}
	
	/**
	 * 连接服务端函数并建立监听
	 */
	public void connect() {
		try {
			s = new Socket("192.168.1.104",2478);
			dos = new DataOutputStream(s.getOutputStream());
			Server ser = new Server(s);
			new Thread(ser).start();
System.out.println("Connect");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 建立窗口关闭监听函数
	 * @author 默1451
	 *
	 */
	private class WindowE extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent arg0) {
			try {
				dos.close();
				s.close();			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.exit(0);
		}
		
	}
	
	/**
	 * 建立文本输入监听函数
	 * @author 默1451
	 *
	 */
	private class TFEvent implements ActionListener {

		String str = null;
		@Override
		public void actionPerformed(ActionEvent e) {
			str = tf.getText();
			this.sendMessage();
		//	ta.setText(str);
			tf.setText("");
		}
		
		private void sendMessage() {
			
			try {				
				dos.writeUTF(str);
				dos.flush();				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		
	}

	/**
	 * 建立接收服务端消息线程
	 * @author 默1451
	 *
	 */
	private class Server implements Runnable {

		Socket s = null;
		DataInputStream dis = null;
		boolean flagConnected = false;
		
		Server(Socket s) {
			this.s = s;
			try {
				this.dis = new DataInputStream(s.getInputStream());
				flagConnected = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub			
			String str;
			try {
				while(flagConnected) {
				str = dis.readUTF();
				ta.setText(ta.getText() + str + "\n");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} finally {
				try {
					if(dis != null) dis.close();
					if(s != null) s.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				dis = null;
				s = null;
			}			
		}
		
	}
}
