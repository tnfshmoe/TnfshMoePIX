package indexapp;

import java.io.*;
import java.util.*;
import java.lang.reflect.Type;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
import com.google.gson.*;
import com.google.gson.reflect.*;

import datastore.*;

@SuppressWarnings("serial")
public class PostServlet extends HttpServlet{
	public void doPost(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		int index;
		
		DatastoreService ds;
		MemcacheService ms;
			
		Gson gson;
		Type postObjType;
		List<PostObj> postObjList;
		String linkID;
		List<String> linkList;
		
		ds = DatastoreServiceFactory.getDatastoreService();
		ms = MemcacheServiceFactory.getMemcacheService();
		
		gson = new Gson();
		
		postObjType = new TypeToken<List<PostObj>>(){}.getType();
		postObjList = gson.fromJson(req.getParameter("postlist"),postObjType);
		
		for(index = 0;index < postObjList.size();index++){
			postObjList.get(index).putDB(ds);
		}
		
		linkID = req.getParameter("linkid");
		linkList = gson.fromJson(req.getParameter("linklist"),List.class);
		
		ms.put("LinkList_" + linkID,linkList);
	}
}
