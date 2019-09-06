package Client;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import MultiThread.Server;

public class Client {
	 private static Socket clientSocketM;
	 private static Socket clientSocketF;
	    private static BufferedReader consoleReader;
	    private static BufferedReader in_M;
	    private static DataInputStream in_F;
	    private static PrintWriter out_M;
	    private static DataOutputStream out_F;
	    private static File clientDir;
	    public static String login;
	    
	    public static void main (String[] args) throws InterruptedException{
	        try {
	        	Scanner sc = new Scanner (System.in);
	        	System.out.println("Введите IP-адрес сервера: ");
	        	String h = sc.nextLine();
	        	
	        	//просьба в подключении
	        	clientSocketM = new Socket (h, Server.M_PORT);
	        	clientSocketF = new Socket (h, Server.F_PORT);
	        	System.out.println("------------------------------LOG------------------------------");
	        	//поток, читающий консоль
	        	consoleReader = new BufferedReader (new InputStreamReader(System.in, "Cp1251"));
	        	System.out.println("Консольный поток создан!");
	        	//принимающий поток
	        	in_M = new BufferedReader (new InputStreamReader (new DataInputStream (clientSocketM.getInputStream()), "Cp1251"));
	        	in_F = new DataInputStream (clientSocketF.getInputStream());
	        	System.out.println("Принимающий поток (клиент) создан!");
	        	//отправляющий (серверу) поток
	        	out_M = new PrintWriter(new BufferedWriter (new OutputStreamWriter (new DataOutputStream (clientSocketM.getOutputStream()), "Cp1251")), true);
	        	out_F = new DataOutputStream (clientSocketF.getOutputStream());
	        	
	        	System.out.println("Отправляющий поток (клиент) создан!");    
	        	System.out.println("------------------------------LOG------------------------------");
	        	System.out.println();
	        	
	        	login = login();
	        	clientDir = new File (login + " files"); // папка с файлами клиента
	        	if (!clientDir.exists()) {
	        		clientDir.mkdir();
	        	}

	        	//ждем в консоли пока клиент на связи
	        	while (!clientSocketM.isClosed()) {
	        		while (true) {
		        		System.out.println("МЕНЮ:\n"
		        				+ "1 - Написать сообщение\n"
		        				+ "2 - Показать список своих сообщений\n"
		        				+ "3 - Удалить свое сообщение\n"
		        				+ "4 - Показать сообщения всех пользователей\n"
		        				+ "5 - Загрузить файл на сервер\n"
		        				+ "6 - Скачать файл с сервера\n"
		        				+ "7 - Выход");
		        		String g = consoleReader.readLine();
		        		if (g.length() != 0) {
			        		Boolean fl = true;
			        		int ch = 0;
			        		try {
			        			ch = Integer.parseInt(g);
			        		}
			        		catch (NumberFormatException e) {
			        			fl = false;
			        			System.out.println("Некорректные данные!");
			        			break;
			        			}
			        		if (fl) {
				        		out_M.write(ch + "\n");
				        		out_M.flush();
				        		switch (ch) {
				        		case 1: 
				        			newMessage();
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
				        			uploadFile();
				        			break;
				        		case 6:
				        			downloadFile();
				        			break;
				        		case 7:
				        			exit();
				        			break;
				        		
								default: 
				        			System.out.println("Некорректные данные!");
				        			}
			        		}
		        		}
		        		break;
	        		}         
	        		
	        		}
	        	sc.close();
	        	}
	        catch (IOException e){
	        	System.err.println(e);
	        }
	    }
	    
	    private static String login () throws IOException {
	    	System.out.println("Введите логин (максимальная длина 4 символа, a-z): ");
			String login = "";
			String respond = "1";
			while (true) {
				login = consoleReader.readLine();
				out_M.write(login + "\n");
				out_M.flush();
				respond = in_M.readLine();
				if (respond.equals("тут")) {
					break;
				}				
				else {
					System.out.println("Вы ввели некорректный логин, попробуйте ещё раз: ");
				}
			}
			return login;
	    }
	    
	    private static void newMessage() throws IOException {
	    	
	    	System.out.println("Введите сообщение: ");
	    	
	    		String txt = consoleReader.readLine();	
	    		//new Message(counter, login, txt, new Date());
	    		
                	//отправляем cообщение клиента на сервер
	    		out_M.write(txt+"\n");
	    		out_M.flush();
	                //ждем ответа от сервера
	                String txtSrv = in_M.readLine();
	                //выводим
	                System.out.println(txtSrv);
                	

	    }
	    
	    private static void exit() throws IOException {
	    	System.out.println("Для выхода из чата введите ваш логин:");
	    	String loginE = "";
	    	String respond = "1";
	    	while (true) {
	    		loginE = consoleReader.readLine();
	    		out_M.write(loginE + "\n");
	    		out_M.flush();
	    		respond = in_M.readLine();
	    		if (respond.equals("0")) {
	    			break;
	    			}
	    		else {
	    			System.out.println("Не правильный логин! Попробуйте ещё раз: ");
	    			}
	    	}
	    	consoleReader.close();
	    	in_M.close();
	    	out_M.close();
	    	clientSocketM.close();
	    }
	    
	    private static void deleteMessage() throws IOException {
	    	
	    		String idFound;
	    		String messagesFound = in_M.readLine();
	    		switch(messagesFound) {
	    		//не найдены - 0
	    		case "0":{
	    			System.out.println("Вы не писали сообщений!");
	    			break;
	    		}
	    		//найдены - 1
	    		case "1":{
	    			System.out.println("Введите id сообщения, которое хотите удалить:");
    				int id = Integer.parseInt(consoleReader.readLine());
    				out_M.write(id +"\n");
    				out_M.flush();
    				idFound = in_M.readLine();
    				switch (idFound) {
    				//нет с таким id - 10
    				case "10": {
    					System.out.println("Сообщение с таким ID не найдено!");
    					break;
    				}
    				//есть с таким id - 11
    				case "11": {
    					//прослушать найдена ли, удалена ли
    					String str = in_M.readLine();
    					System.out.println(str);
    					str = in_M.readLine();
    					System.out.println(str);
    					break;
    					}
    				}
	    		}  
	    		}
	    		
	    }
	    private static void showMessages() throws IOException {
	    	//считать количество сообщений
	    	String countS = in_M.readLine();
	    	int count = Integer.parseInt(countS);
	    	String mess;
	    	//считать и вывести столько раз
	    	for (int i = 0; i < count; i++) {
	    		mess = in_M.readLine();
	    		System.out.println(mess);
	    	}
	    }
	    private static void uploadFile() throws IOException, InterruptedException {
	    	System.out.println("Укажите полный путь к файлу:");
	    	String path = consoleReader.readLine();
	    	out_F.writeUTF(path);
	    	out_F.flush();
	    	if(path.length()!=0) {
	    		File file = new File (path);
	    		if (file.exists()) {
		    		String fileName = file.getName();
		    		out_F.writeUTF(fileName);
		    		out_F.flush();
		    		FileInputStream fis = new FileInputStream (file);
		    		long size = file.length();
		    		out_F.writeLong(size);
		    		out_F.flush();
		    		byte [] byteArray = new byte [64*1024];
		    		int count;
		    		while ((count = fis.read(byteArray)) != -1) {
	                    out_F.write(byteArray, 0, count);
	                }	
		    		out_F.flush();
		    		fis.close();
		    		System.out.println(login + " загрузила(а) на сервер файл " + file.getName());
	    		}
	    		else {
	    			System.out.println("По указанному пути файл не найден!");
	    			}
	    		}
	    	else {
	    		System.out.println("Вы ничего не ввели!");
	    	}
	    }
	    
	    private static void downloadFile() throws IOException {
	    	boolean f = in_F.readBoolean();
	    	if (f) {
	    		System.out.println("Выберите файл по нормеру из списка: ");
		    	int lenList = in_F.readInt();
		    	for (int i = 0; i < lenList; i++) {
		    		String str = in_F.readUTF();
		    		System.out.println((i+1) + ") "+str);
		    	}
		    	String choice = consoleReader.readLine();
		    	int p = 0;
		    	int temp = lenList;
		    	while (temp!=0) {
		    		temp = temp/10;
		    		p++;
		    	}
		    
		    	if (choice.matches("\\d{" + p + "}")) {
		    		int ch = Integer.parseInt(choice);
		    		if (ch > lenList || ch < 1) {
		    			System.out.println("Файла под таким номером нет!");
		    			out_F.writeInt(-1);
		    		}
		    		else {
			    		out_F.writeInt(ch-1);	
			    		String fileName = in_F.readUTF();
						String h = login + " files/"+ fileName;
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
			            System.out.println(login+" скачал(а) файл "+fileName);
			    		
		    		}
		    	}
		    	else {
		    		
		    		if (choice.matches("\\d{"+choice.length()+"}")) {
		    			System.out.println("Файла под таким номером нет!");
		    		}
		    		else {
		    			System.out.println("Вы ввели не число!");
		    		}
		    		
		    		out_F.writeInt(-1);
		    	}
	    	}
	    	else {
	    		System.out.println("На сервере нет файлов!");
	    	}
	    	
	    }
	    private static void getMessages() throws IOException {
	    	System.out.println("1 - Сгруппировать сообщения по логину\n"
	    			+ "2 - Отсортировать сообщения по времени\n"
	    			+ "3 - Просто вывести сообщения");
	    	String choice = consoleReader.readLine();
	    	if (choice.length()!=0) {
	    		int ch = 0;
        		try {
        			ch = Integer.parseInt(choice);
        			if (ch == 1 || ch == 2 || ch == 3) {
        				out_M.println(Boolean.toString(true));
            			out_M.println(Integer.toString(ch));
            			switch (ch) {
            			case 1:
            				sortByTime();
            				break;
            			case 2:
            				getSortMessages();
            				break;
            			case 3:
            				if(Boolean.parseBoolean(in_M.readLine())) {
								int iter = Integer.parseInt(in_M.readLine());
								for (int i = 0; i < iter; i++) {
									String u = in_M.readLine();
							    	System.out.println(u);
								}
							}
							else {
								System.out.println("История сообщений пуста.");
							}
            				
            			}
        			}
        		}        		catch (NumberFormatException e) {
        			out_M.println(Boolean.toString(false));
        			System.out.println("Некорректные данные!");
        			}
        		
	    	}
	    	else {
	    		out_M.println(Boolean.toString(false));
	    		System.out.println("Вы ничего не ввели!");
	    		
	    	}
	    }
	    
	    
	    private static void getSortMessages() throws IOException {
	    	System.out.println("Хотите отсортировать сообщения по времени?\n"
	    			+ "1 - Да, по возрастанию\n"
	    			+ "2 - Да, по убыванию\n");
	    	String choice = consoleReader.readLine();
	    	if (choice.length()!=0) {
	    		int ch = 0;
        		try {
        			ch = Integer.parseInt(choice);
        			if (ch == 1 || ch == 2) {
        				out_M.println(Boolean.toString(true));
            			out_M.println(Integer.toString(ch));
            			sortByTime();
							
        			}
        			else {
        				System.out.println("Этого пункта нет в меню");
        				out_M.println(Boolean.toString(false));
        			}
        		}
        		catch (NumberFormatException e) {
        			out_M.println(Boolean.toString(false));
        			System.out.println("Некорректные данные!");
        			}
	    	}
	    	else {
	    		out_M.println(Boolean.toString(false));
	    		System.out.println("Вы ничего не ввели!");
	    		
	    	}
	    }
	    
	    private static void sortByTime() throws IOException {
	    	
	    	boolean isHistory = Boolean.parseBoolean(in_M.readLine());
	    	if (isHistory) {
	    		int iter = Integer.parseInt(in_M.readLine());
		    	for(int i = 0; i < iter; i++) {
		    		String u = in_M.readLine();
			    	System.out.println(u);
		    	}
	    	}
	    	else {
	    		System.out.println("Истории сообщений нет.");
	    	}
	    	
	    }
	    
	    
	    

}
	    
