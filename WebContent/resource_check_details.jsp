<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20140324
  @copyright EMBL-EBI, BioModels.net
  
  MIRIAM Registry: displays all the details about a resource and its settings for health check (curation purposes).
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<h2>Health check: ${data.id}</h2>

<c:if test="${data.obsolete == true}">
	<br />
       	<div class="message_warning">
           	<b>WARNING:</b> this resource has been deprecated!
            <br />
            Please use <a href="<c:url value='/collections/${data.dataId}' />" title="Go to ${data.dataName}">the other resources providing the same dataset</a>.
        </div>
	<br />
</c:if>

<table class="regular">
    <tr>
        <th colspan="2">Resource Info</th>
    </tr>
    <tr>
        <td class="desc">Resource</td>
        <td class="element">${data.id}</td>
    </tr>
    <tr>
        <td class="desc">&nbsp;</td>
        <td class="element">${data.info}</td>
    </tr>
    <tr>
        <td class="desc">Data type</td>
        <td class="element"><a href="<c:url value='/collections/${data.dataId}' />" title="Go to: ${data.dataName}">${data.dataName}</a></td>
    </tr>
    <tr>
        <td class="desc">&nbsp;</td>
        <td class="element">${data.dataId}</td>
    </tr>
    <tr>
        <th colspan="2">Health Check Report</th>
    </tr>
    <tr>
        <td class="desc">Last known state</td>
        <td class="element" style="background-color:${data.stateColour};color:white;"><span style="font-weight:bold;">${data.stateStr}</span></td>
    </tr>
    <tr>
        <td class="desc">Uptime ratio</td>
        <td class="element">${data.uptimeRatio}% (${data.uptime} checks)</td>
    </tr>
    <tr>
        <td class="desc">Downtime ratio</td>
        <td class="element">${data.downtimeRatio}% (${data.downtime} checks)</td>
    </tr>
    <tr>
        <td class="desc">Unknown ratio</td>
        <td class="element">${data.unknownRatio}% (${data.unknown} checks)</td>
    </tr>
    <c:choose>
        <c:when test="${data.state == 1}">
      <tr>
          <td class="desc">Length of last uptime period</td>
                <td class="element">${data.lastUptimePeriod} days</td>
      </tr>
        </c:when>
        <c:when test="${data.state == 0}">
            <tr>
                <td class="desc">Length of last downtime period</td>
                <td class="element">${data.lastDowntimePeriod} days</td>
            </tr>
        </c:when>
        <c:when test="${data.state == 3}">
            <tr>
                <td class="desc">Length of last uptime period</td>
                <td class="element">${data.lastUptimePeriod} days</td>
            </tr>
        </c:when>
    </c:choose>
    <tr>
        <th colspan="2">Health Check Raw Data</th>
    </tr>
    <tr>
        <td class="desc">URL</td>
        <td class="element"><a class="external" href="${data.htmlUrl}" title="Example of entity stored by this resource">${data.htmlUrl}</a></td>
    </tr>
    <tr>
        <td class="desc">Last check</td>
        <td class="element">${data.lastCheck}</td>
    </tr>
    <tr>
        <td class="desc">Last success check</td>
        <td class="element">${data.lastSuccessCheck}</td>
    </tr>
    <tr>
        <td class="desc">Beginning of last uptime period</td>
        <td class="element">${data.beginUptimePeriod}</td>
    </tr>
    <tr>
        <td class="desc">Beginning of last downtime period</td>
        <td class="element">${data.beginDowntimePeriod}</td>
    </tr>
    <tr>
        <td class="desc">Loads content with Javascript?</td>
        <td class="element">${data.ajax}</td>
    </tr>
    <tr>
        <td class="desc">Prevents usage of (i)frames?</td>
        <td class="element">${data.preventsFrame}</td>
    </tr>
    <tr>
        <td class="desc">Returns binary data?</td>
        <td class="element">${data.binary}</td>
    </tr>
    <tr>
        <td class="desc">Logs</td>
        <td class="element">
      <c:choose>
          <c:when test="${(data.logs != null) && (!empty data.logs)}">
              <div style="height: 100px; width: auto; overflow: auto;"><pre>${data.logs}</pre></div>
          </c:when>
          <c:otherwise>
              ${data.logs}
          </c:otherwise>
      </c:choose>
        </td>
    </tr>
    <tr>
        <td class="desc">Errors</td>
        <td class="element">${data.errors}</td>
    </tr>
</table>


<h3>Update keyword</h3>

<p>
    The following string of characters is used when checking the health of a resource (in order to test if the returned page is the correct one).
</p>

<form method="post" id="keyword_update_form" action="updateCheckKeyword">
    <fieldset id="form_update_keyword">
        <legend>Keyword&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpUpdateKeyword').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
        <table class="nothing">
            <tr>
                <td>
                    Keyword:&nbsp;
                </td>
                <td>
                    <input type="text" name="keyword" size="50" id="keyword_id" value="<c:out value="${data.keyword}"/>" />
                </td>
            </tr>
        </table>
        <input type="hidden" value="${data.id}" name="resourceId" id="resourceId" />
        <input type="submit" value="Update" class="submit_button" />
    </fieldset>
</form>

<div class="help_message" id="HelpUpdateKeyword" style="display: none;">
  <p>This form allows you to update the keyword used to check that the page retrieved during the resource health check process is the right one (not an error message for example).</p>
</div>


<h3>Check health</h3>

<p>
    Use the <em>Check</em> button below in order to test the health of the current resource. This is for information and testing purposes only and no record will be stored in the database.
</p>

<%-- generated a link similar to: /mdb?section=resource_check&id=MIR%3A00100011 --%>
<form method="get" id="test_health_check_form" action="mdb">
    <input type="hidden" value="resource_check" name="section" />
    <input type="hidden" value="${data.id}" name="id" />
    <input type="submit" value="Check" class="submit_button" />
</form>

<br />
        
<section class="grid_24 clearfix">
  <div class="grid_12 alpha bottom_links_left">
    <a href="<c:url value='/mdb?section=health_check' />" title="Return to the list of resources" class="icon icon-functional" data-icon="<">Go back to the list of resources</a>
  </div>
  <div class="grid_12 omega bottom_links_right">
    <a href="<c:url value='/mdb?section=health_check_history&amp;id=${data.id}' />" title="Display the full history of the health checks of this resource" class="icon icon-functional" data-icon="e">Health history</a>
  </div>
</section>


<%-- common footer --%>
<jsp:include page="template_footer-with_jquery.jsp" />
