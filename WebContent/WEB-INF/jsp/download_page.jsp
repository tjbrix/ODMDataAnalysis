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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

</head>
<body>
	
	<form id='download-PDF' action="${pageContext.request.contextPath}/downloadPDF" method="get" >
		<input name="fileName" type="hidden" value="" id="fileName">
		<div id="download-pdf-btn">

				<button class="btn btn-primary" type="submit">Download PDF</button>

		</div>
	</form> 
	<div id="loading-pdf-anim">
		<div class="loader"></div>Loading PDF...
	</div>
	<div id="pdf-view-div">	
	</div>
	
	
<script>
var path = "${pageContext.request.contextPath}/PDFs/${pageContext.session.id}${fileName}.pdf";
getPdf();


function getPdf(){
	$.ajax({
		type: "GET",
		url: path,
		success: function (data) {
			$('#loading-pdf-anim').addClass('hidden');

			$('#pdf-view-div').html(
					"<object id='pdf-object-view' data='${pageContext.request.contextPath}/PDFs/${pageContext.session.id}${fileName}.pdf'><p>Could not preview the document. Please download PDF file to view locally.</p></object>"
			);
		},
		error: function () {
			//callback getMyJson here in 5 seconds
	 		setTimeout(function () {
			getPdf();
			}, 2000)
		}
	});
}

</script>
</body>
</html>
