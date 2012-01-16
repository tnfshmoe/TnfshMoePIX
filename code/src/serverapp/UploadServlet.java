package serverapp;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.urlfetch.*;
import com.google.appengine.api.files.*;
import com.google.gson.*;

import datastore.*;

@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet{
	public final char[] UIDMap = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','Z','Y','Z',
		'0','1','2','3','4','5','6','7','8','9'};
	public String createUID(){
		Long num;
		String uid;
		
		uid = "";
		num = new Date().getTime();
		while(num > 0){
			uid += UIDMap[(int)(num % 62)];
			num /= 62;
		}
		
		uid += Math.round(Math.random()*9.0);
		
		return uid;
	}
	public String postFile(URLFetchService us,List<PostObj> postobjlist) throws Exception{
		int index;
		
		Gson gson;
		String linkID;
		List<String> linkList;
		HTTPRequest req;
		String param;
		
		gson = new Gson();
		
		linkID = createUID();
		linkList = new ArrayList<String>();
		for(index = 0;index < postobjlist.size();index++){
			linkList.add(postobjlist.get(index).filelink);
		}
		
		param = URLEncoder.encode("postlist","UTF-8") + "=" + URLEncoder.encode(gson.toJson(postobjlist),"UTF-8") + '&' +
				URLEncoder.encode("linkid","UTF-8") + "=" + URLEncoder.encode(linkID,"UTF-8") + '&' +
				URLEncoder.encode("linklist","UTF-8") + "=" + URLEncoder.encode(gson.toJson(linkList),"UTF-8");
		req = new HTTPRequest(new URL("http://tnfshmoe.appspot.com/postdf89ksfxsyx9sfdex09usdjksd"),HTTPMethod.POST);
		req.setPayload(param.getBytes());
		us.fetch(req);
		
		return linkID;
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		int index;
		
		DatastoreService ds;
		MemcacheService ms;
		BlobstoreService bs;
		URLFetchService us;
		FileService fs;
		
		Map<String,List<BlobKey>> blobMap;
		BlobKey blobKey;
		BlobInfoFactory blobInfoFactory;
		BlobInfo blobInfo;
		
		int postCount;
		int postIndex;
		String postType;
		String postText;
		String postDelpw;
		String postShowgallery;
		DataObj dataObj;
		String filelink;
		
		String picUrl;
		HTTPResponse picRes; 
		List<HTTPHeader> headerList;
		String picMime;
		String[] fileNamePart;
		AppEngineFile picFile;
		FileWriteChannel writeChannel;
		
		PostObj postObj;
		List<PostObj> postObjList;
		String linkID;
		
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/plain");
		
		try{
			ds = DatastoreServiceFactory.getDatastoreService();
			ms = MemcacheServiceFactory.getMemcacheService();
			bs = BlobstoreServiceFactory.getBlobstoreService();
			us = URLFetchServiceFactory.getURLFetchService();
			fs = FileServiceFactory.getFileService();
			blobInfoFactory = new BlobInfoFactory(ds);
			
			blobMap = bs.getUploads(req);
			postCount = Integer.valueOf(req.getParameter("input_post_count"));
			postObjList = new ArrayList<PostObj>();
			
			for(postIndex = 0;postIndex < postCount;postIndex++){
				try{
					postType = req.getParameter("input_post_type_" + postIndex);
					if(postType == null){
						continue;
					}
					
					postText = req.getParameter("input_post_text_" + postIndex);
					postDelpw = req.getParameter("input_post_delpw_" + postIndex);
					postShowgallery = req.getParameter("input_post_showgallery_" + postIndex);
					if(postShowgallery == null){
						postShowgallery = "";
					}
					
					if(postType.equals("file") == true){
						blobKey = blobMap.get("input_post_file_" + postIndex).get(0);
						if(blobKey == null){
							throw new Exception();
						}
						
						dataObj = new DataObj();
						dataObj.fileid = createUID();
						dataObj.posttime = new Date().getTime();
						dataObj.delpw = postDelpw;
						dataObj.blobkey = blobKey;
						dataObj.putDB(ds);
						
						blobInfo = blobInfoFactory.loadBlobInfo(dataObj.blobkey);
						filelink = "http://" + req.getServerName() + "/down/" + dataObj.fileid + "/" + blobInfo.getFilename();
						postObj = new PostObj(dataObj.fileid,filelink,blobInfo.getSize(),dataObj.posttime,dataObj.delpw,postShowgallery);
						postObjList.add(postObj);
					}else if(postType.equals("url") == true){
						picUrl = postText;
						
						picRes = us.fetch(new URL(picUrl));
						headerList = picRes.getHeaders();
						picMime = "application/octet-stream";
						for(index = 0;index < headerList.size();index++){
							if(headerList.get(index).getName().compareToIgnoreCase("Content-Type") == 0){
								picMime = headerList.get(index).getValue();
								break;
							}
						}
						
						fileNamePart = picUrl.split("/");
						
						picFile = fs.createNewBlobFile(picMime,fileNamePart[fileNamePart.length - 1]);
						writeChannel = fs.openWriteChannel(picFile,true);
						writeChannel.write(ByteBuffer.wrap(picRes.getContent()));
						writeChannel.closeFinally();
						
						dataObj = new DataObj();
						dataObj.fileid = createUID();
						dataObj.posttime = new Date().getTime();
						dataObj.delpw = postDelpw;
						dataObj.blobkey = fs.getBlobKey(picFile);
						dataObj.putDB(ds);
						
						blobInfo = blobInfoFactory.loadBlobInfo(dataObj.blobkey);
						filelink = "http://" + req.getServerName() + "/down/" + dataObj.fileid + "/" + blobInfo.getFilename();
						postObj = new PostObj(dataObj.fileid,filelink,blobInfo.getSize(),dataObj.posttime,dataObj.delpw,postShowgallery);
						postObjList.add(postObj);
					}
				}catch(Exception e){}
			}
			
			linkID = postFile(us,postObjList);
						
			if(req.getParameter("specflag") != null){
				resp.getWriter().print("http://tnfshmoe.appspot.com/link.jsp?linkid=" + linkID);
			}else{
				resp.sendRedirect("http://tnfshmoe.appspot.com/link.jsp?linkid=" + linkID);
			}
		}catch(Exception e){}
	}
}
