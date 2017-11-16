<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>



	</div>
		<footer>
			<%-- local footer (optional) --%>
			<!-- Optional local footer (insert citation / project-specific copyright / etc here -->
			<%-- <div id="local-footer" class="grid_24 clearfix"></div> --%>
			
		    <%-- EBI footer template --%>
			<c:import url="${initParam.www}template_ebi_footer.html" charEncoding="UTF-8" />
		</footer>
		


	<!-- EBI javascript template -->
	<c:import url="${initParam.www}template_ebi_js.html" charEncoding="UTF-8" />

</body>
</html>