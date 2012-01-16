package indexapp;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.memcache.*;
import com.google.gson.*;

import datastore.*;

@SuppressWarnings("serial")
public class GalleryServlet extends HttpServlet{
	public void doPost(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		int index;
		
		DatastoreService ds;
		MemcacheService ms;
		
		Cookie[] cookies;
		boolean insideFlag;
		
		String delpw;
		String paramOffset;
		String paramSize;
		int offset;
		int size;
		
		Key postObjGroupKey;
		Query q;
		List<Entity> postObjList;
		PostObj postObj;
		
		Gson gson;
		List<String> filelinkList;
		
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/plain");
		
		ds = DatastoreServiceFactory.getDatastoreService();
		ms = MemcacheServiceFactory.getMemcacheService();
		
		insideFlag = false;
		try{
			cookies = req.getCookies();
			if(cookies.length > 0){
				if(ms.contains(cookies[0].getValue()) == true){
					insideFlag = true;
				}
			}
		}catch(Exception e){
			insideFlag = false;
		}
		
		delpw = req.getParameter("delpw");
		if(delpw != null){
			if(delpw.equals("") == true){
				delpw = null;
			}
		}
		
		paramOffset = req.getParameter("offset");
		if(paramOffset != null){
			offset = Integer.valueOf(paramOffset);
		}else{
			offset = 0;
		}
		
		paramSize = req.getParameter("size");
		if(paramSize != null){
			size = Integer.valueOf(paramSize);
		}else{
			size = 4096;
		}
		
		postObjGroupKey = KeyFactory.createKey("PostObjGroup",1L);
		q = new Query("PostObj",postObjGroupKey);
		if(delpw != null){
			q.addFilter("delpw",FilterOperator.EQUAL,delpw);
			q.addSort("delpw");
		}
		q.addSort("posttime",SortDirection.DESCENDING);
		postObjList = ds.prepare(q).asList(FetchOptions.Builder.withOffset(offset).limit(size));
		
		postObj = new PostObj();
		filelinkList = new ArrayList<String>();
		for(index = 0;index < postObjList.size();index++){
			postObj.getDB(postObjList.get(index));
			if((postObj.flag.equals("showgallery") == false && insideFlag == true) ||
					(postObj.flag.equals("showgallery") == true && insideFlag == false) ||
					delpw != null){
				filelinkList.add(postObj.filelink);
			}
		}
		
		gson = new Gson();
		resp.getWriter().print(gson.toJson(filelinkList));
	}
}
