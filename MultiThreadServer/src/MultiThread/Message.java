package MultiThread;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Comparable<Message>{
private int id;
private String clientLogin;
private String text;
private Date time;
private SimpleDateFormat df;
public Message (String clientLogin,int id, String text, Date time) {
	this.clientLogin = clientLogin;
	this.id = id;
	this.text = text;
	this.time = time;
	this.df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}

public String toString() {
	return "{\"login\":\"" + this.clientLogin + "\",\"id\":" + this.id
			+",\"text\":\"" + this.text + "\",\"time\":\"" + df.format(this.time)
			+ "\"}"; 
}
public int getID() {
	return this.id;
}
public String getText() {
	return this.text; 
}
public String getTime() {
	return this.df.format(this.time);
}
public String getLogin() {
	return this.clientLogin; 
}
public Date getDate() {
	return this.time;
}

@Override
public int compareTo(Message o) {
	// TODO Auto-generated method stub
	return getDate().compareTo(o.getDate());
}

}
