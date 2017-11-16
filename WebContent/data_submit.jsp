<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20140305
  @copyright BioModels.net, EMBL-EBI
  
  MIRIAM Registry: displays the form for submitting a new data collection.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />


<%-- javascript functions for dynamic forms --%>
<script type="text/javascript" src="${initParam.www}js/MiriamDynamicForms.js"></script>


<h2 class="icon icon-functional" data-icon="D">Submit a new data collection</h2>

<p>
	Please fill this form in order to submit a new data collection to the Registry. Alternatively, you can <a href="<c:url value='/mdb?section=contribute#team' />" title="Contact page">contact us</a> with your query.
</p>

<c:choose>
	<%-- the user is logged in --%>
	<c:when test="${sessionScope.login != null}">
		<p>
			As you are <b>logged in</b>, the new data collection will be added directly in the database after you press the <b>Submit</b> button. You can choose to directly publish it or store it in the curation's pipeline.
		</p>
	</c:when>
	
	<%-- anonymous user  --%>
	<c:otherwise>
		<p>
			You <b>do not</b> need to fill all the fields, you can just enter the information you have. 
		</p>
		<p>
			The new data collection will not be directly publicly available after you pressed the <b>Submit</b> button. A curator will first check it and if necessary correct and complete it before publishing it.
		</p>
	</c:otherwise>
</c:choose>

<h4>Help</h4>
<p>
	You can <a href="javascript:;" title="Display or hide all help messages" onclick="$('.help_message').toggle();">display or hide</a> all help messages.
</p>
<p>
	Moreover, you can display each individual help messages by clicking on the <span class="icon icon-generic" data-icon="?">button</span> located in the title of each section.
</p>


<h3>Information about the new data collection</h3>

<c:choose>
	<%-- the user is logged in --%>
	<c:when test="${sessionScope.login != null}">
		<form method="post" id="submission_form" action="addDataType" onsubmit="return validate_submission();">
	</c:when>
	
	<%-- anonymous user  --%>
	<c:otherwise>
		<form method="post" id="submission_form" action="addDataType">
	</c:otherwise>
</c:choose>

	<p>
		First you need to enter the name of the data collection you want to add to the database. After you can add as many synonym(s) as you want.
	</p>
	
	<fieldset id="form_name_synonyms">
		<legend>Name and synonyms&nbsp;<a onclick="$('#HelpNames').toggle();" title="Help" href="javascript:;" class="icon icon-generic" data-icon="?"></a></legend>
		<table class="nothing">
			<tr>
				<td>
					Primary name:&nbsp;
				</td>
				<td>
					<input type="text" name="name" size="45" id="nameId" />
				</td>
			</tr>
		</table>
		<input type="hidden" value="0" name="synonymsCounter" id="synonymsCounter" />
		<input type="hidden" value="0" name="synonymsCountReal" id="synonymsCounterReal" />
		<div id="synonyms_id"></div>
		<div id="add_synonym_id">
			<a href="javascript:;" title="Add a synonym" onclick="addSynonym();">[Add a synonym]</a>
		</div>
	</fieldset>
	
	<div class="help_message" id="HelpNames" style="display: none;">
		<p>
			The field "Primary name" is mandatory and must be the official name of the data collection (for example: "Gene Ontology" or "UniProt").
		</p>
		<p>
			You can add several synonyms. That can be useful if the name is an acronym or can be summarised with an acronym (for example: "Gene Ontology" and "GO").
		</p>
	</div>
	
	<p>
		Here is some information about the data collection: definition and regular expression (<i>i.e.</i> pattern for identifiers of elements, following the PERL style).
	</p>
	
	<fieldset id="form_def_pattern">
		<legend>Definition and pattern&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpDef').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
		<table class="nothing">
			<tr>
				<td>
					Definition:&nbsp;
				</td>
				<td>
					<textarea cols="70" rows="3" name="def" id="defId" onfocus="javascript:this.select();">Enter definition here...</textarea>
				</td>
			</tr>
			<tr>
				<td>
					Identifier pattern:&nbsp;
				</td>
				<td>
					<textarea cols="70" rows="2" name="pattern" id="patternId" onfocus="javascript:this.select();">Enter Identifier pattern here...</textarea>
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
	
	<p>
		In this part, you need to suggest a <i>namespace</i> for this data collection. The namespace is a short string of characters identifying the data collection in URIs. If you are aware of an older forms still in use, you can add deprecated ones.
	</p>
	
	<fieldset id="form_uris">
		<legend>URIs&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpURIs').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
		<table class="nothing">
			<tr>
				<td>
					<c:choose>
						<%-- the user is logged in --%>
						<c:when test="${sessionScope.login != null}">
							MIRIAM URN:&nbsp;
						</c:when>
						
						<%-- anonymous user  --%>
						<c:otherwise>
							Suggested namespace:&nbsp;
						</c:otherwise>
					</c:choose>
					
				</td>
				<td>
					<input type="text" name="urn" size="45" id="urn_id" />
				</td>
			</tr>
		</table>
		<input type="hidden" value="0" name ="deprecatedCounter" id="deprecatedCounter" />
		<input type="hidden" value="0" name ="deprecatedCountReal" id="deprecatedCounterReal" />
		<div id="deprecated_id"></div>
		<div id="add_deprecated_id">
			<a href="javascript:;" title="Add a deprecated namespace or URI" onclick="addDeprecated();">[Add a deprecated Namespace or URI]</a>
		</div>
	</fieldset>
	
	<div class="help_message" id="HelpURIs" style="display: none;">
		<p>
			A namespace is a unique string of characters used to unambiguously identify a data collection (for example: "obo.go" for Gene Ontology). The namespace is used when generating URIs for entities provided by the data collection.
		</p>
		<p>
			You can also add several deprecated namespaces or URIs.
		</p>
	</div>
	
	<p>
		Here, you need to add all the physical addresses of the resources where one can access the information provided by the data collection.
	</p>
	
	<fieldset id="form_resources">
		<legend>Resources&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpResources').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
		<input type="hidden" value="1" name ="resourcesCounter" id="resourcesCounter" />
		<input type="hidden" value="1" name ="resourcesCounterReal" id="resourcesCounterReal" />
		<div id="resources_id">
			<div id="resources1Div">
				<table class="nothing">
					<tr>
						<td rowspan="3" class="text_bottom">
							<b>#1:&nbsp;</b>
						</td>
						<td>
							Access URL:&nbsp;
						</td>
						<td>
							<input type="text" name="dataEntryPrefix1" size="34" id="depId1" />
						</td>
						<td>
							<b>$id</b>
						</td>
						<td>
							<input type="text" name="dataEntrySuffix1" size="15" id="desId1" />
						</td>
					</tr>
					<tr>
						<td>
							Example of identifier:&nbsp;
						</td>
						<td>
							<input type="text" name="dataExample1" size="30" id="xplId1" />
						</td>
					</tr>
					<tr>
						<td>
							Website:&nbsp;
						</td>
						<td colspan="3">
							<input type="text" name="dataResource1" size="45" id="drId1" />
						</td>
					</tr>
					<tr>
						<td rowspan="3" class="text_top">
							&nbsp;
						</td>
						<td>
							Description:&nbsp;
						</td>
						<td colspan="3">
							<input type="text" name="information1" size="55" id="infoId1" />
							<!-- <textarea cols="55" rows="2" name="def" id="def_id"></textarea> -->
						</td>
					</tr>
					<tr>
						<td>
							Institution:&nbsp;
						</td>
						<td colspan="3">
							<input type="text" name="institution1" size="45" id="instituteId1" />
						</td>
					</tr>
					<tr>
						<td>
							Country:&nbsp;
						</td>
						<td colspan="3">
							<input type="text" name="country1" size="30" id="countryId1" />
						</td>
					</tr>
				</table>
			</div>
		</div>
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
			The "Example of identifier" field should contain an identifier used by this resource, in order to display an example of usage to the user.
		</p>
		<p>
			The "Website" field must contains a physical link to the main page of the resource.
		</p>
		<p>
			The three other fields give more information about the institution managing the resource (one sentence of description, the name of the institution, and the country).
		</p>
	</div>
	
	<p>
		Finally, if you know some pieces of documentation about the data collection (publication, paper, chapter, web sites, ...) you can put them here.
	</p>
	
	<fieldset id="form_docs">
		<legend>Documentation&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpDocumentation').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
		<input type="hidden" value="0" name ="docCounter" id="docCounter" />
		<input type="hidden" value="0" name ="docCounterReal" id="docCounterReal" />
		
		<div id="doc_id"></div>
		<div id="add_doc_id">
			<a href="javascript:;" title="Add a piece of documentation" onclick="addDoc();">[Add a piece of documentation]</a>
		</div>
	</fieldset>
	
	<div class="help_message" id="HelpDocumentation" style="display: none;">
		<p>
			Adding a piece of documentation is not mandatory, but can be useful.
		</p>
		<p>
			If you choose to add one, you can enter either its physical address (the URL you can put in the address bar of a Web browser), its PubMed Identifier (PMID) or its Digital Object Identifier (DOI).
		</p>
		<p>
			The second and third choices are recommended to avoid any problem about unreachable resources in the future (this relies on the MIRIAM Registry).
		</p>
	</div>
	
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
	
	<c:if test="${(sessionScope.login == null) && (curator == null)}">
		<p>
			Please use the following field to give some more information about this submission and yourself.
		</p>
		<fieldset id="form_user">
			<legend>User information&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpUser').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
			<div id="user_form">
				<textarea cols="80" rows="4" name="user" id="userId"></textarea>
			</div>
		</fieldset>
		
		<div class="help_message" id="HelpUser" style="display: none;">
			<p>
				You can enter in this field your name, email address, group, institution, and any other information in order to explain why you would like to add this data collection to the MIRIAM Registry.
			</p>
			<p>
				You can as well give some suggested tags for this data collection (cf. the <a href="<c:url value='/tags/' />" title="List of tags">list of tags</a> currently used) and potential restrictions (due to licensing, data access, ...).
			</p>
		</div>
	</c:if>
	
          <c:if test="${(sessionScope.login != null) || (curator != null)}">
              <fieldset id="form_publi">
                  <legend>Publication&nbsp;<a href="javascript:;" title="Help" onclick="$('#HelpPubli').toggle();" class="icon icon-generic" data-icon="?"></a></legend>
                  <table class="nothing">
                      <tr>
                          <td>Instant publication:&nbsp;</td>
                          <td><input name="publi_option" value="publication" type="radio" /></td>
                      </tr>
                      <tr>
                          <td>Curation pipeline:&nbsp;</td>
                          <td><input name="publi_option" value="curation" type="radio" checked="checked" /></td>
                      </tr>
                  </table>
              </fieldset>
              
              <div class="help_message" id="HelpPubli" style="display: none;">
                  <p>
                      If you wish to make this data collection publicly available right now, please check the information provided and select the <em>Instant publication</em> option.
                  </p>
                  <p>
                      If you wish to submit this data collection to the curation pipeline, please select the <em>Curation pipeline</em> option.
                  </p>
              </div>
          </c:if>
	
	<p>
		<input type="submit" value="Submit" class="submit_button" />
	</p>

	<script type="text/javascript">
	<!--
		document.forms['submission_form'].reset();
		raz();
	//-->
	</script>
	
</form>

<h3>Warning</h3>

<p>
	You need to enable <em>Javascript</em> in your web browser in order to use this form.
</p>


<%-- common footer --%>
<jsp:include page="template_footer-with_jquery.jsp" />
