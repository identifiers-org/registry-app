<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130710
  @copyright EMBL-EBI, BioModels.net
  
  MIRIAM Registry: displays the result of a single (manually triggered) resource health check.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<%-- javascript functions for dynamic forms 
    <script type="text/javascript" src="${initParam.www}js/MiriamDynamicForms.js"></script>
--%>

<h2>Instant health check result: ${resource.id}</h2>

<p>
    Instant resource health check report: <span style="font-style:italic; font-weight:bold;"><c:out value="${resource.info}" /></span> (associated with the data collection: <a href="<c:url value='/collections/${resource.dataId}' />" title="Access to the data collection: ${resource.dataId}"><c:out value="${resource.dataName}" /></a>).
</p>

<table class="regular"> 
    <tr>
        <th colspan="2">Health Check Report</th>
    </tr>
    <tr>
        <td class="desc">Current state</td>
        <td class="element" style="background-color:${stateColour};color:white;"><span style="font-weight:bold;">${stateStr}</span></td>
    </tr>
    <tr>
        <td class="desc">Date</td>
        <td class="element">${date}</td>
    </tr>
    <tr>
        <td class="desc">URL</td>
        <td class="element"><a class="external" href="${resource.htmlUrl}" title="Example of entity stored by this resource">${resource.htmlUrl}</a></td>
    </tr>
    <tr>
        <td class="desc">Responds?</td>
        <td class="element">${report.responsive}</td>
    </tr>
    <tr>
        <td class="desc">Uses redirection?</td>
        <td class="element">${report.redirected}</td>
    </tr>
    <tr>
        <td class="desc">Loads content with Javascript?</td>
        <td class="element">${report.ajax}</td>
    </tr>
    <tr>
        <td class="desc">Returns binary data?</td>
        <td class="element">${report.binary}</td>
    </tr>
    <tr>
        <td class="desc">Logs</td>
        <td class="element">
     <c:choose>
         <c:when test="${(report.logs != null) && (!empty report.logs)}">
             <div style="height: 100px; width: auto; overflow: auto; max-width: 800px"><pre><c:out value="${report.logs}" /></pre></div>
         </c:when>
         <c:otherwise>
             ${report.logs}
         </c:otherwise>
     </c:choose>
        </td>
    </tr>
    <tr>
        <td class="desc">Errors</td>
        <td class="element">${report.errors}</td>
    </tr>
    <tr>
        <td class="desc">Raw data stream</td>
        <td class="element">
            <c:choose>
          <c:when test="${(report.stream != null) && (!empty report.stream)}">
              <div style="height: 200px; width: auto; overflow: auto; max-width: 800px"><pre><c:out value="${report.stream}" /></pre></div>
          </c:when>
          <c:otherwise>
              <c:out value="${report.stream}" />
          </c:otherwise>
         </c:choose>
        </td>
    </tr>
    <c:if test="${report.redirected}">
        <c:forEach var="red" items="${report.redirections}" varStatus="counter">
            <tr>
             <td class="desc">Redirection ${counter.count}</td>
             <td class="element">
                <div style="height: 200px; width: auto; overflow: auto; max-width: 800px">
                    <pre><c:out value="${red.rawReport}" /></pre>
                </div>
             </td>
         </tr>
        </c:forEach>
    </c:if>
</table>

<br />

<%-- generates the back link --%>
<c:url value="/mdb" var="backLink">
    <c:param name="section" value="health_check_details" />
    <c:param name="id" value="${resource.id}" />
</c:url>

<section class="grid_24 clearfix">
  <div class="grid_12 alpha bottom_links_left">
    <a href="<c:out value='${backLink}' />" title="Go back to the resource: ${resource.info} (${resource.id})" class="icon icon-functional" data-icon="<">Go back to the resource: ${resource.info} (${resource.id})</a>
  </div>
</section>


<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
