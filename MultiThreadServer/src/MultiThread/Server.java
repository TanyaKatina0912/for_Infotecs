package MultiThread;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	 private static ServerSocket serverM;
	 private static ServerSocket serverF;
	 private static Socket clientSocketM;
	 private static Socket clientSocketF;
	 public static File SERV_DIR;
	 public static int M_PORT = 45777;
	 public static int F_PORT = 45778;
	 public static int clientsNumber = 0; 
	 public static ArrayList <Message> allMessages;
	 

	    public static void main (String [] args) throws InterruptedException, IOException {
	    	InetAddress ia = InetAddress.getLocalHost();
	    	String str = ia.getHostAddress();
	    	System.out.println("IP-адрес сервера: "+str);
	        // запуск сервера на определенном порту
	    	File history = new File("History.json");
	    	if (history.exists()) {
	    		history.delete();
	    		history.createNewFile();
	    	}
	    	File SERV_DIR = new File("Server files");
	    	SERV_DIR.mkdir();
	    	allMessages = new ArrayList <>();
	        try {
	            serverM = new ServerSocket (M_PORT);
	            serverF = new ServerSocket (F_PORT); 
	            
	            try{
	                System.out.println("Сервер сообщений запущен! Порт: "+M_PORT);
	                System.out.println("Файловый запущен! Порт: "+F_PORT);
	                //будет выключаться по кнопочке поэтому
	                while (true){
	                	
	                	//поставить в режим ожидания подключения
	                	clientSocketM = serverM.accept();
	                	clientSocketF = serverF.accept();
	                	clientsNumber ++;
	                	
	                	try {
	                		new ClientThreadHandler (clientSocketM, clientSocketF, history, allMessages);
	                		
	                	}
	                	catch (IOException e) {
	                		System.out.println(e);
	                		clientSocketM.close();
	                		clientSocketF.close();
	                	}
	                }
	            }
	            finally{
	            	 
	            	serverM.close();
	                serverF.close();
	                System.out.println("Сервер закрыт!");
	            }
	        }
	        catch (IOException e){
	            e.printStackTrace();
	        }
	        }

	    	
	    
	}

