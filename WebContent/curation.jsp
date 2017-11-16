<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130312
  @copyright BioModels.net, EMBL-EBI
  
  MIRIAM Registry: overview of the pending/cancelled/published data collections
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />

<h2>Browse the data collections
    <c:if test="${(type != null) && (!empty type)}">
        <span style="color:rgb(94, 158, 158);">&nbsp;(${type})</span>
    </c:if></h2>

<p style="text-align:right;">
 <c:choose>
     <c:when test="${type == 'SUBMITTED'}">
        Submitted
     </c:when>
     <c:otherwise>
         <a href="<c:url value='mdb?section=curation&amp;type=submitted' />" title="Display only data collections in the 'Submitted' state">Submitted</a>
     </c:otherwise>
 </c:choose>
 |
 <c:choose>
     <c:when test="${type == 'CURATION'}">
        Curation
     </c:when>
     <c:otherwise>
         <a href="<c:url value='/mdb?section=curation&amp;type=curation' />" title="Display only data collections in the 'Curation' state">Curation</a>
     </c:otherwise>
 </c:choose>
 |
 <c:choose>
     <c:when test="${type == 'PUBLISHED'}">
        Published
     </c:when>
     <c:otherwise>
         <a href="<c:url value='/mdb?section=curation&amp;type=published' />" title="Display only data collections in the 'Published' state">Published</a>
     </c:otherwise>
 </c:choose>
 |
 <c:choose>
     <c:when test="${type == 'PENDING'}">
        Pending
     </c:when>
     <c:otherwise>
         <a href="<c:url value='/mdb?section=curation&amp;type=pending' />" title="Display only data collections in the 'Pending' state">Pending</a>
     </c:otherwise>
 </c:choose>
 |
 <c:choose>
     <c:when test="${type == 'CANCELED'}">
        Canceled
     </c:when>
     <c:otherwise>
         <a href="<c:url value='/mdb?section=curation&amp;type=canceled' />" title="Display only data collections in the 'Canceled' state">Canceled</a>
     </c:otherwise>
 </c:choose>
 |
 <c:choose>
     <c:when test="${type == 'ALL'}">
        All
     </c:when>
     <c:otherwise>
         <a href="<c:url value='/mdb?section=curation&amp;type=all' />" title="Display all data collections">All</a>
     </c:otherwise>
 </c:choose>
</p>
   
   <p>
       Brief overview of the data collections:
   </p>
   
   <form method="post" action="deleteCuraDataType">
       <table class="regular">
           <tr>
               <th class="action"><img src="${initParam.www}img/Delete.gif" alt="delete icon" title="Deletes data collection(s)" height="16" width="16" /></th>
            <th>Name</th>
            <th>Definition</th>
            <th>Submission date</th>
            <th>State</th>
            <th>Action</th>
        </tr>
        
        <mir:curationBrowse data="${data}">
            <tr class="${class}">
                <td class="${class}">
                    <input name="datatype2remove" type="checkbox" value="${cura.id}" />
                </td>
                <td class="${class}">
                    <a href="<c:url value='/mdb?section=curation&amp;data=${cura.id}' />" title="Access to the complete information about the data collection: ${cura.name}">${cura.name}</a>
                </td>
                <td class="${class}">${cura.shortDef}</td>
                <td class="${class}">${cura.submissionDateStr}</td>
                <td class="${class}">${cura.state}</td>
                <td class="${class}">
                    <c:choose>
					   <%-- the data collection is published --%>
					   <c:when test="${cura.state == 'Published'}">
					       &nbsp;<a href="<c:url value='/collections/${cura.publicId}' />" title="Access to the published data collection: ${cura.name}">${cura.publicId}</a>&nbsp;
					   </c:when>
                       <%-- the data collection is not published yet --%>
					   <c:otherwise>
					       <a href="<c:url value='/mdb?section=publish&amp;data=${cura.id}' />" title="Publish data collection: ${cura.name}">Publish it!</a>
					   </c:otherwise>
					</c:choose>
                </td>
            </tr>
        </mir:curationBrowse>
    </table>
    
    <p>
        <input type="submit" value="Delete" class="submit_button" />
        <input type="reset" value="Reset" class="reset_button" />
    </p>
</form>

<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
