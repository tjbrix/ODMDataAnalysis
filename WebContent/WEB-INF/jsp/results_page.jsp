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
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<link rel="stylesheet" type="text/css" href="resources/css/analysis.css">
<link rel="stylesheet" type="text/css" href="resources/css/side_nav.css">
<body>
	<!-- Nav tabs -->
	<ul class="nav nav-tabs" role="tablist" id="results-tabs">
		<c:if test="${analysisAdded == true }">
			<li class="nav-item"><a class="nav-link" data-toggle="tab"
				href="#analysis-contentt" role="tab">Analysis</a></li>
		</c:if>
		<c:if test="${ completenessAdded == true }">
			<li class="nav-item"><a class="nav-link" data-toggle="tab"
				href="#completeness-contentt" role="tab">Completeness</a></li>
		</c:if>
		<c:if test="${exportFile == true }">
			<li class="nav-item"><a class="nav-link" data-toggle="tab"
				href="#export-contentt" role="tab">Export PDF</a></li>
		</c:if>
		<c:if test="${ not empty invalidClinicalDataList }">
			<li class="nav-item"><a class="nav-link" data-toggle="tab"
				href="#invalid-contentt" role="tab">Invalid Values</a></li>
		</c:if>
	</ul>

	<!-- Tab panes -->
	<div class="tab-content" id="results-tabs-content">
		<c:if test="${analysisAdded == true }">
			<div class="tab-pane" id="analysis-contentt" role="tabpanel">
				<jsp:include page="analysis_page.jsp"></jsp:include>
			</div>
		</c:if>
		<c:if test="${completenessAdded == true }">
			<div class="tab-pane" id="completeness-contentt" role="tabpanel">
				<jsp:include page="data_completeness_page.jsp"></jsp:include>
			</div>
		</c:if>
		<c:if test="${exportFile == true }">
			<div class="tab-pane" id="export-contentt" role="tabpanel">
				<jsp:include page="download_page.jsp"></jsp:include>
			</div>
		</c:if>
		<c:if test="${not empty invalidClinicalDataList }">
			<div class="tab-pane" id="invalid-contentt" role="tabpanel">
				<jsp:include page="invalid_values_page.jsp"></jsp:include>
			</div>
		</c:if>
	</div>
	<script src="resources/js/data_completeness.js"></script>
	<script src="resources/js/results.js"></script>
</body>
</html>
