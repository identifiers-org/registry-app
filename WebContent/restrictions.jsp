<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130805
  @copyright BioModels.net, EMBL-EBI
  
  MIRIAM Registry: displays some miscellaneous information about a given data collection.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<h2 class="icon icon-generic" data-icon="R">Data collection: <em>${data.name}</em></h2>

<c:if test="${data.obsolete == true}">
   <br />
   <div class="message_warning">
       	<b>WARNING:</b> this data collection has been deprecated!
          	<br />
          	${data.obsoleteComment}
      		<c:if test="${data.replacedBy != null}">
      			<br />
      			We recommend the usage of the following data collection instead: <a href="<c:url value='/collections/${data.replacedBy}' />" title="Go to ${replacementName}">${replacementName}</a>.
      		</c:if>
   </div>
	<br />
</c:if>

<!-- tabs -->
<div class="menutabs">
	<a class="tab" href="<c:url value='/collections/${data.id}' />" title="Overview of the data collection">Overview</a>
    <%--<a class="tab" href="<c:url value='/tags/${data.id}' />" title="Categorisation of the data collection (tags)">Categories</a>--%>
    <a class="tab" href="<c:url value='/misc/${data.id}' />" title="Miscellaneous information about the data collection">Miscellaneous</a>
    <a class="active_restriction" href="<c:url value='/restrictions/${data.id}' />" title="Restriction(s) information about the data collection">Restriction(s)</a>
    <%-- download links --%>
    <span data-icon="=" class="icon icon-functional download">
    	<a href="<c:url value="/collections/${data.id}.rdf" />" title="Download the record of this data collection in RDF/XML">RDF/XML</a>
    	<a href="<c:url value="/collections/${data.id}.ttl" />" title="Download the record of this data collection in Turtle">Turtle</a>
    </span>
</div>

<c:choose>
	<c:when test="${empty data.restrictions}">
		<p style="font-weight: bold;">There is no restriction associated with this data collection!</p><br />
	</c:when>
	<c:when test="${!empty data.restrictions && f:length(data.restrictions) == 1}">
		<p><b>One restriction</b> has been associated to this data collection. Please read below for more information.</p>
	</c:when>
	<c:otherwise>
		<p><b>Several restrictions</b> have been associated with this data collection. Please read below for more information.</p>
	</c:otherwise>
</c:choose>

<c:forEach var="restriction" items="${data.restrictions}">
	<h3><c:out value="${restriction.type.category}" /></h3>
	<p style="font-style:italic; margin-top:0;"><c:out value="${restriction.type.desc}" /></p>
	<br />
	<p style="margin-top:0;">The reason why this restriction is associated with the data collection is: <br />
	<span style="font-weight:bold; font-size:110%; padding-left:20px;"><c:out value="${restriction.info}" /></span></p>
	<c:if test="${! empty restriction.link}">
		<p style="padding-top: 5px; margin-top:0;">For more information, please refer to: <a class="external" title="External link to further information." href="<c:out value="${restriction.link}" />"><c:out value="${restriction.linkText}" /></a></p>
	</c:if>
</c:forEach>

<br />

<section class="grid_24 clearfix">
	<div class="grid_12 alpha bottom_links_left">
		<a href="<c:url value='/collections/' />" title="Return to the list of data collections" class="icon icon-functional" data-icon="<">Go back to the list of data collections</a>
	</div>
	<div class="grid_12 omega bottom_links_right">
	    <a href="<c:url value='/mdb?section=support&amp;info=Restrictions_modification_suggestion-${data.id}' />" title="Suggest modifications to: ${data.name}" class="icon icon-functional" data-icon="e">Suggest modifications to this data collection</a>
	</div>
</section>

<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
