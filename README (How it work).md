# -Java-Export-to-Excel-from-Client-side
This is a partial project if try to compile this doesnt work
heare i gone to explain what this do.

first in the jsp file i have the front end, this charge in front end data recovery from a DB.
at the end of the page have a Button called Export, when u clik on them the idea was can export all that data into an Excel file to the client side

using the Post method y pass all data to the .java tho the function ExportExcel.

this function catch the data fron the array and filter it.

i usea POI API to create and write data into a new Excel.

then in the Response i set the contexttype to can Save the data filtered before.
   response.setContentType("application/x-msdownload");            
   
i Set the Header of the response with the File name i want.

response.setHeader("Content-disposition", "attachment; filename="+fileName);

and at the end i set the OutputStream with the response 

OutputStream out = response.getOutputStream();  

and i pass the Out to the Workbook.write(out);

and this is how i saved any data from the front end applien filter in excel or not if i want.
this actuali is working fine in IE 11 o lower.
