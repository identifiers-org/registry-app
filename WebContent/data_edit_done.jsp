<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20140312
  @copyright BioModels.net, EMBL-EBI
  
  MIRIAM Registry: displays all the information about a new data collection (in order to submit it).
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<c:choose>
	<c:when test="${actionType == 'add'}">
		<h2 class="icon icon-functional" data-icon="e">New data collection: <em>${data.name}</em></h2>
	</c:when>
	<c:when test="${actionType == 'edit'}">
		<h2 class="icon icon-functional" data-icon="e">Updated data collection: <em>${data.name}</em></h2>
	</c:when>
</c:choose>

<c:choose>
	<c:when test="${actionType == 'edit'}">
		<c:choose>
			<c:when test="${(empty data.name) || (empty data.URL && empty data.URN) || (empty data.definition) || (empty data.regexp) || (empty data.resources)}">
				<p class="warning">
					One or more mandatory field(s) are not filled! These ones are shown in red colour. Please, re-edit the data collection taking care of the mandatory fields.
				</p>
			</c:when>
			<c:otherwise>
				<p>Please check the edition summary displayed below.</p>
				<p>We thank you for taking part in the development of the Registry!</p>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:when test="${actionType == 'add'}">
		<c:choose>
			<c:when test="${(empty data.name) || (empty data.URL && empty data.URN) || (empty data.definition) || (empty data.regexp) || (empty data.resources)}">
				<p class="warning">
					One or more mandatory field(s) are not filled! These ones are shown in red colour. Please, resubmit your data collection taking care of the mandatory fields.
				</p>
			</c:when>
			<c:otherwise>
				<p>Please check the submission summary displayed below.</p>
				<p>We thank you for taking part in the development of the Registry!</p>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<%-- nothing for the moment --%>
	</c:otherwise>
</c:choose>


<h3 class="display_collection">General information</h3>
<table class="collection_item">
	<tr>
		<td class="desc">Recommended name</td>
		<td class="element">
			<c:choose>
				<c:when test="${empty data.name}">
					<span style="font-weight:bold;"><span class="warning">NOT FILLED!</span></span>
				</c:when>
				<c:otherwise>
					<span style="font-weight:bold;"><c:out value="${data.name}" /></span>
				</c:otherwise>
		</c:choose>
		</td>
	</tr>
	
	<c:if test="${! empty data.synonyms}">
		<tr>
			<td class="desc">Alternative name(s)</td>
			<td>
				<ul style="list-style-type: none;">
					<c:choose>
						<c:when test="${empty data.synonyms}">
							<li class="element"><i>No synonym</i></li>
						</c:when>
						<c:otherwise>
							<c:forEach var="synonym" items="${data.synonyms}">
								<li><c:out value="${synonym}" /></li>
							</c:forEach>
						</c:otherwise>
					</c:choose>
				</ul>
			</td>
		</tr>
	</c:if>
	
	<tr>
		<td class="desc">Description</td>
		<td class="element">
			<c:choose>
				<c:when test="${empty data.definition}">
					<span class="warning">NOT FILLED!</span>
				</c:when>
				<c:otherwise>
					<c:out value="${data.definition}" />
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	
	<tr>
		<td class="desc">Identifier pattern</td>
		<td class="element">
			<c:choose>
				<c:when test="${empty data.regexp}">
					<span class="warning">NOT FILLED!</span>
				</c:when>
				<c:otherwise>
					<c:out value="${data.regexp}" />
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	
    <tr class="last">
		<td class="desc">Registry identifier</td><td class="element">${data.id}</td>
	</tr>
</table>

<h3 class="display_collection">Identification schemes</h3>
<table class="collection_item">
	<tr>
		<td class="desc">Official URN</td>
		<td class="element">
			<c:choose>
				<c:when test="${empty data.URL && empty data.URN}">
					<span class="warning">NOT FILLED!</span>
				</c:when>
				<c:otherwise>
					<c:out value="${data.URN}" />
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<tr>
		<td class="desc">Official URL</td>
		<td class="element">
			<i>similar Identifiers.org URI</i>
		</td>
	</tr>
</table>

<c:if test="${!empty data.deprecatedURIs}">
	<h5 class="display_collection_level2">Other root URI(s) &nbsp; <img src="${initParam.www}img/plus.gif" alt="Display/Hide other root URI(s)" title="Display/Hide other root URI(s)" onclick="$('#list_other_root_uris').toggle();" class="action_button" /></h5>
	<div id="list_other_root_uris" style="display: none;">
		<ul style="margin-left: 1em;">
			<c:if test="${! empty data.deprecatedURIs}">
				<c:forEach var="deprecated" items="${data.deprecatedURIs}">
					<li class="element"><c:out value="${deprecated}" /></li>
				</c:forEach>
			</c:if>
		</ul>
	</div>
</c:if>

<h3 class="display_collection">Physical locations (resources)</h3>
<table class="collection_item">
	<mir:resourcesBrowse data="${data.resources}">
		<tr>
			<td class="desc21 uptime_${uptime_class}" rowspan="4">
				<c:if test="${primary == true}">
					<img src="${initParam.www}img/primary_resource.png" title="Primary resource for this data collection" alt="primary" />
				</c:if>
			</td>
			<td class="desc22" rowspan="4">
				<a href="<c:url value='/resources/${resourceId}' />" title="Uptime: ${uptime}%, click here for details...">Resource<br />${resourceId}</a>
			</td>
			<td class="desc23">Description</td>
			<td class="element">${info}</td>
		</tr>
		<tr>
			<td class="desc23">Access URL</td>
			<td class="element">${prefix}<b><abbr class="idInfo" title="This identifier follows the pattern: ${data.regexp}">$id</abbr></b>${suffix}<c:if test="${! empty example}">&nbsp;&nbsp;[Example:&nbsp;<a class="external" href="${htmlUrl}" title="Example of entity stored by this resource">${example}</a>]</c:if></td>
		</tr>
		<tr>
			<td class="desc23">Institution</td>
			<td class="element"><c:if test="${! empty institution}">${institution}</c:if><c:if test="${! empty institution and ! empty location}">, </c:if><c:if test="${! empty location}">${location}</c:if></td>
		</tr>
		<tr <c:if test="${end}">class="last"</c:if>>
			<td class="desc23">Website</td>
			<td class="element"><a href="${base}" title="External link to: ${base}">${base}</a></td>
		</tr>
	</mir:resourcesBrowse>
	<c:if test="${empty data.resources}">
		<tr class="last">
			<td colspan="2" style="font-style: italic; padding-left: 5px;"><span class="warning">NOT FILLED!</span></td>
		</tr>
	</c:if>
</table>

<c:if test="${! empty data.documentationURLs}">
	<h3 class="display_collection">References</h3>
	<c:if test="${! empty data.documentationURLs}">
		<ul style="margin-left: 1em;">
			<c:forEach var="docs_url" items="${data.documentationURLs}">
				<li><a href="<c:url value="${docs_url}" />" title="External link to: ${docs_url}" class="external"><c:url value="${docs_url}" /></a></li>
			</c:forEach>
		</ul>
	</c:if>
	<c:if test="${! empty data.documentationIDs}">
		<%-- MIRIAM URNs converted into Identifiers.org URLs --%>
		<ul style="margin-left: 1em;">
			<c:forEach var="docs_uri" items="${data.documentationIDs}">
				<li><a href="<c:url value="${docs_uri}" />" title="External link to: ${docs_uri}" class="external"><c:url value="${docs_uri}" /></a></li>
			</c:forEach>
		</ul>
	</c:if>
</c:if>

<%--
			<mir:resourcesBrowse data="${data.resources}">
				<tr>
					<td class="desc2" rowspan="4">
						<acronym class="idInfo" title="Stable identifier or this resource: ${resourceId}">Resource #${id}</acronym>
						<c:if test="${obsolete == '1'}">
							&nbsp;<img src="${initParam.www}img/Warning.gif" title="WARNING: this resource is obsolete!" alt="Resource obsolete!" align="bottom" />
						</c:if>
					</td>
					<td class="desc2">Access URL</td>
					<td class="element">${prefix}<b><acronym class="idInfo" title="This identifier follows the pattern: ${data.regexp}">$id</acronym></b>${suffix}</td>
				</tr>
				<tr>
					<td class="desc2">Website</td><td class="element"><a href="${base}" title="External link to: ${base}">${base}</a></td>
				</tr>
				<tr>
					<td class="desc2">Description</td><td class="element">${info}</td>
				</tr>
				<tr>
					<td class="desc2">Institution</td><td class="element">${institution}, ${location}</td>
				</tr>
			</mir:resourcesBrowse>
 --%>

<%-- if edition (instead of submission): link back to the data collection just edited --%>
<c:if test="${! empty data.id}">
	<div class="grid_12 alpha bottom_links_left">
    	<a href="<c:url value='/collections/${data.id}' />" title="Go back to the data collection ${data.name} (${data.id})" class="icon icon-functional" data-icon="&lt;">Go back to the data collection: ${data.name} (${data.id})</a>
    </div>
</c:if>


<%-- common footer --%>
<jsp:include page="template_footer-with_jquery.jsp" />
