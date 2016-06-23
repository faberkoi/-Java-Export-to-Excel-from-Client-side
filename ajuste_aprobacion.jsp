<html>
<%@ page import="java.util.*, com.XXXXXX.admEfvo.*,com.listados.bean.Style" errorPage="/JSPErrorPage.jsp" %>
<head>
<title>Aprobación de Ajustes</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<meta name="keywords" content="HTML, CSS, XML, XHTML, JavaScript">
<SCRIPT language="JavaScript">
	
	
function Click(obj){
	if(obj.value == "Manual")
	{
		document.getElementById("Aprob_aju").style.visibility = "hidden";
		document.getElementById("Aprob_aju_Exc").style.visibility = "visible";
		document.getElementById("Auto").style.visibility = "visible";
		document.getElementById("Manu").style.visibility = "hidden";
		document.getElementById("Manu").style.position = "absolute";
		document.getElementById("Auto").style.position = "relative";
		document.getElementById("Export").style.visibility = "visible";
		//document.getElementById("FileName").style.visibility = "visible";
		obj.value ="Automatico";
	}
	else
	{
		document.getElementById("Aprob_aju").style.visibility = "visible";
		document.getElementById("Aprob_aju_Exc").style.visibility = "hidden";
		document.getElementById("Auto").style.visibility = "hidden";
		document.getElementById("Manu").style.visibility = "visible";
		document.getElementById("Auto").style.position = "absolute";
		document.getElementById("Manu").style.position = "relative";
		document.getElementById("Export").style.visibility = "hidden";
		//document.getElementById("FileName").style.visibility = "hidden";
		obj.value ="Manual";
		
	}		
}
</script>
</head>

<%    
		String mensajeOK=(String)request.getAttribute("ajuste");
		if(mensajeOK==null){mensajeOK="";}
     
        Boolean acc = (Boolean)request.getAttribute("tieneAcceso");
		String 	urlVolver="/com.XXXXXX.admEfvo.AjustesMenuServlet";
		
        if (acc == null) throw new Exception("La página debe ser accedida mediante un servlet");

        boolean tieneAcceso = acc.booleanValue();
        if (!tieneAcceso){
                        
%>      
        <p>NO TIENE AUTORIZACION PARA VER EL CONTENIDO DE ESTA PAGINA<p>

<%}else{    
            
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Acceso opAjustesAprob = usuario.getAcceso("ajuste_aprob");
		String linkOpAjustesAprob = opAjustesAprob.getServletName();
		String ajusteAprobar = linkOpAjustesAprob + "?ajusteAprobar=0k";
		String ajusteCancelar = linkOpAjustesAprob + "?ajusteCancelar=0k";
		String ajusteCancelarExc = linkOpAjustesAprob + "?ajusteCancelarExc=0k";
%>
		
<body bgcolor="#A0A0A0" text="#FFFFFF" link="#FFFFFF" vlink="#FFFFFF" alink="#00CCFF">
<form name="ajusteAprobacion" method="post" action="/com.XXXXX.admEfvo.AjusteAprobacionServlet" >
<input type="hidden" name="tieneAcceso" value="<%= acc %>">
<br><br>
<table style="position: fixed;  color: black;" border="1">
	<tr bgcolor="#FF0000">
		<input type="button" value="Manual" id="Automatic" onclick="Click(this)" style="background-color: red" ></input>		
	</tr>
</table>
<%List ajustesNoAprobados = (List) session.getAttribute("ajustesNoAprobadosPorUsuario");%>
 <%List ajustesNoAprobadosExc = (List) session.getAttribute("ajustesNoAprobadosExc");%>
<center>
	<table id="Aprob_aju" style="visibility: visible; position: absolute; " width="100%" border="0" cellspacing="2" cellpadding="2">
		<tr>
			<td width="100%" height="40" valign="top">
				<div align="center">
					<H1>Aprobación de Ajuste Manuales</H1>
				</div>
			</td>
		</tr>
	</table>
	<table id="Aprob_aju_Exc" style="visibility: hidden;" width="100%" border="0" cellspacing="2" cellpadding="2">
		<tr>
			<td width="100%" height="40" valign="top">
				<div align="center">
					<H1>Aprobación de Ajuste Automaticos</H1>
				</div>
			</td>
		</tr>
	</table>
	<table id="Manu" width="85%" border="1" style="visibility: visible; position: relative;">
			<tr bgcolor="#FF0000">
				<td align="center"><font class="itemBold">Nro.Movimiento</font></td>
				<td align="center"><font class="itemBold">Fecha Carga</font></td>
				<td align="center"><font class="itemBold">Fecha Valor</font></td>
				<td align="center"><font class="itemBold">Tipo Ajuste</font></td>				
				<td align="center"><font class="itemBold">Moneda</font></td>
				<td align="center"><font class="itemBold">Monto</font></td>
				<td align="center"><font class="itemBold">Usuario</font></td>		
				<td align="center"><font class="itemBold">Cambiar</font></td>																
				<td align="center"><font class="itemBold">Eliminar</font></td>																
			</tr>
			<%
		
			if(ajustesNoAprobados.size()>0)
			{
		
				Iterator it = ajustesNoAprobados.iterator();
				Ajuste ajuste = null;
				while (it.hasNext()) 
				{
					ajuste = (Ajuste) it.next();%>
					<tr>
					<td align="center"><font class="item"><%=ajuste.getNroMovimiento()%></font></td>
					<td align="center"><font class="item"><%=ajuste.getFechaCarga()%></font></td>				
					<td align="center"><font class="item"><%=ajuste.getFechaValor()%></font></td>
					<td align="center"><font class="item"><%=ajuste.getTipoAjuste()%></font></td>				
					<td align="center"><font class="item"><%=ajuste.getMoneda()%></font></td>				
					<td align="right"><font class="item"><%=ajuste.getMonto()%></font></td>				
					<td align="center"><font class="item"><%=ajuste.getUsuario()%></font></td>								
					<td align="center"><font class="item"><input type="submit" name="<%=ajuste.getNroMovimiento()%>" value="Aprobar" onClick="return aprobarAjuste(this,<%=ajuste.getTipoAjuste()%>)"></font></td>												
					<td align="center"><font class="item"><input type="submit" name="<%=ajuste.getNroMovimiento()%>" value="Eliminar" onClick="return cancelarAjuste(this)"></font></td>																
					</tr>
			<%	}
		
			}
			else
			{%>
				<tr bgcolor="#D0CCF2">
					<td class="textoNegroCommon"  COLSPAN=11 align= center  >No hay ajustes registrados&nbsp;</td>		
				</tr>
			<%
			}		
									
			%>						
		</table>
		<table  id="Auto" width="85%" border="1" style="visibility: hidden; position: absolute;">
		 		<tr bgcolor="#FF0000">
			    <td align="center" nowrap="nowrap"><font class="itemBold">Nro.Movimiento</font></td>
				<td align="center" nowrap="nowrap"><font class="itemBold">Fecha Carga</font></td>
				<td align="center" nowrap="nowrap"><font class="itemBold">Tipo Ajuste</font></td>			
				<td align="center" nowrap="nowrap"><font class="item">Cliente</font></td>
				<td align="center" nowrap="nowrap"><font class="item">Sucursal</font></td>
				<td align="center" nowrap="nowrap"><font class="item">Cuenta</font></td>
				<td align="center" nowrap="nowrap"><font class="item">CUIT</font></td>
				<td align="center" nowrap="nowrap"><font class="itemBold">Monto</font></td>
				<td align="center" nowrap="nowrap"><font class="item">Transportadora</font></td>
				<td align="center" nowrap="nowrap"><font class="item">Usuario</font></td>
				<td align="center"><font class="itemBold">Cambiar</font></td>																
				<td align="center"><font class="itemBold">Eliminar</font></td>																	
			</tr>					
			<%
			//Faba
			if( ajustesNoAprobadosExc.size() > 0)
			{
			//Fin FAba
					
				Iterator it = ajustesNoAprobadosExc.iterator();
				Ajuste ajustexc = null;
								
				while (it.hasNext()) 
				{
					ajustexc = (Ajuste) it.next();
					
					
					%>
					<tr>
					<td align="center"><font class="item"><%=ajustexc.getNroMovimiento()%></font></td>
					<td align="center"><font class="item"><%=ajustexc.getFechaCarga()%></font></td>				
					<td align="center"><font class="item"><%=ajustexc.getTipoAjuste()%></font></td>				
					<td align="center" nowrap="nowrap"> <font class="item"><%=ajustexc.getNombre_CLIENTE()%></font></td>
					<td align="center"> <font class="item"><%=ajustexc.getMOV_SUCU()%></font></td>
					<td align="center" nowrap="nowrap"> <font class="item"><%=ajustexc.getNroCuenta()%></font></td>
					<td align="center" nowrap="nowrap"><font class="item"><%if(!" ".equalsIgnoreCase(ajustexc.getNro_CUIT())){%><%=ajustexc.getNro_CUIT()%><%}%><%else {%>0<%}%></font></td>
					<td align="right"><font class="item"><%=ajustexc.getMOV_IMPORTE()%></font></td>				
					<td align="center" nowrap="nowrap"><font class="item"><%=ajustexc.getMOV_TRANSPORTADORA().toString()%></font></td> 
					<td align="center" nowrap="nowrap"><font class="item"><%=ajustexc.getUsuario()%></font></td>
					<td align="center"><font class="item"><input type="submit" name="<%=ajustexc.getNroMovimiento()%>" value="Aprobar" onClick="return aprobarAjusteExc(<%=ajustexc.getNroMovimiento()%>,<%=ajustexc.getTipoAjuste()%>)" <%if(!"0".equals(ajustexc.getAcuerdo()) && !"0".equals(ajustexc.getProducto())){%>disabled<%}%>></font></td>									
					<td align="center"><font class="item"><input type="submit" name="<%=ajustexc.getNroMovimiento()%>" value="Eliminar" onClick="return cancelarAjusteExc(<%=ajustexc.getNroMovimiento()%>)"></font></td>																			
					</tr>
										
			<%}
		
			}
			else
			{
			%>
				<tr bgcolor="#D0CCF2" >             
			    	<td class="textoNegroCommon"  COLSPAN=11 align= center  >No hay ajustes registrados&nbsp;</td>
				</tr>
			<% 
			} 
			%>				
		</table>
		
			<p align="center" >
				<font style="font-weight:bold;"><%=mensajeOK %></font>
			</p>  
		
		<input type="hidden" name="ajusteAprobar" id="ajusteAprobar" value="">
		<input type="hidden" name="ajusteAprobacionExc" id="ajusteAprobacionExc" value="">
		<input type="hidden" name="ajusteCancelar" id="ajusteCancelar" value="">
		<input type="hidden" name="ajusteCancelarExc" id="ajusteCancelarExc" value="">
		<input type="hidden" name="ajuste" id="ajuste" value="">
		<input type="hidden" name="ajusteExc" id="ajusteExc" value="">
		<input type="hidden" name="TipoAjuste" id="TipoAjuste" value="">
		<input type="hidden" name="ExportExcel" id="ExportExcel" value="">		
</center>
<br>
<P></P>
<p align=center" style="margin-top: 100px; position: relative"><input type="submit" value="Export" id="Export" align="middle" onclick="Exportar(this)" style="background-color: red; font: bold; visibility: hidden"/></p>
<p align=center" style="margin-top: 100px; position: relative;"><a href="<%=urlVolver%>"><img align="middle" style="padding-top: 1px; padding-right: 0px; padding-left: 500px;" src="/images/volver_bot.gif" border="0"></a></p>
</form>
</body>
<script>
function Exportar(obj){
    
	document.getElementById('ExportExcel').value = "Ok";
	document.getElementById('ajusteExc').value = obj;
	
}
function cancelarAjuste(obj){
	var acepta = confirm("Esta seguro de querer cancelar el ajuste?");
	if (acepta){
		document.getElementById('ajuste').value = obj.name;
		document.getElementById('ajusteAprobar').value =  "Ok";  
		return true;
	}else{
		return false;
	}	
}
function cancelarAjusteExc(obj){
	var acepta = confirm("Esta seguro de querer cancelar el ajuste?");
	if (acepta){
		document.getElementById('ajusteExc').value = obj;
		document.getElementById('ajusteCancelarExc').value =  "Ok";  
		return true;
	}else{
		return false;
	}	
}
/*FAH Se agrego un Nuevo Ajuste para que no haga la contabilidad*/
function aprobarAjuste(obj,Tipo_Ajuste){
	var acepta = confirm("Esta seguro que desea aprobar el ajuste?");
	if (acepta){
		document.getElementById('ajuste').value = obj.name;
		document.getElementById('TipoAjuste').value = Tipo_Ajuste;
		document.getElementById('ajusteAprobar').value = "Ok";	
		return true;
	}else{
		return false;
	}	
}
function aprobarAjusteExc(obj,Tipo_Ajuste){
	var acepta = confirm("Esta seguro que desea aprobar el ajuste?");
	if (acepta){
		document.getElementById('ajusteExc').value = obj;
		document.getElementById('TipoAjuste').value = Tipo_Ajuste;
		document.getElementById('ajusteAprobacionExc').value = "Ok";	
		return true;
	}else{
		return false;
	}	
}
</script>

<%}%>
</html>
