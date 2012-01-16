package indexapp;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import datastore.*;

@SuppressWarnings("serial")
public class DelPostServlet extends HttpServlet{
	public void doPost(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		int index;
		
		DatastoreService ds;
		
		Gson gson;
		Type delObjType;
		List<DelObj> delObjList;
		DelObj delObj;
		List<String> fileidList;
		List<String> delpwList;
		
		Key postObjGroupKey;
		Query q;
		List<Entity> postObjList;
		Entity postObjEntity;
		PostObj postObj;
		
		ds = DatastoreServiceFactory.getDatastoreService();
		
		gson = new Gson();
		delObjType = new TypeToken<List<DelObj>>(){}.getType();
		delObjList = gson.fromJson(req.getParameter("dellist"),delObjType);
		
		fileidList = new ArrayList<String>();
		delpwList = new ArrayList<String>();
		for(index = 0;index < delObjList.size();index++){
			delObj = delObjList.get(index);
			if(delObj.delpw.equals("") == false){
				fileidList.add(delObj.fileid);
				delpwList.add(delObj.delpw);
			}
		}
		
		postObjGroupKey = KeyFactory.createKey("PostObjGroup",1L);
		q = new Query("PostObj",postObjGroupKey);
		q.addFilter("fileid",FilterOperator.IN,fileidList);
		postObjList = ds.prepare(q).asList(FetchOptions.Builder.withLimit(1024));
		
		postObj = new PostObj();
		for(index = 0;index < postObjList.size();index++){
			postObjEntity = postObjList.get(index);
			postObj.getDB(postObjEntity);
			
			if(postObj.delpw.equals(delpwList.get(index)) == true){
				ds.delete(postObjEntity.getKey());
			}
		}
	}
}

class DelObj{
	public String fileid;
	public String delpw;
}
