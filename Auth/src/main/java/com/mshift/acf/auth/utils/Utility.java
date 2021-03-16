package com.mshift.acf.auth.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Utility {

    public static Date convertLdtToDate(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }
}
