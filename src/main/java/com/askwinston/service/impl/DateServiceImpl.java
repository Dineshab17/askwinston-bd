package com.askwinston.service.impl;

import com.askwinston.service.DateService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Service
public class DateServiceImpl implements DateService {
    public static final String SERVER_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String CLIENT_DATETIME_FORMAT = "MMM d, yyyy h:mm a";
    public static final String SERVER_DATE_FORMAT = "yyyy-MM-dd";
    public static final String CLIENT_DATE_FORMAT = "MMM d, yyyy";

    /**
     * @param dateString
     * @return Date
     * To parse string date into server date time format
     */
    @Override
    public Date parseDateTime(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SERVER_DATETIME_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date format is not valid");
        }
    }

    /**
     * @param dateString
     * @param format
     * @param timezone
     * @return Date
     * To parse string date into given date format and time zone
     */
    @Override
    public Date parseDateTime(String dateString, String format, String timezone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * @param date
     * @param format
     * @param timezone
     * @return String
     * To convert Date into string date with given date format and time zone
     */
    @Override
    public String formatDateTime(Date date, String format, String timezone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        return dateFormat.format(date);
    }

    /**
     * @param date
     * @param timezone
     * @return String
     * To convert Date into string date with given time zone
     */
    @Override
    public String formatDateTime(Date date, String timezone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(CLIENT_DATETIME_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        return dateFormat.format(date);
    }

    /**
     * @param birthday
     * @return String
     * To format birthday date of the user
     */
    @Override
    public String formatBirthday(String birthday) {
        SimpleDateFormat serverDateFormat = new SimpleDateFormat(SERVER_DATE_FORMAT);
        SimpleDateFormat clientDateFormat = new SimpleDateFormat(CLIENT_DATE_FORMAT);
        try {
            Date birthdayDate = serverDateFormat.parse(birthday);
            return clientDateFormat.format(birthdayDate);
        } catch (ParseException e) {
            return null;
        }
    }

}
