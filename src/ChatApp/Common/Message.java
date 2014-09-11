package ChatApp.Common;

import java.io.Serializable;


@SuppressWarnings("serial")
public class Message implements Serializable{
	private String sender = null;
	private String receiver = null;
	private String body = null;
	
	public Message(String sender, String receiver, String body) {
		super();
		this.sender = sender;
		this.receiver = receiver;
		this.body = body;
	}
	
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	public String toString(){
		return "<"+sender+"> " + body;
	}
	
	

}
