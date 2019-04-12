package com.http.monitor.parser;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {

    private static final String DATE_FORMAT = "dd/MMM/yyyy:hh:mm:ss Z";

    private static final Pattern SECTION_PATTERN = Pattern.compile(".*\"GET (\\S+) .*");

    private static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);

    public static LogEntry convert(String logLine) {

        final String[] splitLogLine = logLine.split(",");
        LogEntry log = new LogEntry();


        log.setHostName(splitLogLine[0]);
        log.setUserName(splitLogLine[1]);

        Long timeStamp = Long.valueOf(splitLogLine[3]);
        Date start = Date.from( Instant.ofEpochSecond( timeStamp )) ;

        log.setDateTime(start);

        Matcher matcher = SECTION_PATTERN.matcher(splitLogLine[4]);

        if(matcher.find()){
            log.setResource(matcher.group(1));

        }
        log.setStatus(splitLogLine[5]);
        log.setResponseSize(splitLogLine[6]);




        return log;
    }

}
