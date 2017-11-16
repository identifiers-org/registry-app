<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="m" %>

<!doctype html>
<!-- paulirish.com/2008/conditional-stylesheets-vs-css-hacks-answer-neither/ -->
<!--[if lt IE 7]> <html class="no-js ie6 oldie" lang="en"> <![endif]-->
<!--[if IE 7]>    <html class="no-js ie7 oldie" lang="en"> <![endif]-->
<!--[if IE 8]>    <html class="no-js ie8 oldie" lang="en"> <![endif]-->
<!-- Consider adding an manifest.appcache: h5bp.com/d/Offline -->
<!--[if gt IE 8]><!--> <html class="no-js" lang="en"> <!--<![endif]-->
<head>

  <!-- header template -->
  <c:import url="${initParam.www}template_head.html" charEncoding="UTF-8" />
  
  <!-- Registry's specific CSS -->
  <link rel="stylesheet" type="text/css" media="screen" href="${initParam.www}style/Registry.css" />
  
  <!-- Registry's RSS feed -->
  <link rel="alternate" type="application/rss+xml" title="MIRIAM News Feed" href="${initParam.www}rss/MiriamNews.xml" />
	<meta name="google-signin-client_id" content="801652923959-6bfk14gekh5pqf4518c74d5nu8p3j5jp.apps.googleusercontent.com">
</head>


<body class="level2">

	<!-- skip links template -->
	<c:import url="${initParam.www}template_skip_links.html" charEncoding="UTF-8" />
	
	<!-- title and menus -->
	<div data-sticky-container="">
		<div id="local-masthead" class="meta-background-image" data-sticky="true"
			 data-sticky-on="large" data-top-anchor="180" data-btm-anchor="300000">

	    <header>
	    	
			<!-- ebi menu template -->
	        <c:import url="${initParam.www}template_ebi_menu.html" charEncoding="UTF-8" />
	        
			<!-- registry menu template -->
			<jsp:include page="menu.jsp" />
	        
	    </header>
		</div>
	</div>
		<!-- main content (full) -->
		<div id="content" role="main" class="row">
			
			<%-- displays which version of the app is running (not displayed for 'main') --%>
			<%--<m:displayVersion version="${initParam.version}" />--%>
			
			<%-- displays the login box (if a user is logged)
			<m:loginBox login="${sessionScope.login}" />
			--%>
			 
			<%-- displays the message coming from the previous request (if any) --%>
			<%--<m:displayMessage message="${message}" />--%>
			
			<!-- navigation breadcrumbs 
			<nav>
			   <p id="breadcrumbs">
			   <a href="" title="">MIRIAM Registry</a> &gt; Home
			   </p>
		    </nav>
		    -->
