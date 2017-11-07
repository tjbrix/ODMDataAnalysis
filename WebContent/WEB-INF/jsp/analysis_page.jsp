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
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<body>

<div class='analysis-body page-resize'>

	<c:if test="${not empty benchmarkList}">
		<table>
			<thead>
			<tr>
				<th>Task</th>
				<th>Time Taken (seconds)</th>
			</tr>		
			</thead>
			<tbody>
			<c:forEach items="${benchmarkList}" var="bm">
				<tr>
					<td> 
						<c:choose>
							<c:when test="${ bm.getKey().substring(0,5).equals('Total')}" >
								<b>${bm.getKey()}</b>
							</c:when>
							<c:otherwise>
								${bm.getKey()}	
							</c:otherwise>
						</c:choose>
					</td>
					<td>${ bm.getValue() }</td>			
				</tr>
			</c:forEach>
			
			</tbody>
		</table>
	</c:if>	
	<span style="font-size: 30px; cursor: pointer" id='nav-btn'>&#9776;
		StudyEvents-List</span>

	<!-- Use 12.34 Notation -->
	<fmt:setLocale value="en_US" scope="session" />
	<!-- Title -->
	<h4 class="heading-name">Dataset OID: ${ODMFile.OID}</h4>


	<!-- Analysis Data -->
	<div class="container-fluid">

		<!-- Side Navigation bar starts here -->
		<div id="mySidenav" class="sidenav sidenav-open">
			<a href="javascript:void(0)" class="sidenav-closebtn">&times;</a>
			<!-- Collapse-able bootstrap list -->
			<div class="panel-group">

				<c:set var="tabCounter" value="0" />
				<c:forEach items="${studyEventList}" var="studyEvent">
					<c:choose>
						<c:when test="${tabCounter==0}">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4 class="panel-title">
										<a class="nav-link active"
											href="#${fn:replace(studyEvent.OID, '.', '')}"
											role="tab" data-toggle="collapse" aria-expanded='true'>
											${studyEvent.name} <c:if
												test="${studyEvent.repeating}">
												
												<i class="fa fa-key" aria-hidden="true" title="StudyEvent is contained multiple times for at least one subject in this ODMFile! See 'StudyEventRepeatKey'."></i>
											</c:if>
											
											<c:if test="${studyEvent.isContainsInvalidValues() }"> 
												<i class="fa fa-exclamation-triangle" aria-hidden="true" title="StudyEvent contains invalid values."></i>
											</c:if>
										</a>
									</h4>
								</div>	
						</c:when>
						<c:when test="${tabCounter>0}">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h4 class="panel-title">
									<a class="nav-link"
										href="#${fn:replace(studyEvent.OID, '.', '')}"
										role="tab" data-toggle="collapse"> ${studyEvent.name} <c:if
											test="${studyEvent.repeating}">
											
											<i class="fa fa-key" aria-hidden="true" title="StudyEvent is contained multiple times for at least one subject in this ODMFile! See 'StudyEventRepeatKey'."></i>
										</c:if>
										<c:if test="${studyEvent.isContainsInvalidValues() }">
												<i class="fa fa-exclamation-triangle" aria-hidden="true" title="StudyEvent contains invalid values."></i>
										</c:if>
									</a>
								 </h4>
							</div>
						
				    	</c:when>

					</c:choose>



					
					<!-- Tab Content for Side-Navigation bar starts here -->
					
					<c:choose>
						<c:when test="${tabCounter==0 }">
							<div class="panel-collapse collapse in show"	id="${fn:replace(studyEvent.OID, '.', '')}">
						</c:when>
						<c:when test="${tabCounter>0 }">
						<div class="panel-collapse collapse in"	id="${fn:replace(studyEvent.OID, '.', '')}">
						</c:when>
					
					</c:choose>	
				




				<!-- Forms Names Begin here -->
					<ul class="nav flex-column panel-body">						
						<c:forEach items="${studyEvent.metaFormList}" var="form">
							<c:choose>
								<c:when test="${tabCounter==0}">
									<li class="nav-item"><a
										class="nav-link active"
										href="#${fn:replace(studyEvent.OID, '.', '')}${fn:replace(form.OID, '.', '')}"
										role="tab" > ${form.name} 
											<c:if test="${form.repeating}">
												<i class="fa fa-key" aria-hidden="true" title="Form is contained multiple times for at least one subject in this StudyEvent! See 'FormRepeatKey'."></i>
											</c:if>
											<c:if test="${form.isContainsInvalidValues()}">
												<i class="fa fa-exclamation-triangle" aria-hidden="true" title="Form contains invalid values."></i>
											</c:if>
									</a></li>
									<c:set var="tabCounter" value="${tabCounter+1}" />
								</c:when>
								<c:when test="${tabCounter>0}">
									<li class="nav-item"><a
										class="nav-link"
										href="#${fn:replace(studyEvent.OID, '.', '')}${fn:replace(form.OID, '.', '')}"
										role="tab"> ${form.name} 
											<c:if test="${form.repeating}">
												<i class="fa fa-key" aria-hidden="true" title="Form is contained multiple times for at least one subject in this StudyEvent! See 'FormRepeatKey'."></i>
											</c:if>
											<c:if test="${form.isContainsInvalidValues()}">
												<i class="fa fa-exclamation-triangle" aria-hidden="true" title="Form contains invalid values."></i>
											</c:if>
									</a></li>
								</c:when>
							</c:choose>
						</c:forEach>
					</ul>
					  </div>

					</div> <!-- One Panel ends here -->
				</c:forEach>

			</div> <!-- Collapse div ends here  -->


		</div> <!-- Side Navigation bar ends here -->


		<c:set var="counter" value="0" />
		<div>
			<c:forEach items="${studyEventList}" var="studyEvent">


			<!-- Tables Content Start here -->				
					<div>
					
						<c:forEach items="${studyEvent.metaFormList}" var="form">
							<c:choose>
								<c:when test="${counter==0}">
									<div
										id="${fn:replace(studyEvent.OID, '.', '')}${fn:replace(form.OID, '.', '')}">
										<!-- Close button to disable tables -->
										<a href="javascript:void(0)" class="closebtn-table">&times;</a>
										
										<c:set var="counter" value="${counter+1}" />
								</c:when>
								<c:when test="${counter>0}">
									<div class='disabled-table'
										id="${fn:replace(studyEvent.OID, '.', '')}${fn:replace(form.OID, '.', '')}">
										<!-- Close button to disable tables -->
										<a href="javascript:void(0)" class="closebtn-table">&times;</a>
								
								</c:when>
							</c:choose>
							<fieldset>

	
								<table class="table table-hover" id='analysis-table'>
									<thead class="table-inverse">
										<tr class='collapsed-all'>
											<th class="itemGroup-head">Type</th>
											<th><span class="sign"></span></th>
											<th>Name</th>
											<th>Data Count</th>
											<th>Subject Count</th>
										
										</tr>
									</thead>
									<tbody>
									<!-- Meta information (of event and form) starts here -->
										<tr>
											<th>Study Event</th>
											<td></td>
											<td>${studyEvent.name}
												<c:if test="${studyEvent.repeating}">
														<i class="fa fa-key" aria-hidden="true" title="StudyEvent is contained multiple times for at least one subject in this ODMFile! See 'StudyEventRepeatKey'."></i>
													<!--</a>-->
												</c:if>
												<c:if test="${studyEvent.isContainsInvalidValues()}">
														<i class="fa fa-exclamation-triangle" aria-hidden="true" title="StudyEvent contains invalid values."></i>
												</c:if>
											</td>
											<td>${studyEvent.dataCounts}</td>
											<td>${studyEvent.subjectCounts}</td>
										</tr>
										<tr>
											<th>Form</th>
											<td></td>
											<td>${form.name}
												<c:if test="${form.repeating}">
														<i class="fa fa-key" aria-hidden="true" title="Form is contained multiple times for at least one subject in this StudyEvent! See 'FormRepeatKey'."></i>
													<!--</a>-->
												</c:if>
												<c:if test="${form.isContainsInvalidValues()}">
														<i class="fa fa-exclamation-triangle" aria-hidden="true" title="Form contains invalid values."></i>
												</c:if>
											
											</td>
											<td>${form.dataCounts}</td>
											<td>${form.subjectCounts}	</td>
										</tr>									
									<!-- Meta information (of event and form) ends here -->
			
										<c:set var="itemGroupSize" value="${ form.metaItemGroupList.size()*2+1}"/> 
										<tr class='itemGroup-head'>
												<th rowspan="${itemGroupSize }">ItemGroup</th>
										</tr>
										<c:forEach items="${form.metaItemGroupList}" var="itemGroup">
											<c:set var="oid"
												value="${studyEvent.OID} ${form.OID} ${itemGroup.OID}" />
											<tr class="itemGroup-tr minimize">	
												<th><span class="sign"></span></th>
												<td>${itemGroup.name} <c:if
														test="${itemGroup.repeating}">
														<i class="fa fa-key" aria-hidden="true" title="ItemGroup is contained multiple times for at least one subject in this Form! See 'ItemGroupRepeatKey'."></i>
													</c:if>
													<c:if test="${itemGroup.isContainsInvalidValues()}">
														<i class="fa fa-exclamation-triangle" aria-hidden="true" title="ItemGroup contains invalid values."></i>
													</c:if>
												</td>
												<td>${itemGroup.dataCounts}</td>
												<td>${itemGroup.subjectCounts}</td>
											</tr>
											<tr class='items-table-tr'>
												<td colspan="5">
													<table id="formData" class="sub-table" width="100%">
														<thead>
															<tr>
																<th>Item Name</th>
																<th>Data Count</th>
																<th>Subject Count</th>
																<th>Data Type</th>
																<th>Graph</th>
																<th colspan="2">Statistics</th>
															</tr>
														</thead>
														<tbody class="table-bordered">

															<c:forEach items="${itemGroup.metaItemList}" var="item">
																<c:set var="oid"
																	value="${studyEvent.OID} ${form.OID } ${itemGroup.OID } ${item.OID }" />
																<tr>
																	<td>${item.name} <c:if test="${item.isContainsInvalidValues()}"><i class="fa fa-exclamation-triangle" aria-hidden="true" title="ItemGroup contains invalid values."></i></c:if> </td>
																	<td>${item.dataCounts}</td>
																	<td>${item.subjectCounts}</td>
																	<td>${item.dataType}<c:if test="${not empty item.codeList}">(CL)</c:if></td>
																	<c:choose>
																		<%-- Check for null-values --%>
																		<c:when test="${empty itemStatistics[oid]}">
																			<td>N/A</td>
																			<td>No values found!</td>
																		</c:when>
																		<%-- String, Text and Unsupported have no charts. (Exclude CodeLists)--%>
																		<c:when
																			test="${empty item.codeList && (item.dataType eq 'String' || item.dataType eq 'Text' || item.dataType eq 'Unsupported')}">
																			<td>Not Provided</td>
																			<td>
																			<%-- ${StringStatistics.getStatisticString(item.value,itemStatistics[oid],true)}  --%>
																				${itemStatistics[oid].toString(true)}
																			</td>
																		</c:when>
																		<%-- Everthing looks good --%>
																		<c:otherwise>
																		
																			<td class="charts-td">
																		<c:if test="${not empty itemStatistics[oid].getStatisticChartBase64()}">
																				<!-- Symbol icon --> <img id="symbol"
																				src="${pageContext.request.contextPath}/resources/pics/charts.png" />
																		 		<img
																				src='data:image/png;base64,${itemStatistics[oid].getStatisticChartBase64()}' />
																		 </c:if>
																		 	</td>

																			<td class="table-text">
																			<%-- 	${StringStatistics.getStatisticString(item.value,itemStatistics[oid],true)}   --%>
																				${itemStatistics[oid].toString(true)}
																			</td>
																		</c:otherwise>
																	</c:choose>
																</tr>
															</c:forEach>

														</tbody>
													</table>
												</td>
											</tr> 
										</c:forEach>
									</tbody>
								</table>


							</fieldset>
						  </div>
					   </c:forEach>
					</div> <!-- Tables Content Ends here -->


		    </c:forEach>
	</div> <!-- Container Fluid ends here -->
</div>
</div>
</body>
</html>
