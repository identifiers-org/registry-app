<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20120306
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Web Interface,
    'view' part of the application: displays the extra information about a precise data collection
    (annotation using a specific format, like SBML, CellML or BioPAX)  
  
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
                    
                    <h1>Edit tags for: <em>${data.name}</em></h1>
                    
                    <%-- displays which version of the app is running (not displayed for 'main') --%>
                    <m:displayVersion version="${initParam.version}" />
                    <%-- displays the login box (if a user is logged) --%>
                    <m:loginBox login="${sessionScope.login}" />
                    
                    <%-- displays the message coming from the previous request (if any) --%>
                    <m:displayMessage message="${message}" />
                    
                    <form method="post" id="new_tag_form1" action="deleteTag" <c:if test="${sessionScope.login == null}">onsubmit="return validate_edit_tag('new_tag_form1');"</c:if>>
	                    <fieldset id="form_tags_existing">
	                        <legend>Existing tags&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpTagsExisting')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
	                        <c:choose>
	                         <%-- no tags stored yet --%>
	                         <c:when test="${empty tags}">
	                             <p>
	                                 Currently there is no tag associated with this data collection.
	                             </p>
	                         </c:when>
	                         
	                         <%-- some tags are stored --%>
	                         <c:otherwise>
	                             <ul>
	                                 <c:forEach var="tag" items="${tags}">
	                                     <li style="list-style-type:none;">
	                                         <input name="tags2remove" type="checkbox" value="${tag.id}" />&nbsp;${tag.name}
	                                     </li>
	                                 </c:forEach>
	                             </ul>
	                             <input type="submit" value="Delete" class="submit_button" />
	                         </c:otherwise>
	                     </c:choose>
	                    </fieldset>
	                    
	                    <div class="help_message" id="HelpTagsExisting" style="display: none;">
	                        <div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
	                            <p>
	                                Here are displayed all the tags currently linked to this data collection. You can remove any of these tags by using the the "delete" function. That will not delete the tag, just its association to this data collection.
	                            </p>
	                        </div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
	                    </div>
	                    
	                    <input type="hidden" value="${data.id}" name="dataTypeId" id="dataTypeId1" />
	                </form>
                    
                    
                    <form method="post" id="new_tag_form2" action="addExistingTag" <c:if test="${sessionScope.login == null}">onsubmit="return validate_edit_tag('new_tag_form2');"</c:if>>
                        <fieldset id="form_add_existing_tag">
                            <legend>Add existing tag&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpAddTagExisting')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
                            Tag:&nbsp;
                            <select name="tag">
                                <c:forEach var="tag" items="${allTags}">
                                    <option value="${tag.id}">${tag.name}</option>
                                </c:forEach>
                            </select>
                            <input type="submit" value="Add" class="submit_button" />
                        </fieldset>
                        
                        <div class="help_message" id="HelpAddTagExisting" style="display: none;">
                            <div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
                                <p>
                                    Adds a new association to this data collection, using an existing tag.
                                </p>
                            </div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
                        </div>
                        
                        <input type="hidden" value="${data.id}" name="dataTypeId" id="dataTypeId2" />
                    </form>
                        
                    <form method="post" id="new_tag_form3" action="addNewTag" <c:if test="${sessionScope.login == null}">onsubmit="return validate_edit_tag('new_tag_form3');"</c:if>>
                        <fieldset id="form_add_new_tag">
                            <legend>Add new tag&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpAddNewTag')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
                            <table summary="new_tag_table">
                                <tr>
                                    <td>
                                        Tag name:&nbsp;
                                    </td>
                                    <td>
	                                    <input type="text" name="tag" size="46" id="newTag_id" />
	                                </td>
	                            </tr>
	                            <tr>
	                                <td>
	                                    Definition:&nbsp;
	                                </td>
	                                <td>
	                                    <textarea cols="60" rows="3" name="def" id="newTagDef_id"></textarea>
	                                </td>
	                            </tr>
	                        </table>
	                        <input type="submit" value="Add" class="submit_button" />
                        </fieldset>
                        
                        <div class="help_message" id="HelpAddNewTag" style="display: none;">
                            <div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
                                <p>
                                    Adds a new association to this data collection, with a new tag. Therefore this tag need to be created and a definition should be provided.
                                </p>
                            </div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
                        </div>
                        
                        <input type="hidden" value="${data.id}" name="dataTypeId" id="dataTypeId3" />
	                </form>
	                
	                <br />
	                <div class="bottomSeparator"></div>
	                <table width="100%" summary="table bottom links">
                        <tr>
                            <td align="left">
                                <a href="<c:url value='/collections/${data.id}' />" title="Return to the data collection"><img src="${initParam.www}img/prev.png" alt="Previous arrow icon" align="top" />&nbsp;Go back to the data collection</a>
                            </td>
                        </tr>
                    </table>
                    
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