package datastore;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.blobstore.*;

public class DataObj {
	public String fileid;
	public Long posttime;
	public String delpw;
	public BlobKey blobkey;
	
	public void getDB(Entity entity){
		this.fileid = (String)entity.getProperty("fileid");
		this.posttime = (Long)entity.getProperty("posttime");
		this.delpw = (String)entity.getProperty("delpw");
		this.blobkey = (BlobKey)entity.getProperty("blobkey");
	}
	
	public void putDB(DatastoreService ds){
		Key groupKey;
		Entity entity;
		
		groupKey = KeyFactory.createKey("DataObjGroup",1L);
		entity = new Entity("DataObj",groupKey);
		
		entity.setProperty("fileid",this.fileid);
		entity.setProperty("posttime",this.posttime);
		entity.setProperty("delpw",this.delpw);
		entity.setProperty("blobkey",this.blobkey);
		
		ds.put(entity);
	}
}
