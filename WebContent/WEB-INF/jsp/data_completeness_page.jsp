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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<link rel="stylesheet" type="text/css" href="resources/css/data_completeness.css">

<body>
<div class='radio-buttons'>
	<input type="radio" name="mandatoryOnly" value="true" checked="checked"> Mandatory Values Only  
	<input type="radio" name="mandatoryOnly" value="false" checked="checked"> All Values
</div>			
<div class='mandatory-values hidden'>
	<div class='additional-info'><span>${completedSubjects} out of ${totalSubjects} subjects are complete!</span></div>
	<table class="table table-hover" id='completeness-table'>
		<thead>
		<tr class="table-inverse row">
			<th class='col-2'>Study Event</th>
			<th class='col-1'><span class="sign"></span></th>
			<th class='col-7'>Completeness</th>
			<th class='col-2'>Stat</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${studyEventListCompletenessMandatory}" var="studyEvent">
		<c:if test="${ studyEvent.isContainsMandatoryValues()== true}">
			<tr>
				<td class='row'>
					<div class="col-2">
						${studyEvent.name}	
					</div>
					<div class="col-10 second-column">
					 <div class="row">
						<div class="col-1"><span class="sign"></span></div>
								
						<div  class="col-9">
							<div class="progress">									
							<fmt:formatNumber value = "${studyEvent.mandatoryCompleteness.getPercentage()}" 
										var="seCompleteness" maxFractionDigits="0"/>					
							  <div class="progress-bar" role="progressbar" 
							  aria-valuemin="0" aria-valuemax="100" style="width:${seCompleteness}%">
							    ${seCompleteness}% Complete				    
							  </div>
							  </div>
							
						</div>
						<div class="col-2">
							${studyEvent.mandatoryCompleteness.count} / ${studyEvent.mandatoryCompleteness.total}
						</div>
					</div>
					<br>
					<div class="row sub-table"> <!-- Form sub-table starts here -->
						<table width="100%" class='hidden col-12' id='completeness-table-forms'>
							<thead>
								<tr class="row">
									<th class="col-2" >Form</th>
									<th class="col-1"><span class="sign"></span></th>
									<th class="col-7">Completeness</th>
									<th class="col-2">Stat</th>
								</tr>					
							</thead>
							<tbody>
							<c:forEach items="${studyEvent.metaFormList}" var="form">
							<c:if test="${ form.isContainsMandatoryValues()}">
								<tr>
									<td class='row'>
										<div class="col-2">
											${form.name}	
										</div>
										<div class="col-10 second-column">
					 					  <div class="row">
											<div class="col-1"><span class="sign"></span></div>
													
											<div  class="col-9">
												<div class="progress">									
													<fmt:formatNumber value = "${form.mandatoryCompleteness.getPercentage() }" 
															var="fCompleteness" maxFractionDigits="0"/>
													  <div class="progress-bar" role="progressbar" 
													  aria-valuemin="0" aria-valuemax="100" style="width:${fCompleteness}%">
													    ${fCompleteness}% Complete				    
													  </div>
												  </div>
												
											</div>
											<div class="col-2">
												${form.mandatoryCompleteness.count} / ${form.mandatoryCompleteness.total}
											</div>
										   </div>
										   <br>
											<!-- ItemGroup sub-table starts here  -->
											<div class="row sub-table">
												<table width="100%" class='hidden col-12' id='completeness-table-itemgroup'>
													<thead>
														<tr class="row">
															<th class="col-2" >ItemGroup</th>
															<th class="col-1"><span class="sign"></span></th>
															<th class="col-7">Completeness</th>
															<th class="col-2">Stat</th>
														</tr>					
													</thead>
													<tbody>
													<c:forEach items="${form.metaItemGroupList}" var="itemGroup">
													<c:if test="${ itemGroup.isContainsMandatoryValues()}">
														<tr>
															<td class='row'>
																<div class="col-2">
																	${itemGroup.name}
																</div>
																<div class="col-10 second-column">
					 					  						  <div class="row">
																	<div class="col-1"><span class="sign"></span></div>
																			
																	<div  class="col-9">
																		<div class="progress">									
																			<fmt:formatNumber value = "${itemGroup.mandatoryCompleteness.getPercentage() }" 
																						var="igCompleteness" maxFractionDigits="0"/>											
																			  <div class="progress-bar" role="progressbar" 
																			  aria-valuemin="0" aria-valuemax="100" style="width:${igCompleteness}%">
																			    ${igCompleteness}% Complete				    
																			  </div>
																		  </div>
																		
																	</div>
																	<div class="col-2">
																		${itemGroup.mandatoryCompleteness.count} / ${itemGroup.mandatoryCompleteness.total}
																	</div>
																   </div>		
																   <br>						
																	<!-- Item sub-table starts here -->
																	<div class="row sub-table">
																		<table width="100%" class='hidden col-12' id='completeness-table-item'>
																			<thead>
																				<tr class="row">
																					<th class="col-2" >Item</th>
																					<th class="col-8">Completeness</th>
																					<th class="col-2">Stat</th>
																				</tr>					
																			</thead>
																			<tbody>
																			<c:forEach items="${itemGroup.metaItemList}" var="item">
																			<c:if test="${ item.mandatoryCompleteness != null && item.isMandatory() == true}">
																				<tr>
																					<td class='row'>
																						<div class="col-2">
																							${item.name}
																						</div>
																					
																						<div  class="col-8">
																							<div class="progress">									
																								<fmt:formatNumber value = "${item.mandatoryCompleteness.getPercentage()  }" 
																										var="itemCompleteness" maxFractionDigits="0"/>
																								  <div class="progress-bar" role="progressbar" 
																								  aria-valuemin="0" aria-valuemax="100" style="width:${itemCompleteness}%">
																								    ${itemCompleteness}% Complete				    
																								  </div>
																							  </div>
																							
																						</div>
																						<div class="col-2">
																							${item.mandatoryCompleteness.count} / ${item.mandatoryCompleteness.total}
																						</div>
																						<!-- Item sub-table ends here -->
																						
																					</td>
																				</tr>
																				</c:if>
																				</c:forEach>
																				</tbody>
																			</table>
																		</div> <!-- Item sub-table ends here -->
																  </div>
																</td>
															</tr>
														</c:if>
														</c:forEach>
													</tbody>			 
												</table>
											</div> <!-- ItemGroup sub-table ends here -->
									  </div>
									</td>
									
								</tr> 
							</c:if>
							</c:forEach>
							</tbody>			 
						</table>		
					</div>	 <!-- Form sub-table ends here -->
					
					</div>
				 </td>
			</tr>
		</c:if>
		</c:forEach> 
	</tbody>
	</table>
</div>
<div class='all-values'>
	<table class="table table-hover" id='completeness-table'>
		<thead>
		<tr class="table-inverse row">
			<th class='col-2'>Study Event</th>
			<th class='col-1'><span class="sign"></span></th>
			<th class='col-7'>Completeness</th>
			<th class='col-2'>Stat</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${studyEventListCompletenessAll}" var="studyEvent">
		
			<tr>
				<td class='row'>
					<div class="col-2">
						${studyEvent.name}	
					</div>
					<div class="col-10 second-column">
					 <div class="row">
						<div class="col-1"><span class="sign"></span></div>
								
						<div  class="col-9">
							<div class="progress">									
							<fmt:formatNumber value = "${studyEvent.completeness.getPercentage()}" 
										var="seCompleteness" maxFractionDigits="0"/>					
							  <div class="progress-bar" role="progressbar" 
							  aria-valuemin="0" aria-valuemax="100" style="width:${seCompleteness}%">
							    ${seCompleteness}% Complete				    
							  </div>
							  </div>
							
						</div>
						<div class="col-2">
							${studyEvent.completeness.count} / ${studyEvent.completeness.total}
						</div>
					</div>
					<br>
					<div class="row sub-table"> <!-- Form sub-table starts here -->
						<table width="100%" class='hidden col-12' id='completeness-table-forms'>
							<thead>
								<tr class="row">
									<th class="col-2" >Form</th>
									<th class="col-1"><span class="sign"></span></th>
									<th class="col-7">Completeness</th>
									<th class="col-2">Stat</th>
								</tr>					
							</thead>
							<tbody>
							<c:forEach items="${studyEvent.metaFormList}" var="form">
							
								<tr>
									<td class='row'>
										<div class="col-2">
											${form.name}	
										</div>
										<div class="col-10 second-column">
					 					  <div class="row">
											<div class="col-1"><span class="sign"></span></div>
													
											<div  class="col-9">
												<div class="progress">									
													<fmt:formatNumber value = "${form.completeness.getPercentage() }" 
															var="fCompleteness" maxFractionDigits="0"/>
													  <div class="progress-bar" role="progressbar" 
													  aria-valuemin="0" aria-valuemax="100" style="width:${fCompleteness}%">
													    ${fCompleteness}% Complete				    
													  </div>
												  </div>
												
											</div>
											<div class="col-2">
												${form.completeness.count} / ${form.completeness.total}
											</div>
										   </div>
										   <br>
											<!-- ItemGroup sub-table starts here  -->
											<div class="row sub-table">
												<table width="100%" class='hidden col-12' id='completeness-table-itemgroup'>
													<thead>
														<tr class="row">
															<th class="col-2" >ItemGroup</th>
															<th class="col-1"><span class="sign"></span></th>
															<th class="col-7">Completeness</th>
															<th class="col-2">Stat</th>
														</tr>					
													</thead>
													<tbody>
													<c:forEach items="${form.metaItemGroupList}" var="itemGroup">
													
														<tr>
															<td class='row'>
																<div class="col-2">
																	${itemGroup.name}
																</div>
																<div class="col-10 second-column">
					 					  						  <div class="row">
																	<div class="col-1"><span class="sign"></span></div>
																			
																	<div  class="col-9">
																		<div class="progress">									
																			<fmt:formatNumber value = "${itemGroup.completeness.getPercentage() }" 
																						var="igCompleteness" maxFractionDigits="0"/>											
																			  <div class="progress-bar" role="progressbar" 
																			  aria-valuemin="0" aria-valuemax="100" style="width:${igCompleteness}%">
																			    ${igCompleteness}% Complete				    
																			  </div>
																		  </div>
																		
																	</div>
																	<div class="col-2">
																		${itemGroup.completeness.count} / ${itemGroup.completeness.total}
																	</div>
																   </div>		
																   <br>						
																	<!-- Item sub-table starts here -->
																	<div class="row sub-table">
																		<table width="100%" class='hidden col-12' id='completeness-table-item'>
																			<thead>
																				<tr class="row">
																					<th class="col-2" >Item</th>
																					<th class="col-8">Completeness</th>
																					<th class="col-2">Stat</th>
																				</tr>					
																			</thead>
																			<tbody>
																			<c:forEach items="${itemGroup.metaItemList}" var="item">
																			
																				<tr>
																					<td class='row'>
																						<div class="col-2">
																							${item.name}
																						</div>
																					
																						<div  class="col-8">
																							<div class="progress">									
																								<fmt:formatNumber value = "${item.completeness.getPercentage()  }" 
																										var="itemCompleteness" maxFractionDigits="0"/>
																								  <div class="progress-bar" role="progressbar" 
																								  aria-valuemin="0" aria-valuemax="100" style="width:${itemCompleteness}%">
																								    ${itemCompleteness}% Complete				    
																								  </div>
																							  </div>
																							
																						</div>
																						<div class="col-2">
																							${item.completeness.count} / ${item.completeness.total}
																						</div>
																						<!-- Item sub-table ends here -->
																						
																					</td>
																				</tr>
																				
																				</c:forEach>
																				</tbody>
																			</table>
																		</div> <!-- Item sub-table ends here -->
																  </div>
																</td>
															</tr>
														
														</c:forEach>
													</tbody>			 
												</table>
											</div> <!-- ItemGroup sub-table ends here -->
									  </div>
									</td>
									
								</tr> 
							
							</c:forEach>
							</tbody>			 
						</table>		
					</div>	 <!-- Form sub-table ends here -->
					
					</div>
				 </td>
			</tr>
		
		</c:forEach> 
	</tbody>
	</table>
</div>



</body>
</html>
