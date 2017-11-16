<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130710
  @copyright EMBL-EBI, BioModels.net
  
  MIRIAM Registry: displays the the whole list of resources according to their health state.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<h2>Resources health check
    <c:if test="${(type != null) && (!empty type)}">
        <span style="color:rgb(94, 158, 158);">&nbsp;(${type}&nbsp;-&nbsp;${counter})</span>
    </c:if></h2>

<p style="text-align:right;">
    <c:choose>
        <c:when test="${type == 'UP'}">
           Up
        </c:when>
        <c:otherwise>
            <a href="<c:url value='/mdb?section=health_check&amp;type=up' />" title="Display only resources in 'up' state">Up</a>
        </c:otherwise>
    </c:choose>
    |
    <c:choose>
        <c:when test="${type == 'DOWN'}">
           Down
        </c:when>
        <c:otherwise>
            <a href="<c:url value='/mdb?section=health_check&amp;type=down' />" title="Display only resources in 'down' state">Down</a>
        </c:otherwise>
    </c:choose>
    |
    <c:choose>
        <c:when test="${type == 'PROBABLY UP'}">
           Probably Up
        </c:when>
        <c:otherwise>
            <a href="<c:url value='/mdb?section=health_check&amp;type=probably' />" title="Display only resources in 'probably up' state">Probably Up</a>
        </c:otherwise>
    </c:choose>
    |
    <c:choose>
        <c:when test="${type == 'UNKNOWN'}">
           Unknown
        </c:when>
        <c:otherwise>
            <a href="<c:url value='/mdb?section=health_check&amp;type=unknown' />" title="Display only resources in 'unknown' state">Unknown</a>
        </c:otherwise>
    </c:choose>
    |
    <c:choose>
        <c:when test="${type == 'RESTRICTED ACCESS'}">
           Restricted
        </c:when>
        <c:otherwise>
            <a href="<c:url value='/mdb?section=health_check&amp;type=restricted' />" title="Display only resources in 'restricted' state">Restricted</a>
        </c:otherwise>
    </c:choose>
    |
    <c:choose>
        <c:when test="${type == 'OBSOLETE RESOURCE'}">
           Obsolete
        </c:when>
        <c:otherwise>
            <a href="<c:url value='/mdb?section=health_check&amp;type=obsolete' />" title="Display only resources in 'obsolete' state">Obsolete</a>
        </c:otherwise>
    </c:choose>
    |
    <c:choose>
        <c:when test="${type == 'ALL'}">
           All
        </c:when>
        <c:otherwise>
            <a href="<c:url value='/mdb?section=health_check&amp;type=all' />" title="Display all resources (except obsolete ones)">All</a>
        </c:otherwise>
    </c:choose>
</p>

<jsp:scriptlet>
request.setAttribute("dyndecorator", new org.displaytag.decorator.TableDecorator()
{
    public String addRowClass()
    {
        return ((uk.ac.ebi.miriam.db.ResourcesCheckReport) getCurrentRowObject()).getStateShortStr();
    }
});
</jsp:scriptlet>

<display:table name="${data}" class="regular" defaultsort="2" requestURI="/mdb?section=health_check_details" decorator="dyndecorator">
    <display:setProperty name="basic.msg.empty_list" value="Sorry there is no resource to display in this category." />
    
	<display:column property="resourceLink" title="Resource" sortable="true" />
	<display:column property="dataTypeLink" title="Data collection" sortable="true" defaultorder="ascending" style="text-align:left;" />
	<display:column property="stateStr" title="State" class="state" />
	<display:column property="uptimeRatioStr" title="Uptime" sortable="true" />
</display:table>

<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
