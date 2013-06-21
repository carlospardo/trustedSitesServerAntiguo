package utils;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;

import services.SiteServices;
import services.UserServices;

import models.User;

@Path("/users")
public class RestUsers {
	
	protected final static Logger log = Logger.getLogger(SiteServices.class.getName());

	@PUT
	@Path("register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(User user) {
		
		try {	
			UserServices.registerUser(user);
			ResponseBuilder rBuild = Response.ok("ok", MediaType.APPLICATION_JSON);
			return rBuild.build();
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("ERROR tipo: " + e.getClass().getName(), e);		
			ResponseBuilder rBuild = Response.status(Response.Status.BAD_REQUEST);
			return rBuild.type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
		}
	}
	
	@PUT
	@Path("friends")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public static Response getListFriends(List<String> listUserIds, @QueryParam("idFacebook") String idFacebook){
		
		try {
			UserServices.updateFriends(idFacebook, listUserIds);
			List<User> listUsers = UserServices.getFriendsList(idFacebook);
			ResponseBuilder rBuild = Response.ok(listUsers, MediaType.APPLICATION_JSON);
			return rBuild.build();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("ERROR tipo: " + e.getClass().getName(), e);		
			ResponseBuilder rBuild = Response.status(Response.Status.BAD_REQUEST);
			return rBuild.type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
		} 
	}	
}
