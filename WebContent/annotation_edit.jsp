<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130306
  @copyright BioModels.net, EMBL-EBI
  
  MIRIAM Registry: allows the edition of the example of use (annotation using a specific format, like SBML, CellML or BioPAX)  
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ajax" uri="http://ajaxtags.org/tags/ajax" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />

                 
<h2 class="icon icon-functional" data-icon="e">Edit examples of use: <span class="searchterm">${data.name}</span></h2>

<form method="post" id="new_anno1" action="addExistingAnnotation" class="basicForm">
    <fieldset>
        <legend>Existing examples of use&nbsp;<a href="javascript:;" title="Display some help" onclick="Effect.toggle('HelpAnnoExisting');"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
    
        <c:forEach var="anno" items="${annotation}">
            <ul>
                <li>${anno.format}
                    <ul>
                  <c:forEach var="tag" items="${anno.tags}">
                      <li>${tag.name}</li>
                  </c:forEach>
              </ul>
          </li>
            </ul>
        </c:forEach>
    </fieldset>
    
    <div class="help_message" id="HelpAnnoExisting" style="display: none;">
         <p>
             Here is the list of all the examples of use stored for this data collection.
         </p>
    </div>
    
<script type="text/javascript" src="${initParam.www}js/prototype.js"></script>
<script type="text/javascript" src="${initParam.www}js/scriptaculous.js"></script>
<script type="text/javascript" src="${initParam.www}js/ajaxtags.js"></script>
<script type="text/javascript" src="${initParam.www}js/ajaxtags_controls.js"></script>
<script type="text/javascript" src="${initParam.www}js/ajaxtags_parser.js"></script>

    
    <fieldset>
        <legend>Add new usage&nbsp;<a href="javascript:;" title="Help" onclick="Effect.toggle('HelpAddAnnoExisting');"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
        
        <table class="nothing">
            <tr>
                <td>Format:&nbsp;</td>
                <td>
                    <select id="format">
		                <option value="">Select format</option>
		                <c:forEach var="format" items="${formats}">
		                    <option value="${format}">${format}</option>
	                    </c:forEach>
           			</select>
           			&nbsp;<span id="progressMsg" style="display:none;"><img alt="Indicator" src="${initParam.www}img/Throbber.gif" /> Loading...</span>
                </td>
            </tr>
            <tr>
                <td>Tag:&nbsp;</td>
                <td>
                    <select id="tag" name="tag" disabled="disabled">
		                <option value="">Select tag</option>
		           </select>
                </td>
            </tr>
        </table>
        
        <br />
        <input type="submit" value="Add" class="submit_button" />
    </fieldset>


    <div id="errorMsg" style="display:none;border:1px solid #e00;background-color:#fee;padding:2px;margin-top:8px;width:300px;font:normal 12px Arial;color:#900"></div>
    
    <input type="hidden" value="${data.id}" name="dataTypeId" id="dataTypeId" />
</form>

<div class="help_message" id="HelpAddAnnoExisting" style="display: none;">
    <p>
        Adds a new example of use, based on the ones already used by other data collections.
    </p>
</div>



<%-- Jquery
<script type="text/javascript">
	function initProgress()
	{
		$('#progressMsg').fadeToggle();
	}
	
    function resetProgress()
    {
    	$('#progressMsg').fadeToggle();
    }
    
    function reportError()
    {
    	var $select = $('#tag');
    	var $options = $('option', $select);
        if (($options.size() == 0) || (($options.size() == 1) && ($options.val() == "")))
        {
            $('#errorMsg').innerHTML = "Sorry, an error occurred!";
        }
    	$('#errorMsg').fadetoggle();
    }
</script>
--%>
<script type="text/javascript">
	function initProgress()
	{
		Element.show('progressMsg');
    }
    
    function resetProgress()
    {
        Effect.Fade('progressMsg');
    }
    
    function reportError()
    {
        if ($('tag').options.length == 0)
        {
            $('errorMsg').innerHTML = "Sorry, an error occurred!";
        }
        Element.show('errorMsg');
        setTimeout("Effect.DropOut('errorMsg')", 2500);
    }
</script> 
<ajax:select baseUrl="ajaxGetAnnotationTags" 
             source="format" 
             target="tag" 
             parameters="format={format}" 
             preFunction="initProgress" 
             emptyOptionName="Select format" 
             postFunction="resetProgress" 
             errorFunction="reportError" />

<br />

<section class="grid_24 clearfix">
	<div class="grid_12 alpha bottom_links_left">
		<a href="<c:url value='/collections/${data.id}' />" title="Return to the data collection" class="icon icon-functional" data-icon="<">Go back to the data collection</a>
	</div>
</section>


<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
