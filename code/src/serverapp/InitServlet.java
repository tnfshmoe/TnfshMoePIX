package serverapp;

import java.io.*;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

@SuppressWarnings("serial")
public class InitServlet extends HttpServlet{
	public void doGet(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		DatastoreService ds;
		MemcacheService ms;
		
		boolean flag;
		Entity entity;
		
		ds = DatastoreServiceFactory.getDatastoreService();
		ms = MemcacheServiceFactory.getMemcacheService();
		
		ms.clearAll();
		
		flag = true;
		if(flag == true){
			entity = new Entity("ServerObj");
			entity.setProperty("serverlink","");
			ds.put(entity);
		}
	}
}