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

var $table_rows = $('#invalid-values-table tbody tr');
var uniqueSubjectValues = new Array();
var uniqueSeOIDValues = new Array();
var uniqueSeRKValues = new Array();
var uniqueFOIDValues = new Array();
var uniqueFRKValues = new Array();
var uniqueIgValues = new Array();
var uniqueIgRKValues = new Array();
var uniqueIOIDValues = new Array();
var uniqueValueValues = new Array();
var uniqueReasonValues = new Array();


//inserting option values from table cells for each filter 
$table_rows.each(function(){

	var subjectValue = $(this).find('td:eq(0)').text();
	var seOIDValue = $(this).find('td:eq(1)').text();
	var seRKValue = $(this).find('td:eq(2)').text();
	var fOIDValue = $(this).find('td:eq(3)').text();
	var fRKValue = $(this).find('td:eq(4)').text();
	var igValue = $(this).find('td:eq(5)').text();
	var igRKValue = $(this).find('td:eq(6)').text();
	var iOIDValue = $(this).find('td:eq(7)').text();
	var valueValue = $(this).find('td:eq(8)').text();
	var reasonValue = $(this).find('td:eq(9)').text();
	
	if(uniqueSubjectValues.indexOf(subjectValue) == -1 ){
		uniqueSubjectValues.push(subjectValue);
		$('#subjectKeySelect').append('<option>'+ subjectValue +'</option>');
	}
	
	if(uniqueSeOIDValues.indexOf(seOIDValue) == -1 ){
		uniqueSeOIDValues.push(seOIDValue);
		$('#sEventOIDSelect').append('<option>'+ seOIDValue +'</option>');
	}
	
	if(uniqueSeRKValues.indexOf(seRKValue) == -1 ){
		uniqueSeRKValues.push(seRKValue);
		$('#seRKeySelect').append('<option>'+ seRKValue +'</option>');
	}
	
	if(uniqueFOIDValues.indexOf(fOIDValue) == -1 ){
		uniqueFOIDValues.push(fOIDValue);
		$('#fOIDSelect').append('<option>'+ fOIDValue +'</option>');
	}
	
	if(uniqueFRKValues.indexOf(fRKValue) == -1 ){
		uniqueFRKValues.push(fRKValue);
		$('#fRKeySelect').append('<option>'+ fRKValue +'</option>');
	}
	
	if(uniqueIgValues.indexOf(igValue) == -1 ){
		uniqueIgValues.push(igValue);
		$('#igSelect').append('<option>'+ igValue +'</option>');
	}
	
	if(uniqueIgRKValues.indexOf(igRKValue) == -1 ){
		uniqueIgRKValues.push(igRKValue);
		$('#igRKeySelect').append('<option>'+ igRKValue +'</option>');
	}
	
	if(uniqueIOIDValues.indexOf(iOIDValue) == -1 ){
		uniqueIOIDValues.push(iOIDValue);
		$('#igOIDSelect').append('<option>'+ iOIDValue +'</option>');
	}
	
	if(uniqueValueValues.indexOf(valueValue) == -1 ){
		uniqueValueValues.push(valueValue);
		$('#valueSelect').append('<option>'+ valueValue +'</option>');
	}
	
	if(uniqueReasonValues.indexOf(reasonValue) == -1 ){
		uniqueReasonValues.push(reasonValue);
		$('#reasonSelect').append('<option>'+ reasonValue +'</option>');
	}

	//result = getUniqeValues(subjectValue,resultArray);
	
	

});


/**
 * Main filter function which handles all the option selection task
 * Implementation: It hides the column cell values which are not matched against
 * selected value in filter. For every click, visibility will reset by default and 
 * then rows will be hidden based on current selections
 */
$('select option').click(function(){
	//reset display to visible for each click function
	$('#invalid-values-table tbody tr').show();
	
	var selectedValues = new Array();
	
	var subjectSelectedValue = $('#subjectKeySelect option:gt(0):selected').text(); //0 index specifies no filter selected
	var sEventOIDSelectedValue = $('#sEventOIDSelect option:gt(0):selected').text();
	var seRKeySelectedValue = $('#seRKeySelect option:gt(0):selected').text();
	var fOIDSelectedValue = $('#fOIDSelect option:gt(0):selected').text();
	var fRKeySelectedValue = $('#fRKeySelect option:gt(0):selected').text();
	var igSelectedValue = $('#igSelect option:gt(0):selected').text();
	var igRKeySelectedValue = $('#igRKeySelect option:gt(0):selected').text();
	var igOIDSelectedValue = $('#igOIDSelect option:gt(0):selected').text();
	var valueSelectedValue = $('#valueSelect option:gt(0):selected').text();
	var reasonSelectedValue = $('#reasonSelect option:gt(0):selected').text();
	
	selectedValues.push(subjectSelectedValue);
	selectedValues.push(sEventOIDSelectedValue);
	selectedValues.push(seRKeySelectedValue);
	selectedValues.push(fOIDSelectedValue);
	selectedValues.push(fRKeySelectedValue);
	selectedValues.push(igSelectedValue);
	selectedValues.push(igRKeySelectedValue);
	selectedValues.push(igOIDSelectedValue);
	selectedValues.push(valueSelectedValue);
	selectedValues.push(reasonSelectedValue);
	
	
	
	$('#invalid-values-table tbody tr').each(function(){
		
		for(var i=0 ; i< selectedValues.length ; i++){
			var cellValue = $(this).find('td:eq('+i+')').text();
			if(selectedValues[i].length > 0){
				console.log(i+' ' +"selected value: "+ selectedValues[i]);
				console.log(i+' '+"Cell value: "+ cellValue);
				if(selectedValues[i] != cellValue){
					console.log('hiding');
					$(this).hide();

				}
			}
		}


	});
	
	
});





