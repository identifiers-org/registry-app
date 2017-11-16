<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130219
  @copyright EMBL-EBI, BioModels.net
  
  MIRIAM Registry: displays the login form (includes a password reset form).
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


            <h2 class="icon icon-functional" data-icon="l">Curator sign in</h2>

<table cellpadding="10">
    <tr>
        <td style="horiz-align: middle;">
    <form name="login" method="post" action="openId">
        <input type="hidden" name="openid_identifier" id="openid_identifier" />
         <c:if test="${! empty referrer}">
            <input type="hidden" value="${referrer}" name="referrer" id="referrerId" />
        </c:if>
    </form>

            <div id="my-signin2"></div>
            <script>
                function onSuccess(googleUser) {
                    document.getElementById("openid_identifier").value = googleUser.getBasicProfile().getEmail();
                    document.forms["login"].submit();
                }
                function onFailure(error) {
                    console.log(error);
                }
                function renderButton() {
                    gapi.signin2.render('my-signin2', {
                        'scope': 'profile email',
                        'width': 240,
                        'height': 50,
                        'longtitle': true,
                        'theme': 'dark',
                        'onsuccess': onSuccess,
                        'onfailure': onFailure
                    });
                }
            </script>

            <script src="https://apis.google.com/js/platform.js?onload=renderButton" async defer></script>

        </td>
        <td style="vertical-align: middle;"> <h3>OR</h3> </td>
        <td>
            </br>
            <p>
                Sign in here using your username and password:
            </p>

            <form method="post" action="signIn">
                <table class="nothing" style="width:auto;">
                    <tr>
                        <td>
                            <label for="login_id">Username:</label>
                        </td>
                        <td>
                            <input type="text" size="20" name="username" id="login_id" onfocus="javascript:this.select();" />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="pass_id">Password:</label>
                        </td>
                        <td>
                            <input type="password" size="20" name="password" id="pass_id" onfocus="javascript:this.select();" />
                        </td>
                    </tr>
                </table>
                <p>
                    <input type="submit" value="SignIn" class="submit_button" />
                    <input type="reset" value="Reset" class="reset_button" />
                </p>
                <c:if test="${! empty referrer}">
                    <input type="hidden" value="${referrer}" name="referrer" id="referrerId" />
                </c:if>
            </form>

            <p>
                To receive a new password, type your username below and click the <i>Reset Pass</i> button. The newly generated password will be sent to you by email.
            </p>

            <form method="post" action="passReset">
                <table class="nothing" style="width:auto;">
                    <tr>
                        <td>
                            <label for="username_id">Username:</label>
                        </td>
                        <td>
                            <input type="text" size="20" name="username" id="username_id" onfocus="javascript:this.select();" />
                        </td>
                    </tr>
                </table>
                <p>
                    <input type="submit" value="Reset Pass" class="submit_button" />
                </p>
            </form>
        </td>
    </tr>
</table>
			<h3>Warning</h3>
			<p>
			    You need to enable <i>cookies</i> in your web browser in order to use this <b>Sign In</b> functionality.
			</p>
			
			<!-- focus on the input field of the login -->
			<script type="text/javascript">
			    document.forms[1]['login_id'].focus();
			</script>


<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
