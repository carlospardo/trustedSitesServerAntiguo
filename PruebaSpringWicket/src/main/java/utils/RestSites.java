package utils;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;

import services.SiteServices;

import models.Site;

@Path("/sites")
public class RestSites {
	
	protected final static Logger log = Logger.getLogger(SiteServices.class.getName());

	@PUT
	@Path("register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(Site site, @QueryParam("idFacebook") String idFacebook) {
		try {	
			SiteServices.registerSite(site, idFacebook);
			ResponseBuilder rBuild = Response.ok("ok", MediaType.APPLICATION_JSON);
			return rBuild.build();
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("ERROR tipo: " + e.getClass().getName(), e);		
			ResponseBuilder rBuild = Response.status(Response.Status.BAD_REQUEST);
			return rBuild.type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
		}
	}
	@GET
	@Path("remove")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response remove(@QueryParam("idSite") String idSite, @QueryParam("idFacebook") String idFacebook) {
		
		try {	
			SiteServices.removeSite(idSite, idFacebook);
			ResponseBuilder rBuild = Response.ok("ok", MediaType.APPLICATION_JSON);
			return rBuild.build();
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("ERROR tipo: " + e.getClass().getName(), e);		
			ResponseBuilder rBuild = Response.status(Response.Status.BAD_REQUEST);
			return rBuild.type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
		}
	}
		
	@GET
	@Path("mine")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public static Response getMySites(@QueryParam("idFacebook") String idFacebook){		
		
		try {
			List<Site> listSites = SiteServices.getMySites(idFacebook);
			ResponseBuilder rBuild = Response.ok(listSites, MediaType.APPLICATION_JSON);
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
	public static Response getSitesNamesList(List<String> listFriendsIds){
		
		try {
			List<Site> listSites = SiteServices.getSitesNamesList(listFriendsIds);
			ResponseBuilder rBuild = Response.ok(listSites, MediaType.APPLICATION_JSON);
			return rBuild.build();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("ERROR tipo: " + e.getClass().getName(), e);		
			ResponseBuilder rBuild = Response.status(Response.Status.BAD_REQUEST);
			return rBuild.type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
		} 
	}
	
	@PUT
	@Path("detailed")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public static Response getSitesList(List<String> listSiteIds){
		
		System.out.println("listSiteIds.size(): " + listSiteIds.size());
		try {
			List<Site> listSites = SiteServices.getSitesList(listSiteIds);
			ResponseBuilder rBuild = Response.ok(listSites, MediaType.APPLICATION_JSON);
			return rBuild.build();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("ERROR tipo: " + e.getClass().getName(), e);		
			ResponseBuilder rBuild = Response.status(Response.Status.BAD_REQUEST);
			return rBuild.type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
		} 
	}
}
