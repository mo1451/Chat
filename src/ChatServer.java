import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * ����˳���
 * @author Ĭ1451
 *
 */
public class ChatServer {

	//ServerSocket
	ServerSocket ss = null;
	//�ж��Ƿ�����
	boolean flagConnected = true;
	//���ÿͻ�������
	ArrayList<Client> clients = new ArrayList<Client>();
	
	/**
	 * �����������
	 * @param args δʹ��
	 */
	public static void main(String[] args) {
		
		new ChatServer().start();
		
	}
	
	/**
	 * ��ʼ��������
	 */
	private void start() {
		
		this.buildServer();
		this.connectClient();
		
	}
	
	/**
	 * ����������
	 */
	private void buildServer() {
		
		try {
			ss = new ServerSocket(2478);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * ���տͻ�����Ϣ����
	 */
	private void connectClient() {
		
		try {
			
			while(flagConnected) {
				Socket s = ss.accept();
System.out.println("a client has been connected");
				DataInputStream dis = new DataInputStream(s.getInputStream());
				Client c = new Client(s);	
				clients.add(c);
				new Thread(c).start();			    			    	
			}	
			
		} catch (IOException e) {			
			//e.printStackTrace();
			System.out.println("Server is closed.");
		} finally {
			
			try {
				if(ss != null) ss.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			ss = null;
			
		}
	}
	
	/**
	 * ������ͻ��˷�����Ϣ�߳�
	 * @author Ĭ1451
	 *
	 */
	private class Client implements Runnable {
		Socket s = null;
		DataInputStream dis = null;
		DataOutputStream dos = null;
		String str = null;
		boolean flagReceived = false;
		
		Client(Socket s) {
			this.s = s;
			try {
				this.dis = new DataInputStream(s.getInputStream());
				this.dos = new DataOutputStream(s.getOutputStream());
				flagReceived = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private void send(String str) {
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try {
				while(flagReceived) {
			    	str = dis.readUTF();
			    	System.out.println(str);
			    	for(int i=0; i<clients.size();i++) {
			    		Client c = clients.get(i);
			    		c.send(str);
			    	}
			    }	
			} catch (IOException e) {			
				//e.printStackTrace();
				System.out.println("Client is closed.");
			} finally {
				try {
					if(dis != null) dis.close();
					if(dos != null) dos.close();
					if(s != null) s.close();
					clients.remove(this);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
					
		}	
	}
}


