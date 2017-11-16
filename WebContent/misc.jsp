<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130805
  @copyright BioModels.net, EMBL-EBI
  
  MIRIAM Registry: displays some miscellaneous information about a given data collection.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <a class="active" href="<c:url value='/misc/${data.id}' />" title="Miscellaneous information about the data collection">Miscellaneous</a>
    <c:if test="${!empty data.restrictions}">
    	<a class="restriction" href="<c:url value='/restrictions/${data.id}' />" title="Restriction(s) information about the data collection">Restriction(s)</a>
    </c:if>
    <%-- download links --%>
    <span data-icon="=" class="icon icon-functional download">
    	<a href="<c:url value="/collections/${data.id}.rdf" />" title="Download the record of this data collection in RDF/XML">RDF/XML</a>
    	<a href="<c:url value="/collections/${data.id}.ttl" />" title="Download the record of this data collection in Turtle">Turtle</a>
    </span>
</div>

<h3>Collection history</h3>

<ul>
	<li><span style="font-style: italic;">Date of creation:</span> &nbsp; ${data.dateCreationStr}</li>
    <li><span style="font-style: italic;">Date of last modification:</span> &nbsp; ${data.dateModificationStr}</li>
</ul>

<h3>Resource maintainers</h3>

<mir:resourcesBrowse data="${data.currentResources}">
    <ul>
        <c:if test="${! empty ownerList}">
            <li><span style="font-style: italic;">${info}:</span> &nbsp;
            <c:forEach var="ownerlist" items="${ownerList}">
                    <c:choose>
                        <c:when test="${! empty ownerlist.firstName}">
                            <c:out value="${ownerlist.firstName} ${ownerlist.lastName}" />
                        </c:when>

                        <c:otherwise>
                            <c:out value="${ownerlist.login}" />
                        </c:otherwise>
                    </c:choose>
                &nbsp;
            </c:forEach>
            </li>
        </c:if>
    </ul>

</mir:resourcesBrowse>


<h3>Examples of usage</h3>
<p>
	Here are some possible annotations using this data collection. These are classified by format (like <a href="http://sbml.org/" title="SBML">SBML</a>, <a href="http://www.cellml.org/" title="CellML">CellML</a> or <a href="http://www.biopax.org/" title="BioPAX">BioPAX</a>) and show the element(s) (tags for XML based formats) which can have a MIRIAM URI -using the current data collection- to bring extra knowledge about the entity described.
</p>
<p style="font-weight: bold;">
	Those examples are purposely limited to a few formats and domains and <i>do not</i> reflect the wide scope of usage of MIRIAM URIs!
</p>

<c:forEach var="expl" items="${examples}">
	<h4>${expl.format}</h4>
	<ul>
		<c:forEach var="tag" items="${expl.tags}">
			<li><a href="<c:url value='/usage/${tag.id}' />" title="Access to all the data collection(s) which can be used to annotate this element">${tag.name}</a> (${tag.info})</li>
		</c:forEach>
	</ul>
</c:forEach>

<br />

<p>
     For complete details about an element of a specific format, please always refer to the official specifications.
</p>

<br />

<section class="grid_24 clearfix">
	<div class="grid_12 alpha bottom_links_left">
		<a href="<c:url value='/collections/' />" title="Return to the list of data collections" class="icon icon-functional" data-icon="<">Go back to the list of data collections</a>
	</div>
	<div class="grid_12 omega bottom_links_right">
	    <a href="<c:url value='/mdb?section=edit_anno&amp;data=${data.id}' />" title="Suggest modifications to: ${data.name}" class="icon icon-functional" data-icon="e">Suggest modifications to this data collection</a>
	</div>
</section>

<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
