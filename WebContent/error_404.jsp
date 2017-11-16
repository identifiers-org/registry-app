<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20140305
  @copyright EMBL-EBI, BioModels.net
  
  MIRIAM Registry: displays the custom 404 error page.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<h2>Error 404</h2>

<p>
    Sorry, the page you are requesting doesn't exist...
</p>
<p>
    Feel free to contact us, either by using the <a href="<c:url value='/mdb?section=support&amp;info=Error%20404' />" title="MIRIAM Registry support form">provided form</a> or <a href="<c:url value='/mdb?section=contribute#team' />" title="MIRIAM Registry contact page">directly by email</a>, in order to help us improve the Registry.
</p>
<p>
    Thank you.
</p>                    


<%-- common footer --%>
<jsp:include page="template_footer.jsp" />                   
