package services;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import neo4j.InitializeDB;
import neo4j.Types.NodeTypes;
import neo4j.Types.RelationTypes;
import models.User;

public class UserServices {

	static EmbeddedGraphDatabase graphDb;
	static Index<Node> nodeIndex;
	
	protected final static Logger log = Logger.getLogger(UserServices.class.getName());
	
	public static void registerUser(User user) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		
		log.info("SERVICE: registerUser" +
				"\nPARAMETERS: user.getName()= " + user.getName());
		useConnection();
		
		user.putConectionInfo(graphDb, nodeIndex);		
		user.register(NodeTypes.User);
		
	}
	
	@SuppressWarnings("unchecked")
	public static List<User> getFriendsList(String idFacebook) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
				
		log.info("SERVICE: getFriendsList" +
				"\nPARAMETERS: idFacebook="+idFacebook);
		useConnection();
		
		Node foundNode = nodeIndex.get("idFacebook",idFacebook).getSingle();
		User user = new User(graphDb, nodeIndex);
		user.initThroughNode(foundNode);
		
		List<User> listUsers = (List<User>) user.getEndEntitiesOfMyRelationships(RelationTypes.Knows);
		listUsers = processListUsers(listUsers);		
		log.info("RETURN: listUsers.size()" + listUsers.size());
		return listUsers;
	}
	
	public static void updateFriends(String idFacebook, List<String> listUserIds) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{

		log.info("SERVICE: updateFriends" +
				"\nPARAMETERS: listsFriends.size()="+  listUserIds.size());
		useConnection();
		
		Node foundNode = nodeIndex.get("idFacebook",idFacebook).getSingle();
		User user = new User(graphDb, nodeIndex);
		user.initThroughNode(foundNode);
		
		Node friendNode;
		User friendUser = new User(graphDb, nodeIndex);
		for(String idFriend : listUserIds){			
			friendNode = nodeIndex.get("idFacebook",idFriend).getSingle();
			/*
			 * Si el usuario esta registrado en Trusted Sites -> creamos la relación
			 */
			if(friendNode != null){
				user.createRelationship("idFacebook", idFriend, RelationTypes.Knows);
				friendUser.initThroughNode(friendNode);
				friendUser.createRelationship("idFacebook", idFacebook, RelationTypes.Knows);
			}
		}
	}
	
	private static List<User> processListUsers(List<User> listUsers){
		
		List<User> finalList = new ArrayList<User>();
		User user = null;
		for (User u : listUsers){			
			user = new User(u.getIdFacebook(), u.getName(), u.getUrlPhoto());
			finalList.add(user);
		}
		return finalList;
	}

	private static void useConnection(){
		
		graphDb = InitializeDB.getGraphDb();
		nodeIndex = InitializeDB.getNodeIndex();
	}

}
