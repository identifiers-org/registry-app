<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20140305
  @copyright BioModels.net, EMBL-EBI
  
  Registry: displays the details of one registered profile.
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<h2 class="icon icon-generic" data-icon="q">Profile: ${project.shortName}</h2>


<div class="grid_20">   <%-- left column --%>
	<h3>${project.name}</h3>
	
	<div style="padding-left:1em;">${project.desc}</div>
	
	<br />

    <c:if test="${project.openAccessStr=='private'}">
	
	<h4>Key (URL)</h4>
	<samp style="margin-top:0; padding-left:1em;">${project.keyUrl}</samp> <span style="padding-left:2em;"><a href="javascript:;" title="Display/Hide update key form" onclick="$('#updateKeyForm').toggle();">[Update key]</a></span>
	
	<div id="updateKeyForm" style="display:none;">
		<form method="post" action="updateMyMiriamProjectKey">
			<table style="padding-left:1em; padding-top:6px; border:none;">
		          <tr>
		              <td>
		                  Current key:&nbsp;
		              </td>
		              <td>
		                  <input type="password" name="oldKey" size="70" id="old_key_id" />
		              </td>
		          </tr>
		          <tr>
		              <td>
		                  New key:&nbsp;
		              </td>
		              <td>
		                  <input type="password" name="newKey1" size="70" id="new_key_one_id" />
		              </td>
		          </tr>
		          <tr>
		              <td>
		                  New key (bis):&nbsp;
		              </td>
		              <td>
		                  <input type="password" name="newKey2" size="70" id="new_key_two_id" />
		              </td>
		          </tr>
		          <tr>
				<td>&nbsp;</td>
				<td class="col_right"><input type="submit" class="submit_button" value="Update" /></td>
			</tr>
		</table>
		<input type="hidden" name="projectId" value="${project.id}" />
		</form>
	</div>

    </c:if>

	<h4>Links</h4>
	<div style="padding-left:1em;">
        <c:choose>
            <c:when test="${project.openAccessStr=='public'}">
                <a href="<c:url value='/export/xml?project=${project.shortName}' />" title="customised export for: ${project.shortName}">profile specific XML export</a>
            </c:when>
            <c:otherwise>
                <a href="<c:url value='/export/xml?project=${project.shortName}&amp;key=${project.keyUrl}' />" title="customised export for: ${project.shortName}">profile specific XML export</a>
            </c:otherwise>
        </c:choose>

	</div>
</div>

<div class="grid_4 alpha">   <%-- right column --%>
	<div id="news_side_panel">
		<div class="news_heading" style="padding-bottom: 0px;">${project.shortName}</div>
		<div class="home_news_item">
	        <%--<div class="iconbox2contents" id="stats_content" style="overflow: visible; display: block;">--%>
        	<ul>
            	<li>Access: &nbsp; <span style="color:red;">${project.openAccessStr}</span></li>
                <li>Nb data collections: &nbsp; ${project.nbDataTypes}</li>
                <li>Contact: <br /> <span style="font-style:italic;">${project.contactEmail}</span></li>
                <li>Created: <br /> ${project.dateCreationStr}</li>
                <li>Modified: <br /> ${project.dateLastModifStr}</li>
            </ul>
		</div>
	</div>
</div>


<br />



<div class="grid_24 clearfix">   <%-- full width --%>
	<h4>Data collections</h4>
	
	<div style="text-align:center;">
		<mir:generateProjectBrowsingLinks query="${set}">
			<a href="<c:url value='/mdb?section=admin_profile&amp;id=${project.id}&amp;set=${subset}' />" title="${title}" style="${class}">${subset}</a> <c:if test="${end != true}">|</c:if>
		</mir:generateProjectBrowsingLinks>
	</div>

    <c:choose>
        <c:when test="${(sessionScope.login == null)}">
            <table class="regular">
          			<tr>
          				<th>Name</th><th>Resource(s)</th><th>Date added</th><th>Date modified</th>
          			</tr>
          			<c:forEach var="datatype" items="${datatypes}">
                        <c:if test="${datatype.selected}">
                            <tr>
                                <td><a href="<c:url value='/collections/${datatype.id}' />" title="Display data collection: ${datatype.name}">${datatype.name}</a></td>
                                <c:forEach var="resource" items="${datatype.resources}">
                                    <c:if test="${resource.preferred}">
                                        <td><a href="<c:url value='/resources/${resource.id}' />" title="Display data resource: ${resource.info}">${resource.info}</a></td>
                                    </c:if>
                                </c:forEach>
                                <td>
                                    ${datatype.dateAddedStr}
                                </td>
                                <td>
                                    ${datatype.dateModifStr}
                                </td>
                            </tr>
                        </c:if>
          			</c:forEach>
          		</table>
        </c:when>
        <c:otherwise>
            <form method="post" action="updateMyMiriamDatatypes">
            		<table class="regular">
            			<tr>
            				<th>Selected</th><th>Name</th><th>Resource(s)</th><th>Date added</th><th>Date modified</th>
            			</tr>
            			<c:forEach var="datatype" items="${datatypes}">
            				<tr>
            					<td>
            						<c:choose>
            							<c:when test="${datatype.selected}">
            								<input type="checkbox" name="${datatype.id}" value="${datatype.id}" checked="checked" />
            							</c:when>
            							<c:otherwise>
            								<input type="checkbox" name="${datatype.id}" value="${datatype.id}" />
            							</c:otherwise>
            						</c:choose>
            					</td>
            					<td><a href="<c:url value='/collections/${datatype.id}' />" title="Display data collection: ${datatype.name}">${datatype.name}</a></td>
            					<td class="col_left" style="padding-left:5px;">
            						<select name="resources_${datatype.id}">
            							<c:if test="${!datatype.selected}">
            								<option value="">Please choose a preferred resource...</option>
            							</c:if>
            							<c:forEach var="resource" items="${datatype.resources}">
            								<c:choose>
            									<c:when test="${resource.preferred}">
            										<c:choose>
            											<c:when test="${resource.obsolete}">
            												<option value="${resource.id}" selected="selected">${resource.id} - ${resource.info} [OBSOLETE]</option>
            											</c:when>
            											<c:otherwise>
            												<option value="${resource.id}" selected="selected">${resource.id} - ${resource.info}</option>
            											</c:otherwise>
            										</c:choose>
            									</c:when>
            									<c:otherwise>
            										<c:choose>
            											<c:when test="${resource.obsolete}">
            												<option value="${resource.id}">${resource.id} - ${resource.info} [OBSOLETE]</option>
            											</c:when>
            											<c:otherwise>
            												<option value="${resource.id}">${resource.id} - ${resource.info}</option>
            											</c:otherwise>
            										</c:choose>
            									</c:otherwise>
            								</c:choose>
            							</c:forEach>
            						</select>
            					</td>
            					<td>
            						${datatype.dateAddedStr}
            					</td>
            					<td>
            						${datatype.dateModifStr}
            					</td>
            				</tr>
            			</c:forEach>
            		</table>

            		<c:choose>
            			<c:when test="${set == 'selected'}">
            				<div style="text-align:right; padding-right: 1em;"><b>${nbData}</b> data collections associated with this profile</div>
            			</c:when>
            			<c:otherwise>
            				<div style="text-align:right; padding-right: 1em;"><b>${nbData}</b> data collections with a name starting by <i>${set}</i></div>
            			</c:otherwise>
            		</c:choose>

            		<input type="hidden" name="projectId" value="${project.id}" />
            		<input type="hidden" name="query" value="${set}" />
            		<input type="submit" value="Update" class="submit_button" />
            	</form>
        </c:otherwise>
    </c:choose>


</div>


<%-- common footer --%>
<jsp:include page="template_footer-with_jquery.jsp" />
