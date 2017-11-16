<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130304
  @copyright BioModels.net, EMBL-EBI
  
  MIRIAM Registry: displays the list of tags in a cloud way (font size varying).
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<h2 class="icon icon-generic" data-icon="[">Categories &amp; tags</h2>

<p>
    Here are the different categories and tags currently used to label the data collections:
</p>

<form method="post" action="<c:url value='/tagSearch' />">
<mir:tagsCloud data="${data}">
	<c:if test="${null != prefix}">
		${prefix}
	</c:if>
	<div class="grid_${divSize}" style="font-size:${fontSize}%; text-align:center; padding-top:0; padding-bottom:0; margin-top:0; margin-bottom:0;">
		<a href="<c:url value='/tags/${tag.id}' />" title="Number of data collection(s) associated with this tag: ${tag.nbOccurrence}">${tag.name}</a>
		<br />
		<input name="tags2search" type="checkbox" value="${tag.id}" />
	</div>
	<c:if test="${null != suffix}">
		${suffix}
	</c:if>
</mir:tagsCloud>

 <%--
 <table
     <mir:tagsCloud data="${data}">
         ${prefix}
         <td><input name="tags2search" type="checkbox" value="${tag.id}" />&nbsp;<span style="font-size:${nbOccurr}%;"><a href="<c:url value='/tags/${tag.id}' />" title="Number of occurrences: ${tag.nbOccurrence}">${tag.name}</a></span></td>
         ${suffix}
     </mir:tagsCloud>
 </table>
 --%>
 
 <br />
 
 Match: <input name="qualifier" value="all" type="radio" checked="checked" />all the tags &nbsp; <input name="qualifier" value="any" type="radio" />any tags
 
 <br />
 <input type="submit" value="Search" class="submit_button" />
 <input type="reset" value="Reset" class="reset_button" />
</form>


<h3>Help</h3>
<p>
	The size of each tag is proportional to the number of collections labelled with it.
</p>
<p>
    A click on one category will return the list of data collections associated with it.
</p>
<p>
    By selecting several categories and using the 'Search' button, you will be able to see the list of data collections associated with <b>all</b> the selected categories.
</p>


<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
