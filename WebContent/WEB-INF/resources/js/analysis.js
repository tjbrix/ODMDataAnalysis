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
 * This file deals with all the processing related to data analysis page only
 * @author Saad Sarfraz
 */

//expand items for an itemgroup
$(document).on('click','.itemGroup-tr', function(){
	
	$(this).toggleClass('minimize').nextUntil('tr.itemGroup-tr').slideToggle(10);
});

//collapse all itemgroups
$(document).on('click', '.collapsed-all', function(){	
	
	var $table = $(this).closest('table');
	
	//change symbol for all closed itemgroups
	$table.find('.itemGroup-tr.minimize').removeClass('minimize');
	$table.find('.items-table-tr').show();
	
	//change heading sign
	$(this).addClass('expanded-all').removeClass('collapsed-all');
});

//expand all itemgroups
$(document).on('click', '.expanded-all', function(){	
	
	var $table = $(this).closest('table');
	
	//change symbol for all opened itemgroups
	$table.find('.itemGroup-tr:not(.minimize)').addClass('minimize');
	$table.find('.items-table-tr').hide();
	
	
	//change heading sign
	$(this).addClass('collapsed-all').removeClass('expanded-all');
});



//clicked on symbol
//hide symbol and display image
$(document).on('click','#symbol',function(){
	$(this).toggle("slow");
	$(this).next('img').toggle("slow");
} );

//clicked on graph image
//hide image and display symbol
$(document).on('click','.charts-td > img:not(#symbol)',function(){
	$(this).toggle("slow");
	$(this).prev('img').toggle();
} );


$(document).on('click','#nav-btn', function(){
	
	$('#mySidenav').toggleClass('sidenav-open');
	$('.analysis-body').toggleClass('page-resize');
});

$(document).on('click','.sidenav-closebtn', function(){
	
	$('#mySidenav').removeClass('sidenav-open');
	$('.analysis-body').removeClass('page-resize');
});

$(document).on('click','.closebtn-table', function(){
	//find the table division and hide it
	$tablediv = $(this).closest('div');
	
	var id = $tablediv.attr('id');
	
	console.log("a[href="+id+"]");
	//disable active link in side-nav
	$('a[href="#'+id+'"]').removeClass('active');
	
	$tablediv.fadeOut('slow');
	
	$panel = $('a[href="#'+id+'"]').closest('.panel');
	$childrenActive = $panel.find('a.nav-link.active');
	
	
	//deactivate header (events) when all forms are deselected
	if($childrenActive.length <= 1){ 	//no other selected form found						
		$panel.find('.panel-title a').removeClass('active');
	}
});

//removes the selected table-form from list items
$(document).on('click','#mySidenav li a.nav-link.active', function(){
	
	// disable form link
	$(this).removeClass('active');	
	//disable form-link content(table)
	var id = $(this).attr('href');
	//animate fade out
	$(id).fadeOut('slow', function(){
		$(id).removeClass('active');
	});

	$panel = $(this).closest('.panel');
	
	$childrenActive = $panel.find('a.nav-link.active');
	
	
	//deactivate header (events) when all forms are deselected
	if($childrenActive.length <= 1){ //no other selected form found					
		$panel.find('.panel-title a').removeClass('active');
	}
	
});

//animate the table when selected
$(document).on('click','#mySidenav li a.nav-link:not(.active)', function(){
	
	$(this).addClass('active');
	
	//disable form-link content(table)
	var id = $(this).attr('href');
	
	//if not already hidden (but only not active)
//	$(id).css('display','none');
	
	//animate fade in
	$(id).fadeIn('slow');

});

//active events tag when form activated
$(document).on('click','li a:not(.active)', function(){
	$panel = $(this).closest('.panel');
	$panel.find('.panel-title a').addClass('active');
});

//view completeness page
$(document).on('click','#view-completeness-btn', function(){
	console.log('clicked');
	//copy hidden content from results page
	$content = $('.completeness-hidden-content').html();
	
	//display sub tab
	$('.data-completeness-nav-tab').removeClass('hidden');
	//paste inner html content
	$('#data-completeness-tab-content').html($content);
	
	//hide data analysis tab and view completeness tab
	$('#analysis-tab').removeClass('active');
	$('#data-completeness-tab').addClass('active');
	
	$('#analysis-tab-content').removeClass('active');
	$('#data-completeness-tab-content').addClass('active');
	
	
});





