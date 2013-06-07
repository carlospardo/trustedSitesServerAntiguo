package modelsTest;

import java.sql.Timestamp;
import java.util.Date;


public class Site {

	private String idSite;
	private String name;
	private String urlPhoto;
	private String positionX;
	private String positionY;
	private String info;
	private String modifiedDate;
	private String nameOwner;
	private String ownerId;
	
	public Site(){
		super();
	}
	
	public Site(String idSite, String name, String urlPhoto, String positionX,
			String positionY, String info, String nameOwner, String ownerId) {
		super();
		this.idSite = idSite;
		this.name = name;
		this.urlPhoto = urlPhoto;
		this.positionX = positionX;
		this.positionY = positionY;
		this.info = info;
		this.modifiedDate = this.obtainTimestamp();
		this.nameOwner= nameOwner;
		this.ownerId = ownerId;
	}
	
	public Site(String idSite, String name, String urlPhoto, String positionX,
			String positionY, String nameOwner) {
		super();
		this.idSite = idSite;
		this.name = name;
		this.urlPhoto = urlPhoto;
		this.positionX = positionX;
		this.positionY = positionY;
		this.nameOwner= nameOwner;
	}

	public String obtainTimestamp() {		
		Date utilDate = new java.util.Date();
		Timestamp timestamp = new Timestamp(utilDate.getTime());
		String time = timestamp.toString();
		time = time.substring(0, time.indexOf("."));
		return time;
	}	
	public String getIdSite() {
		return idSite;
	}
	public void setIdSite(String idSite) {
		this.idSite = idSite;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrlPhoto() {
		return urlPhoto;
	}
	public void setUrlPhoto(String urlPhoto) {
		this.urlPhoto = urlPhoto;
	}
	public String getPositionX() {
		return positionX;
	}
	public void setPositionX(String positionX) {
		this.positionX = positionX;
	}
	public String getPositionY() {
		return positionY;
	}
	public void setPositionY(String positionY) {
		this.positionY = positionY;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getNameOwner() {
		return nameOwner;
	}

	public void setNameOwner(String nameOwner) {
		this.nameOwner = nameOwner;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
}
