<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20111012
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Web Interface,
  	'view' part of the application: displays the query page, which uses Web Services in the backend.
--%><?xml version="1.0" encoding="utf-8"?>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="ajax" uri="http://ajaxtags.org/tags/ajax" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<meta name="description" content="European Bioinformatics Institute - Computational Neurobiology" />
	<meta name="author" content="Camille Laibe and Nicolas Le Novère" />
	<meta http-equiv="Content-Language" content="en-GB" />
	<meta http-equiv="Window-target" content="_top" />
	<meta name="no-email-collection" content="http://www.unspam.com/noemailcollection/" />
	<!-- ===================================== -->
	<!-- TemplateBeginEditable name="doctitle" -->
	<!-- ===================================== -->
	
	<title>MIRIAM Registry</title>
	<!-- ===================================== -->
	<!-- TemplateEndEditable -->
	<!-- ===================================== -->
	<link rel="stylesheet" href="http://www.ebi.ac.uk/inc/css/contents.css" type="text/css" />
	<link rel="stylesheet" href="http://www.ebi.ac.uk/inc/css/userstyles.css" type="text/css" />
	<script src="http://www.ebi.ac.uk/inc/js/contents.js" type="text/javascript"></script>
	<link rel="stylesheet" href="http://www.ebi.ac.uk/inc/css/sidebars.css" type="text/css" />
	<link rel="SHORTCUT ICON" href="http://www.ebi.ac.uk/bookmark.ico" />
	<!-- ===================================== -->
	<!-- TemplateBeginEditable name="head" -->
	<!-- ===================================== -->
	<!-- start meta tags, css , javascript here -->
	<!-- ===================================== -->
    
    <meta name="keywords" content="Nicolas Le Novère, Camille Laibe, EBI, EMBL, bioinformatics, software, databases, genomics, computational neurobiology, neuroinformatics, systems biology" />
	<link rel="stylesheet" type="text/css" media="screen" href="${initParam.www}style/MIRIAM.css" />
	<link rel="alternate" type="application/rss+xml" title="MIRIAM News Feed" href="${initParam.www}rss/MiriamNews.xml" />
    
    <script type="text/javascript" src="${initParam.www}js/prototype.js"></script>
	<script type="text/javascript" src="${initParam.www}js/scriptaculous.js"></script>
	<script type="text/javascript" src="${initParam.www}js/overlibmws.js"></script>
	<script type="text/javascript" src="${initParam.www}js/overlibmws_crossframe.js"></script>
	<script type="text/javascript" src="${initParam.www}js/overlibmws_iframe.js"></script>
	<script type="text/javascript" src="${initParam.www}js/overlibmws_hide.js"></script>
	<script type="text/javascript" src="${initParam.www}js/overlibmws_shadow.js"></script>
	<script type="text/javascript" src="${initParam.www}js/ajaxtags.js"></script>
	<script type="text/javascript" src="${initParam.www}js/ajaxtags_controls.js"></script>
	<script type="text/javascript" src="${initParam.www}js/ajaxtags_parser.js"></script>
    
    
    <script type="text/javascript">
        // display (nicely) the waiting message
        function MsgWait()
		{
			Effect.BlindDown('getRequestResultMsg');
 		}
 		// clear (nicely) the waiting message
 		function MsgWaitEnd()
		{
			setTimeout("Effect.DropOut('getRequestResultMsg');", 800);
		}
    </script>
    
	<!-- ===================================== -->
	<!-- end meta tags, css , javascript here -->
	<!-- ===================================== -->
	<!-- TemplateEndEditable -->
	<!-- ===================================== -->
    
</head>

<!-- WHAT IS THIS SHIT? -->
<body onload="if(navigator.userAgent.indexOf('MSIE') != -1) {document.getElementById('head').allowTransparency = true;}">
  <!-- ===================================== -->
  <div class="headerdiv" id="headerdiv" style="position: absolute; z-index: 1;">
    <iframe src="/inc/head.html" name="head" id="head" marginwidth="0" marginheight="0" style="position: absolute; z-index: 1; height: 57px;" frameborder="0" scrolling="no" width="100%"></iframe>
  </div>
  <!-- ===================================== -->


  <div class="contents" id="contents">
    <table class="contentspane" id="contentspane" summary="The main content pane of the page" style="width: 100%">
      <tr>
        <td class="leftmargin">
          <img src="http://www.ebi.ac.uk/inc/images/spacer.gif" class="spacer" alt="spacer" />
        </td>
        <!-- ===================================== -->

        <td class="leftmenucell" id="leftmenucell">
          <div class="leftmenu" id="leftmenu" style="width: 145px; visibility: visible; display: block;">
            <!-- InstanceBeginEditable name="leftnav" -->

            <!-- start left menu here  -->
            <div id="leftmenu2">
            	<mir:menuSelect user="${sessionScope.role}">
                    <c:import url="${initParam.www}${menu}.html" charEncoding="UTF-8" />
                </mir:menuSelect>
            </div>
            <!-- end left menu here -->

            <!-- ===================================== -->
            <!-- TemplateEndEditable -->
            <script type="text/javascript" src="http://www.ebi.ac.uk/inc/js/sidebars.js"></script>
            <img src="http://www.ebi.ac.uk/inc/images/spacer.gif" class="spacer" alt="spacer" />
          </div>
        </td>
        <!-- TemplateBeginEditable name="contents" -->
        <!-- ===================================== -->
        <!-- start contents here -->
        <!-- ===================================== -->

        <td class="contentsarea" id="contentsarea">
          <div class="breadcrumbs">
            <a href="http://www.ebi.ac.uk/" class="firstbreadcrumb">EBI</a>
            <a href="http://www.ebi.ac.uk/Groups/">Groups</a>
            <a href="http://www.ebi.ac.uk/compneur/">Computational Neurobiology</a>
            <a href="http://www.ebi.ac.uk/compneur/research.html">Research</a>
            <a href="@MIR_DYNAMIC_URL@/">MIRIAM Registry</a>
          </div>
          
          <h1>Web Services Demonstration</h1>
		  
			<%-- displays which version of the app is running (not displayed for 'main') --%>
            <m:displayVersion version="${initParam.version}" />
            
			<%-- displays the login box (if a user is logged) --%>
            <m:loginBox login="${sessionScope.login}" />
			
			<c:import url="${initParam.www}request_intro.html" charEncoding="UTF-8" />
			
			<h2>Query</h2>
			
			<table class="simple" summary="list of all the available methods">
				<tr>
					<td>
						Choose one action from the list:&nbsp;
					</td>
					<td>
						<form method="get" action="mdb">
							<div>
								<input type="hidden" name="section" value="request" />
								<select name="request">
					  	            <option value="">Select one</option>
                                          <option value="getURI" <c:if test="${request == 'getURI'}">selected="selected"</c:if>>get the MIRIAM URI of an element</option>
                                          <option value="getLocations" <c:if test="${request == 'getLocations'}">selected="selected"</c:if>>get links to access an element</option>
                                          <option value="getDataTypeURI" <c:if test="${request == 'getDataTypeURI'}">selected="selected"</c:if>>get the MIRIAM URI of a data collection</option>
						            <option value="getDataTypeDef" <c:if test="${request == 'getDataTypeDef'}">selected="selected"</c:if>>get the definition of a data collection</option>
						            <option value="getDataTypePattern" <c:if test="${request == 'getDataTypePattern'}">selected="selected"</c:if>>get the regular expression of a data collection</option>
                                          <option value="checkRegExp" <c:if test="${request == 'checkRegExp'}">selected="selected"</c:if>>check identifier pattern</option>
				  	 		    </select>
				  	 		    <input type="submit" value="Go!" class="submit_button" />
			  	 		    </div>
			  	 	    </form>
				    </td>
		  	    </tr>
	  	    </table>
	  	    
			
			<div id="requestForm" class="request">
				<c:choose>
					<c:when test="${request == 'getDataTypeURI'}">
						<p>
							get the MIRIAM URI of a data collection:
						</p>
						<form id="getDataTypeURIForm" action="" onsubmit="getDataTypeURIButton.click(); return false;">
							<fieldset class="request">
								<legend><a href="<c:url value='/mdb?section=ws_help#ID_getDataTypeURI_2' />" title="Access to the documentation of this query">getDataTypeURI</a></legend>
								<label>data collection name</label>
								<input type="text" id="getDataTypeURIParam1" size="30" />
								<input type="button" value="Search" id="getDataTypeURIButton" class="submit_button" />
							</fieldset>
						</form>
						<ajax:htmlContent baseUrl="getDataTypeURI.do" source="getDataTypeURIButton" target="getRequestResult" parameters="getDataTypeURIParam1={getDataTypeURIParam1}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
					</c:when>
                          
					<c:when test="${request == 'getDataTypeDef'}">
						<p>
							Get the definition of a data collection:
						</p>
						<form id="getDataTypeDefForm" action="" onsubmit="getDataTypeDefButton.click(); return false;">
							<fieldset class="request">
								<legend><a href="<c:url value='/mdb?section=ws_help#ID_getDataTypeDef_1' />" title="Access to the documentation of this query">getDataTypeDef</a></legend>
								<label>data collection name or URI</label>
								<input type="text" id="getDataTypeDefParam" size="30" />
								<input type="button" value="Search" id="getDataTypeDefButton" class="submit_button" />
							</fieldset>
						</form>
						<ajax:htmlContent baseUrl="getDataTypeDef.do" source="getDataTypeDefButton" target="getRequestResult" parameters="getDataTypeDefParam={getDataTypeDefParam}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
					</c:when>
					
					<c:when test="${request == 'getURI'}">
						<p>
							Get the MIRIAM URI of an element or entity:
						</p>
						<form id="getURIForm" action="" onsubmit="getURIButton.click(); return false;">
							<fieldset class="request">
								<legend><a href="<c:url value='/mdb?section=ws_help#ID_getURI_2' />" title="Access to the documentation of this query">getURI</a></legend>
								<label>data collection name</label>
								<input type="text" id="getURIParam1" size="30" />
								<br />
								<label>element id</label>
								<input type="text" id="getURIParam2" size="30" />
								<input type="button" value="Search" id="getURIButton" class="submit_button" />
							</fieldset>
						</form>
						<ajax:htmlContent baseUrl="getURI.do" source="getURIButton" target="getRequestResult" parameters="getURIParam1={getURIParam1},getURIParam2={getURIParam2}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
					</c:when>
					
					<c:when test="${request == 'getLocations'}">
						<p>
							Get links to access an element:
						</p>
						<form id="getLocationsForm" action="" onsubmit="getLocationsButton.click(); return false;">
							<fieldset class="request">
								<legend><a href="<c:url value='/mdb?section=ws_help#ID_getLocations_2' />" title="Access to the documentation of this query">getLocations</a></legend>
								<label>MIRIAM URI</label>
								<input type="text" id="getLocationsParam1" size="30" value="urn:miriam:" />
								<input type="button" value="Search" id="getLocationsButton" class="submit_button" />
							</fieldset>
						</form>
						<ajax:htmlContent baseUrl="getLocations.do" source="getLocationsButton" target="getRequestResult" parameters="getLocationsParam1={getLocationsParam1}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
					</c:when>
					
					<c:when test="${request == 'getDataTypePattern'}">
						<p>
							Get the regular expression of a data collection:
						</p>
						<form id="getDataTypePatternForm" action="" onsubmit="getDataTypePatternButton.click(); return false;">
							<fieldset class="request">
								<legend><a href="<c:url value='/mdb?section=ws_help#ID_getDataTypePattern_1' />" title="Access to the documentation of this query">getDataTypePattern</a></legend>
								<label>data collection name or URI</label>
								<input type="text" id="getDataTypePatternParam" size="30" />
								<input type="button" value="Search" id="getDataTypePatternButton" class="submit_button" />
							</fieldset>
						</form>
						<ajax:htmlContent baseUrl="getDataTypePattern.do" source="getDataTypePatternButton" target="getRequestResult" parameters="getDataTypePatternParam={getDataTypePatternParam}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
					</c:when>
                          
                          <c:when test="${request == 'checkRegExp'}">
                              <p>
                                  Checks if an identifier belongs to a given data collection:
                              </p>
                              <form id="checkRegExpForm" action="" onsubmit="checkRegExpButton.click(); return false;">
                                  <fieldset class="request">
                                      <legend><a href="<c:url value='/mdb?section=ws_help#ID_checkRegExp_1' />" title="Access to the documentation of this query">checkRegExp</a></legend>
                                      <label>data collection name</label>
                                      <input type="text" id="checkRegExpParam1" size="30" />
                                      <br />
                                      <label>identifier to check</label>
                                      <input type="text" id="checkRegExpParam2" size="30" />
                                      <input type="button" value="Search" id="checkRegExpButton" class="submit_button" />
                                  </fieldset>
                              </form>
                              <ajax:htmlContent baseUrl="checkRegExp.do" source="checkRegExpButton" target="getRequestResult" parameters="checkRegExpParam1={checkRegExpParam1},checkRegExpParam2={checkRegExpParam2}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
                          </c:when>
					
					<c:otherwise>
						<p>Choose one of the queries from the list above...</p>
					</c:otherwise>
				</c:choose>
			</div>
			
			<!-- displays the result of the request -->
			<h2>Answer</h2>
			
			<div class="right">
				<div id="getRequestResult" class="answer"></div>
				<div id="getRequestResultMsg" class="answerStatus" style="display:none;">
					<p>
						<img alt="Indicator" src="${initParam.www}img/Throbber.gif" /> Searching...
					</p>
				</div>
			</div>
			
			<!-- displays an help message -->
			<h2>Help</h2>
			<div id="request_help">
				<p>
					Here is a sample summarising all the parameters and the expected result:
				</p>
				
				<br />
				
				<div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
				
				<c:choose>
					<c:when test="${request == 'getDataTypeURI'}">
						<table class="table_help" summary="usage example">
							<tr>
								<td class="help_element">Data collection name:</td><td class="help_value">uniprot</td>
							</tr>
							<tr>
								<td class="help_element">Result:</td><td class="help_result">urn:miriam:uniprot</td>
							</tr>
						</table>
					</c:when>
					
					<c:when test="${request == 'getDataTypeDef'}">
						<table class="table_help" summary="usage example">
							<tr>
								<td class="help_element">Data collection name or URI:</td><td class="help_value">biomodels</td>
							</tr>
							<tr>
								<td class="help_element">Result:</td><td class="help_result">BioModels Database is a data resource that allows biologists to store, search and retrieve published mathematical models of biological interests.</td>
							</tr>
						</table>
					</c:when>
					
					<c:when test="${request == 'getURI'}">
						<table class="table_help" summary="usage example">
							<tr>
								<td class="help_element">Data collection name:</td><td class="help_value">uniprot</td>
							</tr>
							<tr>
								<td class="help_element">Element id:</td><td class="help_value">P47757</td>
							</tr>
							<tr>
								<td class="help_element">Result:</td><td class="help_result">urn:miriam:uniprot:P47757</td>
							</tr>
						</table>
					</c:when>
                          
					<c:when test="${request == 'getDataTypePattern'}">
						<table class="table_help" summary="usage example">
							<tr>
								<td class="help_element">Data collection name or URI:</td><td class="help_value">uniprot</td>
							</tr>
							<tr>
								<td class="help_element">Result:</td><td class="help_result">^([A-N,R-Z][0-9][A-Z][A-Z, 0-9][A-Z, 0-9][0-9](_[\dA-Z]{1,5})?)$</td>
							</tr>
						</table>
					</c:when>
                          
                          <c:when test="${request == 'checkRegExp'}">
                              <table class="table_help" summary="usage example">
                                  <tr>
                                      <td class="help_element">Data collection name or URI:</td><td class="help_value">uniprot</td>
                                  </tr>
                                  <tr>
                                      <td class="help_element">identifier:</td><td class="help_value">P123456</td>
                                  </tr>
                                  <tr>
                                      <td class="help_element">Result:</td><td class="help_result">The identifier 'P123456' is not valid for the data collection 'uniprot'!</td>
                                  </tr>
                              </table>
                          </c:when>
                          
                          <c:when test="${request == 'getLocations'}">
                              <table class="table_help" summary="usage example">
                                  <tr>
                                      <td class="help_element">MIRIAM URI:</td><td class="help_value">urn:miriam:pubmed:16333295</td>
                                  </tr>
                                  <tr>
                                      <td class="help_element" rowspan="4">Result:</td><td class="help_result">http://www.ncbi.nlm.nih.gov/pubmed/16333295</td>
                                  </tr>
                                  <tr>
                                      <td class="help_result">http://srs.ebi.ac.uk/srsbin/cgi-bin/wgetz?-view+MedlineFull+[medline-PMID:16333295]</td>
                                  </tr>
                                  <tr>
                                      <td class="help_result">http://www.ebi.ac.uk/citexplore/citationDetails.do?dataSource=MED&amp;externalId=16333295</td>
                                  </tr>
                                  <tr>
                                      <td class="help_result">http://www.hubmed.org/display.cgi?uids=16333295</td>
                                  </tr>
                              </table>
                          </c:when>
                          
					<c:otherwise>
						<p>
							<i>the help information will be displayed after choosing one action from the list above...</i>
						</p>
					</c:otherwise>
				</c:choose>
				
				</div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
				
			</div>
		
			<!-- focus on the input field of the first parameter (if necessary) -->
			<script type="text/javascript">
				if (document.forms[1])
				{
					if (document.forms[1][1])
					{
						document.forms[1][1].focus();
					}
				}
			</script>
	
			<!-- end contents here -->

          <!-- InstanceEndEditable -->
          <img src="http://www.ebi.ac.uk/inc/images/spacer.gif" class="spacer" alt="spacer" />
        </td>
        <td class="rightmenucell" id="rightmenucell">
          <div class="rightmenu" id="rightmenu">
            <img src="http://www.ebi.ac.uk/inc/images/spacer.gif" class="spacer" alt="spacer" />
          </div>
        </td>
      </tr>
    </table>
    <table class="footerpane" id="footerpane" summary="The main footer pane of the page">
      <tr>
        <td colspan ="4" class="footerrow">
          <div class="footerdiv" id="footerdiv" style="z-index:2;">
            <iframe src="/inc/foot.html" name="foot" frameborder="0" marginwidth="0" marginheight="0" scrolling="no"  height="22" width="100%"  style="z-index:2;">
            </iframe>
          </div>
        </td>
      </tr>
    </table>
    <script src="http://www.ebi.ac.uk/inc/js/footer.js" type="text/javascript"></script>
  </div>
</body>
<!-- InstanceEnd -->
</html>
