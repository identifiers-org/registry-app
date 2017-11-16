<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130704
  @copyright BioModels.net, EMBL-EBI
  
  MIRIAM Registry: displays general public information about a resource (including the health check history).
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<%-- javascript functions for dynamic forms 
<script type="text/javascript" src="${initParam.www}js/MiriamDynamicForms.js"></script>

<script type="text/javascript">
//<![CDATA[
// open a URL in a popup
function openPopup(url)
{
    window.open(url, 'open_window', 'scrollbars, resizable, dependent, width=640, height=480, left=0, top=0');
}
//]]>
</script>
--%>

<h2 class="icon icon-generic" data-icon="R">Resource: ${resource.info}</h2>

<c:if test="${resource.obsolete == true}">
	<br />
	    <div class="message_warning">
	        WARNING! This resource has been deprecated!
	        <br />
	        Please use <a href="<c:url value='/collections/${resource.collectionId}' />" title="Go to: ${dataCollection}">the other resources providing the same dataset</a>.
	    </div>
    <br />
</c:if>

<h3>General information</h3>
<table class="nothing" style="margin-left:2em;">
	<tr>
        <td>Description:&nbsp;</td>
        <td><span style="font-style:italic; font-weight:bold; padding-right:1em;">${resource.info}</span></td>
    </tr>
    <tr>
        <td>Institution:&nbsp;</td>
        <td>${resource.institution}<c:if test="${! empty resource.location}">, ${resource.location}</c:if></td>
    </tr>
    <tr>
        <td>Website:&nbsp;</td>
        <td><a href="<c:url value='${resource.url_root}' />" title="Access to: ${resource.url_root}" class="external">${resource.url_root}</a></td>
    </tr>
    <tr>
        <td>Example access URL:&nbsp;</td>
        <td><a href="<c:url value='${resource.url}' />" title="Access to: ${resource.url}" class="external">${resource.url}</a></td>
    </tr>
    <tr>
        <td>Parent data collection:&nbsp;</td>
        <td><a href="<c:url value='/collections/${resource.collectionId}' />" title="Access to the data collection: ${dataCollection}">${dataCollection}</a></td>
    </tr>
	<tr>
        <td>Registry identifier:&nbsp;</td>
        <td>${resource.id}</td>
    </tr>
</table>

<br />

<c:if test="${! empty restrictions}">
	<h3>Restriction(s)</h3>
	<c:forEach var="restriction" items="${restrictions}">
		<h4><c:out value="${restriction.type.category}" /></h4>
		<p style="font-style:italic; margin-top:0;"><c:out value="${restriction.type.desc}" /></p>
		<p style="margin-top:5px; margin-bottom:0;">The reason why this restriction is associated with the parent data collection is: <br />
		<span style="font-weight:bold; font-size:110%; padding-left:20px;"><c:out value="${restriction.info}" /></span></p>
		<c:if test="${! empty restriction.link}">
			<p style="padding-top: 5px; margin-top:0;">For more information, please refer to: <a class="external" title="External link to further information." href="<c:out value="${restriction.link}" />"><c:out value="${restriction.linkText}" /></a></p>
		</c:if>
	</c:forEach>
</c:if>

<h3>Health summary</h3>

<c:choose>
	<c:when test="${healthInfo}">
		<table class="regular">
      <tr>
          <th colspan="2">Health statistics</th>
      </tr>
      <tr>
          <td class="desc">Last known state</td>
          <td class="element" style="color:white; background-color:${stateColour};"><span style="font-weight:bold;">${state}</span></td>
      </tr>
      <tr>
          <td class="desc">Last check</td>
          <td class="element">${lastCheck}</td>
      </tr>
      <tr>
          <td class="desc">Uptime ratio</td>
          <td class="uptime_${reliability}" style="padding-left:5px; color:white;">${uptimeRatio}% (${uptime} checks)</td>
      </tr>
      <tr>
          <td class="desc">Downtime ratio</td>
          <td class="element">${downtimeRatio}% (${downtime} checks)</td>
      </tr>
      <tr>
          <td class="desc">Unknown ratio</td>
          <td class="element">${unknownRatio}% (${unknown} checks)</td>
      </tr>
      <tr>
          <td class="desc">URL used</td>
          <td class="element"><a class="external" href="${url}" title="Access to this page">${url}</a></td>
      </tr>
  </table>
  
  <h3>Health history</h3>
  
  <p>
      Full record of the health checks performed on this resource.
  </p>
  
  <c:forEach var="historyYears" items="${data}">
      <h4>${historyYears.key}</h4>
      
      <table class="gridCalendar" style="width:auto;">
          <tr>
              <th class="topLeftCell">&nbsp;</th>
              <th><span style="visibility: hidden;">0</span>1</th>
              <th><span style="visibility: hidden;">0</span>2</th>
              <th><span style="visibility: hidden;">0</span>3</th>
              <th><span style="visibility: hidden;">0</span>4</th>
              <th><span style="visibility: hidden;">0</span>5</th>
              <th><span style="visibility: hidden;">0</span>6</th>
              <th><span style="visibility: hidden;">0</span>7</th>
              <th><span style="visibility: hidden;">0</span>8</th>
              <th><span style="visibility: hidden;">0</span>9</th>
              <th>10</th>
              <th>11</th>
              <th>12</th>
              <th>13</th>
              <th>14</th>
              <th>15</th>
              <th>16</th>
              <th>17</th>
              <th>18</th>
              <th>19</th>
              <th>20</th>
              <th>21</th>
              <th>22</th>
              <th>23</th>
              <th>24</th>
              <th>25</th>
              <th>26</th>
              <th>27</th>
              <th>28</th>
              <th>29</th>
              <th>30</th>
              <th>31</th>
          </tr>
      <c:forEach var="historyMonths" items="${historyYears.value}">
          <tr>
              <td class="firstColumn">${historyMonths.key}</td>
              <c:forEach var="historyMonthValue" items="${historyMonths.value}">
                  <td class="state_${historyMonthValue}">&nbsp;</td>
              </c:forEach>
          </tr>
      </c:forEach>
      </table>
  </c:forEach>
  
  <h4>Legend</h4>
  
  <table class="nothing" style="margin-left: 2em;">
      <tr>
          <td class="state_1" style="width: 10px; border: 1px solid rgb(191, 191, 191);"></td>
          <td style="padding-left:1em; font-weight:bold;">Working fine</td>
      </tr>
      <tr>
          <td style="width: 12px;">&nbsp;</td>
          <td style="padding-left: 1em;">A complete health check was performed successfully: the resource is working as expected.</td>
      </tr>
      <tr>
          <td class="state_3" style="width: 10px; border: 1px solid rgb(191, 191, 191);"></td>
          <td style="padding-left: 1em; font-weight:bold;">Probably working</td>
      </tr>
      <tr>
          <td style="width: 12px;">&nbsp;</td>
          <td style="padding-left: 1em;">Only a partial health check was performed successfully (the reason can be that the resource uses Javascript to load the main content of the page or that the returned information is not text based).</td>
      </tr>
      <tr>
          <td class="state_5" style="width: 10px; border: 1px solid rgb(191, 191, 191);"></td>
          <td style="padding-left: 1em; font-weight:bold;">Access restricted</td>
      </tr>
      <tr>
          <td style="width: 12px;">&nbsp;</td>
          <td style="padding-left: 1em;">When the access to a resource is restricted, no health check can be performed.</td>
      </tr>
      <tr>
          <td class="state_0" style="width: 10px; border: 1px solid rgb(191, 191, 191);"></td>
          <td style="padding-left: 1em; font-weight:bold;">Not working</td>
      </tr>
      <tr>
          <td style="width: 12px;">&nbsp;</td>
          <td style="padding-left: 1em;">The resource was not working at the time of the health check. This could be caused by various reasons (server temporarily down, way to access the data changed, ...).</td>
      </tr>
      <tr>
          <td style="width: 12px;">&nbsp;</td>
          <td style="padding-left: 1em; font-style:italic;">We are always investigating these cases and updating our records accordingly.</td>
      </tr>
      <tr>
          <td class="state_4" style="width: 10px; border: 1px solid rgb(191, 191, 191);"></td>
          <td style="padding-left: 1em; font-weight:bold;">Obsolete</td>
      </tr>
      <tr>
          <td style="width: 12px;">&nbsp;</td>
          <td style="padding-left: 1em;">When a resource is declared obsolete, no health check is performed.</td>
      </tr>
      <tr>
          <td class="state_2" style="width: 10px; border: 1px solid rgb(191, 191, 191);"></td>
          <td style="padding-left: 1em; font-weight:bold;">Unknown state</td>
      </tr>
      <tr>
          <td style="width: 12px;">&nbsp;</td>
          <td style="padding-left: 1em;">The health check was not able to determine whether the resource is working or not.</td>
      </tr>
      <tr>
          <td class="state_8" style="width: 10px; border: 1px solid rgb(191, 191, 191);"></td>
          <td style="padding-left: 1em; font-weight:bold;">No data available</td>
      </tr>
      <tr>
          <td style="width: 12px;">&nbsp;</td>
          <td style="padding-left: 1em;">No health check was performed this specific day (this may be due to the fact that the resource is obsolete or to technical reasons on our side).</td>
      </tr>
  </table>
	</c:when>
	<c:otherwise>
		<p>There is no health information recorded for this resource.</p>
		<p>This may be caused by the fact that the resource has been recently created or because an <span style="font-weight:bold; font-style:italic;">access restriction</span> has been associated to its parent data collection.</p>
	</c:otherwise>
</c:choose>

<br />

<section class="grid_24 clearfix">
	<div class="grid_12 alpha bottom_links_left">
        <a href="<c:url value='/collections/${dataTypeId}' />" title="Go back to the data collection: ${dataType}" class="icon icon-functional" data-icon="<">Go back to the data collection: ${dataType}</a>
	</div>
</section>


<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
