/**
 * ODM Data Analysis - a tool for the automatic validation, monitoring and
 * generation of generic descriptive statistics of clinical data.
 * 
 * Copyright (c) 2017 Institut für Medizinische Informatik, Münster
 *
 * ODM Data Analysis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, version 3.
 *
 * ODM Data Analysis is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 * more details.
 *
 * You should have received a copy of the GNU General Public License in the file
 * COPYING along with ODM Data Analysis. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @author Saad Sarfraz
 */

// displays file name when selected by user
$(function() {
		
	
	var progress_bar_event;
	var async_requests;
	
	// We can attach the `fileselect` event to all file inputs on the page
	$(document).on('change', ':file', function() {
		var input = $(this),
		numFiles = input.get(0).files ? input.get(0).files.length : 1,
		label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
		input.trigger('fileselect', [numFiles, label]);
	});

	// We can watch for our custom `fileselect` event like this
	$(document).ready( function() {
		$(':file').on('fileselect', function(event, numFiles, label) {
		var input = $(this).parents('.input-group').find(':text'),
		log = numFiles > 1 ? numFiles + ' files selected' : label;

		if( input.length ) {
			input.val(log);
		} else {
			if( log ) alert(log);
		}

	 	});
		
	});
	
	//returns the selected file name with extension by trimming fullPath to 
	//the selected file e.g. ODM.xml is returned for /home/Downloads/odm files/ODM.xml
	function getSelectedFileName(){
		var fullPath = $('#file').val();  
		var startIndex = (fullPath.indexOf('\\') >= 0 ? fullPath.lastIndexOf('\\') : fullPath.lastIndexOf('/'));
		var filename = fullPath.substring(startIndex);
		if (filename.indexOf('\\') === 0 || filename.indexOf('/') === 0) {
			filename = filename.substring(1);
		}
		return  filename;

	}

	function getSelectedFileNameWithOutExt(){
		var name = getSelectedFileName();
		return name.substring(0, name.indexOf("."));
	}
	// Prepares a form to be submitted via AJAX by adding all of the
	// necessary event listeners
	$('#target').ajaxForm({
		beforeSerialize: function($form, options) {
				//check if file is selected
				var filePath = $("#file").val();
				filePath = filePath.trim();
				if(!filePath){
				//console.log('file not selected');
				return false;
				}
				
				//hide sub navigation tab(upload, analysis, download)
				$('#sub-nav-container').addClass('hidden');

				$('.results-nav-tab').addClass('hidden');
				
				// disable submit button
				$('#submit, #download-xml-btn input').prop("disabled",true);
				//disable check-boxes and browse button
			$('.form-check-input, #file').attr("onclick","return false");

				
				//$('#file').prop("readonly","readonly");
				
				
				//$('#submit, #download-xml-btn input, .form-check-input').attr("disabled",true);
			
				// display progress bar
				$('.progress-bar-div').show();
				// resettinng progress bar
				$('.upload-file').attr('aria-valuenow',0).css('width','0%')
				$('.u-percent-completed').html('Upload File '+'0' + '% Completed' );
				$('.parse-file').attr('aria-valuenow',0).css('width','0%');
				$('.p-percent-completed').html('Parsing File '+'0' + '% Completed' );
				$('.calc-statistics').attr('aria-valuenow',0).css('width','0%');
				$('.s-percent-completed').html('Calculating Statistics '+'0' + '% Completed' );
				$('.calc-completeness').attr('aria-valuenow',0).css('width','0%');
				$('.c-percent-completed').html('Calculating Completeness '+'0' + '% Completed' );
				$('.generate-pdf').attr('aria-valuenow',0).css('width','0%');
				$('.pdf-percent-completed').html('Generating PDF '+'0' + '% Completed' );
				
				//hide unimportant
				if(!$('#calculateStat').prop('checked') && $('#calculateCompleteness').prop('checked')) {
					$('.calc-statistics').hide();
					$('.s-percent-completed').hide();
				} else {
					$('.calc-statistics').show();
					$('.s-percent-completed').show();
				}
				if(!$('#calculateCompleteness').prop('checked')) {
					$('.calc-completeness').hide();
					$('.c-percent-completed').hide();
				} else {
					$('.calc-completeness').show();
					$('.c-percent-completed').show();
				}					
				if(!$('#exportFile').prop('checked')) {
					$('.generate-pdf').hide();
					$('.pdf-percent-completed').hide();
				} else {
					$('.generate-pdf').show();
					$('.pdf-percent-completed').show();
				} 
				
				
			},
			uploadProgress: function(event, position, total, percentComplete) {

				var percentVal = percentComplete + '%';
				// update style and attribute value
				$('.upload-file').attr('aria-valuenow',percentComplete).css('width',percentVal);
				// update inner text
				$('.u-percent-completed').html('Upload File ' + percentComplete + '% Completed' );
				//Start sending requests to server to get Parsing progress
				if(percentComplete == 100){

					//console.log('upload completed, starting sending parsing status request');
					
					var completedPar = 0;
					var completedSta = 0;
					var completedCom = 0;
					var completedPdf = 0;					
					
					var fileName = getSelectedFileNameWithOutExt();
					
					//console.log('file name is '+ fileName);
					
					//sent request after specified interval
					async_requests = setInterval(function() {
						$.post({
							async: true,
							data: {
								"fileName":fileName
						},
							url: contextPath + "/parsingStatus",
						success: function(data){
						
							//console.log('data reaceive' + data);    
							
							completedPar = parseInt(data.currentState[0]);
							completedSta = parseInt(data.currentState[1]);
							completedCom = parseInt(data.currentState[2]);
							completedPdf = parseInt(data.currentState[3]);
														
							//parsing
							$('.parse-file').attr('aria-valuenow',completedPar).css('width',completedPar+'%');
							$('.p-percent-completed').html('Parsing File ' + completedPar + '% Completed' );
							//statistics
							$('.calc-statistics').attr('aria-valuenow',completedSta).css('width',completedSta+'%');
							$('.s-percent-completed').html('Calculating Statistics ' + completedSta + '% Completed' );
							//completeness
							$('.calc-completeness').attr('aria-valuenow',completedCom).css('width',completedCom+'%');
							$('.c-percent-completed').html('Calculating Completeness ' + completedCom + '% Completed' );
							//pdf
							$('.generate-pdf').attr('aria-valuenow',completedPdf).css('width',completedPdf+'%');
							$('.pdf-percent-completed').html('Generating PDF ' + completedPdf + '% Completed' );
														
							if(completedPdf>=100 ){ //when all reports are being calculated
								$('#loading-page-anim-overlay').removeClass("hidden");
							}else if (!$('#calculateStat').prop('checked') && completedCom>= 100 ){ //when only completeness calculated
								$('#loading-page-anim-overlay').removeClass("hidden");
							}
							
						},
						error: function(jqXHR, textStatus, errorThrown) {
							
							//clear interval
							if(async_requests)
								clearInterval(async_requests);     
							
							console.log("error thrown" + jqXHR.status );
							if(jqXHR.status == 404 ){ //not found (task completed and key removed)
								$('#loading-page-anim-overlay').removeClass("hidden");
							}
							
						}
						});
						
						
						
					},3500);
				}
			},
			complete: function(xhr) {
				
				//hide loading page animation
				$('#loading-page-anim-overlay').addClass("hidden");
				
				//enable check-boxes and Browse button to be click-able again
				$('.form-check-input, #file').attr("onclick","");
				//stop progress bar event 
				if(async_requests)
					clearInterval(async_requests);
				
				console.log('upload and parsing completed');
				
				//if file uploaded and parsed successfully
				if(xhr.status ==200){
	
						//	display upload
						$('.results-nav-tab').removeClass('hidden');
						// reset upload file button
						$('#submit, #download-xml-btn input, .form-check-input').attr("disabled",false);
						//hide progress bar
						$('.progress-bar-div').hide();
						//display received response to the page
						$('.response').html(xhr.responseText);
						// deactivate default upload tab and its content
						$('#header-navbar  .active').removeClass('active');
						$('#home').removeClass('active');
						// activating results tab
						$('.results-nav-tab a').addClass('active');
						$('#results-tab-content').addClass('active');
						// automatically navigate to the section
						$('html, body').animate({scrollTop: $('.response').offset().top}, 'slow');
				}else {
					alert('Oops!!! Something went wrong. Please try again. \n(Make sure you file is valid xml file and you are connected to server) ');
					//refresh page
					location.reload();
				}
			}
		});

			
		//switch to about page when user click on description option
		$('#details-button').click(function(){
			
			$('#home-a').removeClass('active');
			$('#home').removeClass('active');
			
			
			$('#about-a').addClass('active');
			$('#about-tab-content').addClass('active');
			
		});
		
		
		//submitting get request to get download page and
		//inserting the content in the main page
		$(document).on('submit','#download-form', function(e){
			
			//console.log("file name without extension is "+ getSelectedFileNameWithOutExt())
			
			e.preventDefault(); //Prevent Default action.
			
			//console.log('form submitted');
			$.ajax({
				async: true,
				type: $(this).attr('method'),
				url: $(this).attr('action'),
				data:{
					"fileName":getSelectedFileNameWithOutExt()  
				},
				success: function(data){
					$('#downloads-tab-content').html(data);
				},
				error: function(jqXHR, textStatus, errorThrown) 
				{
					//console.log('failed');     
				}
				});
			
			$('.download-nav-tab').removeClass('hidden');
			
			// deactivate default nav-item and its content
			$('#sub-nav-container  .active').removeClass('active');
			$('#analysis-tab-content').removeClass('active');
			//activate download tab
			$('#downloads-tab-content').addClass('active');
			$('#downloads-tab').addClass('active');
		});
	
		$(document).on('submit','#download-PDF', function(e){
			
			//e.preventDefault();
			var fileName = getSelectedFileNameWithOutExt() ;
			
			$('#download-PDF > #fileName').val(fileName);
		});
		
		$(document).on('click','#goback-btn-dwn', function(e){	
			$('#downloads-tab').removeClass('active');
			$('#analysis-tab').addClass('active');
			
			$('#downloads-tab-content').removeClass('active');
			$('#analysis-tab-content').addClass('active');
			
		});
		
		//select statistics, if pdf is gernated
		//AND display warning message when none of checkbox is selected 
		$('.form-check-input').change(function(){
			
			if ($('#exportFile').prop('checked') == true) {
				$('#calculateStat').prop('checked',true);
			}

			var n = $( ".form-check-input:checked" ).length;
			
			if(n==0){ //no checkbox selected
				$('#valueRequired').removeClass('hidden');
			}else if(n > 0){ //atleast one checkbox is selected
				$('#valueRequired').addClass('hidden');
			}
			
			
		});
		
		$(document).on('submit','#download-CSV',function(e){
			//e.preventDefault();
			var fileName = getSelectedFileNameWithOutExt() ;
			
			$('#download-CSV > #fileName').val(fileName);

		});
});
