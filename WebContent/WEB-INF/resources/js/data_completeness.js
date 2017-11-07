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

$(function(){
	console.log('loaded');
	//assign class to progress-bar based on value
	$('#completeness-table .progress-bar').each(function(){
		//console.log('found'); 
		$inner_text = $(this).text();
		width = $inner_text.substring(0, $inner_text.indexOf('%'));
		
		var progress_class;
		
		if(width ==100){
			progress_class = "bg-success";
		}else if (width > 75) {
			progress_class = "bg-info";
		}else if (width	> 50){
			progress_class = "bg-warning";
		}else{
			progress_class = "bg-danger";
		}
		
		$(this).addClass(progress_class);
		
	});
	
	//display hidden row for given type (event/form/itemgroup)
	$(document).on('click','#completeness-table  tbody > tr > td > div.second-column > .row:not(.sub-table) .sign:not(.maximized)',function(){
		
		//change sign content
		$(this).addClass('maximized');
		
		$div_row = $(this).closest('div.second-column');
		$next_div = $div_row.children('.sub-table');
		$next_div.children('table').removeClass('hidden');
		
		
	});
	
	//hide displayed row for given type (event/form/itemgroup)
	$(document).on('click','#completeness-table  tbody > tr > td > div.second-column > .row:not(.sub-table) .sign.maximized',function(){
		
		//change sign content
		$(this).removeClass('maximized');
		
		$div_row = $(this).closest('div.second-column');
		$next_div = $div_row.children('.sub-table');
		$next_div.children('table').addClass('hidden');
		
		
	});
	

	//minimize all events/forms/itemgroups rows when clicked for a given type heading
	$(document).on('click',
			'#completeness-table > thead .sign.maximized , #completeness-table-forms > thead .sign.maximized , #completeness-table-itemgroup > thead .sign.maximized',
			function(){
		
		$(this).removeClass('maximized');
		
		$table = $(this).closest('table');
		
		$table.find(' > tbody > tr > td > div.second-column > .row:not(.sub-table) .sign.maximized').each(function(){
			$(this).removeClass('maximized');
			$div_row = $(this).closest('div.second-column');
			$next_div = $div_row.children('.sub-table');
			$next_div.children('table').addClass('hidden');
		});
		
	});
	
	
	//maximize all events/forms/itemgroups rows when clicked for a given type heading
	$(document).on('click',
			'#completeness-table > thead .sign:not(.maximized), #completeness-table-forms > thead .sign:not(.maximized), #completeness-table-itemgroup > thead .sign:not(.maximized)',
			 function(){
		
		$(this).addClass('maximized');
		
		$table = $(this).closest('table');
		
		$table.find(' > tbody > tr > td > div.second-column > .row:not(.sub-table) .sign:not(.maximized)').each(function(){
			$(this).addClass('maximized');
			$div_row = $(this).closest('div.second-column');
			$next_div = $div_row.children('.sub-table');
			$next_div.children('table').removeClass('hidden');
		});
		
	});
	
	//switch completeness view between mandatory and all values
	$(document).on('click','input[name=mandatoryOnly]',function(){
		$value = $(this).attr('value');	
		if($value == 'true'){//mandatory
			$('.all-values').addClass('hidden');
			$('.mandatory-values').removeClass('hidden');
		}else{
			$('.mandatory-values').addClass('hidden');
			$('.all-values').removeClass('hidden');
			
		}
	});
	
	
});
