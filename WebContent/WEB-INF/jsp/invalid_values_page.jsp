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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="resources/css/invalid_values.css">
</head>
<body>
	<!-- Using javascript to set the value of 'fileName' input before submission 
	fileName must be same as when file was uploaded-->
	<form id='download-CSV'
		action="${pageContext.request.contextPath}/downloadCSV" method="get">
		<input name="fileName" type="hidden" value="" id="fileName">
		<div id="download-pdf-btn">
			<button class="btn btn-primary" type="submit">Download CSV</button>
		</div>
	</form>


	<h2>Invalid Values</h2>
	<table id='invalid-values-table' class="table">
		<thead>
			<tr class="row">
				<th class="col-1">Subject</th>
				<th class="col-1">Study Event OID</th>
				<th class="col-1">Study Event RepeatKey</th>
				<th class="col-1">Form OID</th>
				<th class="col-1">Form Repeat Key</th>
				<th class="col-1">Item Group</th>
				<th class="col-1">Item Group Repeat Key</th>
				<th class="col-1">Item OID</th>
				<th class="col-1">Value</th>
				<th class="col-3">Reason</th>
			</tr>
			<!-- Drop Down Selectors for filter -->
			<tr class="row">
				<td class="col-1"><select class="form-control"
					id='subjectKeySelect'>
						<option>No Filter</option>
				</select></td>
				<td class="col-1"><select class="form-control"
					id='sEventOIDSelect'>
						<option>No Filter</option>
				</select></td>
				<td class="col-1"><select class="form-control"
					id='seRKeySelect'>
						<option>No Filter</option>
				</select></td>
				<td class="col-1"><select class="form-control" id='fOIDSelect'>
						<option>No Filter</option>
				</select></td>
				<td class="col-1"><select class="form-control" id='fRKeySelect'>
						<option>No Filter</option>
				</select></td>
				<td class="col-1"><select class="form-control" id='igSelect'>
						<option>No Filter</option>
				</select></td>
				<td class="col-1"><select class="form-control"
					id='igRKeySelect'>
						<option>No Filter</option>
				</select></td>
				<td class="col-1"><select class="form-control" id='igOIDSelect'>
						<option>No Filter</option>
				</select></td>
				<td class="col-1"><select class="form-control" id='valueSelect'>
						<option>No Filter</option>
				</select></td>
				<td class="col-3"><select class="form-control"
					id='reasonSelect'>
						<option>No Filter</option>
				</select></td>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${invalidClinicalDataList}" var="icd">
				<tr class='row'>
					<td class="col-1">${icd.subjectKey }</td>
					<td class="col-1">${not empty icd.metaStudyEventOID ?  icd.metaStudyEventOID : "null"}</td>
					<td class="col-1">${empty icd.studyEventRepeatKey ? "null" :  icd.studyEventRepeatKey}</td>
					<td class="col-1">${not empty icd.metaFormOID ? icd.metaFormOID : "null" }</td>
					<td class="col-1">${empty icd.formRepeatKey ? "null" : icd.formRepeatKey}</td>
					<td class="col-1">${not empty icd.metaItemGroupOID ? icd.metaItemGroupOID : "null" }</td>
					<td class="col-1">${empty icd.itemGroupRepeatKey ? "null" : icd.itemGroupRepeatKey}</td>
					<td class="col-1">${not empty icd.metaItemOID ? icd.metaItemOID : "null" }</td>
					<td class="col-1">${empty icd.value ? "null" : icd.value}</td>
					<td class="col-3">${icd.reason }</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

	<script type="text/javascript" src="resources/js/invalid_values.js"></script>

</body>
</html>
