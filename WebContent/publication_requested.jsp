<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20140304
  @copyright BioModels.net, EMBL-EBI
  
  Registry: displays the confirmation before publication of a data collection previously stored in the curation pipeline.
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<h2 class="icon icon-functional" data-icon="(">Publication requested for: ${data.name}</h2>

<p>
    Do you want to publish the following data collection: <b>${data.name}</b> (${data.id})?
</p>

<br />
<form method="post" action="publishDataType">
    <input type="hidden" value="${data.id}" name="dataId" id="dataIdId" />
    <input type="hidden" value="${data.name}" name="dataName" id="dataNameId" />
    <input type="submit" value="Publish" class="submit_button" />
    <input type="reset" value="No" onclick="location.href='${pageContext.request.contextPath}/mdb?section=curation&amp;type=all'" class="reset_button" />
</form>

<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
