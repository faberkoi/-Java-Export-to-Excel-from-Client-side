package com.bancoRio.admEfvo;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.bancoRio.excel.ExportAjustesAutomaticos;
import com.softOffice.forms.MensajeOK;


public class AjusteAprobacionServlet extends BaseHttpServlet 
{
	/** Author : Capgemini
	 * 
	 */
	private static final long serialVersionUID = 1306385669664033190L;
	private static Logger logger = Logger.getLogger(AjustesServlet.class);

	private final String  ABLI_COMPROBANTE = "/com.bancoRio.admEfvo.AjusteComprobanteServlet";
	
	public AjusteAprobacionServlet() 
	{
		setDescAcceso("ajuste_aprob");
		setUrlExceptionJsp("/AjustesException.jsp");		
	}

	public void performTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{}
	
	private void obtenerAjustesNoAprobadosPorUsuario(HttpServletRequest request) throws Exception
	{
		AjusteDao ajusteDao = null;
		List ajustesNoAprobadosPorUsuario = null;
		List ajustesNoAprobadosExc = null;
		HttpSession session = null;
		try{
			
			ajusteDao = new AjusteDao((Usuario) request.getSession().getAttribute("usuario"));
			ajustesNoAprobadosPorUsuario = ajusteDao.obtenerAjustesNoAprobadosPorUsuario();
			session = request.getSession();
			session.setAttribute("ajustesNoAprobadosPorUsuario", ajustesNoAprobadosPorUsuario);	
			
		}catch(Exception e){
			throw e;
		}
		try{
			
			ajusteDao = new AjusteDao((Usuario) request.getSession().getAttribute("usuario"));
			ajustesNoAprobadosExc = ajusteDao.obtenerAjustesNoAprobadosExcel();
			session = request.getSession();
			session.setAttribute("ajustesNoAprobadosExc", ajustesNoAprobadosExc);	
			
		}catch(Exception e){
			throw e;
		}
	}
	private String getFechaHoraFormato()
	{
		String fechaC;
		Date date = Calendar.getInstance().getTime();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		fechaC = formatter.format(date);

		return fechaC;
	}
	/*FAH Se agrego un Nuevo Ajuste para que no haga la contabilidad*/
	private Comprobante aprobarAjustes(HttpServletRequest request) throws Exception
	{
		Long 				movID; 
		String				nComp     = null;
		String 				usrAten   = null;
		Usuario         	user      = null;
		Ajuste          	ajuste    = null;
		AjusteDao       	ajusteDao = null;
		AjusteContable		ajusteCon = null;
		Comprobante			comp      = null;
		List 				ajustesNoAprobadosPorUsuario = null;
		String 				usuario = null;
		int					Tipo_Ajuste = 0;
		
			try
			{
				Tipo_Ajuste = new Integer(request.getParameter("TipoAjuste"));
				movID     = new Long(request.getParameter("ajuste"));
				user      = (Usuario) request.getSession().getAttribute("usuario");
				ajusteDao = new AjusteDao(user);
				ajusteCon = new AjusteContable();
	            
				usuario = ajusteDao.verificarAprobacion(movID.toString());
				
				if(usuario == null){
					ajustesNoAprobadosPorUsuario = (List) request.getSession().getAttribute("ajustesNoAprobadosPorUsuario");
					Iterator iter = ajustesNoAprobadosPorUsuario.iterator();
					
					while (iter.hasNext()) 
					{
						ajuste = (Ajuste) iter.next();
						
						if (ajuste.getNroMovimiento().equals(movID.toString()) )
						{
							ajusteCon.setEspecie(ajuste.getEspecie());
							ajusteCon.setMonto(ajuste.getMonto());
							ajusteCon.setMoneda(ajuste.getMonID());
							ajusteCon.setFechaAlta(ajuste.getFechaCarga());
							ajusteCon.setSentido(ajuste.getSentido());						
							ajusteCon.setTipoAjuste(ajuste.getTipoAjuste());
		
							if (ajusteCon.getSentido().equals(ajuste.getSentidoA()))		
							{
								ajusteCon.setSucOrigen(ajuste.getSucID());
								ajusteCon.setSucDestino(ajuste.getLocID());
							}
							else if (ajusteCon.getSentido().equals(ajuste.getSentidoD()))	
							{
								ajusteCon.setSucOrigen(ajuste.getLocID());						
								ajusteCon.setSucDestino(ajuste.getSucID());
							}	
							ajusteCon.setIdStock(ajuste.getNroMovimiento());
							ajusteCon.setUser(user.getUserId());				
		
							if ( Integer.parseInt(ajuste.getTipoAjuste())== ajuste.getTipoDif() )
							{
								nComp = ajusteDao.getNroComprobante().toString();
		
								ajuste.diferenciaClientes(ajuste, user, nComp);
								ajusteDao.aprobarAjustes(ajusteCon);
								comp  	  = new Comprobante();
								comp.setMoneda(ajuste.getMonID());
								comp.setTipoCuenta( ajuste.getLeyendaTipoCuenta());
								comp.setSucursal( ajuste.getNroCuenta().getSucursal());
								comp.setCuenta( ajuste.getNroCuenta().getNumero());
								comp.setTitular(ajuste.getApellido()); 
								comp.setImporte(ajuste.getMonto());
								comp.setNroTalon(nComp);
								comp.setSentido(ajuste.getSentido());
								
								usrAten = ajusteDao.obtenerNombreUsuario(user.getUserId());
								
								comp.setUsuarioAbli(usrAten);
								comp.setfechaHora(getFechaHoraFormato());
								
								//return comp;	
							}
							//else
							//{
							if (Tipo_Ajuste != 6)
							{
								ajusteDao.aprobarAjustes(ajusteCon);
							}
							else
							{
								ajusteDao.aprobarAjusteSinContabilidad(ajusteCon);								
							}
								this.obtenerAjustesNoAprobadosPorUsuario(request);						
							//}
							break;
							
						}
					}
				}else{
					throw new Exception("El ajuste " + movID.toString() + " ya ha sido aprobado por el usuario " + usuario);
				}
	
			}
			catch(Exception e)
			{
				throw e;
			}		
		return comp;
	}
	
//	private void Exportar(HttpServletRequest request){
//			
//		if (Boolean.valueOf((String)request.getParameter("ExportExcel"))){
//			
//			ExportAjustesAutomaticos Export = new ExportAjustesAutomaticos();
//			
//			Export.ExpostarAjuste(request);			
//		}
//	}
	private Comprobante aprobarAjustesExc(HttpServletRequest request) throws Exception
	{
		Long 				movID; 
		String				nComp     = null;
		String 				usrAten   = null;
		Usuario         	user      = null;
		Date 				fechaaprov = null;
		Ajuste          	ajusteExc    = null;
		AjusteDao       	ajusteDao = null;
		AjusteContable		ajusteCon = null;
		Comprobante			comp      = null;
		List 				ajustesNoAprobadosExc = null;
		String 				usuario = null;
		int					Tipo_Ajuste = 0;
		
			try
			{
				Tipo_Ajuste = new Integer(request.getParameter("TipoAjuste"));
				movID     = new Long(request.getParameter("ajusteExc"));
				user      = (Usuario) request.getSession().getAttribute("usuario");
				ajusteDao = new AjusteDao(user);
				ajusteCon = new AjusteContable();
	            
				fechaaprov = ajusteDao.verificarAprovacionExc(movID.toString());
				
				if(fechaaprov == null){
					ajustesNoAprobadosExc = (List) request.getSession().getAttribute("ajustesNoAprobadosExc");
					Iterator iter = ajustesNoAprobadosExc.iterator();
					
					while (iter.hasNext()) 
					{
						ajusteExc = (Ajuste) iter.next();
						
						if (ajusteExc.getNroMovimiento().equals(movID.toString()) )
						{
							//ajusteCon.setEspecie(ajuste.getEspecie());
							
							ajusteCon.setMonto(ajusteExc.getMonto()); 
							ajusteExc.setMonID("1");//PESOS
							ajusteCon.setMoneda(ajusteExc.getMonID());
							ajusteCon.setFechaAlta(ajusteExc.getFechaCarga());
							ajusteExc.setSentido("A"); //Si es Credito va "A" y si es Debito va "D"
							ajusteCon.setSentido(ajusteExc.getSentido());					
							ajusteCon.setTipoAjuste(ajusteExc.getTipoAjuste());
		
//							if (ajusteCon.getSentido().equals(ajusteExc.getSentidoA()))		
//							{
//								ajusteCon.setSucOrigen(ajusteExc.getSucID());
//								ajusteCon.setSucDestino(ajusteExc.getLocID());
//							}
//							else 
							if (ajusteCon.getSentido().equals(ajusteExc.getSentidoA()))	
							{
								ajusteExc.setLocID(ajusteDao.getDatosTransportadora(ajusteExc.getMOV_TRANSPORTADORA(),true).toString());
								ajusteCon.setSucOrigen(ajusteExc.getLocID());	//Es la Transportadora					
								ajusteCon.setSucDestino(ajusteExc.getMOV_SUCU()); //Es la Sucursal.
							}	
							ajusteCon.setIdStock(ajusteExc.getNroMovimiento());
							ajusteCon.setUser(user.getUserId());				
		
							if ( Integer.parseInt(ajusteExc.getTipoAjuste())== ajusteExc.getTipoDif() ) //5
							{
								nComp = ajusteDao.getNroComprobante().toString();
		
								ajusteExc.diferenciaClientes(ajusteExc, user, nComp);
								if (ajusteCon.getMonto() == null)
								{
									ajusteCon.setMonto(ajusteExc.getMOV_IMPORTE());									
								}else
								{
									ajusteCon.setMonto(ajusteExc.getMonto()); 
								}
								ajusteDao.aprobarAjustesExc(ajusteCon);
								
								comp  	  = new Comprobante();
								comp.setMoneda(ajusteExc.getMonID());
								comp.setTipoCuenta( ajusteExc.getLeyendaTipoCuenta());
								comp.setSucursal( ajusteExc.getNroCuenta().getSucursal());
								comp.setCuenta( ajusteExc.getNroCuenta().getNumero());
								comp.setTitular(ajusteExc.getApellido()); 
								comp.setImporte(ajusteExc.getMOV_IMPORTE());
								comp.setNroTalon(nComp);
								comp.setSentido(ajusteExc.getSentido());
								
								usrAten = ajusteDao.obtenerNombreUsuario(user.getUserId());
								
								comp.setUsuarioAbli(usrAten);
								comp.setfechaHora(getFechaHoraFormato());
								
								//return comp;	
							}
							//else
							//{
//							if (Tipo_Ajuste != 6 & Tipo_Ajuste !=5)
//							{
//								ajusteDao.aprobarAjustes(ajusteCon);
//							}
//							else
//							{
//								ajusteDao.aprobarAjusteSinContabilidad(ajusteCon);								
//							}
//								this.obtenerAjustesNoAprobadosPorUsuario(request);						
//							//}
							break;
							
						}
					}
				}else{
					throw new Exception("El ajuste " + movID.toString() + " ya ha sido aprobado por el usuario " + usuario);
				}
	
			}
			catch(Exception e)
			{
				throw e;
			}		
		return comp;
	}
	
	private void cancelarAjustes(HttpServletRequest request) throws Exception
	{
		AjusteDao ajusteDao = null;
		try
		{
			ajusteDao = new AjusteDao((Usuario) request.getSession().getAttribute("usuario"));
			ajusteDao.eliminarAjustesNoAprobados(new Long(request.getParameter("ajuste")));
		}
		catch(Exception e)
		{
			throw e;
		}		
		
		this.obtenerAjustesNoAprobadosPorUsuario(request);
	}	
	private void cancelarAjustesExc(HttpServletRequest request) throws Exception{
		AjusteDao ajusteDao = null;

		try{
			ajusteDao = new AjusteDao((Usuario) request.getSession().getAttribute("usuario"));
			ajusteDao.eliminarAjustesNoAprobadosExc(new Long(request.getParameter("ajusteExc")));
		}catch(Exception e){
			throw e;
		}
	}

	public void performTaskDoGet(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		try
		{
			this.obtenerAjustesNoAprobadosPorUsuario(request);
		} 
		catch (Exception e) 
		{
			logger.error( "ERROR: AjustesServlet.performTaskDoGet --> Exception=" + e.getClass().getName() + " - Message=" + e.getMessage());
			this.RedirectError(request, response, this.getServletName().toString(), e, true);
		} 
	}

	public void performTaskDoPost( HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException, Exception
	{
		performTask(request, response);
	
		if (request.getParameterValues("ajusteCancelar")[0].equalsIgnoreCase(("0k")))
		{
			try	
			{
				this.cancelarAjustes(request);				
			} 
			catch (Exception e)	
			{
				logger.error( "ERROR: AjustesServlet.performTaskDoPost --> Exception=" + e.getClass().getName() + " - Message=" + e.getMessage());
				this.RedirectError(request, response, this.getServletName().toString(), e, false);			
			} 			
		}
		else{
			if (request.getParameterValues("ajusteCancelarExc")[0].equalsIgnoreCase("Ok"))
			{
				try	
				{
					this.cancelarAjustesExc(request);	
					this.obtenerAjustesNoAprobadosPorUsuario(request);
				} 
				catch (Exception e)	
				{
					logger.error( "ERROR: AjustesServlet.performTaskDoPost --> Exception=" + e.getClass().getName() + " - Message=" + e.getMessage());
					this.RedirectError(request, response, this.getServletName().toString(), e, false);			
				} 			
			}
		}
		if (request.getParameterValues("ExportExcel")[0].equalsIgnoreCase("Ok")){
			
			try{
			ExportAjustesAutomaticos Export = new ExportAjustesAutomaticos();
			
			Export.ExportarAjuste(request);
			
			MensajeOK Msg = new MensajeOK();
			
			Msg.mostrar("Los datos se exportaron correctamente.");
			
			}			
			catch(Exception e)
			{
				throw e;
			}
		}
		if (request.getParameterValues("ajusteAprobar")[0].equalsIgnoreCase("Ok"))
		{
			HttpSession session = request.getSession();
			Comprobante comp    = null;
			try	
			{
				comp = this.aprobarAjustes(request);
				request.setAttribute("ajuste","El ajuste "+(String)request.getParameter("ajuste")+" se aprobó exitosamente");				
				
				if (comp != null)
				{
					session.setAttribute("comprobanteAjuste", comp);
					request.setAttribute("REDIRECT", "FALSE");
					response.sendRedirect(ABLI_COMPROBANTE);
				}
			} 
			catch (Exception e)	
			{
				logger.error( "ERROR: AjustesServlet.performTaskDoPost --> Exception=" + e.getClass().getName() + " - Message=" + e.getMessage());
				this.RedirectError(request, response, this.getServletName().toString(), e, false);
			} 				
		} else if (request.getParameterValues("ajusteAprobacionExc")[0].equalsIgnoreCase("Ok"))
			{
			HttpSession session = request.getSession();
			Comprobante comp    = null;
			try	
			{
				comp = this.aprobarAjustesExc(request);
				request.setAttribute("ajuste","El ajuste "+(String)request.getParameter("ajuste")+" se aprobó exitosamente");				
				
				if (comp != null)
				{
					session.setAttribute("comprobanteAjuste", comp);
					request.setAttribute("REDIRECT", "FALSE");
					response.sendRedirect(ABLI_COMPROBANTE);
				}
			} 
			catch (Exception e)	
			{
				logger.error( "ERROR: AjustesServlet.performTaskDoPost --> Exception=" + e.getClass().getName() + " - Message=" + e.getMessage());
				this.RedirectError(request, response, this.getServletName().toString(), e, false);
			} 	
			}
		
	}
}
