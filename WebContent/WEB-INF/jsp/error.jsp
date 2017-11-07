<!--
 ODM Data Analysis - a tool for the automatic validation, monitoring and
 generation of generic descriptive statistics of clinical data.
 
 Copyright (c) 2017 Institut für Medizinische Informatik, Münster

 ODM Data Analysis is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by the
 Free Software Foundation, version 3.

 ODM Data Analysis is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 more details.

 You should have received a copy of the GNU General Public License in the file
 COPYING along with ODM Data Analysis. If not, see <http://www.gnu.org/licenses/>.
-->
<%-- author: Saad Sarfraz --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>error</title>
</head>
<body>

<h3 class='error-msg'><Strong>Error Summary:</Strong></h3>
<p><b>Following Errors were found in the file</b></p>
	<c:forEach items="${errorMsgs}" var="errorMsg" varStatus="counter">
		<strong>(${counter.index+1}.) </strong> <span>${errorMsg}</span>
		<br>
	</c:forEach>
</body>
</html>
