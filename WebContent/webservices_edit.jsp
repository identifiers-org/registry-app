<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20120306
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Web Interface,
    'view' part of the application: displays web services, for edition purposes, provided for a given data collection.
    
    TODO:
    - add documentation links
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
    
    <%-- javascript functions for dynamic forms --%>
    <script type="text/javascript" src="${initParam.www}js/MiriamDynamicForms.js"></script>
    
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
                    
                    <h1>Web Services provided for: <em>${datatypeName}</em></h1>
                    
                    <%-- displays which version of the app is running (not displayed for 'main') --%>
                    <m:displayVersion version="${initParam.version}" />
                    
                    <%-- displays the login box (if a user is logged) --%>
                    <m:loginBox login="${sessionScope.login}" />
                    
                    <%-- displays the message coming from the previous request (if any) --%>
                    <m:displayMessage message="${message}" />
                    
                    <p>
                        Edition of the Web Services provided for the data collection: <a href="<c:url value='/collections/${datatypeId}' />" title="">${datatypeName}</a>
                    </p>
                    
                    <c:forEach var="services" items="${data}" varStatus="status1">
                        <h3>${services.key}</h3>
                        
                        <c:choose>
                            <c:when test="${(services.value == null) || (empty services.value)}">
                                <p>No ${services.key} service has been registered in MIRIAM Registry yet. If such a service does exist, please contribute by using the <i>Suggest modifications</i> link at the bottom right of this page.</p>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="service" items="${services.value}" varStatus="status2">
                                    <form method="post" id="ws_update_form_${status1.count}-${status2.count}" action="webServicesUpdate">
			                            <fieldset id="ws_update_field_${status1.count}-${status2.count}">
			                                <legend>Update existing Web Services #${status2.count}&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpWSExisting_${status1.count}-${status2.count}')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
			                            
			                                <table summary="ws_edit_table_${status1.count}-${status2.count}">
			                                    <tr>
			                                        <td>
			                                            Type:&nbsp;
			                                        </td>
			                                        <td>
			                                            <select name="type">
			                                                <c:forEach var="type" items="${types}">
			                                                    <option value="${type}" <c:if test="${service.type == type}"> selected="selected"</c:if>>${type}</option>
			                                                </c:forEach>
			                                            </select>
			                                        </td>
			                                    </tr>
			                                    <tr>
			                                        <td>
			                                            Provider:&nbsp;
			                                        </td>
			                                        <td>
			                                            <input type="text" name="provider" size="55" readonly="readonly" value="${service.resInfo} (${service.org})" style="background-color: rgb(238, 238, 238);" />
			                                        </td>
			                                    </tr>
			                                    <tr>
			                                        <td>
			                                            Description:&nbsp;
			                                        </td>
			                                        <td>
			                                            <textarea cols="71" rows="4" name="desc">${service.desc}</textarea>
			                                        </td>
			                                    </tr>
			                                    <tr>
			                                        <td>
			                                            Enpoint:&nbsp;
			                                        </td>
			                                        <td>
			                                            <input type="text" name="endpoint" size="55" value="${service.endpoint}" />
			                                        </td>
			                                    </tr>
			                                    <tr>
			                                        <td>
			                                            WSDL location:&nbsp;
			                                        </td>
			                                        <td>
			                                            <input type="text" name="wsdl" size="55" value="${service.wsdl}" />
			                                        </td>
			                                    </tr>
			                                    <tr>
			                                        <td>
			                                            Documentation:&nbsp;
			                                        </td>
			                                        <td>
			                                            <input type="text" name="doc" size="55" value="${service.doc}" />
			                                        </td>
			                                    </tr>
			                                </table>
			                                
			                                <input type="reset" value="Reset" class="reset_button" />
			                                <input type="submit" value="Update" class="submit_button" />
			                            </fieldset>
			                            
			                            <div class="help_message" id="HelpWSExisting_${status1.count}-${status2.count}" style="display: none;">
			                                <div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
			                                    <p>
			                                        Update one record of Web Services provided for the this data collection.
			                                    </p>
                                                <p>
                                                    "WSDL location" should only be provided for SOAP Web Services, <b>not</b> for REST ones.
                                                </p>
			                                </div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
			                            </div>
			                            
			                            <input type="hidden" value="${datatypeId}" name="dataTypeId" />
                                        <input type="hidden" value="${service.resId}" name="resourceId" />
                                        <input type="hidden" value="${service.id}" name="serviceId" />
			                        </form>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    
                    
                    <h2>New Web Services</h2>
                    
                    <form method="post" id="ws_create_form" action="webServicesCreate">
                        <fieldset id="ws_create_field">
                            <legend>Add new Web Services&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpWSCreate')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
                        
                            <table summary="ws_create_table">
                                <tr>
                                    <td>
                                        Type:&nbsp;
                                    </td>
                                    <td>
                                        <select name="type">
                                            <c:forEach var="type" items="${types}">
                                                <option value="${type}">${type}</option>
                                            </c:forEach>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        Provider:&nbsp;
                                    </td>
                                    <td>
                                        <select name="provider">
                                            <c:forEach var="res" items="${resources}">
                                                <option value="${res.id}">${res.info} (${res.institution})</option>
                                            </c:forEach>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        Description:&nbsp;
                                    </td>
                                    <td>
                                        <textarea cols="71" rows="4" name="desc"></textarea>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        Enpoint:&nbsp;
                                    </td>
                                    <td>
                                        <input type="text" name="endpoint" size="55" />
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        WSDL location:&nbsp;
                                    </td>
                                    <td>
                                        <input type="text" name="wsdl" size="55" />
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        Documentation:&nbsp;
                                    </td>
                                    <td>
                                        <input type="text" name="doc" size="55" />
                                    </td>
                                </tr>
                            </table>
                            
                            <input type="hidden" value="${datatypeId}" name="dataTypeId" />
                            <input type="reset" value="Reset" class="reset_button" />
                            <input type="submit" value="Create" class="submit_button" />
                        </fieldset>
                        
                        <div class="help_message" id="HelpWSCreate" style="display: none;">
                            <div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
                                <p>
                                    Adds new Web Services to this data collection.
                                </p>
                                <p>
                                    Each Web Services is provided by a specific resource which should be created prior to use this form.
                                </p>
                                <p>
                                    The enpoint, WSDL location and documentation fields should be URLs. 
                                </p>
                            </div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
                        </div>
                        
                        <input type="hidden" value="${datatypeId}" name="dataTypeId" />
                    </form>
                    
                    
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
