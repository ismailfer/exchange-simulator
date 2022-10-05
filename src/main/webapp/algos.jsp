<%@page import="com.ismail.darkpool.config.DakPoolConfig"%>
<%@page import="com.ismail.exchsim.web.PageInfo"%>
<%@page import="com.ismail.exchsim.AppClientServices"%>
<%@page import="com.ismail.exchsim.util.WebUtil"%>
<%@page import="com.ismail.darkpool.config.ColorThemeConfig"%>
<%@page import="com.ismail.exchsim.Algo"%>
<%@page import="java.util.List"%>
<%@page import="com.ismail.exchsim.service.ExchangeSimulatorService"%>
<%@page import="com.ismail.exchsim.ExchangeSimulatorApp"%>

<%
AppClientServices appServices = AppClientServices.getInstance();
ColorThemeConfig theme = appServices.colorTheme;
ExchangeSimulatorService apiService = appServices.apiService;
ExchangeSimulatorConfig algoConfig = appServices.config;

// set this page title
PageInfo pageInfo = WebUtil.getPageInfo(request);
pageInfo.appTitle = algoConfig.getWebMainTitle();
pageInfo.pageTitle = algoConfig.getWebMainTitle() + " - Algos";

List<Algo> algoList = null;

try
{
    algoList = apiService.getAlgos();
}
catch (Throwable t)
{    
	pageInfo.setError(t);    
}
%>

<jsp:include page="_header.jsp" flush="true" />
<jsp:include page="_navigation.jsp" flush="true" />

<br>

<%
if (algoList != null) {
%>

<table width="1100" align="center">

<%
for (int i=0; i<algoList.size(); i++) 
{ 
Algo algo = algoList.get(i);
%>
<tr bgcolor="<%=theme.rawHighlight%>" height=25 onClick="document.location='algo.jsp?strategyID=<%=algo.getStrategyID()%>';">
	<td align="left">
	<a href="algo.jsp?strategyID=<%=algo.getStrategyID()%>">
	<b><%=algo.getStrategyID()%></b>	
	</a>
	</td>
</tr>

<tr>
	<td>
	<%=WebUtil.toHtmlFormat(algo.getShortDesc())%>	
	</td>
</tr>

<tr><td>&nbsp;</td></tr>

<% } %>

</table>

<% } %>

<jsp:include page="_footer.jsp" flush="true" />

