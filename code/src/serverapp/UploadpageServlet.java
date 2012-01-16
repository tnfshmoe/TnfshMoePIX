package serverapp;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.http.*;
import com.google.appengine.api.memcache.*;
import com.google.appengine.api.urlfetch.*;
import com.google.appengine.api.blobstore.*;

import datastore.*;

@SuppressWarnings("serial")
public class UploadpageServlet extends HttpServlet{
	public void doGet(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		MemcacheService ms;
		URLFetchService us;
		BlobstoreService bs;
		
		String code;
		String uploadlink;
		
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html");
		
		ms = MemcacheServiceFactory.getMemcacheService();
		us = URLFetchServiceFactory.getURLFetchService();
		bs = BlobstoreServiceFactory.getBlobstoreService();
		
		code = (String)ms.get("UploadpageCodeCache");
		if(code == null){
			code = new String(us.fetch(new URL("http://tnfshmoe.appspot.com/uploadpagedf8duxe45890dfjhwer38her8erhd98efkdf490.html")).getContent(),"UTF-8");
			ms.put("UploadpageCodeCache",code);
		}
		
		uploadlink = bs.createUploadUrl("/upload");
		code = code.replaceAll("<%= uploadlink %>",uploadlink);
		
		resp.getWriter().print(code);
	}
}
