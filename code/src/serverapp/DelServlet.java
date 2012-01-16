package serverapp;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import javax.servlet.http.*;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.memcache.*;
import com.google.gson.*;
import com.google.gson.reflect.*;

import datastore.*;

@SuppressWarnings("serial")
public class DelServlet extends HttpServlet{
	public void doOptions(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		resp.addHeader("Access-Control-Allow-Origin","*");
		resp.addHeader("Access-Control-Allow-Headers","Origin,X-Prototype-Version,X-Requested-With,Content-type,Accept");
		resp.addHeader("Access-Control-Allow-Methods","POST");
		resp.addHeader("Access-Control-Max-Age","3628800");
	}
	public void doPost(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		int index;
		
		DatastoreService ds;
		MemcacheService ms;
		BlobstoreService bs;
		
		Gson gson;
		Type delObjType;
		List<DelObj> delObjList;
		DelObj delObj;
		List<String> fileidList;
		List<String> delpwList;
		
		Key dataObjGroupKey;
		Query q;
		List<Entity> dataObjList;
		Entity dataObjEntity;
		DataObj dataObj;
		
		resp.addHeader("Access-Control-Allow-Origin","*");
		
		ds = DatastoreServiceFactory.getDatastoreService();
		ms = MemcacheServiceFactory.getMemcacheService();
		bs = BlobstoreServiceFactory.getBlobstoreService();
		
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
		
		dataObjGroupKey = KeyFactory.createKey("DataObjGroup",1L);
		q = new Query("DataObj",dataObjGroupKey);
		q.addFilter("fileid",FilterOperator.IN,fileidList);
		dataObjList = ds.prepare(q).asList(FetchOptions.Builder.withLimit(1024));
		
		dataObj = new DataObj();
		for(index = 0;index < dataObjList.size();index++){
			dataObjEntity = dataObjList.get(index);
			dataObj.getDB(dataObjEntity);
			
			if(dataObj.delpw.equals(delpwList.get(index)) == true){
				ds.delete(dataObjEntity.getKey());
				bs.delete(dataObj.blobkey);
				ms.delete("DataObjEntityCache_" + dataObj.fileid);
			}
		}
	}
}

class DelObj{
	public String fileid;
	public String delpw;
}