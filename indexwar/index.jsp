<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="com.google.appengine.api.memcache.*" %>
<%@ page import="com.google.appengine.api.blobstore.*" %>

<%
	DatastoreService ds;
	MemcacheService ms;
	BlobstoreService bs;
	
	Query q;
	List<Entity> serverObjList;
	String serverlink;
	
	ds = DatastoreServiceFactory.getDatastoreService();
	ms = MemcacheServiceFactory.getMemcacheService();
	bs = BlobstoreServiceFactory.getBlobstoreService();
	
	serverObjList = (List<Entity>)ms.get("ServerObjListCache");
	if(serverObjList == null){
		q = new Query("ServerObj");
		serverObjList = ds.prepare(q).asList(FetchOptions.Builder.withLimit(1024));
		
		ms.put("ServerObjListCache",serverObjList);
	}
	serverlink = (String)serverObjList.get(Math.round((float)Math.random()*(serverObjList.size()-1))).getProperty("serverlink");
%>

<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<title>TnfshMoe</title>
	</head>
	<frameset>
		<frame src="<%= serverlink %>/uploadpage" />
	</frameset>
</html>
