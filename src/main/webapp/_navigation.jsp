<%@page import="com.ismail.exchsim.AppClientServices"%>
<%@page import="com.ismail.exchsim.util.WebUtil"%>
<%@page import="com.ismail.exchsim.ExchangeSimulatorApp"%>
<%@page import="com.ismail.exchsim.config.ColorThemeConfig"%>

<!--  Main Navigation -->

<%
AppClientServices appServices = AppClientServices.getInstance();
ColorThemeConfig theme = appServices.colorTheme;

String spath = request.getServletPath();

String strategyID = WebUtil.getParameter(request, "strategyID", "");

boolean selectedTab = false;
%>

<br>       
<table width="100%" align="center" cellspacing=0 cellpadding=1>
<tr height=30>
    <td width=20%></td>

	<!--     
  selectedTab = spath.contains("/index.jsp");"); %>
    <td width=5></td>   
    <td class="tab_round<%=selectedTab ? "s" : "" %>" width=150 align="center" bgcolor="<%=selectedTab ? theme.tab0s : theme.tab0 %>"> 
        <a href="/" style="color: <%=theme.tab0Text %>;">Home</a>       
    </td>
 -->
 
    <%
     selectedTab = spath.contains("/algo") || spath.contains("/index.jsp");
     %>
    <td width=5></td>   
    <td class="tab_round<%=selectedTab ? "s" : ""%>" width=150 align="center" bgcolor="<%=selectedTab ? theme.tab0s : theme.tab0%>"
    	title="View Available Algorithms, their objectives, parameters and default values"> 
        <a href="/algos.jsp" style="color: <%=theme.tab0Text%>;">Algos</a>       
    </td>

    <%
    selectedTab = spath.equals("/new_order.jsp");
    %>
    <td width=2></td>       
    <td class="tab_round<%=selectedTab ? "s" : ""%>" width=150 align="center" bgcolor="<%=selectedTab ? theme.tab0s : theme.tab0%>"
    	title="Place a new Algo order; and watch it trade">
        <a href="/new_order.jsp<%=WebUtil.isDefined(strategyID) ? "?strategyID=" + strategyID : ""%>" 
        	style="color: <%=theme.tab0Text %>;">New Order Request</a>       
    </td>


    <% selectedTab = spath.equals("/orders.jsp") || spath.startsWith("/order_"); %>
    <td width=2></td>       
    <td class="tab_round<%=selectedTab ? "s" : "" %>" width=150 align="center" bgcolor="<%=selectedTab ? theme.tab0s : theme.tab0 %>"
    	title="View orders and trades">
        <a href="/orders.jsp" style="color: <%=theme.tab0Text %>;">Orders</a>       
    </td>

    <% selectedTab = spath.contains("/instruments.jsp"); %>
    <td width=5></td>   
    <td class="tab_round<%=selectedTab ? "s" : "" %>" width=150 align="center" bgcolor="<%=selectedTab ? theme.tab0s : theme.tab0 %>"
    	title="View live top of book">
        <a href="instruments.jsp" style="color: <%=theme.tab0Text %>;">Instruments</a>     
    </td>
    
    <% selectedTab = spath.contains("/topOfBooks.jsp"); %>
    <td width=5></td>   
    <td class="tab_round<%=selectedTab ? "s" : "" %>" width=150 align="center" bgcolor="<%=selectedTab ? theme.tab0s : theme.tab0 %>"
    	title="View live top of book; from multiple sources">
        <a href="topOfBooks.jsp" style="color: <%=theme.tab0Text %>;">Top Of Books</a>     
    </td>
    
       <!-- 
    <% selectedTab = spath.contains("/instruments.jsp"); %>
    <td width=5></td>   
    <td class="tab_round<%=selectedTab ? "s" : "" %>" width=150 align="center" bgcolor="<%=selectedTab ? theme.tab0s : theme.tab0 %>"
    	title="View instrument list">
        <a href="/instruments.jsp" style="color: <%=theme.tab0Text %>;">Instruments</a>     
    </td>
    
 
    <% selectedTab = spath.contains("/support.jsp"); %>
    <td width=5></td>   
    <td class="tab_round<%=selectedTab ? "s" : "" %>" width=150 align="center" bgcolor="<%=selectedTab ? theme.tab0s : theme.tab0 %>"
    	title="Support">
        <a href="/support.jsp" style="color: <%=theme.tab0Text %>;">Support</a>     
    </td>
     -->
                                                        
    <td width=20%></td>         
</tr>

<tr><td colspan=31 height=4 bgcolor=<%=theme.tab0s %>></td>

</table>