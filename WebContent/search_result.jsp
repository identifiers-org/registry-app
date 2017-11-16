<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130219
  @copyright EMBL-EBI, BioModels.net
  
  MIRIAM Registry: search results
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<c:choose>
	<%-- some results were returned --%>
	<c:when test="${!empty data}">
		<%-- common header --%>
		<jsp:include page="template_header.jsp" />
		
		<section class="grid_18 alpha">
			<h2 class="icon icon-functional" data-icon="1">Search result(s) for <span id="search_query" class="searchterm">${words}</span></h2>
		</section>
		
		<aside class="grid_6 omega shortcuts expander" id="search-extras">	    	
	    	<div id="ebi_search_results">
	    		<h3 class="slideToggle icon icon-functional" data-icon="u">Show more data from EMBL-EBI</h3>
	    	</div>
		</aside>
		
		<section class="grid_24 alpha" id="local_search_results">
	        <h3>Published data collections</h3>
	        
            <table class="regular">
				<tr>
					<th>Name</th><th>Definition</th>
				</tr>
         		<mir:searchBrowse data="${data}">
					<tr class="${class}">
						<td class="${class}">
                        	<a href="<c:url value='/collections/${id}' />" title="Access to the complete information about the data collection: ${name}">${name}</a>
                        </td>
                        <td class="${class}" style="text-align:left; padding-left:1em;">${def}</td>
            		</tr>
          		</mir:searchBrowse>
         	</table>
         	
	        <p>
	            ${counter} published data collection<c:if test="${counter > 1}">s</c:if> returned.
	        </p>
		</section>
	</c:when>
	
	
	<%-- no result found --%>
	<c:otherwise>
		<%-- common header --%>
		<jsp:include page="template_header-noresult.jsp" />
		
		<h2 class="icon icon-functional" data-icon="1">No search result for <span id="search_query" class="searchterm">${words}</span></h2>
		
		<p class="alert">We're sorry, but we couldn’t find anything that matched your search for <span style="font-weight: bold;">${words}</span>.</p>
		
		<section class="grid_16 alpha">
			<h3>Did you try searching using tags?</h3>
			<p>Tags are associated with each data collections. You can <a href="<c:url value='/tags/' />" title="Search using tags">search the Registry using those tags</a>.</p>
			
			<h3>Did you try browsing the Registry?</h3>
			<p>You can also directly <a href="<c:url value='/collections' />" title="Browse the Registry">browse the various data collections</a> recorded in the Registry.</p>
			
			<h4>Still can't find what you're looking for?</h4>
			<p>Please leave us <a href="<c:url value='/mdb?section=support&amp;info=generic' />" title="Send us questions, comments and suggestions">some feedback</a> or directly <a href="<c:url value='/mdb?section=contribute#team' />" title="Contact the team developing and maintaining the Registry">contact us</a> for help.</p>
	    </section>	
		
		<aside class="grid_8 omega shortcuts" id="search-extras">	    	
	    	<div id="ebi_search_results">
	    		<h3>More data from EMBL-EBI</h3>
	    	</div>
		</aside>
	</c:otherwise>
</c:choose>

<!-- if the user was logged in, we also display search results from the curation pipeline -->
<c:if test="${userLogged}">
	<h3>Data collections under curation</h3>
	
	<c:if test="${!empty curationData}">
  		<table class="regular">
      		<tr>
          		<th>Name</th><th>Definition</th><th>Status</th>
      		</tr>
   			<mir:searchBrowse data="${curationData}">
        		<tr class="${class}">
            		<td class="${class}">
                      <a href="<c:url value='/mdb?section=curation&amp;data=${id}' />" title="Access to the complete information about the data collection: ${name}">${name}</a>
                 </td>
                 <td class="${class}" style="text-align:left; padding-left:1em;">${def}</td>
                 <td class="${class}" style="text-align:center;">${status}</td>
        		</tr>
   			</mir:searchBrowse>
  		</table>
 	</c:if>
 	
 	<p>
    	${curationCounter} data collection<c:if test="${curationCounter > 1}">s</c:if> under curation returned.
	</p>
</c:if>


<%-- common footer --%>
<jsp:include page="template_footer-with_jquery.jsp" />
