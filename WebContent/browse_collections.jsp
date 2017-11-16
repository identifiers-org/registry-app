<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20140310
  @copyright BioModels.net, EMBL-EBI
  
  MIRIAM Registry: lists all data collections
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


			<h2 class="icon icon-generic" data-icon="D">Data collections: <i>${startByTitle}</i> 
	            <c:if test="${(preferences != null) && (!empty preferences)}">
	                <span style="color:rgb(94, 158, 158);">&nbsp;(${preferences})</span>
	            </c:if>
	        </h2>
			
			<div style="text-align:center;">
				<mir:generateBrowsingLinks query="${startBy}">
					<a href="<c:url value='/mdb?section=browse&amp;startBy=${letter}${option}' />" title="${title}" style="${class}">${link}</a> <c:if test="${end != true}">|</c:if>
				</mir:generateBrowsingLinks>
				| <a href="<c:url value='/tags/' />" title="Browse data collections by catgories or tags">Categories</a>
				<c:if test="${(sessionScope.login != null) && (!empty sessionScope.login)}">
				| <a href="<c:url value='/mdb?section=browse&amp;display=restricted' />" title="Diplay only data collection with restrictions">Restricted</a>
				| <a href="<c:url value='/mdb?section=browse&amp;display=obsolete' />" title="Display only obsolete data collections">Obsolete</a>
				</c:if>
			</div>
			
			<form method="post" action="dataCollectionDeprecate">
				<table class="regular">
					<tr>
						<c:if test="${(sessionScope.role == 'admin') || (sessionScope.role == 'cura')}">
							<th class="action"><img src="${initParam.www}img/Delete.gif" alt="delete icon" title="Deprecate data collection" height="16" width="16" /></th>
						</c:if>
						<th>Name</th><th>Namespace</th><th>Definition</th>
					</tr>
					
					<mir:arrayBrowse data="${data}">
						<tr class="${class}">
							<c:if test="${(sessionScope.role == 'admin') || (sessionScope.role == 'cura')}">
								<td class="${class}">
									<input name="collection2deprecate" type="radio" value="${datatype.id}" />
								</td>
							</c:if>
							<c:choose>
								<c:when test="${datatype.restricted}">
									<%-- trick to directly access data collections without needing to encode the ':'  by '%3A'. I suppose this is better regarding search engines --%>
		                            <td class="${class}" style="background-color:#FDF2F2;">
		                            	<span style="position:relative; padding-right: 1em; cursor:help;"><img class="regular_top" src="${initParam.www}img/restricted_19.png" alt="restricted logo" title="This data collections has some restriction(s)!" /></span><a href="<c:url value='/collections/${datatype.id}' />" title="Access to the complete information about the data collection: ${datatype.name}">${datatype.name}</a></td>
		                            <td class="${class}" style="background-color:#FDF2F2;">${datatype.namespace}</td>
		                            <td class="${class}" style="text-align:left; padding-left:1em; background-color:#FDF2F2;">${datatype.definition}</td>
								</c:when>
								<c:otherwise>
									<%-- trick to directly access data collections without needing to encode the ':'  by '%3A'. I suppose this is better regarding search engines --%>
		                            <td class="${class}"><a href="<c:url value='/collections/${datatype.id}' />" title="Access to the complete information about the data collection: ${datatype.name}">${datatype.name}</a></td>
		                            <td class="${class}">${datatype.namespace}</td>
		                            <td class="${class}" style="text-align:left; padding-left:1em;">${datatype.definition}</td>
								</c:otherwise>
							</c:choose>
						</tr>
					</mir:arrayBrowse>
				</table>
				
				<div>
					<b>${nb_data}</b> ${query}.
				</div>
                
                <c:if test="${(sessionScope.role == 'admin') || (sessionScope.role == 'cura')}">
                    <p>
                        <input type="submit" value="Deprecate" class="submit_button" />
                        <input type="reset" value="Reset" class="reset_button" />
                    </p>
                </c:if>
			</form>


<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
