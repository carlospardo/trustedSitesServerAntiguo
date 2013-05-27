package models;

import java.sql.Timestamp;
import java.util.Date;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import dao.SiteDAOImpl;

public class Site extends SiteDAOImpl{

	private String idSite;
	private String name;
	private String urlPhoto;
	private String positionX;
	private String positionY;
	private String info;
	private String modifiedDate;
	
	public Site( EmbeddedGraphDatabase graphDb, Index<Node> nodeIndex){
		super();
		this.graphDb = graphDb;
		this.nodeIndex = nodeIndex;
	}
	
	public Site(String idSite, String name, String urlPhoto, String positionX,
			String positionY, String info, EmbeddedGraphDatabase graphDb, 
			Index<Node> nodeIndex) {
		super();
		this.idSite = idSite;
		this.name = name;
		this.urlPhoto = urlPhoto;
		this.positionX = positionX;
		this.positionY = positionY;
		this.info = info;
		this.modifiedDate = this.obtainTimestamp();
		this.graphDb = graphDb;
		this.nodeIndex = nodeIndex;
	}
	public void putConectionInfo(EmbeddedGraphDatabase graphDb, Index<Node> nodeIndex){
		this.graphDb = graphDb;
		this.nodeIndex = nodeIndex;
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
	

}
