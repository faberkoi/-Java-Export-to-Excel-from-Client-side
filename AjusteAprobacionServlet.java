	public void performTaskDoPost( HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException, Exception
	{
		performTask(request, response);
	
		if (request.getParameterValues("ExportExcel")[0].equalsIgnoreCase("Ok")){
			
			try{
			ExportAjustesAutomaticos Export = new ExportAjustesAutomaticos();
			
			Export.ExportarAjuste(request,response);
			
			MensajeOK Msg = new MensajeOK();
			
			Msg.mostrar("Los datos se exportaron correctamente.");
			
			}			
			catch(Exception e)
			{
				throw e;
			}
	}
}
