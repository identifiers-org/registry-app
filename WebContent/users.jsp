<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20140304
  @copyright EMBL-EBI, BioModels.net
  
  Registry: users management page: displays all the users registered to access the Registry.
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<h2 class="icon icon-generic" data-icon="f">Users</h2>


<p>
    Here are the users registered to access the Registry:
</p>

<table class="regular">
    <tr>
        <th>Login</th><th>Name</th><th>Role</th><th>Last login</th>
    </tr>
    <mir:usersBrowse data="${data}">
        <tr class="${class}">
            <td class="${class}">${user.login}</td><td class="${class}">${user.name}</td><td class="${class}">${user.role}</td><td class="${class}">${user.lastLoginStr}</td>
        </tr>
    </mir:usersBrowse>
</table>

<p>
    ${counter} user<c:if test="${counter > 1}">s</c:if> registered.
</p>


<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
