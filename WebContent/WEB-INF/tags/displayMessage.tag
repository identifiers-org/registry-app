<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="message" required="true" rtexprvalue="true" %>

<c:if test="${(message != null) && (!empty message)}">
	<br />
	<div class="message_warning">
		${message}
	</div>
</c:if>
