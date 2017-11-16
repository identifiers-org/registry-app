<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

		</div>
		
		
		<footer>
			<%-- local footer (optional) --%>
			<!-- Optional local footer (insert citation / project-specific copyright / etc here -->
			<div id="local-footer" class="grid_24 clearfix">
				<h3 style="color:#BBBBBB;">Funding bodies</h3>
				<div class="grid_2 alpha">&nbsp;</div>
				<div class="grid_4 funders_logo">
					<a href="http://www.bbsrc.ac.uk/" title="Biotechnology and Biological Sciences Research Council (BBSRC)"><img src="${initParam.www}/img/BBSRC_logo.png" title="Biotechnology and Biological Sciences Research Council (BBSRC)" alt="BBSRC logo" /></a>
				</div> 
				<div class="grid_4 funders_logo">
					<a href="http://www.elixir-europe.org/" title="European Life sciences Infrastructure for Biological Information"><img src="${initParam.www}/img/ELIXIR_logo.png" title="European Life sciences Infrastructure for Biological Information" alt="ELIXIR logo" /></a>
				</div>
				<div class="grid_4 funders_logo">
					<a href="http://www.embl.org/" title="European Molecular Biology Laboratory (EMBL)"><img src="${initParam.www}/img/EMBL_logo.png" title="European Molecular Biology Laboratory (EMBL)" alt="EMBL logo" /></a>
				</div>
				<div class="grid_4 funders_logo">
					<a href="http://www.nigms.nih.gov/" title="National Institute of General Medical Sciences (NIGMS)"><img src="${initParam.www}/img/NIGMS_logo.png" title="National Institute of General Medical Sciences (NIGMS)" alt="NIGMS logo" /></a>
				</div>
				<div class="grid_4 funders_logo">
					<a href="http://www.openphacts.org/" title="Open Pharmacological Space (Open PHACTS)"><img src="${initParam.www}/img/OPS_logo.png" title="Open Pharmacological Space (Open PHACTS)" alt="Open PHACTS logo" /></a>
				</div>
				<div class="grid_2 omega">&nbsp;</div>
			</div>
			
		    <%-- EBI footer template --%>
			<c:import url="${initParam.www}template_ebi_footer.html" charEncoding="UTF-8" />
		</footer>
		
	</div> <!--! end of #wrapper -->


	<!-- EBI javascript template -->
	<c:import url="${initParam.www}template_ebi_js.html" charEncoding="UTF-8" />

</body>
</html>