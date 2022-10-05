<%@page import="com.ismail.exchsim.web.PageInfo"%>
<%@page import="com.ismail.darkpool.config.DakPoolConfig"%>
<%@page import="com.ismail.exchsim.AppClientServices"%>
<%@page import="com.ismail.exchsim.util.WebUtil"%>
<%@page import="com.ismail.exchsim.AlgoParamValidValue"%>
<%@page import="com.ismail.exchsim.AlgoParamDef"%>
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

String strategyID = WebUtil.getParameter(request, "strategyID", "TWAP");

Algo algo = null;

//set this page title
PageInfo pageInfo = WebUtil.getPageInfo(request);
pageInfo.appTitle = algoConfig.getWebMainTitle();
pageInfo.pageTitle = algoConfig.getWebMainTitle() + " - " + strategyID;

try
{
    algo = apiService.getAlgoByStrategyID(strategyID);
    
    if (algo != null)
        request.setAttribute("algo", algo);
}
catch (Throwable t)
{    
	pageInfo.setError(t);    
}
%>

<jsp:include page="_header.jsp" flush="true" />
<jsp:include page="_navigation.jsp" flush="true" />

<br>

<table width="1100" cellpadding=5 align="center">
<tr>
<td align="center">

<jsp:include page="algo_.jsp" flush="true" />

</td>
</tr>
</table>


<jsp:include page="_footer.jsp" flush="true" />

