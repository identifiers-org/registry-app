<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130312
  @copyright BioModels.net, EMBL-EBI
  
  MIRIAM Registry: displays the complete information about a given data collection in the curation pipeline.
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<%-- javascript functions for dynamic forms --%>
<script type="text/javascript" src="${initParam.www}js/MiriamDynamicForms.js"></script>

<script type="text/javascript">
//<![CDATA[
// Show more or less resource links
function displayResources(res)
{
  var str = "document." + res;
  var classStr = "optional " + res;

  for (i=0; i<document.getElementsByTagName('li').length; i++)
  {
    if (document.getElementsByTagName('li')[i].className == classStr)
    {
      if (document.getElementsByTagName('li')[i].style.display == "block")
      {
        document.getElementsByTagName('li')[i].style.display = "none";
        (eval(str)).title = "Display more resources";
        (eval(str)).src = "${initParam.www}img/plus.gif";
      }
      else
      {
        document.getElementsByTagName('li')[i].style.display = "block";
        (eval(str)).title = "Display only one resource";
        (eval(str)).src = "${initParam.www}img/minus.gif";
      }
    }
  }
}
//]]>
</script>


<h2 class="icon icon-functional" data-icon="e">Curate data collection: <em>${data.name}</em></h2>

<form method="post" id="submission_form" action="curaDataEdit" onsubmit="return validate_submission();">    
    <fieldset id="form_name_synonyms">
        <legend>Name and synonyms&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpNames').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
        <table class="nothing">
            <tr>
                <td>
                    Identifier:&nbsp;
                </td>
                <td>
                    <input type="text" name="id" size="45" id="id" value="${data.id}" style="background-color: #C5CED5;" onfocus="this.blur();" />
                </td>
            </tr>
            <tr>
                <td>
                    Name:&nbsp;
                </td>
                <td>
                    <input type="text" name="name" size="45" id="name_id" value="${data.name}" />
                </td>
            </tr>
        </table>
        
        <div id="synonyms_id">
            <c:set var="synnonymsCounter" value="0" />
          <c:forEach var="syn" items="${data.synonyms}" varStatus="synCount">
                <div id="synonym${synCount.count}Div">
                    Synonym:&nbsp;<input name="synonym${synCount.count}" size="45" type="text" value="${syn}" /> &nbsp; <a href="javascript:;" title="Remove this synonym" onclick="removeSynonym('synonym${synCount.count}Div')"><img src="${initParam.www}img/Delete.gif" alt="Delete logo" height="16" width="16" /></a>
                </div>
                <c:set var="synnonymsCounter" value="${synCount.count}" />
            </c:forEach>
        </div>
        
        <input type="hidden" value="${synnonymsCounter}" name="synonymsCounter" id="synonymsCounter" />
        <input type="hidden" value="${synnonymsCounter}" name="synonymsCountReal" id="synonymsCounterReal" />
        <div id="add_synonym_id">
            <a href="javascript:;" title="Add a synonym" onclick="addSynonym();">[Add a synonym]</a>
        </div>
    </fieldset>
    
    
    <div class="help_message" id="HelpNames" style="display: none;">
        <p>
            The field "Primary name" is mandatory and must be the official name of the data collection (for example: "Gene Ontology" or "Uniprot").
        </p>
        <p>
            You can add several synonyms to the official name. That can be useful if the name is an acronym or can be summarised with an acronym (for example: "Gene Ontology" and "GO").
        </p>
    </div>
    
    <fieldset id="form_def_pattern">
        <legend>Definition and pattern&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpDef').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
        <table class="nothing">
            <tr>
                <td>
                    Definition:&nbsp;
                </td>
                <td>
                    <textarea cols="50" rows="2" name="def" id="def_id">${data.definition}</textarea>
                </td>
            </tr>
            <tr>
                <td>
                    Identifier pattern:&nbsp;
                </td>
                <td>
                    <textarea cols="50" rows="2" name="pattern" id="pattern_id">${data.regexp}</textarea>
                </td>
            </tr>
        </table>
    </fieldset>
    
    <div class="help_message" id="HelpDef" style="display: none;">
        <p>
            The field "Definition" must be filled with one or two of sentences explaining what kind of elements are stored in the data collection.
        </p>
        <p>
            The field "Identifier pattern" is the pattern (or PERL-style regular expression) of the identifiers used by the data collection (for example: "^GO:\d{7}$" for Gene Ontology).
        </p>
    </div>
    
    <fieldset id="form_uris">
        <legend>URIs&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpURIs').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
        <table class="nothing">
            <%--
            <tr>
                <td>
                    Official URL:&nbsp;
                </td>
                <td>
                    <input type="text" name="url" size="45" id="url_id" value="${data.URL}" />
                </td>
            </tr>
            --%>
            <tr>
                <td>
                    MIRIAM URN:&nbsp;
                </td>
                <td>
                    <input type="text" name="urn" size="45" id="urn_id" value="${data.URN}" />
                </td>
            </tr>
        </table>
        <div id="deprecated_id">
            <c:set var="deprecatedCounter" value="0" />
            <c:forEach var="deprec" items="${data.deprecatedURIs}" varStatus="deprecCount">
                <div id="deprecated${deprecCount.count}Div">
                    Deprecated URI:&nbsp;<input name="deprecated${deprecCount.count}" size="45" type="text" value="${deprec}" /> &nbsp; <a href="javascript:;" title="Remove this deprecated URI" onclick="removeDeprecated('deprecated${deprecCount.count}Div')"><img src="${initParam.www}img/Delete.gif" alt="Delete logo" height="16" width="16" /></a>
                </div>
                <c:set var="deprecatedCounter" value="${deprecCount.count}" />
            </c:forEach>
        </div>
        <input type="hidden" value="${deprecatedCounter}" name="deprecatedCounter" id="deprecatedCounter" />
        <input type="hidden" value="${deprecatedCounter}" name="deprecatedCountReal" id="deprecatedCounterReal" />
        <div id="add_deprecated_id">
            <a href="javascript:;" title="Add a deprecated URI" onclick="addDeprecated();">[Add a deprecated URI]</a>
        </div>
    </fieldset>
    
    <div class="help_message" id="HelpURIs" style="display: none;">
        <p>
            The URI is a unique string of characters used to unambiguously identify the data collection (for example: "urn:miriam:uniprot" for UniProt). The only official URI takes a URN form.
        </p>
        <p>
            You can add several deprecated versions of the URIs, which can be URLs or URNs. If they are URLs, keep in mind that they don't necessarily need to be valid physical addresses. They are only used as identifiers not as physical locations on Internet!
        </p>
    </div>
    
    <fieldset id="form_resources">
        <legend>Resources&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpResources').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
        <div id="resources_id">
        	<c:choose>
        		<c:when test="${empty data.resources}">
        			<input value="0" name="resourcesCounter" id="resourcesCounter" type="hidden" />
                    <input value="0" name="resourcesCounterReal" id="resourcesCounterReal" type="hidden" />
        		</c:when>
        		<c:otherwise>
        			<mir:resourcesBrowse data="${data.resources}" edit="true">
                  <div id="resources${id}Div">
                      <input type="hidden" value="${resourceId}" name="resourceId${id}" id="resourceId${id}" />
                      <input type="hidden" value="${obsolete}" name="obsolete${id}" id="obsolete${id}" />
                      <table class="resources">
                          <tr>
                              <td rowspan="2" class="text_bottom">
                                  <c:choose>
                                      <c:when test="${obsolete == '1'}">
                                          <img src="${initParam.www}img/Warning.gif" title="WARNING: this resource is obsolete!" alt="Resource obsolete!" align="bottom" />
                                      </c:when>
                                      <c:otherwise>
                                          &nbsp;
                                      </c:otherwise>
                                  </c:choose>
                              </td>
                              <td>
                                  Access URL:&nbsp;
                              </td>
                              <td>
                                  <input type="text" name="dataEntryPrefix${id}" size="34" id="depId${id}" value="${prefix}" />
                              </td>
                              <td>
                                  <b>$id</b>
                              </td>
                              <td>
                                  <input type="text" name="dataEntrySuffix${id}" size="15" id="desId${id}" value="${suffix}" />
                              </td>
                          </tr>
                          <tr>
                              <td>
                                  Example of identifier:&nbsp;
                              </td>
                              <td>
                                  <input type="text" name="dataExample${id}" size="30" id="xplId${id}" value="${example}" />
                              </td>
                              <td>
							      <c:if test="${!empty example}">
	                                  &nbsp;[<a class="external" href="#" onclick="generateExampleLink('depId${id}', 'desId${id}', 'xplId${id}');return false;" title="Link generated using the provided information (opens a new window)">Test link</a>]
                                  </c:if>
                              </td>
                              <td>&nbsp;</td>
                          </tr>
                          <tr>
                              <td>
                                  <b>#${id}:&nbsp;</b>
                              </td>
                              <td>
                                  Website:&nbsp;
                              </td>
                              <td colspan="3">
                                  <input type="text" name="dataResource${id}" size="45" id="drId${id}" value="${base}" />
                              </td>
                          </tr>
                          <tr>
                              <td rowspan="3" class="text_top">
                                  <a href="javascript:;" title="Remove this resource" onclick="removeResource('resources${id}Div')"><img src="${initParam.www}img/Delete.gif" alt="Delete logo" height="16" width="16" /></a>
                              </td>
                              <td>
                                  Description:&nbsp;
                              </td>
                              <td colspan="3">
                                  <input type="text" name="information${id}" size="55" id="infoId${id}" value="${info}" />
                              </td>
                          </tr>
                          <tr>
                              <td>
                                  Institution:&nbsp;
                              </td>
                              <td colspan="3">
                                  <input type="text" name="institution${id}" size="45" id="instituteId${id}" value="${institution}" />
                              </td>
                          </tr>
                          <tr>
                              <td>
                                  Country:&nbsp;
                              </td>
                              <td colspan="3">
                                  <input type="text" name="country${id}" size="30" id="countryId${id}" value="${location}" />
                              </td>
                          </tr>
                      </table>
                  </div>
                  
                  <c:if test="${end == 'true'}">
                      </div>
                      <input type="hidden" value="${id}" name="resourcesCounter" id="resourcesCounter" />
                      <input type="hidden" value="${id}" name="resourcesCounterReal" id="resourcesCounterReal" />
                  </c:if>
                  
              </mir:resourcesBrowse>
        		</c:otherwise>
        	</c:choose>
        <div id="add_resources_id">
            <a href="javascript:;" title="Add a resource" onclick="addResource();">[Add a resource]</a>
        </div>
    </fieldset>
    
    <div class="help_message" id="HelpResources" style="display: none;">
        <p>
            To access (via a web browser or a piece of software) a data collection, you need to enter, at least, one resource.
        </p>
        <p>
            The "Access URL" field is divided into three parts (one of them cannot be edited): this is the physical address used to access a precise element stored by the data collection. The field in the middle ($id) stands for the identifier of an element (it complies with the pattern entered previously).
        </p>
        <p>
            The "Example of identifier" field should contain an identifier used by this resource, in order to display an example of usage to the user.
        </p>
        <p>
            The "Website" field must contains a physical link to the main page of the resource.
        </p>
        <p>
            The three other fields give more information about the institution managing the resource (one sentence of description, the name of the institution, and the country).
        </p>
    </div>
    
    <fieldset id="form_docs">
        <legend>Documentation&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpDocumentation').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
        <%-- TODO: create a Tag Handler (and check if the documentationURLs contains also the transformed URIs -if this is the case: this a problem-) --%>
        <c:set var="docsCounter" value="0" />
        <div id="doc_id">
            <c:forEach var="url" items="${data.documentationURLs}" varStatus="id">
                <div class="docSubmitForm" id="docSubmitForm${id.count}">
                    <div>
                        <a href="javascript:;" title="Remove this piece of documentation" onclick="removeDoc('docSubmitForm${id.count}')"><img src="${initParam.www}img/Delete.gif" alt="Delete logo" height="16" width="16" /></a>&nbsp;<span id="docTypeBox${id.count}">Type: PMID: <input type="radio" name="docType${id.count}" value="PMID" onclick="return displayPubMedForm('docForm${id.count}', ${id.count});" />&nbsp;DOI: <input type="radio" name="docType${id.count}" value="DOI" onclick="return displayDoiForm('docForm${id.count}', ${id.count});" />&nbsp;Physical Location: <input type="radio" name="docType${id.count}" value="URL" checked="checked" onclick="return displayUrlForm2('docForm${id.count}', ${id.count}, '${url}');" /></span>
                    </div>
                    <div id="docForm${id.count}" class="indentDiv">
                        Location: <input name="docUri${id.count}" size="45" id="docUri${id.count}" value="${url}" type="text" />
                    </div>
                </div>
                <c:set var="docsCounter" value="${id.count}" />
            </c:forEach>
            
            <mir:resourcesEdit uris="${data.documentationIDs}" types="${data.documentationIDsType}" start="${docsCounter}">
                <c:choose>
                    <c:when test="${uriType == 'DOI'}">
                        <div class="docSubmitForm" id="docSubmitForm${id}">
                            <div>
                                <a href="javascript:;" title="Remove this piece of documentation" onclick="removeDoc('docSubmitForm${id}')"><img src="${initParam.www}img/Delete.gif" alt="Delete logo" height="16" width="16" /></a>&nbsp;<span id="docTypeBox${id}">Type: PMID: <input type="radio" name="docType${id}" value="PMID" onclick="return displayPubMedForm('docForm${id}', ${id});" />&nbsp;DOI: <input type="radio" name="docType${id}" value="DOI" onclick="return displayDoiForm2('docForm${id}', ${id}, '${uri}');"  checked="checked" />&nbsp;Physical Location: <input type="radio" name="docType${id}" value="URL" onclick="return displayUrlForm('docForm${id}', ${id});" /></span>
                            </div>
                            <div id="docForm${id}" class="indentDiv">
                                DOI:&nbsp;<input type="text" name="docUri${id}" size="30" id="docUri${id}" value="${uri}" />
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="docSubmitForm" id="docSubmitForm${id}">
                            <div>
                                <a href="javascript:;" title="Remove this piece of documentation" onclick="removeDoc('docSubmitForm${id}')"><img src="${initParam.www}img/Delete.gif" alt="Delete logo" height="16" width="16" /></a>&nbsp;<span id="docTypeBox${id}">Type: PMID: <input type="radio" name="docType${id}" value="PMID" onclick="return displayPubMedForm2('docForm${id}', ${id}, '${uri}');"  checked="checked" />&nbsp;DOI: <input type="radio" name="docType${id}" value="DOI" onclick="return displayDoiForm('docForm${id}', ${id});" />&nbsp;Physical Location: <input type="radio" name="docType${id}" value="URL" onclick="return displayUrlForm('docForm${id}', ${id});" /></span>
                            </div>
                            <div id="docForm${id}" class="indentDiv">
                                PMID:&nbsp;<input type="text" name="docUri${id}" size="20" id="docUri${id}" value="${uri}" />
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
                <c:set var="docsCounter" value="${id}" />
            </mir:resourcesEdit>
        </div>
        <input type="hidden" value="${docsCounter}" name ="docCounter" id="docCounter" />
        <input type="hidden" value="${docsCounter}" name ="docCounterReal" id="docCounterReal" />
        <div id="add_doc_id">
            <a href="javascript:;" title="Add a piece of documentation" onclick="addDoc();">[Add a piece of documentation]</a>
        </div>
    </fieldset>
    
    <div class="help_message" id="HelpDocumentation" style="display: none;">
        <p>
            Adding a piece of documentation is not mandatory, but can be useful.
        </p>
        <p>
            If you choose to add one (or several), you can either enter a full physical address (URL), a PubMed ID or a DOI.
        </p>
        <p>
            The second and third choices are recommended to avoid any problem about unreachable resources in the future (only relies on MIRIAM Registry).
        </p>
    </div>
    
    
    <fieldset id="form_restriction">
        <legend>Restriction(s)&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpRestriction').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
        
    	<%-- already stored restriction(s) --%>
    	<div id="restrictions_div">
	     	<c:if test="${! empty data.restrictions}">
		      	<ul id="restricted" style="border-bottom: 1px solid #BFBFBF; list-style-type: none; padding-left:5px;">
		      		<c:forEach var="restriction" items="${data.restrictions}">
		      			<li id="restriction_${restriction.id}">
		      				<a href="javascript:;" title="Remove this restriction" onclick="alert('Feature not yet implemented!');"><img src="${initParam.www}img/Delete.gif" alt="Delete logo" height="16" width="16" /></a>&nbsp;
		      				<c:out value="${restriction.type.category}" />:	<span style="font-style:italic;"><c:out value="${restriction.info}" /></span>
		<c:if test="${! empty restriction.link}">&nbsp; Cf. <a class="external" title="External link to further information." href="<c:out value="${restriction.link}" />"><c:out value="${restriction.linkText}" /></a></c:if>
		      			</li>
		      		</c:forEach>
		      	</ul>
	     	</c:if>
    	</div>
    	
        <div id="state_restriction">
            <table class="nothing">
                <tr>
            		<td>Category:&nbsp;</td>
            		<td>
            			<select id="res_cat" name="restrictionCategory">
            				<option value="0">Please select a category...</option>
			               	<c:forEach items="${restriction_types}" var="res">
			                   	<option value="${res.id}">${res.category}</option>
		                  	</c:forEach>
		               </select>
            		</td>
            	</tr>
	            <tr>
                	<td>
                    	Description:&nbsp;
                 	</td>
                 	<td>
                    	<textarea cols="76" rows="2" name="res_desc" id="res_desc"></textarea>
                 	</td>
             	</tr>
             	<tr>
                	<td>
                    	Link:&nbsp;
                 	</td>
                 	<td>
                    	<input type="text" name="res_link" size="66" id="res_link" />
                 	</td>
             	</tr>
             	<tr>
                	<td>
                    	Link description:&nbsp;
                 	</td>
                 	<td>
                    	<input type="text" name="res_link_desc" size="66" id="res_link_desc" />
                 	</td>
             	</tr>
         	</table>
         	<button type="button" class="submit_button" onclick="addRestriction('cura');" id="restriction_add_button">Add restriction</button>
        </div>
    </fieldset>
    
    <div class="help_message" id="HelpRestriction" style="display: none;">
        <p>
            List here all the restriction(s) on the usage of the data collection.
        </p>
        <p>
        	The "description" should contain explanations about the restriction. The "Link" is optional and allows you to provide a URL towards more information. If you provide a link, you need to also fill the "Link description", which is a very short description of where the link points to.
        </p>
        <p>
            Please use the "Add" and "Delete" buttons to save your modifications.
        </p>
    </div>
    
    
    <fieldset id="form_user">
        <legend>Submission's comment&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpSubmissionComment').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
        <div id="user_form">
            <textarea cols="100" rows="4" name="subComment" id="subCommentId" style="background-color: #C5CED5;" onfocus="this.blur();">${data.subInfo}</textarea>
        </div>
    </fieldset>
    
    <div class="help_message" id="HelpSubmissionComment" style="display: none;">
        <p>
            Here is the comment left by the person who submitted the data collection.
        </p>
    </div>
    
    
    <fieldset id="form_curator">
        <legend>Curator's comment&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpCuratorComment').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
        <div id="curator_form">
            <textarea cols="100" rows="10" name="curator" id="curatorId">${data.comment}</textarea>
        </div>
    </fieldset>
    
    <div class="help_message" id="HelpCuratorComment" style="display: none;">
        <p>
            You can enter in this field any comments you wish to make about this data collection. This comment will not be made public but every curators of MIRIAM Registry will be able to see (and modify it) it.
        </p>
    </div>
    
    
    <fieldset id="form_state">
        <legend>State&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpState').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
        <div id="state_form">
            <select id="stateId" name="state">
                <option value="Submitted"<c:if test="${data.state == 'Submitted'}"> selected="selected"</c:if>>Submitted</option>
                <option value="Curation"<c:if test="${data.state == 'Curation'}"> selected="selected"</c:if>>Curation</option>
                <option value="Pending"<c:if test="${data.state == 'Pending'}"> selected="selected"</c:if>>Pending</option>
                <option value="Canceled"<c:if test="${data.state == 'Canceled'}"> selected="selected"</c:if>>Canceled</option>
                <c:if test="${data.state == 'Published'}">
                	<option value="Published" selected="selected">Published</option>
                </c:if>
            </select>
            &nbsp;&nbsp;Current state: <em>${data.state}</em>
        </div>
    </fieldset>
    
    <div class="help_message" id="HelpState" style="display: none;">
        <p>
            State of the data collection in the curation pipeline:
        </p>
        <ul>
            <li><b>Submitted</b>: an anonymous user (not logged) has submitted a new data collection</li>
            <li><b>Curation</b>: a curator started to work on a previously submitted data collection</li>
            <li><b>Published</b>: the data collection has been curated and has now moved to the public website</li>
            <li><b>Pending</b>: the data collection cannot be published as it is now for a specific reason (this reason should be given in comment)</li>
            <li><b>Canceled</b>: the data collection has been canceled (will never be published) for a specific reason (explain in comment)</li>
        </ul>
    </div>
	
	
	<div class="grid_12 alpha bottom_links_left">
		<input type="submit" value="Update" class="submit_button" />		
	</div>
	<div class="grid_12 omega bottom_links_right">
    	<a href="<c:url value='mdb?section=curation&amp;type=${data.state}' />" title="Go back to data collections in state '${data.state}'" class="icon icon-functional" data-icon="e">Go back to data collections in state '${data.state}'</a>
	</div>

	<%-- <input type="hidden" value="<%= session.getId() %>" name="session" id="session" />  --%>
	<input type="hidden" value="<c:out value="${f:length(data.restrictions)}" />" id="nb_restrictions" />
	
</form>


<script type="text/javascript">
<!--
      document.forms['submission_form'].reset();
      razEdit();
//-->
</script>


<%-- common footer --%>
<jsp:include page="template_footer-with_jquery.jsp" />
