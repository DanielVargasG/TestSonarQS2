package edn.cloud.business.api.util;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * java.util.Date serializer/deserializer in UTC format.
 * 
 * @author Chavdar Baikov
 */
public class UtilDateTimeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

	private static final String UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"; //$NON-NLS-1$

	private final DateFormat dateFormat;

	public UtilDateTimeAdapter() {
		dateFormat = new SimpleDateFormat(UTC_DATE_FORMAT, Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); //$NON-NLS-1$
	}

	@Override
	public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
		synchronized (dateFormat) {
			return new JsonPrimitive(dateFormat.format(date));
		}
	}

	@Override
	public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
		try {
			synchronized (dateFormat) {
				return dateFormat.parse(jsonElement.getAsString());
			}
		} catch (ParseException e) {
			throw new JsonParseException(e);
		}
	}

	/**
	 * return date current
	 */
	public static String getDateNow() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		return (year + "-" + month + "-" + day);
	}

	/**
	 * return date current
	 */
	public static String getDateNowDelta(String num) {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, Integer.parseInt(num));

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		return (year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day));
	}

	/**
	 * Format date from format pattern
	 * 
	 * @param format
	 * @param date
	 * @return date string
	 */
	public static String getDateFormat(String format, Date date) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		}

		return "";
	}

	/**
	 * get date from string format date
	 * 
	 * @param String format
	 * @param String date 
	 */
	public static Date getDateFromString(String format, String date) {
		try {
			DateFormat dateFormat = new SimpleDateFormat(format);
			return dateFormat.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * get date from string format date
	 * 
	 * @param String format
	 * @param String date
	 * @return String 
	 */
	public static String getDateFormatFromString(String format, String date) {
		try {
			DateFormat dateFormat = new SimpleDateFormat(format);
			return dateFormat.parse(date).toString();
		} catch (Exception e) {
			return date;
		}
	}	
	
	
	/**
	 * Add minutes to Date
	 * @param Timestamp time
	 * @param Long addMinutes
	 * */
	public static Date getDateAddMinutes(Timestamp time, Long addMinutes) 
	{
		Date dateNew = new Date(time.getTime() + (addMinutes * 60000));
		return dateNew;
	}
	
	
	/**
	 * Return date without time
	 * @return Date
	 * */
	public static Date getDateWithoutTime()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dateWithoutTime;
		try {
			dateWithoutTime = sdf.parse(sdf.format(new Date()));
			return dateWithoutTime;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new Date();
	}
		
	
	

	public static Date JSONTarihConvert(String date) {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String datereip = date.replace("/Date(", "").replace(")/", "");

		Long timeInMillis = Long.valueOf(datereip);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);

		Date dt = c.getTime();

		return dt;
	}
	
	
	/**
	 * get date from zone date time
	 * @param String zoneId
	 * @return date of zone
	 * */
	public static Date getCurrentDateFromZoneDateTime(String zoneId)
	{
		try
		{
			if(!(zoneId!=null && !zoneId.equals("")))
				zoneId = UtilCodesEnum.CODE_TIME_ZONE_UNIVERSAL.getCode();
			
			Instant nowUtc = Instant.now();
			ZoneId asiaSingapore = ZoneId.of(zoneId);			
			ZonedDateTime nowAsiaSingapore = ZonedDateTime.ofInstant(nowUtc, asiaSingapore);
			return getDateFromString("dd/MM/yyyy",
						nowAsiaSingapore.getDayOfMonth()+"/"+
						nowAsiaSingapore.getMonthValue()+"/"+
						nowAsiaSingapore.getYear());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return new Date();
	}
	
	/**
	 * validate if current minute/date is par
	 * */
	public static Boolean isCurrentMinutePar()
	{
		try {
			Calendar now = Calendar.getInstance();
			int minute= now.get(Calendar.MINUTE);
			
			if(minute%2==0){
				return true;
			}
		}catch (Exception e) {
			e.printStackTrace();
		} 
		
		return false;
		
	}
}
