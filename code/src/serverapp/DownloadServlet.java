package serverapp;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.memcache.*;
import com.google.appengine.api.images.*;

import datastore.*;

@SuppressWarnings("serial")
public class DownloadServlet extends HttpServlet{
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String[] urlPart;
		String fileid;
		
		DatastoreService ds;
		MemcacheService ms;
		BlobstoreService bs;
		ImagesService is;
		
		Key dataObjGroupKey;
		Query q;
		Entity dataObjEntity;
		DataObj dataObj;
		
		String downSize;
		int picSize;
		Image pic;
		
		try{
			urlPart = req.getRequestURI().split("/");
			if(urlPart.length != 4){
				throw new Exception();
			}
		
			ds = DatastoreServiceFactory.getDatastoreService();
			ms = MemcacheServiceFactory.getMemcacheService();
			bs = BlobstoreServiceFactory.getBlobstoreService();
			is = ImagesServiceFactory.getImagesService();
			
			fileid = urlPart[2];
			
			try{
				dataObjEntity = (Entity)ms.get("DataObjEntityCache_" + fileid);
				if(dataObjEntity == null){
					throw new Exception();
				}
			}catch(Exception e){
				dataObjGroupKey = KeyFactory.createKey("DataObjGroup",1L);
				q = new Query("DataObj",dataObjGroupKey);
				q.addFilter("fileid",FilterOperator.EQUAL,fileid);
				
				dataObjEntity = ds.prepare(q).asSingleEntity();
				ms.put("DataObjEntityCache_" + fileid,dataObjEntity);
			}
			
			dataObj = new DataObj();
			dataObj.getDB(dataObjEntity);
			
			downSize = req.getParameter("size");
			if(downSize != null){
				picSize = Integer.valueOf(downSize);
				pic = ImagesServiceFactory.makeImageFromBlob(dataObj.blobkey);
				pic = is.applyTransform(ImagesServiceFactory.makeResize(picSize,picSize),pic);
				resp.setContentType("image/" + pic.getFormat().name());
				resp.getOutputStream().write(pic.getImageData());
			}else{
				bs.serve(dataObj.blobkey,resp);
			}
		}catch(Exception e){}
	}
}
