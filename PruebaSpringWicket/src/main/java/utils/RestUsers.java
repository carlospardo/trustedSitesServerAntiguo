package utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;
import org.json.JSONArray;

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
	
//	@PUT
//	@Path("prueba")
//	@Produces(MediaType.APPLICATION_JSON)
//	@Consumes(MediaType.APPLICATION_JSON)
//	public static Response getListPrueba(List<String> list, @QueryParam("idFacebook") String idFacebook){
//		
//		//System.out.println("Size: " + listUserIds.length + " idFace: " + idFacebook);
//		System.out.println("idFace: " + idFacebook);
//		List<User> listUsers = null;
//		
//		for(String i : list){
//			System.out.println("i: " + i);
//		}
//
//		try {
//			ResponseBuilder rBuild = Response.ok("ok", MediaType.APPLICATION_JSON);
//			return rBuild.build();
//		}catch (Exception e) {
//			// TODO Auto-generated catch block
//			log.error("ERROR tipo: " + e.getClass().getName(), e);		
//			ResponseBuilder rBuild = Response.status(Response.Status.BAD_REQUEST);
//			return rBuild.type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
//		} 
//	}
	
	
}
