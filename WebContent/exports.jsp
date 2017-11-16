<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130215
  @copyright EMBL-EBI, BioModels.net
  
  MIRIAM Registry: provides exports of the database and some basic statistics about its content.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<h2 class="icon icon-functional" data-icon="=">Download exports</h2>

<!-- main content (3/4 of total width) -->
<div id="home_main_content" class="grid_20 alpha">
	<%-- static content --%>
	<c:catch var="exception">
		<c:import url="${initParam.www}exports.html" charEncoding="UTF-8" />
	</c:catch>
	<c:if test="${not empty exception}">
		Sorry: unable to load content...
	</c:if>
</div>

<!-- statistics panel -->
<div id="side_panel" class="grid_4">
	<div class="panel_heading">Registry statistics</div>
	<div class="sub_panel">
		<div class="panel_sub_heading">Published</div>
		<div class="panel_item">
        		<div class="panel_item_desc">Data collections:</div>
            	<div class="panel_item_value">${nbDataTypes} (<abbr title="Including deprecated data collections">${nbAllDataTypes}</abbr>)</div>
		</div>
		<div class="panel_item">
			<div class="panel_item_desc">Resources:</div>
			<div class="panel_item_value">${nbResources} (<abbr title="Including deprecated resources">${nbAllResources}</abbr>)</div>
		</div>
		<div class="panel_item">
			<div class="panel_item_desc">Last update:</div>
			<div class="panel_item_value">${dateLastUpdate}</div>
		</div>
	</div>

	<div class="sub_panel">
		<div class="panel_sub_heading">Under&nbsp;curation</div>
		<div class="panel_item">
		 	<div class="panel_item_desc">Data collections:</div>
		 	<div class="panel_item_value">${nbDataTypesCura}</div>
		</div>
		<div class="panel_item">
			<div class="panel_item_desc">Resources:</div>
			<div class="panel_item_value">${nbResourcesCura}</div>
		</div>
		<div class="panel_item">
			<div class="panel_item_desc">Last update:</div>
			<div class="panel_item_value">${dateLastUpdateCura}</div>
        </div>
	</div>
</div>

<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
