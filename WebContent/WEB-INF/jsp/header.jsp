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

<div class="page-header">
	<h3>ODM Data Analysis</h3>

	<nav class="navbar navbar-light bg-faded rounded mb-3"
		id='header-navbar'>
		<div class="navbar-toggleable-md" id="navbarCollapse">
			<ul class="nav navbar-nav text-md-center justify-content-md-between"
				role="tablist">
				<li class="nav-item"><a class="nav-link active" id='home-a'
					href="#home" role="tab" data-toggle="tab">Upload </a></li>
				<li class="nav-item results-nav-tab hidden"><a class="nav-link"
					id='results-tab' href='#results-tab-content' role='tab'
					data-toggle='tab'>Results</a></li>
				<li class="nav-item"><a class="nav-link" id='about-a'
					href="#about-tab-content" role="tab" data-toggle="tab">About </a></li>
			</ul>
		</div>
	</nav>


</div>
