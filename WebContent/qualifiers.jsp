<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20100705
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Web Interface,
    'view' part of the application: displays dynamically the list of BioModels.net qualifiers + additional information.
  
--%><?xml version="1.0" encoding="utf-8"?>

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
                    
                    <h1>BioModels.net qualifiers</h1>
                    
                    <%-- displays which version of the app is running (not displayed for 'main') --%>
                    <m:displayVersion version="${initParam.version}" />
                    
                    <%-- displays the login box (if a user is logged) --%>
                    <m:loginBox login="${sessionScope.login}" />
                    
                    <%-- displays the message coming from the previous request (if any) --%>
                    <m:displayMessage message="${message}" />
                    
                    <script type="text/javascript" src="http://www.ebi.ac.uk/inc/js/iconboxslider.js"></script>
                    <div class="infoBox">
                        <div class="slidecontainer_open">
                            <div class="iconbox2heading">
                                <span class="headerToggle" id="stats"><img class="headerToggleImage" src="http://www.ebi.ac.uk/inc/images/minus.gif" alt="minus image" /></span>
                                <a href="<c:url value='/mdb?section=qualifiers' />" title="BioModels.net Qualifiers">Table of Contents</a>
                            </div> 
                            <div class="iconbox2contents" id="stats_content" style="overflow: visible; display: block;">
                                <ul>
                                    <li><a href="#introduction" title="Introduction">Introduction</a></li>
                                    <li><a href="#list" title="List of qualifiers">List of qualifiers</a></li>
                                    <li><a href="#download" title="Download">Download the list</a></li>
                                    <li><a href="#howto" title="How to use them">How to use them</a></li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    
                    
                    <%-- static content (intro) --%>
                    <c:import url="${initParam.www}qualifiers_intro.html" charEncoding="UTF-8" />
                    
                    <br id="list" />
                    
                    <c:forEach var="group" items="${qualifiers}">
                        <h2>${group.type}</h2>
                        <p>${group.definition}</p>
                        <p>If you use these qualifiers in a XML based format, please use the following namespace: <code>${group.namespace}</code>.</p>
                        <c:forEach var="qualifier" items="${group.qualifiers}">
                            <h3>${qualifier.name}</h3>
                            <p class="indentParagraph">${qualifier.definition}</p>
                        </c:forEach>
                    </c:forEach>
                    
                    
                    <h2 id="download">Download the qualifiers</h2>
                    <p>
                        If you need the list of BioModels.net qualifiers in a computer readable format, you can download the whole list in different formats. 
                    </p>
                    <ul>
                        <li><a href="<c:url value='/qualifiers/xml/' />" title="XML export of the BioModels.net qualifiers">XML</a> (this is generated on demand)</li>
                        <li><a href="${initParam.www}xml/BioModelsNetQualifiers.xsd" title="XML Schema of the BioModels.net qualifiers export">XML Schema</a></li>
                    </ul>
                    
                    <ul>
                        <li><a href="<c:url value='/qualifiers/rdf/' />" title="RDF/XML export of the BioModels.net qualifiers">RDF</a> <em style="padding-left:1em; color:red;">Beta</em></li>
                    </ul>
                    
                    
                    <%-- static content (howto) --%>
                    <c:import url="${initParam.www}qualifiers_howto.html" charEncoding="UTF-8" />
                    
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
