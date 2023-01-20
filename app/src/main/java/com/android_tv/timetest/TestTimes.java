package com.android_tv.timetest;

import java.util.Scanner;

public class TestTimes {
    public static void main(String[] args) {
        Scanner s= new Scanner(System.in);


        int utc = s.nextInt();
        int offset =  s.nextInt();
        int dateTime =  s.nextInt();
        int open =  s.nextInt();
        int close =  s.nextInt();

        int cf = utc - dateTime + offset;

        int clt = utc + offset + cf;

        String qualification = "";
        if (clt > 24) {
            clt = clt - 24;
            qualification = " next day";
        } else if (clt < 0) {
            clt = 24 + clt;
            qualification = " prior day";
        }

        String status = clt >= open && clt <= close ? "open" : "closed";

        System.out.println("Correction factor is " + cf);
        System.out.println("Corrected local time is " + clt + qualification + ". Store is " + status);

    }
}