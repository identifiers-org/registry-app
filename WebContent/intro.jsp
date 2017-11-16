<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20130215
  @copyright EMBL-EBI, BioModels.net
  
  MIRIAM Registry: home page
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<%-- common header --%>
<jsp:include page="template_header.jsp" />
			
			
			<!-- main content (3/4 of total width) -->
			<div id="home_main_content" class="small-12 medium-9 column">

				<script type="text/javascript" src="//static.identifiers.org/js/home.js"></script>
				<!-- actual content of the page -->
<%--				<c:import url="http://static.identifiers.org/intro.html" charEncoding="UTF-8" />--%>

				<p>Identifiers.org is an established resolving system that enables the referencing of data for the scientific community, with a current focus on the Life Sciences domain. It handles persistent identifiers in the form of URIs and CURIEs. This allows the referencing of data in both a location-independent and resource-dependent manner. The provision of resolvable identifiers (URLs) fits well with the Semantic Web vision, and the Linked Data initiative.</p>


				<div class="callout ebi-background">
					<label for="resolve"><h3 class="white-color">Resolve Compact Identifiers (prefix:identifier), eg: CHEBI:36927 </h3></label>
					<input type="text" name="resolve" placeholder="Enter a prefix:identifier and press Enter" value="" id="resolve">

					<label class="white-color" id="validate-result"></label>
					<div id="progressbar"></div>
				</div>

				<div class="row button-grid" data-equalizer data-equalize-on="medium" id="large-button-grid">
					<div class="column medium-3 small-6 text-center padding-bottom-large"> <a class="button medium-12 columns" data-equalizer-watch href="//www.ebi.ac.uk/miriam/main/collections"><h3 class="icon icon-generic white-color" data-icon="D"></h3> <h5 class="white-color">Registry</h5></a></div>
					<div class="column medium-3 small-6 text-center padding-bottom-large"> <a class="button medium-12 columns" data-equalizer-watch href="//identifiers.org/request/prefix" target="_blank"><h3 class="icon icon-functional white-color" data-icon="D"></h3> <h5 class="white-color">Request prefix</h5></a></div>
					<div class="column medium-3 small-6 text-center padding-bottom-large"> <a class="button medium-12 columns" data-equalizer-watch href="//identifiers.org/restws"><h3 class="icon icon-generic white-color" data-icon="("></h3> <h5 class="white-color">Web Services</h5></a></div>
					<div class="column medium-3 small-6 text-center padding-bottom-large"> <a class="button medium-12 columns" data-equalizer-watch href="//identifiers.org/download"><h3 class="icon icon-functional white-color" data-icon="="></h3> <h5 class="white-color">Download</h5></a></div>
				</div>

				<div class="row">
					<div class="columns medium-6">
						<table class="hover text-center no-stripe" >
							<thead>
							<tr>
								<th class="text-center" colspan="2">Data records</th>
							</tr>
							</thead>
							<tbody>
							<tr>
								<td>Collections</td>
								<td id="collections"></td>
							</tr>
							<tr>
								<td>Resources</td>
								<td id="resources"></td>
							</tr>
							<tr>
								<td>Last updated</td>
								<td id="modified"></td>
							</tr>
							</tbody>
						</table>

					</div>

					<div class="columns medium-6">
						<div class="callout">
							<h3 class="text-center">Meta resolvers</h3>
							<p>Idetifiers.org share a common prefix registry with N2T resolver, based in California Digital Library which enable users to resolve Compact Identifiers using <a href="https://identifiers.org">Identifiers.org</a> or <a href="https://n2t.net/">N2T</a> resolvers.</p>
						</div>
					</div>
				</div>


				<div id="elixir-banner" data-color="grey" data-name="Identifiers.org" data-description="" data-use-basic-styles="true"></div>
				<script defer="defer" src="https://wwwdev.ebi.ac.uk/web_guidelines/EBI-Framework/v1.3/js/elixirBanner.js"></script>
				
			</div>
			
			<!-- right panel (1/4 of total width) -->

<div class="small-12 medium-3 column hide-for-small-only">

	<div class="shortcuts transparent">
		<div class="panel-pane pane-custom pane-2 clearfix">

			<h3 class="pane-title">Connect with us</h3>

			<ul class="columns small-6 no-bullet">
				<li><a href="//twitter.com/IdentifiersOrg" class='icon icon-socialmedia' data-icon='T'>Twitter</a></li>
			</ul>
			<ul class="columns small-6 no-bullet">
				<li><a href="//github.com/identifiers-org/" class='icon icon-socialmedia' data-icon='g'>GitHub</a></li>
			</ul>
		</div>

		<div class="panel-separator"></div>

		<div class="panel-pane pane-custom pane-4 clearfix">
			<a class="twitter-timeline" data-tweet-limit="2"
			   href="https://twitter.com/IdentifiersOrg" >Tweets by IdentifiersOrg</a> <script async
																							   src="//platform.twitter.com/widgets.js"
																							   charset="utf-8"></script>
		</div>
	</div>
</div>
			
<%--
			<!-- statistics -->
			<div class="grid_6 alpha">
				<div id="side_panel">
					<div class="panel_heading"><span class="icon icon-generic" data-icon="g">Registry statistics</span></div>
					
					<div class="sub_panel">
						<div class="panel_sub_heading">Published</div>
						<div class="panel_item">
		            		<div class="panel_item_desc">Data collections:</div>
		                	<div class="panel_item_value">${nbCollections} (<abbr title="Including deprecated data collections">${nbAllCollections}</abbr>)</div>
		            	</div>
		            	<div class="panel_item">
		            		<div class="panel_item_desc">Resources:</div>
		            		<div class="panel_item_value">${nbResources} (<abbr title="Including deprecated resources">${nbAllResources}</abbr>)</div>
		            	</div>
		            	<div class="panel_item">
		            		<div class="panel_item_desc">Last update:</div>
		            		<div class="panel_item_value">${dateUpdate}</div>
		            	</div>
	            	</div>
	            	
	            	<div class="sub_panel">
			            <div class="panel_sub_heading">Under&nbsp;curation</div>
			            <div class="panel_item">
			            	<div class="panel_item_desc">Data collections:</div>
			            	<div class="panel_item_value">${nbCollectionsCura}</div>
			            </div>
			            <div class="panel_item">
			            	<div class="panel_item_desc">Resources:</div>
			            	<div class="panel_item_value">${nbResourcesCura}</div>
			            </div>
			            <div class="panel_item">
			            	<div class="panel_item_desc">Last update:</div>
			            	<div class="panel_item_value">${dateUpdateCura}</div>
			            </div>
					</div>
				</div>
			</div>
			
			<!-- latest news -->
			<div class="grid_6 alpha">
				<div id="news_side_panel">
					<div class="news_heading">
						<div class="news_heading_left">
							<a href="mdb?section=news" title="Access all news" class="icon icon-generic" data-icon="N">News</a>&nbsp;
						</div>
						<div class="news_heading_right">
							<a href="@MIR_STATIC_URL@/rss/MiriamNews.xml" title="RSS feed" class="icon icon-socialmedia" data-icon="R">&nbsp;</a> <a href="https://twitter.com/biomodels" title="@biomodels" class="icon icon-socialmedia" data-icon="T" style="">&nbsp;</a>
						</div>
					</div>
					
					<c:import url="${initParam.www}home_news.html" charEncoding="UTF-8" />
					
				</div>
			</div>
--%>

<%-- common footer --%>
<jsp:include page="template_footer.jsp" />
