package com.http.monitor.collector;

import com.http.monitor.reader.FileReader;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Collector {

    private static Logger logger = LoggerFactory.getLogger(Collector.class);

    private final FileReader fileReader;


    static String filePath = "/Users/saurabh/IdeaProjects/httplogmonitor/src/main/java/logs/http-log.log";

    public Collector(final FileReader fileReader){
        this.fileReader = fileReader;

    }

    private static PublishSubject<String> subject = PublishSubject.create();

    public  Observable<List<List<String>>> start(){

        final List<String> logLines = fileReader.startStreaming(filePath);

        Observable<List<List<String>>> logsGroupedByTime = Observable.fromIterable(logLines)
                .sorted((o1, o2) -> {
                    String[] logLineOne = o1.split(",");
                    String[] logLineTwo = o2.split(",");
                    return logLineOne[3].compareTo(logLineTwo[3]);
                })
                .toList()
                .flatMap(sortedLogLines -> Single.just(getStatistics(sortedLogLines)))
                .toObservable();

        return logsGroupedByTime;

    }

    private Observable<List<List<String>>>  getFileStreamObservable(final List<String> logLines){
        return  Observable.fromIterable(logLines)
                .sorted((o1, o2) -> {
                    String[] logLineOne = o1.split(",");
                    String[] logLineTwo = o2.split(",");
                    return logLineOne[3].compareTo(logLineTwo[3]);
                })
                .toList()
                .flatMap(sortedLogLines -> Single.just(getStatistics(sortedLogLines)))
                .toObservable();
    }
    private List<List<String>> getStatistics(final List<String> sortedLogLines){
        List<List<String>> groupedByTime = new ArrayList<>();

        List<String> bucket = new ArrayList<>();
        bucket.add(sortedLogLines.get(1));

        Long timeStamp = Long.valueOf(sortedLogLines.get(1).split(",")[3]);
        Date start = Date.from( Instant.ofEpochSecond( timeStamp )) ;

        for (int i = 2; i < sortedLogLines.size(); i++) {

            String logLine = sortedLogLines.get(i);

            timeStamp = Long.valueOf(logLine.split(",")[3]);

            Date thisDate = Date.from( Instant.ofEpochSecond( timeStamp ) );
            long diff = thisDate.getTime() - start.getTime();
            long diffSeconds = diff / 1000 % 60;

            if(diffSeconds > 10 ){
                groupedByTime.add(bucket);
                start = thisDate;
                bucket = new ArrayList<>();
            }
            bucket.add(logLine);

        }
        return groupedByTime;

    }

    public static Observable<String> observeStream(){
        return subject;
    }
}
