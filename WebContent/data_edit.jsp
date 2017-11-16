<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20140312
  @copyright BioModels.net, EMBL-EBI
  
  MIRIAM Registry: displays the complete information about a given data collection in edit mode.

--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<%-- javascript functions for dynamic forms --%>
<script type="text/javascript" src="${initParam.www}js/MiriamDynamicForms.js"></script>
<%--<script type="text/javascript" src="js/MiriamDynamicForms.js"></script>--%>


<h2 class="icon icon-functional" data-icon="e">Edit: <em>${data.name}</em></h2>
                    
<p>
	<c:choose>
        <%--User not logged in--%>
        <c:when test="${(sessionScope.login == null)}">
            The update of the data collection will not be directly available publicly after you press the <b>Update</b> button. A curator will first check it and if necessary correct and complete it before releasing the update.
     	</c:when>
		<%-- the user is logged in --%>
		<c:when test="${(sessionScope.login != null) && (!empty sessionScope.login) && (sessionScope.role == 'user')}">
            You may be authenticated to update some resources directly in the database after you press the <b>Update</b> button.
            For the other resources, a curator will first check it and if necessary correct and complete it before releasing the update.
     	</c:when>
		
		<%-- anonymous user  --%>
		<c:otherwise>
            You are an authenticated user: the data collection will be updated directly in the database after you press the <b>Update</b> button.
		</c:otherwise>
	</c:choose>
</p>

<form method="post" id="submission_form" action="dataTypeEditPart2" onsubmit="return validate_submission();">
	
	<fieldset id="form_name_synonyms">
		<legend>Name and synonyms&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpNames').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
		<table class="nothing">
			<tr>
				<td>
					Identifier:&nbsp;
				</td>
				<td>
					<input type="text" name="id" size="45" id="id" value="${data.id}" style="background-color: #C5CED5;" onfocus="this.blur();" readonly="readonly" />
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
			You can add several synonyms. That can be useful if the name is an acronym or can be summarised with an acronym (for example: "Gene Ontology" and "GO").
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
					<textarea cols="70" rows="3" name="def" id="def_id">${data.definition}</textarea>
				</td>
			</tr>
			<tr>
				<td>
					Identifier pattern:&nbsp;
				</td>
				<td>
					<textarea cols="70" rows="2" name="pattern" id="pattern_id">${data.regexp}</textarea>
				</td>
			</tr>
		</table>
	</fieldset>
	
	<div class="help_message" id="HelpDef" style="display: none;">
		<p>
			The field "Definition" must be filled with one or two of sentences explaining what kind of elements is stored in the data collection.
		</p>
		<p>
			The field "Identifier pattern" is the pattern (or PERL-style regular expression) of the identifiers used by the data collection (for example: "^GO:\d{7}$" for Gene Ontology).
		</p>
	</div>
	
	<fieldset id="form_uris">
		<legend>URIs&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpURIs').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
		<table class="nothing">
			<tr>
				<td>
					MIRIAM URN:&nbsp;
				</td>
				<td>
					<input type="text" name="urn" size="45" id="urn_id" value="${data.URN}" />
				</td>
                <td>
                    <input type="hidden" name="url" size="45" id="url_id" value="${data.URL}" />
                </td>
			</tr>
		</table>


        <br/>


        <div id="uri_id">
            <c:set var="uriCounter" value="0" />
            <c:forEach var="uric" items="${data.orderedUris}" varStatus="uriCount">
                <div id="uri${uriCount.count}Div">
                    URI:&nbsp; <input name="uriVal${uriCount.count}" size="45" type="text" value="${uric.value}" readonly="readonly"/> &nbsp;
                    Convert prefix:&nbsp; <input name="uriCon${uriCount.count}" size="45" type="text" value="${uric.convertPrefix}" /> &nbsp;
                    <c:choose>
                        <c:when test="${uric.deprecated == '1' }">
                            Deprecated:&nbsp; <input name="uriDep${uriCount.count}" type="checkbox" checked="checked" /> &nbsp;
                        </c:when>
                        <c:when test="${uric.deprecated == '2' }">
                            Deprecated:&nbsp; <input name="uriDep${uriCount.count}" type="checkbox"/> &nbsp;
                        </c:when>
                    </c:choose>

                </div>
                <c:set var="uriCounter" value="${uriCount.count}" />
            </c:forEach>
        </div>
        <input type="hidden" value="${uriCounter}" name="uriCounter" id="uriCounter" />
        <input type="hidden" value="${uriCounter}" name="uriCounterReal" id="uriCounterReal" />

        <div id="add_uri_id">
            <a href="javascript:;" title="Add a URI" onclick="addURI();">[Add a URI]</a>
        </div>


	</fieldset>

	<div class="help_message" id="HelpURIs" style="display: none;">
		<p>
            The URI is a unique string of characters used to unambiguously identify the data collection (for example: "urn:miriam:uniprot" for UniProt). The only official URI takes a URN form.
        </p>
        <p>
            You can add different URI schemes, which can be URLs or URNs. If they are URLs, keep in mind that they don't necessarily need to be valid physical addresses. They are only used as identifiers not as physical locations on Internet!
        </p>
        <p>
            The "Convert prefix" field contains part of the URI that is needed to convert between URIs.
        </p>
	</div>

	<fieldset id="form_resources">
		<legend>Resources&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpResources').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
		<div id="resources_id">
			<mir:resourcesBrowse data="${data.resources}" edit="true">
				<div id="resources${id}Div" class="edit_resource">
					<input type="hidden" value="${resourceId}" name="resourceId${id}" id="resourceId${id}" />
					<input type="hidden" value="${obsolete}" name="obsolete${id}" id="obsolete${id}" />
                    <br>
                    <c:choose>
                        <c:when test="${(sessionScope.login != null) && (!empty sessionScope.login) && (sessionScope.role == 'user') && (ownership_status=='1')}">
                            <div style="background-color: #EEEEEE">
                                <b>You are authorised to edit this record.</b>
                                <br/><br/>
                        </c:when>
                        <c:otherwise>
                            <div>
                        </c:otherwise>
                    </c:choose>
<%--                    <c:if test="${(sessionScope.login != null) && (!empty sessionScope.login) && (sessionScope.role == 'user') && (ownership_status=='1')}">
                        <b>You are authorised to edit this record.</b>
                    </c:if>--%>
                    <table class="nothing">
						<tr>
							<td rowspan="3" class="text_bottom">
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
								<input type="text" name="dataEntryPrefix${id}" size="55" id="depId${id}" value="${prefix}" />
							</td>
							<td>
								<b>$id</b>
							</td>
							<td>
								<input type="text" name="dataEntrySuffix${id}" size="20" id="desId${id}" value="${suffix}" />
							</td>
						</tr>
                        <tr>
                            <td>
                                Convert prefix:&nbsp;
                            </td>
                            <td colspan="3">
                                <input type="text" name="convert_prefix${id}" size="55" id="cpId${id}" value="${convert_prefix}" />
                            </td>
                        </tr>
						<tr>
							<td>
								Example of identifier:&nbsp;
							</td>
							<td>
								<input type="text" name="dataExample${id}" size="55" id="xplId${id}" value="${example}" />
							</td>
                            <td>
                                &nbsp;[<a class="external" href="#" onclick="generateExampleLink('depId${id}', 'desId${id}', 'xplId${id}');return false;" title="Link generated using the provided information (opens a new window)">Test link</a>]
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
								<input type="text" name="dataResource${id}" size="55" id="drId${id}" value="${base}" />
							</td>
						</tr>
						<tr>
							<td rowspan="3" class="text_top">
								<a href="javascript:;" title="Remove this resource" onclick="removeResource('resources${id}Div')"><img src="${initParam.www}img/Delete.gif" alt="Delete logo" height="16" width="16" /></a>
							</td>
							<td>
								Description:&nbsp;
							</td>
							<td>
								<input type="text" name="information${id}" size="55" id="infoId${id}" value="${info}" />
							</td>
							<td>
								&nbsp;
							</td>
							<td>
								<c:choose>
									<c:when test="${primary == true}">
										Primary: <input type="radio" name="primaryResource" value="${resourceId}" checked="checked" />
									</c:when>
									<c:otherwise>
										Primary: <input type="radio" name="primaryResource" value="${resourceId}" />
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr>
							<td>
								Institution:&nbsp;
							</td>
							<td colspan="3">
								<input type="text" name="institution${id}" size="55" id="instituteId${id}" value="${institution}" />
							</td>
						</tr>
						<tr>
							<td>
								Country:&nbsp;
							</td>
							<td colspan="3">
								<input type="text" name="country${id}" size="55" id="countryId${id}" value="${location}" />
							</td>
						</tr>
					</table>
                    <p></p>
                    <div id="format_id_${id}">
                           <c:set var="formatCounter" value="0" />
                           <c:forEach var="formatc" items="${formatList}" varStatus="formatCount">
                               <div id="format${id}${formatCount.count}Div">

                                   URI:&nbsp; <input name="formatPre${id}${formatCount.count}" size="45" type="text" id="formatPreId${id}${formatCount.count}" value="${formatc.urlPrefix}" /> &nbsp;
                                   $id:&nbsp; <input name="formatSuf${id}${formatCount.count}" size="20" type="text" id="formatSufId${id}${formatCount.count}" value="${formatc.urlSuffix}" /> &nbsp;
                                   <select name="formatType${id}${formatCount.count}">
                                       <c:forEach var="mediatype" items="${data.mimeTypeList}">
                                           <c:choose>
                                               <c:when test="${formatc.mimeType.id == mediatype.id }">
                                                   <option value="${mediatype.id}" selected>${mediatype.displayText}</option>
                                               </c:when>
                                               <c:otherwise>
                                                   <option value="${mediatype.id}">${mediatype.displayText}</option>
                                               </c:otherwise>
                                           </c:choose>
                                       </c:forEach>
                                   </select>
                                   <c:choose>
                                       <c:when test="${formatc.deprecated == '1' }">
                                           &nbsp;&nbsp;Deprecated:&nbsp; <input name="formatDep${id}${formatCount.count}" type="checkbox" checked="checked" /> &nbsp;
                                       </c:when>
                                       <c:otherwise>
                                           &nbsp;&nbsp;Deprecated:&nbsp; <input name="formatDep${id}${formatCount.count}" type="checkbox"/> &nbsp;
                                       </c:otherwise>
                                   </c:choose>
                                   &nbsp;[<a class="external" href="#" onclick="generateExampleLink('formatPreId${id}${formatCount.count}', 'formatSufId${id}${formatCount.count}', 'xplId${id}');return false;" title="Link generated using the provided information (opens a new window)">Test link</a>]

                               </div>
                               <c:set var="formatCounter" value="${formatCount.count}" />
                           </c:forEach>
                       </div>

                       <input type="hidden" value="${formatCounter}" name="formatCounter${id}" id="formatCounter${id}" />
                       <input type="hidden" value="${formatCounter}" name="formatCounterReal${id}" id="formatCounterReal${id}" />

                       <div id="add_format_id_${id}">
                           <a href="javascript:;" title="Add a format" onclick="addFormat(${id});">[Add a format]</a>
                       </div>

                       </div>
				</div>

				<c:if test="${end == 'true'}">
					</div>
					<input type="hidden" value="${id}" name="resourcesCounter" id="resourcesCounter" />
					<input type="hidden" value="${id}" name="resourcesCounterReal" id="resourcesCounterReal" />
				</c:if>

        <br>
			</mir:resourcesBrowse>
        <%--creating a mimetypelist--%>
        <c:set var="mediaCounter" value="0" />
        <c:forEach var="mediatype" items="${data.mimeTypeList}" varStatus="mediaCount">
            <input type="hidden" value="${mediatype.id}_${mediatype.displayText}" name="mimeTypeList${mediaCount.count}" id="mimeTypeList${mediaCount.count}" />
            <c:set var="mediaCounter" value="${mediaCount.count}" />
        </c:forEach>
        <input type="hidden" value="${mediaCounter}" name="mediaCounter" id="mediaCounter" />

<%--        <c:forEach var="mediatype" items="${data.mimeTypeList}" >
            <input type="hidden" value="${mediatype.id}_${mediatype.displayText}" name="mimeTypeList" id="mimeTypeList" />
        </c:forEach>--%>


		<div id="add_resources_id">
			<a href="javascript:;" title="Add a resource" onclick="addResource();">[Add a resource]</a>
		</div>
	</fieldset>
	
	<div class="help_message" id="HelpResources" style="display: none;">
		<p>
			To access (via a web browser or a piece of software) a data collection, you need to enter, at least, one resource.
		</p>
		<p>
			The "Access URL" field is divided into three parts (one of them cannot be edited): this is the physical address used to access a precise element stored by the data collection. The field in the middle ($id) stands for the identifier of an element (it must comply with the pattern entered previously).
		</p>
        <p>
      	    The "Convert prefix" field contains part of the URI that is needed to convert between URIs.
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
			The second and third choices are recommended to avoid any problem about unreachable resources in the future (this relies on the MIRIAM Registry).
		</p>
	</div>
	
	
	<c:if test="${sessionScope.login != null && (sessionScope.role != 'user')}">
        <fieldset id="form_restriction">
        	<%-- already stored restriction(s) --%>
        	<div id="restrictions_div">
         	<c:if test="${! empty data.restrictions}">
          	<ul id="restricted" style="border-bottom: 1px solid #BFBFBF;">
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
        	
            <legend>Restriction(s)&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpRestriction').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
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
                     <td>
                     	<button type="button" class="submit_button" onclick="addRestriction('publ');" id="restriction_add_button">Add</button>
                     </td>
                 </tr>
             </table>
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
	</c:if>
	
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
	
	<c:if test="${(sessionScope.login == null)}">
		<p>
			Please use the following field to give some more information about this submission and yourself. Provide an email address if you wish to be kept informed about your contribution. Thank you.
		</p>
		<fieldset id="form_user">
			<legend>User information&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpUser').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
			<div id="user_form">
				<textarea cols="80" rows="4" name="user" id="userId"></textarea>
			</div>
		</fieldset>
		
		<div class="help_message" id="HelpUser" style="display: none;">
			<p>
				You can enter in this field your name, email address, group, institution, and any other information in order to explain why you would like to modify this data collection in MIRIAM Registry.
			</p>
		</div>
	</c:if>
	
	
	<div class="grid_12 alpha bottom_links_left">
		<input type="reset" value="Reset" onclick="window.location.reload()" class="reset_button" />
		<input type="submit" value="Update!" class="submit_button" />
	</div>
	<div class="grid_12 omega bottom_links_right">
    	<a href="<c:url value='/collections/' />" title="Return to the list of data collections" class="icon icon-functional" data-icon="&lt;">Go back to the list of data collections</a>
	</div>
	
	<%-- whether the data collection is obsolete or not --%>
	<input type="hidden" value="${data.obsolete}" name ="dataCollectionObsolete" id="dataCollectionObsolete" />
	
	<script type="text/javascript">
	<!--
		document.forms['submission_form'].reset();
		razEdit();
	//-->
	</script>
	
</form>


<%-- common footer --%>
<jsp:include page="template_footer-with_jquery.jsp" />
