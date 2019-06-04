package edn.cloud.business.api.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import edn.cloud.business.dto.integration.SlugItem;

public class UtilFiles 
{
	private UtilLogger loggerSingle =  UtilLogger.getInstance();
	
	/**
	 *  return File Name from upload file
	 * */
	public static String getFileName(MultivaluedMap<String, String> header) {

		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {

				String[] name = filename.split("=");

				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}
		
	/**
	 * get values from text to search
	 * @param InputStream inputStream
	 * @param String textToSearch example \sg1\
	 * */
	public ArrayList<SlugItem> getItemsToDocX(InputStream inputStream,String textToSearch) 
	{
		ArrayList<SlugItem> listSlugItem = new ArrayList<>();				
		
		try
		{
			XWPFDocument document = new XWPFDocument(inputStream);
            List<XWPFParagraph> xwpfParagraphs = document.getParagraphs();
            
            for(XWPFParagraph xwpfParagraph : xwpfParagraphs) 
            {
                List<XWPFRun> xwpfRuns = xwpfParagraph.getRuns();
                for(XWPFRun xwpfRun : xwpfRuns) 
                {
                    String xwpfRunText = xwpfRun.getText(xwpfRun.getTextPosition());
                    
                    if (xwpfRunText!=null && xwpfRunText.contains(textToSearch))
                    {
                    	xwpfRunText = xwpfRunText.replace(UtilCodesEnum.CODE_SIGN_PRINCIPAL_CHAR.getCode(),UtilCodesEnum.CODE_PATRON_SPLIT.getCode());
                    	String[] listString = xwpfRunText.split(UtilCodesEnum.CODE_PATRON_SPLIT.getCode());
                    	
                    	for(String value:listString)
                    	{
                    		if (!value.trim().equals(""))
                    		{
	                    		SlugItem item = new SlugItem(value.replace(" ",""),"");
	                    		listSlugItem.add(item);
	                            System.out.println(value);
	                            loggerSingle.info(value);
                    		}
                    	}
                    }     
                    else if (xwpfRunText!=null)
                    {
                    	System.out.println(xwpfRunText+" "+textToSearch);
                        loggerSingle.info(xwpfRunText+" "+textToSearch);
                    }
                  
                }
            }
            document.close();
            return listSlugItem;
            
		}catch(Exception e) 
		{
			loggerSingle.error(e.getMessage());			
		}
		
		return listSlugItem;
	}
	
	/**
	 * 
	 * @param String text
	 * */
	public static String urlEncode(String text) {
		try {
			return URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			String errMsg = String.format("Fail to encode text [%s]. Unsupported encoding [%s]", text, StandardCharsets.UTF_8.toString());
			throw new IllegalArgumentException(errMsg, e);
		}
	}
}
