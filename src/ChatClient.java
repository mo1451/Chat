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
 * �������ͻ���
 * @author Ĭ1451
 *
 */
public class ChatClient extends Frame {
	//�����
	TextField tf = new TextField();
	//��ʾ��
	TextArea ta = new TextArea();
	//Socket
	Socket s = null;
	//�����
	DataOutputStream dos = null;

	/**
	 * �ͻ���������
	 * @param args δʹ��
	 */
	public static void main(String[] args) {
		new ChatClient().launchFrame();
	}
	
	/**
	 * ���ڽ�������
	 */
	private void launchFrame() {
		//���ó�ʼλ��
		this.setLocation(400, 200);
		//���ó�ʼ��С
		this.setSize(400, 400);
		//���·����������
		this.add(tf, BorderLayout.SOUTH);
		//���Ϸ�������ʾ��
		this.add(ta, BorderLayout.NORTH);
		//����Window����
		this.addWindowListener(new WindowE());
		//�����ı��������
		tf.addActionListener(new TFEvent());
		this.pack();	
		//���ô��ڿɼ�
		this.setVisible(true);
		//���ӷ�����
		this.connect();
	}
	
	/**
	 * ���ӷ���˺�������������
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
	 * �������ڹرռ�������
	 * @author Ĭ1451
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
	 * �����ı������������
	 * @author Ĭ1451
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
	 * �������շ������Ϣ�߳�
	 * @author Ĭ1451
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
