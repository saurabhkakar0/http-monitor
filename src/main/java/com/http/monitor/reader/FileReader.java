package com.http.monitor.reader;

import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.LinkedList;
import java.util.List;

public class FileReader {


    private static Logger logger = LoggerFactory.getLogger(FileReader.class);

    public List<String> startStreaming(final String filePath){

        List<String> logLines = new LinkedList<>();

        getStreamingObservable(filePath)
                .doOnNext(s -> logger.info(s))
                .subscribe(logLine -> logLines.add(logLine));

        return logLines;

    }


    public Flowable<String> getStreamingObservable(final String filePath){

        return Flowable.using(
                () -> new BufferedReader(new java.io.FileReader(filePath)),
                reader -> Flowable.fromIterable(() -> reader.lines().iterator()),
                reader -> reader.close());
    }




}
