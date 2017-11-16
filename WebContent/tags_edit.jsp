<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130709
  @copyright BioModels.net, EMBL-EBI
  
  MIRIAM Registry: tags management page which displays all the tags and allow their edition.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="ajax" uri="http://ajaxtags.org/tags/ajax" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />

<script type="text/javascript" src="${initParam.www}js/MiriamDynamicForms.js"></script>
<script type="text/javascript" src="${initParam.www}js/prototype.js"></script>
<script type="text/javascript" src="${initParam.www}js/scriptaculous.js"></script>
<script type="text/javascript" src="${initParam.www}js/ajaxtags.js"></script>
<script type="text/javascript" src="${initParam.www}js/ajaxtags_controls.js"></script>
<script type="text/javascript" src="${initParam.www}js/ajaxtags_parser.js"></script>


<h2>Tags management</h2>

<p>
    Here are the ${counter} tag<c:if test="${counter > 1}">s</c:if> stored:
</p>

<div class="detailsBoxRight" id="detailsBoxRight_id">
    <p>Click on a tag to see its details...</p>
</div>

<div class="ajaxMsgBox" id="ajaxMsg" style="display:none;"><img alt="Indicator" src="${initParam.www}img/Throbber.gif" />&nbsp;Loading data...</div>
<div class="ajaxMsgBox" id="ajaxErrMsg" style="display:none;"><span style="color: red;">Sorry, an error occurred while trying to access the requested service...</span></div>

<ul>
    <c:forEach var="tag" items="${tags}">
        <li><a href="javascript:;" title="Display this tag details" class="ajaxLink" id="${tag.id}">${tag.name}</a></li>
    </c:forEach>
</ul>


<script type="text/javascript">
    function centerElement(element)
    {
        if ($(element) != null)
        {
            if (typeof window.innerHeight != 'undefined')
            {
                $(element).style.top = '300px';
                $(element).style.left = Math.round((window.innerWidth - $(element).getWidth())/2) + 'px';
            }
            else
            {
                $(element).style.top = '340px';
                $(element).style.left = '12px';
           }
       }
    }
</script>

<script type="text/javascript">
    function initProgress()
    {
        Element.hide('ajaxErrMsg');
        Element.scrollTo('detailsBoxRight_id');
        centerElement('ajaxMsg');
        Element.show('ajaxMsg');
    }
    
    function resetProgress()
    {
        Effect.Fade('ajaxMsg');
    }
    
    function reportError()
    {
        Element.scrollTo('detailsBoxRight_id');
        centerElement('errorMsg');
        Element.show('errorMsg');
        //setTimeout("Effect.DropOut('errorMsg')", 2500);
    }
</script>

<ajax:htmlContent
    baseUrl="ajaxTagDetails" 
    sourceClass="ajaxLink" 
    target="detailsBoxRight_id" 
    parameters="tagId={ajaxParameter}" 
    preFunction="initProgress" 
    postFunction="resetProgress" 
    errorFunction="reportError" />


<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
