package edn.cloud.business.api.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UtilBuilderDateFormat {

	public static String JSONTarihConvertNL(String formatResponse, String date) {
		SimpleDateFormat df = new SimpleDateFormat("d");
		SimpleDateFormat df1 = new SimpleDateFormat("MM");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy");

		if (formatResponse != null)
			df = new SimpleDateFormat(formatResponse);

		String datereip = date.replace("/Date(", "").replace(")/", "");

		Long timeInMillis = Long.valueOf(datereip);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);

		Date dt = c.getTime();

		String out = "";

		switch (df1.format(dt)) {
		case "01":
			out = df.format(dt) + " januari " + df2.format(dt);
			break;
		case "02":
			out = df.format(dt) + " februari " + df2.format(dt);
			break;
		case "03":
			out = df.format(dt) + " maart " + df2.format(dt);
			break;
		case "04":
			out = df.format(dt) + " april " + df2.format(dt);
			break;
		case "05":
			out = df.format(dt) + " mei " + df2.format(dt);
			break;
		case "06":
			out = df.format(dt) + " juni " + df2.format(dt);
			break;
		case "07":
			out = df.format(dt) + " juli " + df2.format(dt);
			break;
		case "08":
			out = df.format(dt) + " augustus " + df2.format(dt);
			break;
		case "09":
			out = df.format(dt) + " september " + df2.format(dt);
			break;
		case "10":
			out = df.format(dt) + " oktober " + df2.format(dt);
			break;
		case "11":
			out = df.format(dt) + " november " + df2.format(dt);
			break;
		case "12":
			out = df.format(dt) + " december " + df2.format(dt);
			break;
		default:
			break;
		}

		return out;
	}
	
	public static String JSONTarihConvertFR(String formatResponse, String date) {
		SimpleDateFormat df = new SimpleDateFormat("d");
		SimpleDateFormat df1 = new SimpleDateFormat("MM");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy");

		if (formatResponse != null)
			df = new SimpleDateFormat(formatResponse);

		String datereip = date.replace("/Date(", "").replace(")/", "");

		Long timeInMillis = Long.valueOf(datereip);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);

		Date dt = c.getTime();

		String out = "";

		switch (df1.format(dt)) {
		case "01":
			out = df.format(dt) + " janvier " + df2.format(dt);
			break;
		case "02":
			out = df.format(dt) + " février " + df2.format(dt);
			break;
		case "03":
			out = df.format(dt) + " mars " + df2.format(dt);
			break;
		case "04":
			out = df.format(dt) + " avril " + df2.format(dt);
			break;
		case "05":
			out = df.format(dt) + " mai " + df2.format(dt);
			break;
		case "06":
			out = df.format(dt) + " juin " + df2.format(dt);
			break;
		case "07":
			out = df.format(dt) + " juillet " + df2.format(dt);
			break;
		case "08":
			out = df.format(dt) + " août " + df2.format(dt);
			break;
		case "09":
			out = df.format(dt) + " septembre " + df2.format(dt);
			break;
		case "10":
			out = df.format(dt) + " octobre " + df2.format(dt);
			break;
		case "11":
			out = df.format(dt) + " novembre " + df2.format(dt);
			break;
		case "12":
			out = df.format(dt) + " décembre " + df2.format(dt);
			break;
		default:
			break;
		}

		return out;
	}

	public static String JSONTarihConvertUK(String formatResponse, String date) {
		SimpleDateFormat df = new SimpleDateFormat("d");
		SimpleDateFormat df1 = new SimpleDateFormat("MM");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy");

		if (formatResponse != null)
			df = new SimpleDateFormat(formatResponse);

		String datereip = date.replace("/Date(", "").replace(")/", "");

		Long timeInMillis = Long.valueOf(datereip);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);

		Date dt = c.getTime();

		String out = "";

		switch (df1.format(dt)) {
		case "01":
			out = df.format(dt) + " January " + df2.format(dt);
			break;
		case "02":
			out = df.format(dt) + " February " + df2.format(dt);
			break;
		case "03":
			out = df.format(dt) + " March " + df2.format(dt);
			break;
		case "04":
			out = df.format(dt) + " April " + df2.format(dt);
			break;
		case "05":
			out = df.format(dt) + " May " + df2.format(dt);
			break;
		case "06":
			out = df.format(dt) + " June " + df2.format(dt);
			break;
		case "07":
			out = df.format(dt) + " July " + df2.format(dt);
			break;
		case "08":
			out = df.format(dt) + " August " + df2.format(dt);
			break;
		case "09":
			out = df.format(dt) + " September " + df2.format(dt);
			break;
		case "10":
			out = df.format(dt) + " October " + df2.format(dt);
			break;
		case "11":
			out = df.format(dt) + " November " + df2.format(dt);
			break;
		case "12":
			out = df.format(dt) + " December " + df2.format(dt);
			break;
		default:
			break;
		}

		return out;
	}

	public static String JSONTarihConvertFullHU(String formatResponse, String date) {
		SimpleDateFormat df = new SimpleDateFormat("d");
		SimpleDateFormat df1 = new SimpleDateFormat("MM");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy");

		if (formatResponse != null)
			df = new SimpleDateFormat(formatResponse);

		String datereip = date.replace("/Date(", "").replace(")/", "");

		Long timeInMillis = Long.valueOf(datereip);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);

		Date dt = c.getTime();

		String out = "";

		switch (df1.format(dt)) {
		case "01":
			out = df2.format(dt) + ". január " + df.format(dt) + ".";
			break;
		case "02":
			out = df2.format(dt) + ". február " + df.format(dt) + ".";
			break;
		case "03":
			out = df2.format(dt) + ". március " + df.format(dt) + ".";
			break;
		case "04":
			out = df2.format(dt) + ". április " + df.format(dt) + ".";
			break;
		case "05":
			out = df2.format(dt) + ". május " + df.format(dt) + ".";
			break;
		case "06":
			out = df2.format(dt) + ". június " + df.format(dt) + ".";
			break;
		case "07":
			out = df2.format(dt) + ". július " + df.format(dt) + ".";
			break;
		case "08":
			out = df2.format(dt) + ". augusztus " + df.format(dt) + ".";
			break;
		case "09":
			out = df2.format(dt) + ". szeptember " + df.format(dt) + ".";
			break;
		case "10":
			out = df2.format(dt) + ". október " + df.format(dt) + ".";
			break;
		case "11":
			out = df2.format(dt) + ". november " + df.format(dt) + ".";
			break;
		case "12":
			out = df2.format(dt) + ". december " + df.format(dt) + ".";
			break;
		default:
			break;
		}

		return out;
	}
	
	public static String JSONTarihConvertFullUS(String formatResponse, String date) {
		SimpleDateFormat df = new SimpleDateFormat("d");
		SimpleDateFormat df1 = new SimpleDateFormat("MM");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy");

		if (formatResponse != null)
			df = new SimpleDateFormat(formatResponse);

		String datereip = date.replace("/Date(", "").replace(")/", "");

		Long timeInMillis = Long.valueOf(datereip);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);

		Date dt = c.getTime();

		String out = "";

		switch (df1.format(dt)) {
		case "01":
			out = "January " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "02":
			out = "February " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "03":
			out = "March " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "04":
			out = "April " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "05":
			out = "Mai " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "06":
			out = "June " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "07":
			out = "July " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "08":
			out = "August " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "09":
			out = "September " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "10":
			out = "October " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "11":
			out = "November " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "12":
			out = "December " + df.format(dt) + ", " + df2.format(dt);
			break;
		default:
			break;
		}

		return out;
	}
	
	public static String JSONTarihConvertFullZH(String formatResponse, String date) {
		SimpleDateFormat df = new SimpleDateFormat("d");
		SimpleDateFormat df1 = new SimpleDateFormat("MM");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy");

		if (formatResponse != null)
			df = new SimpleDateFormat(formatResponse);

		String datereip = date.replace("/Date(", "").replace(")/", "");

		Long timeInMillis = Long.valueOf(datereip);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);

		Date dt = c.getTime();

		String out = "";

		switch (df1.format(dt)) {
		case "01":
			out = df.format(dt) + ". Januar " + df2.format(dt);
			break;
		case "02":
			out = df.format(dt) + ". Februar " + df2.format(dt);
			break;
		case "03":
			out = df.format(dt) + ". März " + df2.format(dt);
			break;
		case "04":
			out = df.format(dt) + ". April " + df2.format(dt);
			break;
		case "05":
			out = df.format(dt) + ". Mai " + df2.format(dt);
			break;
		case "06":
			out = df.format(dt) + ". Juni " + df2.format(dt);
			break;
		case "07":
			out = df.format(dt) + ". Juli " + df2.format(dt);
			break;
		case "08":
			out = df.format(dt) + ". August " + df2.format(dt);
			break;
		case "09":
			out = df.format(dt) + ". September " + df2.format(dt);
			break;
		case "10":
			out = df.format(dt) + ". Oktober " + df2.format(dt);
			break;
		case "11":
			out = df.format(dt) + ". November " + df2.format(dt);
			break;
		case "12":
			out = df.format(dt) + ". Dezember " + df2.format(dt);
			break;
		default:
			break;
		}
		return out;
	}

	public static String JSONTarihConvertHU(String formatResponse, String date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.d.");

		if (formatResponse != null)
			df = new SimpleDateFormat(formatResponse);

		String datereip = date.replace("/Date(", "").replace(")/", "");

		Long timeInMillis = Long.valueOf(datereip);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);

		Date dt = c.getTime();

		return df.format(dt);
	}
	
	public static String JSONTarihConvertM(String formatResponse, String date) {
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");

		if (formatResponse != null)
			df = new SimpleDateFormat(formatResponse);

		String datereip = date.replace("/Date(", "").replace(")/", "");

		Long timeInMillis = Long.valueOf(datereip);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);

		Date dt = c.getTime();

		return df.format(dt);
	}

	public static String JSONTarihConvertGENERIC(String formatResponse, String date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);

		if (formatResponse != null)
			df = new SimpleDateFormat(formatResponse);

		String datereip = date.replace("/Date(", "").replace(")/", "");

		Long timeInMillis = Long.valueOf(datereip);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);

		Date dt = c.getTime();

		return df.format(dt);
	}

	public static String JSONTarihConvertUS(String formatResponse, String date) {
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

		if (formatResponse != null)
			df = new SimpleDateFormat(formatResponse);

		String datereip = date.replace("/Date(", "").replace(")/", "");

		Long timeInMillis = Long.valueOf(datereip);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);

		Date dt = c.getTime();

		return df.format(dt);
	}

	public static String JSONTarihConvert(String date) {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String datereip = date.replace("/Date(", "").replace(")/", "");

		Long timeInMillis = Long.valueOf(datereip);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);

		Date dt = c.getTime();

		return df.format(dt);
	}

	public static String JSONTarihConvertMonth(String date) {
		SimpleDateFormat df = new SimpleDateFormat("MMMM");
		String datereip = date.replace("/Date(", "").replace(")/", "");

		Long timeInMillis = Long.valueOf(datereip);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);

		Date dt = c.getTime();

		return df.format(dt);
	}

	public static String JSONTarihConvertYear(String date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		String datereip = date.replace("/Date(", "").replace(")/", "");

		Long timeInMillis = Long.valueOf(datereip);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);

		Date dt = c.getTime();

		return df.format(dt);
	}

	public static String JSONTarihConvertToday() {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Calendar c = Calendar.getInstance();
		Date dt = c.getTime();
		return df.format(dt);
	}
	
	public static String JSONTarihConvertTodayUS() {
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		Calendar c = Calendar.getInstance();
		Date dt = c.getTime();
		return df.format(dt);
	}
	
	public static String JSONTarihConvertTodayM() {
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		Calendar c = Calendar.getInstance();
		Date dt = c.getTime();
		return df.format(dt);
	}

	public static String JSONTarihConvertTodayUK() {
		SimpleDateFormat df = new SimpleDateFormat("d");
		SimpleDateFormat df1 = new SimpleDateFormat("MM");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy");
		Calendar c = Calendar.getInstance();
		Date dt = c.getTime();

		String out = "";

		switch (df1.format(dt)) {
		case "01":
			out = df.format(dt) + " January " + df2.format(dt);
			break;
		case "02":
			out = df.format(dt) + " February " + df2.format(dt);
			break;
		case "03":
			out = df.format(dt) + " March " + df2.format(dt);
			break;
		case "04":
			out = df.format(dt) + " April " + df2.format(dt);
			break;
		case "05":
			out = df.format(dt) + " May " + df2.format(dt);
			break;
		case "06":
			out = df.format(dt) + " June " + df2.format(dt);
			break;
		case "07":
			out = df.format(dt) + " July " + df2.format(dt);
			break;
		case "08":
			out = df.format(dt) + " August " + df2.format(dt);
			break;
		case "09":
			out = df.format(dt) + " September " + df2.format(dt);
			break;
		case "10":
			out = df.format(dt) + " October " + df2.format(dt);
			break;
		case "11":
			out = df.format(dt) + " November " + df2.format(dt);
			break;
		case "12":
			out = df.format(dt) + " December " + df2.format(dt);
			break;
		default:
			break;
		}
		return out;
	}
	
	public static String JSONTarihConvertTodayFR() {
		SimpleDateFormat df = new SimpleDateFormat("d");
		SimpleDateFormat df1 = new SimpleDateFormat("MM");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy");
		Calendar c = Calendar.getInstance();
		Date dt = c.getTime();

		String out = "";

		switch (df1.format(dt)) {
		case "01":
			out = df.format(dt) + " janvier " + df2.format(dt);
			break;
		case "02":
			out = df.format(dt) + " février " + df2.format(dt);
			break;
		case "03":
			out = df.format(dt) + " mars " + df2.format(dt);
			break;
		case "04":
			out = df.format(dt) + " avril " + df2.format(dt);
			break;
		case "05":
			out = df.format(dt) + " mai " + df2.format(dt);
			break;
		case "06":
			out = df.format(dt) + " juin " + df2.format(dt);
			break;
		case "07":
			out = df.format(dt) + " juillet " + df2.format(dt);
			break;
		case "08":
			out = df.format(dt) + " août " + df2.format(dt);
			break;
		case "09":
			out = df.format(dt) + " septembre " + df2.format(dt);
			break;
		case "10":
			out = df.format(dt) + " octobre " + df2.format(dt);
			break;
		case "11":
			out = df.format(dt) + " novembre " + df2.format(dt);
			break;
		case "12":
			out = df.format(dt) + " décembre " + df2.format(dt);
			break;
		default:
			break;
		}

		return out;
	}
	
	public static String JSONTarihConvertTodayFullZH() {
		SimpleDateFormat df = new SimpleDateFormat("d");
		SimpleDateFormat df1 = new SimpleDateFormat("MM");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy");
		Calendar c = Calendar.getInstance();
		Date dt = c.getTime();

		String out = "";

		switch (df1.format(dt)) {
		case "01":
			out = df.format(dt) + ". Januar " + df2.format(dt);
			break;
		case "02":
			out = df.format(dt) + ". Februar " + df2.format(dt);
			break;
		case "03":
			out = df.format(dt) + ". März " + df2.format(dt);
			break;
		case "04":
			out = df.format(dt) + ". April " + df2.format(dt);
			break;
		case "05":
			out = df.format(dt) + ". Mai " + df2.format(dt);
			break;
		case "06":
			out = df.format(dt) + ". Juni " + df2.format(dt);
			break;
		case "07":
			out = df.format(dt) + ". Juli " + df2.format(dt);
			break;
		case "08":
			out = df.format(dt) + ". August " + df2.format(dt);
			break;
		case "09":
			out = df.format(dt) + ". September " + df2.format(dt);
			break;
		case "10":
			out = df.format(dt) + ". Oktober " + df2.format(dt);
			break;
		case "11":
			out = df.format(dt) + ". November " + df2.format(dt);
			break;
		case "12":
			out = df.format(dt) + ". Dezember " + df2.format(dt);
			break;
		default:
			break;
		}
		return out;
	}

	public static String JSONTarihConvertTodayHU() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.d.");
		Calendar c = Calendar.getInstance();
		Date dt = c.getTime();
		return df.format(dt);
	}

	public static String JSONTarihConvertTodayGENERIC(String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		Calendar c = Calendar.getInstance();
		Date dt = c.getTime();
		return df.format(dt);
	}

	public static String JSONTarihConvertTodayNL() {
		SimpleDateFormat df = new SimpleDateFormat("d");
		SimpleDateFormat df1 = new SimpleDateFormat("MM");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy");
		Calendar c = Calendar.getInstance();
		Date dt = c.getTime();

		String out = "";

		switch (df1.format(dt)) {
		case "01":
			out = df.format(dt) + " januari " + df2.format(dt);
			break;
		case "02":
			out = df.format(dt) + " februari " + df2.format(dt);
			break;
		case "03":
			out = df.format(dt) + " maart " + df2.format(dt);
			break;
		case "04":
			out = df.format(dt) + " april " + df2.format(dt);
			break;
		case "05":
			out = df.format(dt) + " mei " + df2.format(dt);
			break;
		case "06":
			out = df.format(dt) + " juni " + df2.format(dt);
			break;
		case "07":
			out = df.format(dt) + " juli " + df2.format(dt);
			break;
		case "08":
			out = df.format(dt) + " augustus " + df2.format(dt);
			break;
		case "09":
			out = df.format(dt) + " september " + df2.format(dt);
			break;
		case "10":
			out = df.format(dt) + " oktober " + df2.format(dt);
			break;
		case "11":
			out = df.format(dt) + " november " + df2.format(dt);
			break;
		case "12":
			out = df.format(dt) + " december " + df2.format(dt);
			break;
		default:
			break;
		}

		return out;
	}

	public static String JSONTarihConvertTodayFullHU() {
		SimpleDateFormat df = new SimpleDateFormat("d");
		SimpleDateFormat df1 = new SimpleDateFormat("MM");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy");
		Calendar c = Calendar.getInstance();
		Date dt = c.getTime();

		String out = "";

		switch (df1.format(dt)) {
		case "01":
			out = df2.format(dt) + ". január " + df.format(dt) + ".";
			break;
		case "02":
			out = df2.format(dt) + ". február " + df.format(dt) + ".";
			break;
		case "03":
			out = df2.format(dt) + ". március " + df.format(dt) + ".";
			break;
		case "04":
			out = df2.format(dt) + ". április " + df.format(dt) + ".";
			break;
		case "05":
			out = df2.format(dt) + ". május " + df.format(dt) + ".";
			break;
		case "06":
			out = df2.format(dt) + ". június " + df.format(dt) + ".";
			break;
		case "07":
			out = df2.format(dt) + ". július " + df.format(dt) + ".";
			break;
		case "08":
			out = df2.format(dt) + ". augusztus " + df.format(dt) + ".";
			break;
		case "09":
			out = df2.format(dt) + ". szeptember " + df.format(dt) + ".";
			break;
		case "10":
			out = df2.format(dt) + ". október " + df.format(dt) + ".";
			break;
		case "11":
			out = df2.format(dt) + ". november " + df.format(dt) + ".";
			break;
		case "12":
			out = df2.format(dt) + ". december " + df.format(dt) + ".";
			break;
		default:
			break;
		}

		return out;
	}
	
	public static String JSONTarihConvertTodayFullUS() {
		SimpleDateFormat df = new SimpleDateFormat("d");
		SimpleDateFormat df1 = new SimpleDateFormat("MM");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy");
		Calendar c = Calendar.getInstance();
		Date dt = c.getTime();

		String out = "";

		switch (df1.format(dt)) {
		case "01":
			out = "January " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "02":
			out = "February " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "03":
			out = "March " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "04":
			out = "April " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "05":
			out = "Mai " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "06":
			out = "June " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "07":
			out = "July " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "08":
			out = "August " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "09":
			out = "September " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "10":
			out = "October " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "11":
			out = "November " + df.format(dt) + ", " + df2.format(dt);
			break;
		case "12":
			out = "December " + df.format(dt) + ", " + df2.format(dt);
			break;
		default:
			break;
		}

		return out;
	}

	public static String JSONTarihConvertTodayYear() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		Calendar c = Calendar.getInstance();
		Date dt = c.getTime();
		return df.format(dt);
	}

	public static String JSONTarihConvertTodayMonth() {
		SimpleDateFormat df = new SimpleDateFormat("MM");
		Calendar c = Calendar.getInstance();
		Date dt = c.getTime();
		return df.format(dt);
	}

	public static String JSONTarihConvertTodayDay() {
		SimpleDateFormat df = new SimpleDateFormat("dd");
		Calendar c = Calendar.getInstance();
		Date dt = c.getTime();
		return df.format(dt);
	}

	public static Date JSONTarihConvertDate(String date) {
		String datereip = date.replace("/Date(", "").replace(")/", "");

		Long timeInMillis = Long.valueOf(datereip);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);

		Date dt = c.getTime();

		return dt;
	}
	
	public static String JSONTarihConvertDay(String date) {
		SimpleDateFormat df = new SimpleDateFormat("dd");
		String datereip = date.replace("/Date(", "").replace(")/", "");

		Long timeInMillis = Long.valueOf(datereip);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);

		Date dt = c.getTime();

		return df.format(dt);
	}
}
