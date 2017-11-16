<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130214
  @copyright BioModels.net, EMBL-EBI
  
  MIRIAM Registry; displays the tag(s) associated with a given data collection.
  
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />

                    
<c:choose>
    <%-- the data collection exists --%>
    <c:when test="${data != null}">
        <h2 class="icon icon-generic" data-icon="R">Data collection: <em>${name}</em></h2>
        
        <c:if test="${obsolete == true}">
            <br />
            <div class="message_warning">
                <b>WARNING:</b> this data collection has been deprecated!
             	<br />
             	${replacementComment}
         		<c:if test="${replacementId != null}">
         			<br />
         			We recommend the usage of the following data collection instead: <a href="<c:url value='/collections/${replacementId}' />" title="Go to ${replacementName}">${replacementName}</a>.
         		</c:if>
            </div>
            <br />
        </c:if>
        
        <!-- tabs -->
        <div class="menutabs">
			<a class="tab" href="<c:url value='/collections/${id}' />" title="Overview of the data collection">Overview</a>
            <a class="active" href="<c:url value='/tags/${id}' />" title="Categorisation of the data collection (tags)">Categories</a>
            <a class="tab" href="<c:url value='/misc/${id}' />" title="Miscellaneous information about the data collection">Miscellaneous</a>
            <c:if test="${restricted}">
      	        <a class="restriction" href="<c:url value='/restrictions/${id}' />" title="Restriction(s) information about the data collection">Restriction(s)</a>
            </c:if>
            <%--
            <a class="tab" href="<c:url value='/usage/${id}' />" title="Examples of annotation using the data collection">Example Usage</a>
            <a class="tab" href="<c:url value='/webservices/${id}' />" title="Web Services available for the data collection">Web Services</a>
            --%>
        </div>
        
        <c:choose>
            <%-- no tags stored  --%>
            <c:when test="${empty data}">
                <p>
                    Sorry, there is no tag currently associated with the data collection.
                </p>
                <p>
                    Don't hesitate to participate to the development of MIRIAM Registry by submitting corrections and additions.
                </p>
            </c:when>
            
            <%-- some tags exist --%>
            <c:otherwise>
                <p>
                    Here are the tags associated with this data collection:
                </p>
                
                <ul>
                    <c:forEach var="tag" items="${data}">
                        <li><a href="<c:url value='/tags/${tag.id}' />" title="Data collections associated with this tag">${tag.name}</a></li>
                    </c:forEach>
                </ul>
            </c:otherwise>
        </c:choose>
		
        <br />
        
        <section class="grid_24 clearfix">
	        <div class="grid_12 alpha bottom_links_left">
	            <a href="<c:url value='/collections/' />" title="Return to the list of data collections" class="icon icon-functional" data-icon="<">Go back to the list of data collections</a>
			</div>
			<div class="grid_12 omega bottom_links_right">
	            <a href="<c:url value='/mdb?section=edit_tag&amp;data=${id}' />" title="Suggest modifications to the content of this page" class="icon icon-functional" data-icon="e">Suggest modifications to these tags</a>
			</div>
		</section>
    </c:when>
        
    <%-- the data collection doesn't exist, SHOULD NEVER OCCURED: redirection towards the introduction page --%>
    <c:otherwise>
        <h1>MIRIAM Registry</h1>
        <p>
            Sorry, the requested data collection doesn't exist in the database...
        </p>
        <p>
            Please consult the <a href="<c:url value='/collections/' />" title="List of data collections">list of available data collections</a>.
        </p>
    </c:otherwise>
</c:choose>


<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
