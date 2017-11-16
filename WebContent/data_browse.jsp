<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130805
  @copyright EMBL-EBI, BioModels.net
  
  MIRIAM Registry: display the details of one data collection (first tab: overview)
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />

<script type="text/javascript" src="${initParam.www}js/prototype.js"></script>
	
<script type="text/javascript">
//<![CDATA[
	// Shows more or less resource links
	function displayResources(res)
	{
	  var str = "document." + res;
	  var classStr = "optional " + res;
	
	  for (i=0; i<document.getElementsByTagName('li').length; i++)
	  {
	    if (document.getElementsByTagName('li')[i].className == classStr)
	    {
	      if (document.getElementsByTagName('li')[i].style.display == "block")
	      {
	        document.getElementsByTagName('li')[i].style.display = "none";
	        (eval(str)).title = "Display more resources";
	        (eval(str)).src = "${initParam.www}img/plus.gif";
	      }
	      else
	      {
	        document.getElementsByTagName('li')[i].style.display = "block";
	        (eval(str)).title = "Display only one resource";
	        (eval(str)).src = "${initParam.www}img/minus.gif";
	      }
	    }
	  }
	}

    // Shows/hides the deprecated resources
	function displayDeprecatedResources()
	{
		$$('tr.hide_deprecated_resource').each(function(elmt) { elmt.toggle(); });
	}
    
	// Shows/hides the deprecated URLs
	function displayDeprecatedURLs()
	{
		$$('tr.hide_deprecated_urls').each(function(elmt) { elmt.toggle(); });
	}
	
	// Shows/hides the deprecated URNs
	function displayDeprecatedURNs()
	{
		$$('tr.hide_deprecated_urns').each(function(elmt) { elmt.toggle(); });
	}
	
	// opens a URL in a popup
	function openPopup(url)
	{
	    window.open(url, 'open_window', 'scrollbars, resizable, dependent, width=640, height=480, left=0, top=0');
	}
//]]>
</script>

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
	<a class="active" href="<c:url value='/collections/${data.id}' />" title="Overview of the data collection">Overview</a>
    <%--<a class="tab" href="<c:url value='/tags/${data.id}' />" title="Categorisation of the data collection (tags)">Categories</a>--%>
    <a class="tab" href="<c:url value='/misc/${data.id}' />" title="Miscellaneous information about the data collection">Miscellaneous</a>
    <c:if test="${!empty data.restrictions}">
    	<a class="restriction" href="<c:url value='/restrictions/${data.id}' />" title="Restriction(s) information about the data collection">Restriction(s)</a>
    </c:if>
    <%--
    <a class="tab" href="<c:url value='/usage/${data.id}' />" title="Examples of annotation using the data collection">Example Usage</a>
    <a class="tab" href="<c:url value='/webservices/${data.id}' />" title="Web Services available for the data collection">Web Services</a>
    --%>


    <%-- download links --%>
    <span data-icon="=" class="icon icon-functional download">
    	<a href="<c:url value="/collections/${data.id}.rdf" />" title="Download the record of this data collection in RDF/XML">RDF/XML</a>
    	<a href="<c:url value="/collections/${data.id}.ttl" />" title="Download the record of this data collection in Turtle">Turtle</a>
    </span>
</div>

<%-- categories/tags --%>
<div id="tags_for_collection">
	<c:forEach var="tag" items="${tags}">
		<span class="tag_link"><a href="<c:url value="/tags/${tag.id}" />" title="Data collection(s) associated with the category: ${tag.name}">${tag.name}</a></span>
	</c:forEach>
</div>


<h3 class="display_collection">General information</h3>
<table class="collection_item">
	<tr>
		<td class="desc">Recommended name</td>
		<td class="element">
			<span style="font-weight:bold;"><c:out value="${data.name}" /></span>
			<c:if test="${! empty data.restrictions}">
				<span style="position:relative; padding-left: 1em;"><a style="cursor:pointer;" href="<c:url value='/restrictions/${data.id}' />"><img class="regular_top" title="This data collections has some restriction(s)! Click for more information..." alt="Collection has restriction(s) logo" src="${initParam.www}img/restricted_19.png" /></a></span>
				<%-- background-color:#ffecc4; --%>
			</c:if>
		</td>
	</tr>
	
	<c:if test="${! empty data.synonyms}">
		<tr>
			<td class="desc">Alternative name(s)</td>
			<td>
				<ul style="list-style-type: none;">
					<c:forEach var="synonym" items="${data.synonyms}">
						<li><c:out value="${synonym}" /></li>
					</c:forEach>
				</ul>
			</td>
		</tr>
	</c:if>
    
    <%-- now displayed in its own tab
    <c:if test="${! empty data.restrictions}">
		<tr>
		    <th colspan="3" style="background-color: #ed8585;">Restriction(s)</th>
		</tr>
		<c:forEach var="restriction" items="${data.restrictions}">
			<tr>
				<td colspan="3" style="padding-left:5px;">
					<p style="font-weight:bold; margin-top:0;"><c:out value="${restriction.type.desc}" /></p>
					<p style="margin-top:0; padding-left:20px;"><span style="font-style:italic;">
						<c:out value="${restriction.info}" /></span>
						<c:if test="${! empty restriction.link}">&nbsp; Cf. <a class="external" title="External link to further information." href="<c:out value="${restriction.link}" />"><c:out value="${restriction.linkText}" /></a></c:if>
					</p>
				</td>
			</tr>
		</c:forEach>
	</c:if>
	--%>
	
	<tr>
		<td class="desc">Description</td>
		<td class="element"><c:out value="${data.definition}" /></td>
	</tr>
	
	<tr>
		<td class="desc">Identifier pattern</td><td class="element"><c:out value="${data.regexp}" /></td>
	</tr>
	
    <tr class="last">
		<td class="desc">Registry identifier</td><td class="element">${data.id}</td>
	</tr>
</table>


<h3 class="display_collection">Identification schemes</h3>
<table class="collection_item">
	<tr>
		<td class="desc">Namespace</td>
		<td class="element"><c:out value="${data.namespace}" /></td>
	</tr>
	<tr class="last">
		<td class="desc">URI</td>
		<td class="element">http://identifiers.org/<c:out value="${data.namespace}" />/</td>
	</tr>
<%--	<tr class="last">
		<td class="desc">Root URN</td>
		<td class="element">urn:miriam:<c:out value="${data.namespace}" />:</td>
	</tr>--%>
</table>

<c:if test="${! empty data.uris}">
	<h5 class="display_collection_level2">Alternative URI schemes &nbsp;</h5>
	<div id="list_other_uris">
        <ul style="margin-left: 1em;">
            <c:forEach var="uric" items="${data.uris}">
                <c:if test="${uric.deprecated != '1'}">
 <%--                   <li class="element"><c:out value="${uric.value}"/></li>--%>
                    <c:choose>
                        <c:when test="${uric.deprecated == '2'}">
                            <li class="element"><c:out value="${uric.value}"/></li>
                        </c:when>
                        <c:otherwise>
                            <c:if test="${uric.type == 'URN'}">
                                <li class="element"><c:out value="${uric.value}"/></li>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </c:if>
<%--                <c:if test="${data.official && uric.type == 'URN' }">
                    <li class="element"><c:out value="${uric.value}"/></li>
                </c:if>--%>

            </c:forEach>
        </ul>
	</div>
</c:if>

<c:if test="${!empty data.deprecatedURNs or ! empty data.deprecatedURLs}">
	<h5 class="display_collection_level2">Deprecated URI scheme(s) &nbsp; <img src="${initParam.www}img/plus.gif" alt="Display/Hide deprecated URI scheme(s)" title="Display/Hide deprecated URI scheme(s)" onclick="$('#list_other_root_uris').toggle();" class="action_button" /></h5>
	<div id="list_other_root_uris" style="display: none;">
		<ul style="margin-left: 1em;">
			<c:if test="${! empty data.deprecatedURLs}">
				<c:forEach var="deprecated" items="${data.deprecatedURLs}">
					<li class="element"><c:out value="${deprecated}" /></li>
				</c:forEach>
			</c:if>
			<c:if test="${! empty data.deprecatedURNs}">
				<c:forEach var="deprecated" items="${data.deprecatedURNs}">
					<li class="element"><c:out value="${deprecated}" /></li>
				</c:forEach>
			</c:if>
		</ul>
	</div>
</c:if>

<form action="../requestAccess" method="post">
<h3 class="display_collection">Physical locations (resources)</h3>
<table class="collection_item">
	<mir:resourcesBrowse data="${data.currentResources}">
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
            <td rowspan="4">
                <c:if test="${(sessionScope.login != null) && (!empty sessionScope.login) && (sessionScope.role == 'user')}">
                    <c:choose>
                        <c:when test="${ownership_status=='1'}">
                            Maintainer
                        </c:when>
                        <c:when test="${ownership_status=='0'}">
                            Pending maintainer...
                        </c:when>
                        <c:otherwise>
                            <button name="accessResource" type="submit" value="${resourceId}">Become maintainer</button>
                        </c:otherwise>
                    </c:choose>

                </c:if>
            </td>
		</tr>
		<tr>
			<td class="desc23">Access URLs</td>
<%--
			<td class="element">${prefix}<b><abbr class="idInfo" title="This identifier follows the pattern: ${data.regexp}">$id</abbr></b>${suffix}<c:if test="${! empty example}">&nbsp;&nbsp;[Example:&nbsp;<a class="external" href="${htmlUrl}" title="Example of entity stored by this resource">${example}</a>]</c:if></td>
--%>
            <td class="element">
                <a class="external" href="${htmlUrl}" title="This resource is available in">HTML</a>&nbsp;&nbsp;
                <c:set var="formatCounter" value="0" />
                <c:forEach var="format" items="${formatList}" varStatus="formatCounter">
                    <c:choose>
                        <c:when test="${! empty example}">
                            <a class="external" href="${format.urlPrefix}${example}${format.urlSuffix}" title="This resource is available in ${format.mimeType.displayText}">${format.mimeType.displayText}</a>&nbsp;&nbsp;
                        </c:when>
                        <c:otherwise>
                            <b><abbr class="idInfo" title="${format.urlPrefix}$id${format.urlSuffix}">${format.mimeType.displayText}</abbr></b>&nbsp;&nbsp;
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                <c:set var="formatCounter" value="${formatCounter.count}" />
                (using the example identifier: ${example})
            </td>
        </tr>
        <%--<tr>
      		<td class="desc23">Formats</td>
      		<td class="element">
                <c:set var="formatCounter" value="0" />
                <c:forEach var="format" items="${formatList}" varStatus="formatCounter">
                    <c:choose>
                          <c:when test="${! empty example}">
                              <a class="external" href="${format.urlPrefix}${example}${format.urlSuffix}" title="This resource is available in ${format.mimeType.displayText}">${format.mimeType.displayText}</a>&nbsp;&nbsp;
                          </c:when>
                          <c:otherwise>
                              <b><abbr class="idInfo" title="${format.urlPrefix}$id${format.urlSuffix}">${format.mimeType.displayText}</abbr></b>&nbsp;&nbsp;
                          </c:otherwise>
                    </c:choose>
                </c:forEach>
                <c:set var="formatCounter" value="${formatCounter.count}" />
            </td>
      	</tr>--%>
		<tr>
			<td class="desc23">Institution</td>
			<td class="element"><c:if test="${! empty institution}">${institution}</c:if><c:if test="${! empty institution and ! empty location}">, </c:if><c:if test="${! empty location}">${location}</c:if></td>
		</tr>
		<tr <c:if test="${end}">class="last"</c:if>>
			<td class="desc23">Website</td>
			<td class="element"><a href="${base}" title="External link to: ${base}">${base}</a></td>
		</tr>
	</mir:resourcesBrowse>
	<c:if test="${empty data.currentResources}">
		<tr class="last">
			<td colspan="2" style="font-style: italic; padding-left: 5px;">No resource recorded.</td>
		</tr>
	</c:if>
</table>
<input type="hidden" value="${sessionScope.login}" name ="loggedInUser" id="loggedInUser" />
<input type="hidden" value="${data.id}" name ="collectionid" id="collectionid" />
</form>

<c:if test="${!empty data.deprecatedResources}">
	<h5 class="display_collection_level2">Deprecated physical locations &nbsp; <img src="${initParam.www}img/plus.gif" alt="Display/Hide deprecated resources" title="Display/Hide deprecated resources" onclick="$('#list_deprecated_resources').toggle();" class="action_button" /></h5>
	
	<table class="collection_item" id="list_deprecated_resources" style="display: none;">
		<mir:resourcesBrowse data="${data.deprecatedResources}">
			<tr>
				<td class="desc21 uptime_4" rowspan="4">
				</td>
				<td class="desc22" rowspan="4">
					<a href="<c:url value='/resources/${resourceId}' />" title="Obsolete resource: access to historical health records...">Resource<br />${resourceId}</a>
				</td>
				<td class="desc23">Description</td>
				<td class="element">${info}</td>
			</tr>
			<tr>
				<td class="desc23">Access URLs</td>
<%--
				<td class="element">${prefix}<b><abbr class="idInfo" title="This identifier follows the pattern: ${data.regexp}">$id</abbr></b>${suffix}<c:if test="${! empty example}">&nbsp;&nbsp;[Example:&nbsp;<a class="external" href="${htmlUrl}" title="Example of entity stored by this resource">${example}</a>]</c:if></td>
--%>
                <td class="element">
                    <a class="external" href="${htmlUrl}" title="This resource is available in">HTML</a>&nbsp;&nbsp;
                    <c:set var="formatCounter" value="0" />
                    <c:forEach var="format" items="${formatList}" varStatus="formatCounter">
                        <c:choose>
                            <c:when test="${! empty example}">
                                <a class="external" href="${format.urlPrefix}${example}${format.urlSuffix}" title="This resource is available in ${format.mimeType.displayText}">${format.mimeType.displayText}</a>&nbsp;&nbsp;
                            </c:when>
                            <c:otherwise>
                                <b><abbr class="idInfo" title="${format.urlPrefix}$id${format.urlSuffix}">${format.mimeType.displayText}</abbr></b>&nbsp;&nbsp;
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    <c:set var="formatCounter" value="${formatCounter.count}" />
                    (using the example identifier: ${example})
                </td>
            </tr>
            <%--<tr>
          		<td class="desc23">Formats</td>
          		<td class="element">
                    <c:set var="formatCounter" value="0" />
                    <c:forEach var="format" items="${formatList}" varStatus="formatCounter">
                        <c:choose>
                              <c:when test="${! empty example}">
                                  <a class="external" href="${format.urlPrefix}${example}${format.urlSuffix}" title="This resource is available in ${format.mimeType.displayText}">${format.mimeType.displayText}</a>&nbsp;&nbsp;
                              </c:when>
                              <c:otherwise>
                                  <b><abbr class="idInfo" title="${format.urlPrefix}$id${format.urlSuffix}">${format.mimeType.displayText}</abbr></b>&nbsp;&nbsp;
                              </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    <c:set var="formatCounter" value="${formatCounter.count}" />
                </td>
          	</tr>--%>
			<tr>
				<td class="desc23">Institution</td>
				<td class="element"><c:if test="${! empty institution}">${institution}</c:if><c:if test="${! empty institution and ! empty location}">, </c:if><c:if test="${! empty location}">${location}</c:if></td>
			</tr>
			<tr <c:if test="${end}">class="last"</c:if>>
				<td class="desc23">Website</td>
				<td class="element"><a href="${base}" title="External link to: ${base}">${base}</a></td>
			</tr>
	    </mir:resourcesBrowse>
	</table>	
</c:if>

<c:if test="${! empty data.docHtmlURLs}">
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


<section class="grid_24 clearfix">
	<div class="grid_12 alpha bottom_links_left">
		<a href="<c:url value='/collections/' />" title="Return to the list of data collections" class="icon icon-functional" data-icon="<">Go back to the list of data collections</a>
	</div>
	<div class="grid_12 omega bottom_links_right">
		<a href="<c:url value='/mdb?section=edit&amp;data=${data.id}' />" title="Suggest modifications to: ${data.name}" class="icon icon-functional" data-icon="e">Suggest modifications to this data collection</a>
	</div>
</section>

<!-- Hides the deprecated URLs, URNs and resources -->
  <script type="text/javascript">
  //<![CDATA[
      $$('tr.hide_deprecated_resource').each(function(elmt) { elmt.hide() });
      $$('tr.hide_deprecated_urls').each(function(elmt) { elmt.hide() });
      $$('tr.hide_deprecated_urns').each(function(elmt) { elmt.hide() });
  //]]>
  </script>


<%-- common footer --%>
<jsp:include page="template_footer-with_jquery.jsp" />
