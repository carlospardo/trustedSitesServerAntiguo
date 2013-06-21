package services;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import models.Site;
import models.User;
import neo4j.InitializeDB;
import neo4j.Types.NodeTypes;
import neo4j.Types.RelationTypes;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class SiteServices {

	static EmbeddedGraphDatabase graphDb;
	static Index<Node> nodeIndex;
	
	protected final static Logger log = Logger.getLogger(SiteServices.class.getName());
	
	public static void registerSite(Site site, String idFacebook) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		
		log.info("SERVICE: registerSite" +
				"\nPARAMETERS: site.getName()= " + site.getName() + " idFacebook="+idFacebook);
		useConnection();
		
		Node foundNode = nodeIndex.get("idFacebook",idFacebook).getSingle();
		User user = new User(graphDb, nodeIndex);
		user.initThroughNode(foundNode);
		
		site.setNameOwner(user.getName());		
		site.putConectionInfo(graphDb, nodeIndex);
		site.register(NodeTypes.Site);
		site.createRelationship("idFacebook", idFacebook, RelationTypes.Owns);		
		
	}

	public static void removeSite(String idSite, String idFacebook) throws Exception, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		
		log.info("SERVICE: removeSite" +
				"\nPARAMETERS: idSite= " + idSite + " idFacebook="+idFacebook);
		useConnection();
		
		Node foundNode = nodeIndex.get("idSite",idSite).getSingle();
		Site site = new Site(graphDb, nodeIndex);
		site.initThroughNode(foundNode);
			
		if(idFacebook.equals(site.getOwnerId())){
			site.remove();
		}else{
			throw new Exception("You are not the owner of this site!");
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Site> getMySites(String idFacebook) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		log.info("SERVICE: getMySites" +
				"\nPARAMETERS: idFacebook="+idFacebook);
		useConnection();
		
		Node foundNode = nodeIndex.get("idFacebook",idFacebook).getSingle();
		User user = new User(graphDb, nodeIndex);
		user.initThroughNode(foundNode);
		List<Site> listSites = (List<Site>) user.getEndEntitiesOfMyRelationships(RelationTypes.Owns);
		listSites = processListSites(listSites);
		log.info("RETURN: listSites.size()" + listSites.size());
		return listSites;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Site> getSitesNamesList(List<String> listFriendsIds) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		log.info("SERVICE: getSitesNamesList" +
				"\nPARAMETERS: listsFriendsIds.size()="+  listFriendsIds.size());
		useConnection();
		
		List<Site> finalList = new ArrayList<Site>();
		User user = null;
		for (String idFacebook : listFriendsIds){
			Node foundNode = nodeIndex.get("idFacebook",idFacebook).getSingle();
			user = new User(graphDb, nodeIndex);
			user.initThroughNode(foundNode);
			List<Site> listSites = (List<Site>) user.getEndEntitiesOfMyRelationships(RelationTypes.Owns);
			listSites = processListSites(listSites);
			finalList.addAll(listSites);
		}	
		log.info("RETURN: finalList.size()" + finalList.size());
		return finalList;
		
	}
	public static List<Site> getSitesList(List<String> listSiteIds) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		log.info("SERVICE: getSitesList" +
				"\nPARAMETERS: listsSitesIds.size()="+  listSiteIds.size());
		useConnection();
		
		List<Site> finalList = new ArrayList<Site>();
		Site site = null;
		for (String idSite : listSiteIds){
			Node foundNode = nodeIndex.get("idSite",idSite).getSingle();
			site = new Site();
			site.initThroughNode(foundNode);
			finalList.add(site);
		}
		log.info("RETURN: finalList.size()" + finalList.size());
		return finalList;
		
	}
	
	private static List<Site> processListSites(List<Site> listSites){
		List<Site> finalList = new ArrayList<Site>();
		Site site  = null;
		for (Site s : listSites){	
			site = new Site(s.getIdSite(), s.getName(), s.getUrlPhoto(), s.getPositionX(),
					s.getPositionY(), s.getNameOwner(), s.getOwnerId());
			finalList.add(site);
		}
		return finalList;
	}

	private static void useConnection(){
		
		graphDb = InitializeDB.getGraphDb();
		nodeIndex = InitializeDB.getNodeIndex();
	}

}
