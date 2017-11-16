<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130219
  @copyright EMBL-EBI, BioModels.net
  
  MIRIAM Registry: displays all the recorded information about a the currently logged user and allow its edition.
                   This includes: first name, last name, email, organisation and password.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<h2 class="icon icon-functional" data-icon="l">Manage your account</h2>

<h3>Logout</h3>
                    
<p>
    If you want to logout from the authenticated part of the Registry, click on the button below.
</p>

<form method="post" id="logout_form" action="signOut">
    <input type="submit" value="Logout" class="submit_button" onclick="signOut();"/>
</form>

<script src="https://apis.google.com/js/platform.js?onload=onLoad" async defer></script>
<script>
    function signOut() {
        var auth2 = gapi.auth2.getAuthInstance();
        auth2.signOut().then(function () {
            console.log('User signed out.');
        });
    }
    function onLoad() {
        gapi.load('auth2', function() {
            gapi.auth2.init();
        });
    }
</script>

<h3>Personal information</h3>

<p>
    Here are the personal information we store about you. If you want to update it, change the content of the following form and click the "Update Info" button.
</p>

<br />
<form method="post" id="update_info_form" action="updateUserInfo">
    <table class="nothing" style="width:40%">
     <tr>
         <td>
             Login:&nbsp;
         </td>
         <td>
             ${data.login}
         </td>
     </tr>
     <tr>
         <td>
             First name:&nbsp;
         </td>
         <td>
             <input type="text" name="firstName" size="45" id="first_name_id" value="${data.firstName}" />
         </td>
     </tr>
     <tr>
            <td>
                Last name:&nbsp;
            </td>
            <td>
                <input type="text" name="lastName" size="45" id="last_name_id" value="${data.lastName}" />
            </td>
        </tr>
        <tr>
            <td>
                Email:&nbsp;
            </td>
            <td>
                <input type="text" name="email" size="45" id="email_id" value="${data.email}" />
            </td>
        </tr>
        <tr>
            <td>
                Organisation:&nbsp;
            </td>
            <td>
                <input type="text" name="organisation" size="45" id="organisation_id" value="${data.organisation}" />
            </td>
        </tr>
 </table>
 <br />
 <input type="submit" value="Update" class="submit_button" />
</form>


<c:if test="${! empty data.password}">

<h3>Password</h3>

<p>
    If you want to change your password, fill the following form and click the "Change Pass" button.
</p>

    <form method="post" id="update_pass_form" action="passChange">
        <table class="nothing" style="width:40%">
            <tr>
                <td>
                    Current password:&nbsp;
                </td>
                <td>
                    <input type="password" name="oldPass" size="20" id="old_pass_id" />
                </td>
            </tr>
            <tr>
                <td>
                    New password:&nbsp;
                </td>
                <td>
                    <input type="password" name="newPass1" size="20" id="new_pass_one_id" />
                </td>
            </tr>
            <tr>
                <td>
                    New Password (bis):&nbsp;
                </td>
                <td>
                    <input type="password" name="newPass2" size="20" id="new_pass_two_id" />
                </td>
            </tr>
        </table>
        <br />
        <input type="submit" value="Change" class="submit_button" />
    </form>
</c:if>




<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
