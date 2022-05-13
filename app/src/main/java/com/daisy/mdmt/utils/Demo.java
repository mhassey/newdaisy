package com.daisy.mdmt.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

class dmeo {


    public static void main(String[] args) {

        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("dd:MM:yyyy");
        SimpleDateFormat dateFormatGmt1 = new SimpleDateFormat("dd:MM:yyyy hh:mm:ss");

        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        System.out.println(dateFormatGmt.format(new Date()) + "");
        Calendar cal = Calendar.getInstance();
        try {
            Date date = dateFormatGmt.parse(dateFormatGmt.format(new Date(dateFormatGmt.format(new Date()) + " 00:00:00")));
            System.out.println(dateFormatGmt1.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}