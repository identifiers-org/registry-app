<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130105
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Registry: displays the list of data collections associated with one or more given tag(s).
  
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<h2 class="icon icon-generic" data-icon="[">Data collections tagging</h2>

<p>
    Here are the data collections associated to the following tag<c:if test="${tagCount > 1}">s</c:if>:
</p>

<div style="text-align: left;">
    <ul>
        <c:forEach var="tag" items="${tags}">
            <li><a href="<c:url value='/tags/${tag.id}' />" title="Access to the data collections associated with this tag">${tag.name}</a><c:if test="${(tag.info != null) && (!empty tag.info)}">&nbsp;(${tag.info})</c:if></li>
        </c:forEach>
    </ul>
</div>

<c:if test="${!empty data}">
    <table class="regular">
        <tr>
            <th>Name</th><th>Definition</th>
        </tr>
        <mir:simpleDataTypeBrowse data="${data}">
            <tr class="${class}">
                <td class="${class}"><a href="<c:url value='/collections/${id}' />" title="Access to the complete information about the data collection: ${name}">${name}</a>
                </td>
                <td class="${class}" style="text-align:left; padding-left:1em;">${def}</td>
            </tr>
        </mir:simpleDataTypeBrowse>
    </table>
</c:if>

<p>
    ${dataCount} item<c:if test="${dataCount > 1}">s</c:if> returned.
</p>


<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
