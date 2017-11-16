<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


</div>
		<!-- EBI footer template -->
		<footer>
			<c:import url="${initParam.www}template_ebi_footer.html" charEncoding="UTF-8" />
		</footer>
		
	 <!--! end of #wrapper -->


	<!-- EBI javascript template -->
	<c:import url="${initParam.www}template_ebi_js.html" charEncoding="UTF-8" />
	
	<!-- JavaScript at the bottom for fast page loading -->

    <!-- Grab Google CDN's jQuery, with a protocol relative URL; fall back to local if offline -->
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
    <script>window.jQuery || document.write('<script src="${initParam.www}js/jquery-1.8.3.min.js"><\/script>')</script>
    
<%--    <script src="//www.ebi.ac.uk/web_guidelines/js/ebi-global-search-run.js"></script>
	<script src="//www.ebi.ac.uk/web_guidelines/js/ebi-global-search.js"></script>
	
    <!-- scripts concatenated and minified via ant build script-->
    <script defer="defer" src="//www.ebi.ac.uk/web_guidelines/js/cookiebanner.js"></script>  
    <script defer="defer" src="//www.ebi.ac.uk/web_guidelines/js/foot.js"></script>--%>

</body>
</html>