<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="version" required="true" rtexprvalue="true" %>

<c:if test="${(version != 'main') && (version != 'cura')}">
  <div id="versioninfo">
    (${version} version)
  </div>
</c:if>
