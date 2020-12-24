package com.askwinston.service;

import java.util.Date;

public interface DateService {
    Date parseDateTime(String dateString);

    Date parseDateTime(String dateString, String format, String timezone);

    String formatDateTime(Date date, String format, String timezone);

    String formatDateTime(Date date, String timezone);

    String formatBirthday(String birthday);
}
