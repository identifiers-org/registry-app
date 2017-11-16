<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130215
  @copyright EMBL-EBI, BioModels.net
  
  MIRIAM Registry: displays a form which can be used by users seeking support.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


           <h2 class="icon icon-generic" data-icon="s">Support</h2>
           
           <p>
               Please, use the form below if you have any questions or are experiencing issues with the MIRIAM Registry. Alternatively, you can also directly <a href="<c:url value='/mdb?section=contribute#team' />" title="Contact the team">contact us</a>. 
           </p>
           
           <br />
           <form method="post" id="supoprt_form" action="supportProcess">
               <table class="nothing">
                   <tr>
                       <td>Email address</td>
                       <td>
                           <input type="text" name="email" size="64" id="emailId" />
                       </td>
                   </tr>
                   <tr>
                       <td>Problem / Query</td>
                       <td>
                           <select name="type">
                               <option value="database_content">Database content</option>
                               <option value="web_services">Web Services</option>
                               <option value="website">Website</option>
                               <option value="other">Other</option>
                           </select>
                       </td>
                   </tr>
                   <tr>
                       <td>Message</td>
                       <td>
                           <textarea cols="74" rows="8" name="query" id="queryId"></textarea>
                       </td>
                   </tr>
               </table>
               
               <%-- Spam trap: this field is not visible (cf. Miriam.css) and its value should stay empty --%>
		      <fieldset class="SpicedHam">
		          <legend>This field must keep its initial value (that means stay empty)!</legend>
		          <table class="nothing">
		              <tr>
		                  <td>
		                      Information:&nbsp;
		                  </td>
		                  <td>
		                      <input type="text" size="15" value="" name="pourriel" id="pourriel" />
		                  </td>
		              </tr>
		          </table>
		      </fieldset>
               
               <%-- Some more information, if available--%>
               <input type="hidden" value="${info}" name="info" id="infoId" />
               
               <input type="submit" value="Send" class="submit_button" />
           </form>
           
           <br />
           
           <h3>Note</h3>
           <p>
               Providing an email address is not mandatory, but is necessary if you want to be kept informed about your query. 
           </p>


<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
