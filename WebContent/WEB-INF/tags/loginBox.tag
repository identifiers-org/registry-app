<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="login" required="true" rtexprvalue="true" %>

<c:if test="${(login != null) && (!empty login)}">
    <div id="loginfo">
        <b>${login}</b>&nbsp;|&nbsp;<a href="@MIR_DYNAMIC_URL@/mdb?section=user" title="My Account">My Account</a>&nbsp;|&nbsp;<a href="@MIR_DYNAMIC_URL@/signOut" title="Sign Out">Sign Out</a>
    </div>
</c:if>
