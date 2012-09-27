package com.ourlittlegame.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtils {

	public static String relativeDate(String text) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
		SimpleDateFormat outdf = new SimpleDateFormat("MMM dd, yyyy");
		try {
			Date d = sdf.parse(text);		
			Date now = new Date();
			long ms = now.getTime() - d.getTime();
			int seconds = (int)(ms/1000);
			int mins = seconds / 60;
			int hrs = mins / 60;
			int days = hrs / 24;
			
			System.out.println(text);
			
			if (days > 30) {
				return outdf.format(d);
			} else if (days >= 1) {
				return days + " day"+((days>1)?"s":"")+" ago";
			} else if (hrs >= 1) {
				return hrs + " hour"+((hrs>1)?"s":"")+" ago";
			} else if (mins >= 1) {
				return mins + " min"+((mins>1)?"s":"")+" ago";				
			} else 
				return "Few seconds ago";			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private static Date getYesterday() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		return c.getTime();
	}

}
