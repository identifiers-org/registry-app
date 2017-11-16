<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20121209
  @copyright EMBL-EBI, BioModels.net
  
  MIRIAM Registry: display of static pages
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


			<!-- actual content of the page -->
			<c:import url="${initParam.www}${requestScope.section}" charEncoding="UTF-8" />


<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
