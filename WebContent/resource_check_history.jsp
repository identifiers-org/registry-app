<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130710
  @copyright EMBL-EBI, BioModels.net
  
  MIRIAM Registry: displays the full history of the health check of a resource
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<h2>Health history: ${resourceId}</h2>

<c:if test="${obsolete == true}">
    <br />
    <div class="message_display">
        <div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
            WARNING! This resource has been deprecated!
            <br />
            Please use <a href="<c:url value='/collections/${dataTypeId}' />" title="Go to ${dataType}">the other resources providing the same dataset</a>.
        </div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
    </div>
    <br />
</c:if>

<p>
    Health history of: ${resourceId} (${dataType}, <i>${resourceInfo}</i>).
</p>

<c:forEach var="historyYears" items="${data}">
    <h3>${historyYears.key}</h3>
    
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


<h3>Legend</h3>

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

<br />
        
<section class="grid_24 clearfix">
  <div class="grid_12 alpha bottom_links_left">
  	<a href="<c:url value='/mdb?section=health_check_details&amp;id=${resourceId}' />" title="Return to the health check details of this resource" class="icon icon-functional" data-icon="<">Go back to the resource health details</a>
  </div>
</section>


<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
