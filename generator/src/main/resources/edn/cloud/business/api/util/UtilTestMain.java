package edn.cloud.business.api.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

import edn.cloud.business.dto.integration.UserInfo;

public class UtilTestMain {

	public static void main(String[] args) throws ParseException 
	{
		try 
		{
			String[] patternNameOB = new String[] { "_", ".", "0", "2" };
			String[] nameFiles = new String[] {"7_US I9 All Sections.pdf",
											   "1_Hearing Clinic List 2018.pdf",
											   "2_Confidentiality Agreement.pdf",
											   "3_ACA Notice.pdf",
											   "4_PSE_zPRA - coversheet Signature Collier.pdf",
											   "5_US W4.pdf",
											   "6_US I9 Section_2.pdf",
											   "7_US I9 All Sections.pdf",
											   "8_PSE_Confidentiality_Agreement.pdf",
											   "9_Employee Self Identification Form.pdf",
											   "10_PSE_BPR_Task_83_ACA_Notice.pdf",
											   "11_Policy And Procedures.pdf",
											   "12_US I9 Section_1.pdf",
											   "13_ListA_Doc.pdf",
											   "14_I9.pdf"};
			
			for(String nameFile : nameFiles) 
			{
				//String name = (nameFile.substring(Integer.valueOf(patternNameOB[3].toString()), nameFile.length()));
				//System.out.println(name);			
			
				String nameFileExt = UtilMapping.loadStringFromPattern(
						nameFile,
						patternNameOB[0].toString(),
						"",
						Integer.valueOf(patternNameOB[2].toString()));
				
				String name = UtilMapping.loadStringFromPattern(nameFile,
					patternNameOB[0].toString(),
					patternNameOB[1],
					Integer.valueOf(patternNameOB[2].toString()));
			
				System.out.println("document type: "+name);
				System.out.println("name file: "+nameFileExt);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
