<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20120306
  @copyright BioModels.net, EMBL-EBI
  
  MIRIAM Web Interface,
  	'view' part of the application: displays the extra information about a precise data collection
  	(annotation using a specific format, like SBML, CellML or BioPAX)  
  
  TODO
    Should be deprecated and removed: no more link point to this page (now displayed in /misc/ tab)
--%><?xml version="1.0" encoding="UTF-8" ?>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
					
					<%-- displays which version of the app is running (not displayed for 'main') --%>
                    <m:displayVersion version="${initParam.version}" />
					
					<%-- displays the login box (if a user is logged) --%>
                    <m:loginBox login="${sessionScope.login}" />
					
					<c:choose>
						<%-- the data collection exists --%>
						<c:when test="${data != null}">
							<h1>Data collection: <em>${name}</em></h1>
							
							<c:if test="${obsolete == true}">
                                <br />
                                <div class="message_display">
                                    <div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
                                    	<b>WARNING:</b> this data collection has been deprecated!
                                    	<br />
                                    	${replacementComment}
                                   		<c:if test="${replacementId != null}">
                                   			<br />
                                   			We recommend the usage of the following data collection instead: <a href="<c:url value='/collections/${replacementId}' />" title="Go to ${replacementName}">${replacementName}</a>.
                                   		</c:if>
                                    </div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
                                </div>
                                <br />
                            </c:if>
							
							<!-- tabs -->
							<div class="menutabs">
		                    	<a class="tab" href="<c:url value='/collections/${data.id}' />" title="Overview of the data collection">Overview</a>
		                        <a class="tab" href="<c:url value='/tags/${data.id}' />" title="Categorisation of the data collection (tags)">Categories</a>
		                        <a class="tab" href="<c:url value='/misc/${data.id}' />" title="Miscellaneous information about the data collection">Miscellaneous</a>
		                            <%--
		                            <a href="<c:url value='/usage/${data.id}' />" title="Examples of annotation using the data collection">Example Usage</a>
                                    <a href="<c:url value='/webservices/${data.id}' />" title="Web Services available for the data collection">Web Services</a>
                                    --%>
		                        </ul>
		                    </div>
							
							<c:choose>
								<%-- no annotation stored  --%>
								<c:when test="${(data.annotation == null) || (empty data.annotation)}">
									<p>
										Sorry, no example of annotation is stored for this data collection.
									</p>
									<p>
										You can contribute to MIRIAM Registry by adding or modifiying examples of annotation via the 'Edit' link at the bottom of this page. Thank you.
									</p>
								</c:when>
								
								<%-- some annotation exist --%>
								<c:otherwise>
									<p>
										Here are some possible annotations using this data collection. These are classified by format (like <a href="http://sbml.org/" title="SBML">SBML</a>, <a href="http://www.cellml.org/" title="CellML">CellML</a> or <a href="http://www.biopax.org/" title="BioPAX">BioPAX</a>) and show the element(s) (tags for XML based formats) which can have a MIRIAM URI -using the current data collection- to bring extra knowledge about the entity described.
									</p>
									<br />
									
									<c:forEach var="anno" items="${data.annotation}">
										<h3>${anno.format}</h3>
										
										<ul>
											<c:forEach var="tag" items="${anno.tags}">
												<li><a href="<c:url value='/usage/${tag.id}' />" title="Access to all the data collection(s) which can be used to annotate this element">${tag.name}</a> (${tag.info})</li>
											</c:forEach>
										</ul>
									</c:forEach>
									<br />
                                    <p>
                                        Annotation information for <em>MIRIAM Registry</em> purpose only. For complete details about an element of a specific format, please always refer to the official specifications.
                                    </p>
								</c:otherwise>
							</c:choose>
							
							<br />
							
							<div class="bottomSeparator"></div>
							<table width="100%" summary="table bottom links">
		                        <tr>
		                            <td align="left">
		                                <a href="<c:url value='/collections/' />" title="Return to the list of data collections"><img src="${initParam.www}img/prev.png" alt="Previous arrow icon" align="top" />&nbsp;Go back to the list of data collections</a>
		                            </td>
		                            <td align="right">
                                        <a href="<c:url value='/mdb?section=edit_anno&amp;data=${data.id}' />" title="Suggest modifications to the content of this page"><img src="${initParam.www}img/Edit.gif" alt="Edit icon" align="bottom" />&nbsp;Suggest modifications to these examples of annotation</a>
		                            </td>
		                        </tr>
		                    </table>
						</c:when>
						
						<%-- the data collection doesn't exist, SHOULD NEVER OCCURED: redirection towards the introduction page --%>
						<c:otherwise>
						    <h1>MIRIAM Registry</h1>
						    
							<p>
								Sorry, the requested data collection doesn't exist in the database...
							</p>
							<p>
                                You can go back to the <a href="<c:url value='collections' />" title="List of data collections">list of available data collections</a>.
							</p>
						</c:otherwise>
					</c:choose>
					
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
