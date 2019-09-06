package MultiThread;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class ClientThreadHandler extends Thread {
	private Socket socketM;
	private BufferedReader in_M;
	private DataInputStream in_F;
	private PrintWriter out_M;
	private DataOutputStream out_F;
	private String login;
	private File history;
	private List <Message> messages;
	private List <Message> allMessages;
	
	public ClientThreadHandler (Socket socketM, Socket socketF, File history, List <Message> allMessages) throws UnsupportedEncodingException, IOException {
		this.socketM = socketM;
		this.login = null;
		this.history = history;		
		this.messages = new ArrayList <> ();
		this.allMessages = allMessages;
		in_M = new BufferedReader (new InputStreamReader (new DataInputStream(socketM.getInputStream()), "Cp1251"));
		out_M = new PrintWriter (new BufferedWriter (new OutputStreamWriter (new DataOutputStream(socketM.getOutputStream()), "Cp1251")), true);
		in_F = new DataInputStream(socketF.getInputStream());
		out_F = new DataOutputStream(socketF.getOutputStream());
		start();
	}
	
	public void run() {
		try {
			
			
			
			this.login = login();
			int counter = 0;
			
			while (!socketM.isClosed()) {
				int ch = Integer.parseInt(in_M.readLine());
				switch (ch) {
				case 1: 
					newMessage(counter++);
					break;
				case 2: 
					showMessages();
					break;
				case 3:
					deleteMessage();
					break;
				case 4:
					getMessages();
        			break;
        		case 5:
        			uploadFile(counter++);
        			break;
        		case 6:
        			downloadFile(counter++);
        			break;
        		case 7:
        			exit();
        			break;
				default: 
					System.out.println("Ќекорректные данные!");
						
				}
				
			}
			
		}
		catch (IOException e) {
			System.out.println("Client broke connection!");
		}
		finally {
			try {
				socketM.close();
			}
			catch (IOException e) {
				System.out.println("Socket not closed!");
			}
		}
	}
	
	private String login () throws IOException {
		String loginC = "";
		while (true) {
			loginC = in_M.readLine();
			if (((loginC.length() <= 4) && (loginC.matches("[a-z]+")))) {
				out_M.println("тут");
				break;
				}
			else {
				out_M.println("1");
				}
			}
		return loginC;
	}
	
	private void exit() throws IOException {
		String loginCE = "";
		while (true) {
			loginCE = in_M.readLine();
			if (loginCE.equals(this.login)) {
				out_M.println("0");
				break;
			}
			else {
				out_M.println("1");
				}
		}
		System.out.println(" лиент " + this.login + " вышел из чата.");
		socketM.close();
	}
	
	private void newMessage(int counter) throws IOException {
		//слушает сообщение
		String text = in_M.readLine();
		//слушает id
		int id = counter;
		Message msg = new Message (this.login, id, text, new Date());
		messages.add(msg);
		allMessages.add(msg);
		//открывает поток
		FileWriter historyWriter = new FileWriter(history.getAbsoluteFile(), true);
		//дописывает строку
		historyWriter.append(msg.toString()+"\n");
		//закрывает поток
		historyWriter.close();
		//лог на сервер
		System.out.println(" лиент " + this.login + " написал: " + text);
		//пишет клиенту
		out_M.println(login + ": " + text + "	[id:"+ id + "]" );
	}
	
	private void deleteMessage() throws NumberFormatException, IOException {
		//проверка были ли сообщени€
		if (!messages.isEmpty()) {
			out_M.println("1");
			//если ЅџЋ» то удалить из массива
			//слушает id
			int id = Integer.parseInt(in_M.readLine());
			//найти сообщение
			int i = 0;
			Boolean flag = false;
			while (i < messages.size()) {
				if (messages.get(i).getID() == id) {
					flag = true;
					break;
				}
				i++;
			}
			if (!flag) {
				//сообщени€ с таким id нет
				System.out.println(" лиент " + this.login + " не имеет в истории сообщени€ с id = " + id);
				out_M.println("10");
				}
			else {
				out_M.println("11");
				//найти строку в файле и перезаписать его без неЄ
				//открывает поток
				BufferedReader historyReader = new BufferedReader(new FileReader(history.getAbsoluteFile()));
				//новый файл
				File fileToWrite = new File("fileToWrite.json");
				//поток дл€ записи в новый файл
				FileWriter toNew = new FileWriter (fileToWrite.getAbsoluteFile(), true);
				flag = false;
				String pointer;
				String fgh = messages.get(i).toString();
				while ((pointer = historyReader.readLine()) != null) {
					if (!pointer.contains(fgh)) {
						//если не она, то записать
						toNew.append(pointer+"\n");				
					}
					else {
						//иначе, написать, что ее нашли в файле
						flag = true;
						System.out.println("—ообщение найдено!");
						out_M.println("—ообщение найдено!");
					}
				}
				//если строка не была найдена
				if (!flag) {
					out_M.println("—ообщение не найдено!");
					out_M.println("—ообщение не удалено!");
				}
				else {
					out_M.println("—ообщение удалено!");
				}
				//закончить с записью
				toNew.close();
				historyReader.close();
				history.delete();
				fileToWrite.renameTo(history);
				//удалить сообщение из массива
				messages.remove(i);	
				i = 0;
				for ( i = 0; i < allMessages.size(); i ++) {
					if (this.login.equals(allMessages.get(i).getLogin()) && id == allMessages.get(i).getID()) {
						allMessages.remove(i);
					}
				}
			}			
			}
		else {
			//сообщений нет
			System.out.println(" лиент " + this.login + " не имеет истории сообщений.");
			out_M.println("0");
			}
		
	}
	
	public void showMessages() {
		//показать список своих сообщений истори€??
		//посчитать количество сообщений
		int count = messages.size();
		//передать его в клиент
		out_M.println(Integer.toString(count));
		for (Message msg: messages) {
			out_M.println("[id: " + msg.getID() + "]	" + msg.getText() + "	" + msg.getTime() );
		}
	}
	
	public void uploadFile(int counter) throws IOException {
		//считать им€
		String path = in_F.readUTF();
		//out_F.notify();
		if (path.length()!=0) {
			String fileName = in_F.readUTF();
			String h = "Server files/"+ fileName;
			File file = new File (h);
			FileOutputStream fos = new FileOutputStream (file);
			long size = in_F.readLong();
			byte [] byteArray = new byte [64*1024];
			int count, total = 0;
            while ((count = in_F.read(byteArray)) != -1) {
                total += count;
                
                fos.write(byteArray, 0, count);
                if (total == size) {
                    break;
                }                
            }
            fos.flush();
            fos.close();
            int id = counter;
    		Message msg = new Message (this.login, id, fileName, new Date());
    		messages.add(msg);
    		allMessages.add(msg);
    		FileWriter historyWriter = new FileWriter(history.getAbsoluteFile(), true);
    		//дописывает строку
    		historyWriter.append(msg.toString()+"\n");
    		//закрывает поток
    		historyWriter.close();
            }
	}
	public void downloadFile(int counter) throws IOException {
		File servDir = new File ("Server files");
		File [] list = servDir.listFiles();
		int lenList = list.length;
		if (lenList==0) {
			out_F.writeBoolean(false);
		}
		else {
			out_F.writeBoolean(true);
			out_F.writeInt(lenList);//передать кол-во итераций
			String fileName;
			for (File g: list) {
				out_F.writeUTF(g.getName());
			}
			int ch = in_F.readInt();
			if (!(ch > lenList && ch < 1)) {
				File toDownload = list[ch];
				fileName = toDownload.getName();
				out_F.writeUTF(fileName);
				out_F.flush();
				FileInputStream fis = new FileInputStream (toDownload);
				long size = toDownload.length();
	    		out_F.writeLong(size);
	    		out_F.flush();
	    		byte [] byteArray = new byte [64*1024];
	    		int count;
	    		while ((count = fis.read(byteArray)) != -1) {
	                out_F.write(byteArray, 0, count);
	            }	
	    		out_F.flush();
	    		fis.close();
	    		int id = counter;
	    		Message msg = new Message (this.login, id, fileName, new Date());
	    		messages.add(msg);
	    		allMessages.add(msg);
	    		FileWriter historyWriter = new FileWriter(history.getAbsoluteFile(), true);
	    		//дописывает строку
	    		historyWriter.append(msg.toString()+"\n");
	    		//закрывает поток
	    		historyWriter.close();
	    		//System.out.println(fileName);
			}
			else {
				System.out.println("-1");
			}
		}
		
			 
	}
	
	public void getMessages() throws NumberFormatException, IOException {
		boolean flag = Boolean.parseBoolean(in_M.readLine()); 
		if (flag) {
			int ch = Integer.parseInt(in_M.readLine());
			switch (ch) {
			case 1: 
				getGroupMessages();
				break;
			case 2: 
				getSortMessages();
				break;
			case 3:{
				if (allMessages.size()!=0) {
					out_M.println(Boolean.toString(true));
					out_M.println(Integer.toString(allMessages.size()));
					String str;
					for (Message r: allMessages) {
						str = r.getLogin() +": "
								+r.getText() + "		[id: " + r.getID() 
								+ "] [time: "+r.getTime()+"]";
						out_M.println(str);
					}					
				}
				else {
					out_M.println(Boolean.toString(false));
				}
				break;
			}
			}
		}
			
	}
	
	public void getSortMessages() throws IOException {
		boolean flag = Boolean.parseBoolean(in_M.readLine()); 
		if (flag) {

			int ch = Integer.parseInt(in_M.readLine());
			switch (ch) {
			case 1: 
				sortByTimeUp();
				break;
			case 2: 
				sortByTimeDown();
				break;
			case 3:{
				
			}
			default:
				System.out.println("Switch getMessages(): некорректные данные!");			
			}
		}
	}

	public void sortByTimeUp() throws IOException {
		//получить все сообщени€
		if (allMessages.size()!=0) {
			out_M.println(Boolean.toString(true));
			List <Message> n = null ;
			n = allMessages;
			int h = n.size();
			out_M.println(Integer.toString(h));
			String str = null;  
			Collections.sort(n);
			
			for (Message r: n) {
				str = r.getLogin() +": "
						+r.getText() + "		[id: " + r.getID() 
						+ "] [time: "+r.getTime()+"]";
				out_M.println(str);
			}
			
		}
		else {
			out_M.println(Boolean.toString(false));
		}
		
		
	}
	public void sortByTimeDown() throws IOException {
		//получить все сообщени€
		if (allMessages.size()!=0) {
			out_M.println(Boolean.toString(true));
			List <Message> n = allMessages;
			int h = n.size();
			out_M.println(Integer.toString(h));
			String str = null;  
			Collections.sort(n);
			
			for (int i = n.size()-1; i > -1; i--) {
				str = n.get(i).getLogin() +": "
			+n.get(i).getText() + "	[id: " + n.get(i).getID() 
			+ "] [time: "+n.get(i).getTime()+"]";
				out_M.println(str);
			}
			
		}
		else {
			out_M.println(Boolean.toString(false));
		}
		
		
	}
	public void getGroupMessages() {
		//есть ли истори€
		if (allMessages.size()!=0) {
			out_M.println(Boolean.toString(true));
			//если есть
			List <Message> n = allMessages;
			n.sort(Comparator.comparing(Message::getLogin));
			int h = n.size();
			out_M.println(Integer.toString(h));
			String str = null;  
			for (Message r: n) {
				str = r.getLogin() +": "
						+r.getText() + "		[id: " + r.getID() 
						+ "] [time: "+r.getTime()+"]";
				out_M.println(str);
			}
			
		}
		else {
			out_M.println(Boolean.toString(false));
		}
	}
		
	

}
