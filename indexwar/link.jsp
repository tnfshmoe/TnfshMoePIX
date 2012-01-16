<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.google.appengine.api.memcache.*" %>

<%
	int index;

	MemcacheService ms;

	List<String> linkList;
	String link;

	ms = MemcacheServiceFactory.getMemcacheService();
	
	linkList = (List<String>)ms.get("LinkList_" + request.getParameter("linkid"));
	if(linkList == null){
		return;
	}
%>

<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<title>Sarin Pic</title>
		
		<style type="text/css">
			.select_button{
				width:100px;
				height:50px;
				margin:0px auto;
				color:#FAFAFA;
				font-weight:bold;
				background-color:#F62217;
				border:1px solid #E41B17;
				text-align:center;
				line-height:50px;
				cursor:default;
				overflow:hidden;
			}	
			.select_button_hi{
				width:100px;
				height:50px;
				margin:0px auto;
				color:#FAFAFA;
				font-weight:bold;
				background-color:#FF3025;
				border:1px solid #E41B17;
				text-align:center;
				line-height:50px;
				cursor:default;
				overflow:hidden;
			}
			.select_button_b{
				width:100px;
				height:50px;
				margin:0px auto;
				color:#FAFAFA;
				font-weight:bold;
				background-color:#306EFF;
				border:1px solid #2B65EC;
				text-align:center;
				line-height:50px;
				cursor:default;
				overflow:hidden;
			}	
			.select_button_hi_b{
				width:100px;
				height:50px;
				margin:0px auto;
				color:#FAFAFA;
				font-weight:bold;
				background-color:#3C77FF;
				border:1px solid #2B65EC;
				text-align:center;
				line-height:50px;
				cursor:default;
				overflow:hidden;
			}
			.link_text{
				width:100%;;
				height:30px;
				font-size:20px;
				text-align:center;
			}
		</style>
		
		<script type="text/javascript" src="/highslide/highslide.js"></script>
		<link rel="stylesheet" type="text/css" href="/highslide/highslide.css" />
		<script type="text/javascript"src="/zeroclipboard/ZeroClipboard.js"></script>
		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/prototype/1.7.0.0/prototype.js"></script>
		<script type="text/javascript">
			function copybutton(link,index){
				var clip;
				
				clip = new ZeroClipboard.Client();
				clip.setText(link);
				clip.glue('button_copy_' + index,'button_copy_' + index,{'position':'relative','top':'-50px'});
				
				clip.addEventListener('mouseover',function(event){
					$('button_copy_' + index).className = 'select_button_hi';
				},false);
				clip.addEventListener('mouseout',function(event){
					$('button_copy_' + index).className = 'select_button';
				},false);
			}
		</script>
	</head>
	<body>
		<table style="position:absolute; top:10px; left:10px;">
			<tr>
				<td><a href="http://sarinpic.appspot.com/index.jsp" style="font-weight:bold;" onclick="window.top.location.href = 'http://sarinpic.appspot.com/index.jsp'; return false;">上傳</a></td>
				<td><a href="http://sarinpic.appspot.com/gallery.html" style="font-weight:bold;" onclick="window.top.location.href = 'http://sarinpic.appspot.com/gallery.html'; return false;">畫廊</a></td>
				<td><a href="http://sarinpic.appspot.com/del.html" style="font-weight:bold;" onclick="window.top.location.href = 'http://sarinpic.appspot.com/del.html'; return false;">刪除</a></td>
			</tr>
		</table>
		
		<div style="width:100%; margin:60px 0px; text-align:center;">
		<%
		for(index = 0;index < linkList.size();index++){
			link = linkList.get(index);
			%>
			<div style="margin:20px auto 0px auto;">
				<a href="<%= link %>" class="highslide" onclick="return hs.expand(this)">
					<img src="<%= link %>?size=200"/>
				</a>
			</div>
			<input type="text" readonly="readonly" value="<%= link %>" class="link_text" style="width:400px; margin:10px 0px;" onclick="this.select();">
			<table style="margin:0px auto;">
				<tr>
					<td>
						<div id="button_copy_<%= index %>" class="select_button">
							<div style="width:100px; position:absolute;">複製</div>
						</div>
					</td>
					<td><div class="select_button" onclick="window.open('http://www.facebook.com/sharer/sharer.php?u=<%= link %>','_blank','toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no')" onmouseover="this.className = 'select_button_hi';" onmouseout="this.className = 'select_button';">Facebook</div></td>
					<td><div class="select_button_b" onclick="window.top.location.href = 'http://sarinpic.appspot.com/index.jsp';" onmouseover="this.className = 'select_button_hi_b';" onmouseout="this.className = 'select_button_b';">上傳新圖片</div></td>
				</tr>
			</table>
			<script type="text/javascript">
				copybutton('<%= link %>',<%= index %>);
			</script>
			<%
		}
		%>
		</div>
	</body>
</html>