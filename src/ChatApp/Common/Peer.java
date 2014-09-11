package ChatApp.Common;

import java.io.Serializable;


@SuppressWarnings("serial")
public class Peer implements Serializable {
	
	private String name;
	private String ip;
	private String status;
	
	public Peer(String name, String ip, String status) {
		super();
		this.name = name;
		this.ip = ip;
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public boolean equals(Peer p){
		if(this.ip.equals(p.getIp())){
			return true;
		}
		return false;
	}
	
	public String toString(){
		return name+" "+ip+" "+status;
	}
	
	public void incIp(){
		ip = Integer.toString(Integer.parseInt(ip)+1);
	}
	
	public void reverseStatus(){
		if(status.equals("L")){
			status = "J";
		}
		else{
			status = "L";
		}
	}
	
	
	
	

}
