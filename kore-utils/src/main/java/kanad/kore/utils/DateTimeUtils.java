package kanad.kore.utils;

import org.apache.logging.log4j.LogManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {
	private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
	public static final long MILLISECONDS_PER_DAY = 86400000l;
	
	
	public static Date getDateFromStr(String dateStr) {
		
		Date date = null;

		try {
			date = dateFormatter.parse(dateStr);
			
		} catch (Exception e) {
			LogManager.getLogger().error("Exception !", e);
		}
		
		return date;
	}
		
	public static String getStrFromDate(Date date) {
		
		String dateStr = null;
		
		try {
			
			dateStr = dateFormatter.format(date);
			
		} catch (Exception e) {
			LogManager.getLogger().info("Exception !", e);
		}
		
		return dateStr;
	}
	
	public static long getCurrentTimeInMilliSeconds() {
		return new Date().getTime();
	}
	
	public static Date getPastDate(long days) {
		return new Date(getCurrentTimeInMilliSeconds() - (days * MILLISECONDS_PER_DAY ) );
	}
	
	public static int getAge(Date dob) {
		if (dob == null) {
			LogManager.getLogger().info("Date of birth is null.");
			return -1;
		}
		long days = TimeUnit.DAYS.convert((getCurrentTimeInMilliSeconds() - dob.getTime()), TimeUnit.MILLISECONDS);
		return (int) (days/365);
	}
	
}
