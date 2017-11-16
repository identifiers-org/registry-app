<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20140311
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  Registry: display the form requesting the necessary additional information to make obsolete a data collection.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<h2 class="icon icon-functional" data-icon="d">Deprecate data collection: ${collection.name}</h2>

<p>
	Please fill the additional information required for deprecating this data collection: a comment explaining the reason of the deprecation (this will be publicly displayed on the data collection page) and an optional replacement data collection (please provide its identifier).
</p>


<form method="post" id="deprecation_form" action="dataCollectionDeprecate2">
	
	<table class="nothing">
		<tr>
			<td>
				Comment:&nbsp;
			</td>
			<td>
				<textarea cols="70" rows="3" name="comment" id="comment"></textarea>
			</td>
		</tr>
		<tr>
			<td>
				Suggested alternative collection:&nbsp;
			</td>
			<td>
				<input type="text" name="replacementId" size="45" id="replacementId" />
			</td>
		</tr>
	</table>
	
	<input type="hidden" value="${collection.id}" name="collectionId" id="collectionId" />
	
	<input type="submit" value="Deprecate data collection" class="submit_button" />
</form>

<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
