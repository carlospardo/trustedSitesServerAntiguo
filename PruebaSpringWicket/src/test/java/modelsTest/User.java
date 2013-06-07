package modelsTest;


public class User{

	private String idFacebook;
	private String name;
	private String urlPhoto;
	private String positionX;
	private String positionY;
	
	public User(){
		super();
	}
	
	public User(String idFacebook, String name, String urlPhoto,
			String positionX, String positionY) {
		super();
		this.idFacebook = idFacebook;
		this.name = name;
		this.urlPhoto = urlPhoto;
		this.positionX = positionX;
		this.positionY = positionY;
	}
	
	public User(String idFacebook, String name, String urlPhoto) {
		super();
		this.idFacebook = idFacebook;
		this.name = name;
		this.urlPhoto = urlPhoto;
	}
	
	public String getIdFacebook() {
		return idFacebook;
	}
	public void setIdFacebook(String idFacebook) {
		this.idFacebook = idFacebook;
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
}
