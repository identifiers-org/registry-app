<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20140305
  @copyright BioModels.net, EMBL-EBI
  
  Registry: displays the list of all registered profiles.
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />

<%-- javascript functions for dynamic forms --%>
<%--<script type="text/javascript" src="${initParam.www}js/MiriamDynamicForms.js"></script>--%>
<script type="text/javascript" src="js/MiriamDynamicForms.js"></script>

<h2 class="icon icon-generic" data-icon="}">Profiles</h2>

<c:if test="${(sessionScope.login != null) && (!empty sessionScope.login)}">

    <h3>Create a new profile</h3>

    <form name="createp" method="post" id="profilecreate_form" action="createProfile" onsubmit="return validate_createProfile();" autocomplete="off">
        <fieldset id="form_profile">
		<legend>Profiles&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpProfiles').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
		<table class="nothing">
			<tr>
                <td>
                    Name:&nbsp;
                </td>
                <td>
                    <input type="text" name="name" size="50" id="p_name" value="${name}"/>
                </td>
            </tr>
            <tr>
                <td>
                    Short Name:&nbsp;
                </td>
                <td>
                    <input type="text" name="shortname" size="15" id="p_shortname"  value="${shortname}"/>
                </td>
            </tr>
            <tr>
                <td>
                    Description:&nbsp;
                </td>

                <td>
                    <textarea cols="80" rows="4" name="description" id="p_description" value="${description}"></textarea>
                </td>
            </tr>
            <tr>
                <td>
                    Private:&nbsp;
                </td>
                <td>
                    <c:choose>
                        <c:when test="${!empty p_status}">
                            <input type="radio" name="p_status" size="45" value="private" checked="checked" onclick="ps_on()"/>&nbsp;
                        </c:when>
                        <c:otherwise>
                            <input type="radio" name="p_status" size="45" value="private" onclick="ps_on()"/>&nbsp;
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>

            <tbody id="keyfileds" style="display:none">
                <tr>
                    <td>
                        Key:&nbsp;
                    </td>

                    <td>
                        <input type="password" name="key" size="45" id="p_key" value="${key}"/>
                    </td>
                </tr>
            </tbody>
		</table>
	</fieldset>

    <p><input type="submit" value="Create Profile" class="submit_button" /></p>

	<div class="help_message" id="HelpProfiles" style="display: none;">
		<p>
            The "Name" field should contain a name for this profile.
		</p>
        <p>
            The "Short name" field should contain a short unique name for this profile.
        </p>
        <p>
            The "Description" field should contain a short description of this profile.
        </p>
        <p>
            The "Status" indicates whether it is a public or private profile.
        </p>
	</div>
    </form>
</c:if>




<c:if test="${! empty data}">

    <c:if test="${(sessionScope.login != null) && (!empty sessionScope.login)}">
    <h3>Registered Profiles</h3>
    </c:if>

    <table class="regular">

        <tr>
            <th>Short name</th>
            <th>Name</th>
            <th>Description</th>

            <c:if test="${(sessionScope.login != null) && (!empty sessionScope.login)}">
                <th>Access</th>
            </c:if>

            <th>Number of Data Collection(s)</th><%--<th>Creation</th><th>Modification</th>--%>
        </tr>

        <mir:profilesBrowseAdmin data="${data}">
            <tr class="${class}">
                    <%-- trick to directly access data collections without needing to encode the ':'  by '%3A'. I suppose this is better for search engines --%>
                <c:choose>
                    <c:when test="${auto}">
                        <td class="${class}">${shortname} <b><abbr title="This profile is automatically created">(A)</abbr></b> </td>
                    </c:when>
                    <c:otherwise>
                        <td class="${class}"><a href="<c:url value='/mdb?section=admin_profile&amp;id=${id}' />" title="Access to the complete information about the data collection: ${name}">${shortname}</a></td>
                    </c:otherwise>
                </c:choose>

                <td class="${class}">${name}</td>
                <td class="${class}" style="text-align:left; padding-left:1em;">${desc}</td>


                <c:if test="${(sessionScope.login != null) && (!empty sessionScope.login)}">
                    <td class="${class}">${access}</td>
                </c:if>

                <c:choose>
                    <c:when test="${auto}">
                        <td class="${class}">N/A</td>
                    </c:when>
                    <c:otherwise>
                        <td class="${class}">${counter}</td>
                    </c:otherwise>
                </c:choose>

                    <%--            <td class="${class}">${creation}</td>
                                <td class="${class}">${modification}</td>--%>
            </tr>
        </mir:profilesBrowseAdmin>
    </table>
</c:if>

<%-- common footer --%>
<jsp:include page="template_footer.jsp" />

<script type="text/javascript">
 function ps_on()
{
     document.getElementById('keyfileds').style.display='';

}
</script>
