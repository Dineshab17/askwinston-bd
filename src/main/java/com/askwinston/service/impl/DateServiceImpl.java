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

    @Override
    public String formatDateTime(Date date, String format, String timezone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        return dateFormat.format(date);
    }

    @Override
    public String formatDateTime(Date date, String timezone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(CLIENT_DATETIME_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        return dateFormat.format(date);
    }

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
