package com.DataSysCoinv1;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleLogger {

	public void logWithTimestamp(String message) {
        // Get the current date and time
        Date currentDate = new Date();
        
        // Format the date and time using a SimpleDateFormat
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(currentDate);
        
        // Create the log message with timestamp and argument
        String logMessage =   formattedDate + " " + message;
        
        // Print the log message to the console
        System.out.println(logMessage);
    }
	
	public void logWithTimestamp(int number) {
        logWithTimestamp(String.valueOf(number));
    }
    
    public void logWithTimestamp(short number) {
        logWithTimestamp(String.valueOf(number));
    }
    
    public void logWithTimestamp(long number) {
        logWithTimestamp(String.valueOf(number));
    }
	
}
