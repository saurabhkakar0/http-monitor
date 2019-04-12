package com.http.monitor.reader;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestLogFileReader {

    static String filePath = "/Users/saurabh/IdeaProjects/httplogmonitor/src/main/java/logs/http-log.log";

    @Test
    public void testFileReading(){

        FileReader fileReader = new FileReader();
        List<String> logLines = fileReader.startStreaming(filePath);
        Assert.assertEquals(logLines.size(),4830);
    }
}
