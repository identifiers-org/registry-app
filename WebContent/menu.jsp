<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


			<!-- Registry's menu -->
			<div class="masthead row"><div class="columns medium-6" id="local-title">
				<h1><a href="//identifiers.org/"><img src="${initParam.graphics}identifiers-org_logo-new.png" title="Identifiers.org" alt="Identifiers.org logo" width="80" height="80" style="padding-right: 12px;" />Identifiers.org</a></h1>
			</div>



				<div class="columns medium-6  last">
<%--					<form id="local-search" name="local-search" action="<c:url value='/search' />" method="get">
						<fieldset>
							<div class="left">
								<label>
									<c:choose>
										<c:when test="${!empty param.query}">
											<input type="text" name="query" id="local-searchbox" value="${param.query}" />
										</c:when>
										<c:otherwise>
											<input type="text" name="query" id="local-searchbox" />
										</c:otherwise>
									</c:choose>
								</label>
								<!-- some example of search terms -->
						        <span class="examples">Examples: <a href="<c:url value='/search?query=ontology' />" title="Search for 'ontology'">ontology</a>, <a href="<c:url value='/search?query=enzyme' />" title="Search for 'enzyme'">enzyme</a>, <a href="<c:url value='/search?query=Japan' />" title="Search for 'Japan'">Japan</a>, <a href="<c:url value='/search?query=EMBL' />" title="Search for 'EMBL'">EMBL</a></span>
							</div>

							<div class="right">
								<input type="submit" value="Search" class="submit"> <!-- name="submit"  -->
								<span class="adv"><a href="<c:url value='/tags/' />" id="adv-search" title="Search using types of data">Categories&nbsp;&amp;&nbsp;tags</a></span>
							</div>
						</fieldset>
					</form>--%>

					<form id="local-search" name="local-search" action="<c:url value='https://identifiers.org/registry' />" method="get">
						<fieldset>
							<div class="input-group">
								<label>
									<input type="text" name="query" value="" id="query" />
									<div class="text-right">
										<a href="//identifiers.org/search">Advanced search</a>
									</div>

								<!-- some example of search terms -->
								<%--<p class="examples">Examples:
									<a href="//identifiers.org/registry?query=ontology">ontology</a>,
									<a href="//identifiers.org/registry?query=enzyme">enzyme</a>,
									<a href="//identifiers.org/registry?query=EMBL">EMBL</a>,
									<a href="//identifiers.org/registry?query=Japan">Japan</a>
								</p>--%>
								</label>
								<div class="input-group-button"><input type="submit" value="Search"
																	   class="submit button secondary">

								</div>
							</div>
						</fieldset>
					</form>

				</div>
				
				<nav>
					<ul class="grid_24 menu float-left columns medium-12" id="local-nav" data-dropdown-menu="true">
						<li class="first"><a href="//identifiers.org" title="identifiers.org">Home</a></li>
				<%--		<li><a href="<c:url value='/collections' />" title="Browse the Registry">Registry</a></li>--%>
						<li><a href="//identifiers.org/documentation" title="Documentation">Documentation</a></li>
						<li><a href="//identifiers.org/service" title="Services">Services</a></li>
						<li><a href="//identifiers.org/about" title="About">About</a></li>
                        <%--<li><a href="<c:url value='/mdb?section=admin_profiles' />" title="Profiles management">Profiles</a></li>--%>
						
						<c:choose>
							<%-- logged users (curators and administrators) --%>
							<c:when test="${(sessionScope.login != null) && (!empty sessionScope.login) && (sessionScope.role != 'user')}">
								<li><a href="<c:url value='/mdb?section=submit' />" title="Submit new data collection">Submit</a></li>
								<li><a href="<c:url value='/mdb?section=curation&amp;type=pending' />" title="Access data collections in the curation pipeline">Curation</a></li>
								<li><a href="<c:url value='/mdb?section=edit_tags' />" title="Manage tags">Tags</a></li>
								<li><a href="<c:url value='/mdb?section=health_check' />" title="Resources health check report">Checks</a></li>

								<%-- for administrators only --%>
				    			<c:if test="${sessionScope.role == 'admin'}">
				    				<li><a href="<c:url value='/users' />" title="Users management">Users</a></li>
				    			</c:if>
				    			
								<%-- right end side links --%>
								<li class="functional"><a href="<c:url value='/mdb?section=user' />" class="icon icon-generic" data-icon="M" title="Access to your account">${fname}</a></li>
								<%--<li class="functional last"><a href="<c:url value='/signOut' />" class="icon icon-functional" data-icon="l" title="Logout">Logout</a></li>--%>
							</c:when>
							
							<%-- anonymous users --%>
							<c:otherwise>
								<%-- <li><a href="<c:url value='/mdb?section=submit' />" title="Submit new data collections to the Registry">Submit</a></li> --%>
								<%--<li><a href="<c:url value='/export/' />" title="Download the Registry's content">Download</a></li>
		    					<li><a href="<c:url value='/mdb?section=ws' />" title="Web services provided and how to use them">Web services</a></li>
		    					<li><a href="<c:url value='/mdb?section=docs' />" title="Documentation">Documentation</a></li>--%>
				    			<%-- <li><a href="<c:url value='/mdb?section=news' />" title="News">News</a></li> --%>
<%--				    			<li><a href="<c:url value='/mdb?section=contribute' />" title="Contribute">Contribute</a></li>
				    			<li><a href="http://identifiers.org/" title="Identifiers.org (provides resolving services for the URIs generated based on the Registry's dataset)">Identifiers.org</a></li>
				    			<li><a href="<c:url value='/mdb?section=about' />" title="About the Registry">About</a></li>--%>

                                <c:choose>
                                    <c:when test="${(sessionScope.login != null) && (!empty sessionScope.login) && (sessionScope.role == 'user')}">
                                        <%-- right end side links --%>
                                        <li class="functional"><a href="<c:url value='/mdb?section=user' />" class="icon icon-generic" data-icon="M" title="Access to your account">${fname}</a></li>
                                        <%--<li class="functional last"><a href="<c:url value='/signOut' />" class="icon icon-functional" data-icon="l" title="Logout">Logout</a></li>--%>
                                    </c:when>
                                    <c:otherwise>
                                        <%-- right end side links --%>
                                        <%--<li class="functional"><a href="<c:url value='/mdb?section=support&amp;info=generic' />" class="icon icon-generic" data-icon="\" title="Send us questions, comments and suggestions">Feedback</a></li>--%>

                                        <%--display login in cura only--%>
                                        <c:if test="${pageContext.request.contextPath == '/miriam/cura'}">
                                            <li class="functional last"><a href="<c:url value='/mdb?section=signin' />" class="icon icon-functional" data-icon="l" title="Curator sign in">Login</a></li>
                                        </c:if>
                                        <%-- <li class="functional"><a href="#" class="icon icon-functional" data-icon="r">Share</a></li> --%>
                                    </c:otherwise>
                                </c:choose>
							</c:otherwise>
						</c:choose>

						<li class="menu-412 menu-feedback functional float-right" id="feedback"><a href="//www.ebi.ac.uk/support/identifiers.org" target="_blank" class="icon icon-generic" data-icon="\">Feedback</a></li>

					</ul>
				</nav>
			</div>
