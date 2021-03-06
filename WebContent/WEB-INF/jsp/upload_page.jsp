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
<%-- author: Justin Doods --%>
<%-- author: Saad Sarfraz --%>
<%-- author: Tobias Brix --%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
<title>ODMDataAnalysis</title>
<link rel="stylesheet" type="text/css"
	href="webjars/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="resources/css/download.css">
<link rel="stylesheet" type="text/css" href="resources/css/index.css">
<link rel="stylesheet" type="text/css" href="resources/css/colors.css">
<link rel="stylesheet" type="text/css" href="resources/css/footer.css">
<link rel="stylesheet" type="text/css" href="webjars/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body>

		<jsp:include page="header.jsp"></jsp:include>

		<div class="body-container">
		<!-- Header tab content -->
		<div class="tab-content" id="page-content">
			<div role="tabpanel" class="tab-pane active" id="home">
						
						<div class="row"> 
							<!-- Jumbotron -->
							<div class="jumbotron col-sm-8">
								<h1>Upload your file here!</h1>
								<p class="lead"><font color="red"><b>Caution:</b></font> 
								Please upload only ODM files, whose clinical data is anonymized. Although the uploaded 
								data will be deleted after the analysis process, we cannot provide the needed standards 
								to process sensitive personal information.</p>
								<form:form id="target" method="POST" modelAttribute="file" enctype="multipart/form-data" class="lead">
									<div class="form-check mb-2 mr-sm-2 mb-sm-0">
									    <label class="form-check-label">
									      <input class="form-check-input" id="calculateStat" name="calculateStat" type="checkbox" checked="checked"> Calculate Statistics
									    </label>
									 </div>
									<div class="form-check mb-2 mr-sm-2 mb-sm-0">
									    <label class="form-check-label">
									      <input class="form-check-input" id="calculateCompleteness" name="calculateCompleteness" type="checkbox"> Calculate Completeness
									    </label>
									 </div>
									 <div class="form-check mb-2 mr-sm-2 mb-sm-0">
									    <label class="form-check-label">
									      <input class="form-check-input" id="exportFile" name="exportFile" type="checkbox"> Generate PDF Export (requires statistics)
									    </label>
									 </div>
									<div class="form-inline form-group">
										<div class="input-group" id="browsing">
											<label class="input-group-btn"> <span
												class="btn btn-success"> Browse&hellip; <input
													type="file" style="display: none;" name="file" id="file"
													accept=".xml">
											</span>
											</label> <input type="text" class="form-control" readonly="readonly">
										</div>
										<input id="submit" type="submit" value="Upload" class="btn btn-primary" />
									</div>
								</form:form>
								<div id="valueRequired" class="alert alert-danger hidden">
									<span id="infospan" class="help-block" id="info">
										Please select at least one option. 'Statistics' will be
										generated by default if none of the options are selected.
					 				</span>
								</div>
							</div>
							
							<div class="jumbotron jumbotron-download col-sm-4" >
								<h1>Download</h1> 
								<p class="lead">Download an ODM XML example file here to test the system.</p>							
								<form action="${pageContext.request.contextPath}/downloadTestFile" method="get" >
									<div id="download-xml-btn">
										<input class="btn btn-primary" type="submit" value="Download Test ODM File">
									</div>
								</form>
							</div>
						</div>
						
						<!-- Progress bar -->
						<div class="progress-bar-div" >
							<div class="progress">
								<div
									class="progress-bar progress-bar-striped bg-success upload-file"
									role="progressbar"
									aria-valuemin="0" aria-valuemax="100"></div>
									<span class='u-percent-completed'>File Upload</span>
							</div>
							<div class="progress">
								<div
									class="progress-bar progress-bar-striped bg-info parse-file"
									role="progressbar" 
									aria-valuemin="0" aria-valuemax="100"></div>
								<span class='p-percent-completed'>Parsing File</span>
							</div>
							<div class="progress">
								<div
									class="progress-bar progress-bar-striped bg-warning calc-statistics"
									role="progressbar" 
									aria-valuemin="0" aria-valuemax="100"></div>
								<span class='s-percent-completed'>Calculate Statistics</span>
							</div>
							<div class="progress">
								<div
									class="progress-bar progress-bar-striped bg-danger calc-completeness"
									role="progressbar" 
									aria-valuemin="0" aria-valuemax="100"></div>
								<span class='c-percent-completed'>Calculate Completeness</span>
							</div>	
							<div class="progress">
								<div
									class="progress-bar progress-bar-striped bg-primary generate-pdf"
									role="progressbar" 
									aria-valuemin="0" aria-valuemax="100"></div>
								<span class='pdf-percent-completed'>Gernerate PDF</span>
							</div>							
						</div>
						<div id="loading-page-anim-overlay" class="hidden">
							<div id="loader-div">
								<div class="loader"></div>Preparing and Loading Results...
							</div>
						</div>

						<!-- Example row of columns -->
						<div class="row information">
							<div class="col-lg-12">
								<h2>What does it do?</h2>
								<p>ODM Data Analysis is a tool for the automatic
									generation of generic descriptive statistics for each
									data item contained in an ODM file. More details 
									are available on the About page.</p>
								<p>
									<a class="btn btn-primary" id='details-button' style='color: white;'
										 role="button">Go to &raquo;</a>
								</p>
							</div>
						</div>

			</div>
			
			<div role="tabpanel" class="tab-pane" id="results-tab-content">
				<div class="response"></div>
			</div>

			<div role="tabpanel" class="tab-pane" id="about-tab-content">
				<div class="about">
					<!-- HTML code has been outsourced -->
					<%@ include file="html/about_page.html" %>
				</div>
			</div>
		</div>
		<!-- Site footer -->
		<footer class="footer">
			<div id="copyright">
			<table class="copyright">
				<tr>
					<td>&copy; 2017 - 2018</td>
					<td></td>
					<td>Institute of Medical Informatics (IMI), <br/> University of M�nster</td>
				</tr>
			</table>
			</div>
			<div id="impressum">
			<!-- Code from https://bootsnipp.com/snippets/featured/windows-8-bootstrap-modals -->
			<button type="button" class="btn btn-primary text-right" data-toggle="modal" data-target=".bs-example-modal-lg">Impressum</button>
			</div>
			
			<div class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
			  <div class="modal-dialog modal-lg">
				<div class="modal-content">
				  <div class="modal-body">
						<H2>Impressum</H2>
						Westf�lische Wilhelms-Universit�t M�nster <br>
						Schlossplatz 2, 48149 M�nster <br>
						Telephone: +49 (251) 83-0 <br>
						Fax: +49 (251) 83-3 20 90 <br>
						E-mail: <a href="mailto:verwaltung@uni-muenster.de">verwaltung@uni-muenster.de</a> <br>
						<br>
						The WWU M�nster is a statutory body and an institution of the Land of North Rhine-Westphalia. 
						It is represented by the Rector, Professor Dr. Johannes Wessels. <br>
						<br>
						Turnover tax identification number: DE 126118759 <br>
						<br>
						Edited in accordance with �5 TMG by: <br>
						Prof. Dr. Martin Dugas <br>
						Institute of Medical Informatics <br>
						Albert-Schweizer-Campus 1, Building A11 <br>
						48149 M�nster, Germany <br>
						Telephone: +49 (251) 83-55262 <br>
						E-mail: <a href="mailto:imi@uni-muenster.de">imi@uni-muenster.de</a>
				  </div>
				</div>
			  </div>
			</div>
		</footer>
	</div>
	<!-- /container -->

	<script src="webjars/jquery/3.1.1/jquery.min.js" type="text/javascript"></script>
	<script src="webjars/tether/1.4.0/dist/js/tether.min.js"
		type="text/javascript"></script>
	<script src="webjars/bootstrap/4.0.0-alpha.6/js/bootstrap.min.js"
		type="text/javascript"></script>
	<script src="webjars/jquery-form/3.51/jquery.form.js"
		type="text/javascript"></script>

	<script>
		var contextPath = "${pageContext.request.contextPath}";
	</script>
	<script src="resources/js/index.js" type="text/javascript"></script>
	<script src="resources/js/analysis.js" type="text/javascript"></script>

</body>
</html>
