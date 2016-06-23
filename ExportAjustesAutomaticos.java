
import java.awt.FileDialog;
import java.io.*;
import java.text.Format;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JFrame;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.tools.ant.taskdefs.Concat;

import com.bancoRio.admEfvo.Ajuste;
import com.ibm.ws.portletcontainer.cache.util.URLDecoder;
import com.ibm.ws.portletcontainer.cache.util.URLEncoder;


public class ExportAjustesAutomaticos {
	
	private static JFrame frame;
	
	public void ExportarAjuste(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		List 	ajustesNoAprobadosExc = null;
		Ajuste  ajusteExc    = null;
		
		String remotehost = request.getRemoteAddr();  
		
		
		int i=1;

		try{
			
			String fileName = "ExcelRE.xls";
						
			//Se Crea el excel			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Racaudacion Elecatronica");
			HSSFCellStyle style = workbook.createCellStyle();
			HSSFCellStyle styleformat = workbook.createCellStyle();
			HSSFFont font = workbook.createFont();
			//Obtengo los datos de los AjusteExcel
			
			ajustesNoAprobadosExc = (List) request.getSession().getAttribute("ajustesNoAprobadosExc");
			
			Iterator itr = ajustesNoAprobadosExc.iterator();
			
			//Stilos del Libro
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			style.setFont(font);
			style.setFillForegroundColor(HSSFColor.RED.index);
			//style.setFillBackgroundColor(HSSFColor.RED.index);
			styleformat.setDataFormat((short)8);
			
						
			//CABECERAS EXCEL
			Row row1 = sheet.createRow(0);
			Cell cell1 = row1.createCell(1);
			cell1.setCellStyle(style);
			cell1.setCellValue("NOMBRE CLIENTE");
			Cell cell2 = row1.createCell(2);
			cell2.setCellStyle(style);
			cell2.setCellValue("SUCURSAL");
			Cell cell3 = row1.createCell(3);
			cell3.setCellStyle(style);
			cell3.setCellValue("NRO DE CUENTA");
			Cell cell4 = row1.createCell(4);
			cell4.setCellStyle(style);
			cell4.setCellValue("CUIT");
			Cell cell5 = row1.createCell(5);
			cell5.setCellStyle(style);
			cell5.setCellValue("PRODUCTO");
			Cell cell6 = row1.createCell(6);
			cell6.setCellValue("ACUERDO");
			cell6.setCellStyle(style);
			Cell cell7 = row1.createCell(7);
			cell7.setCellValue("IMPORTE");
			cell7.setCellStyle(style);
			
			Cell cell8 = row1.createCell(8);
			cell8.setCellValue("TRANSPORTADORA");
			cell8.setCellStyle(style);
			
			while (itr.hasNext())
			{
				ajusteExc = (Ajuste) itr.next();

				/* Estructura del Excel a Crear
				 * Cliente 	
				 * SUCURSAL	
				 * CUENTA	
				 * CUIT	
				 * PRODUCTO	
				 * ACUERDO	
				 * CLIENTES --Esto no se Inserta y No se recupera.	
				 * Importe a acreditar 
				 * */
				if (!ajusteExc.getProducto().trim().equals("0") && !ajusteExc.getAcuerdo().trim().equals("0")){
				Row row2 = sheet.createRow(i);
				Cell cell9 = row2.createCell(1);
				cell9.setCellValue(ajusteExc.getNombre_CLIENTE());
				Cell cell10 = row2.createCell(2);
				cell10.setCellValue(ajusteExc.getMOV_SUCU());
				Cell cell11 = row2.createCell(3);
				cell11.setCellValue(ajusteExc.getNroCuenta().toString());
				Cell cell12 = row2.createCell(4);
				cell12.setCellValue(ajusteExc.getNro_CUIT());
				Cell cell13 = row2.createCell(5);
				cell13.setCellValue(ajusteExc.getProducto());
				Cell cell14 = row2.createCell(6);
				cell14.setCellValue(ajusteExc.getAcuerdo());
				Cell cell15 = row2.createCell(7);
				cell15.setCellValue(ajusteExc.getMOV_IMPORTE());
				cell15.setCellStyle(styleformat);
				//cell15.setCellValue(ajusteExc.getMOV_IMPORTE());
				Cell cell16 = row2.createCell(8);
				cell16.setCellValue(ajusteExc.getMOV_TRANSPORTADORA());
				i++;
				}
			}
			
			 response.setContentType("application/x-msdownload");            
			 response.setHeader("Content-disposition", "attachment; filename="+fileName);
			 OutputStream out = response.getOutputStream();  
			 workbook.write(out);
			
		    
		}catch(Exception e){
			throw e;
		}
}
}
		
	
