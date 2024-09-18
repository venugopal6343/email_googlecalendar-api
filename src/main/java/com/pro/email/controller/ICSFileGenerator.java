package com.pro.email.controller;

import com.pro.email.model.Event;

import java.time.format.DateTimeFormatter;

public class ICSFileGenerator {

    public static String generateICSContent(Event event) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        String startDateTime = dtf.format(event.getEventDate());
        String endDateTime = dtf.format(event.getEventDate().plusHours(1)); // Assuming 1 hour event duration

        return "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:-//Your Company//NONSGML Event//EN\n" +
                "BEGIN:VEVENT\n" +
                "UID:" + event.getId() + "@yourdomain.com\n" +
                "DTSTAMP:" + startDateTime + "\n" +
                "DTSTART:" + startDateTime + "\n" +
                "DTEND:" + endDateTime + "\n" +
                "SUMMARY:" + event.getTitle() + "\n" +
                "DESCRIPTION:" + event.getDescription() + "\n" +
                "LOCATION:" + event.getLocation() + "\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR";
    }
}