# ODM Data Analysis - a tool for the automatic validation, monitoring and
# generation of generic descriptive statistics of clinical data.
# 
# Copyright (c) 2017 Institut für Medizinische Informatik, Münster
#
# ODM Data Analysis is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by the
# Free Software Foundation, version 3.
#
# ODM Data Analysis is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
# more details.
#
# You should have received a copy of the GNU General Public License in the file
# COPYING along with ODM Data Analysis. If not, see <http://www.gnu.org/licenses/>.

FROM tomcat:8.0-jre8
RUN rm -rf /usr/local/tomcat/webapps/*
COPY ROOT.war /usr/local/tomcat/webapps
RUN sed -ie 's/<session-timeout>30<\/session-timeout>/<session-timeout>120<\/session-timeout>/g' conf/web.xml
EXPOSE 8080
