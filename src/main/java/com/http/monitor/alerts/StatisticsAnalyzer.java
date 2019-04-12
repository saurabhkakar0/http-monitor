package com.http.monitor.alerts;

import com.http.monitor.collector.Collector;
import com.http.monitor.model.Statistics;
import com.http.monitor.parser.LogEntry;
import com.http.monitor.parser.LogParser;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StatisticsAnalyzer {

    private static Logger logger = LoggerFactory.getLogger(StatisticsAnalyzer.class);

    private Executor exceutor = Executors.newSingleThreadExecutor();
    private final Collector collector;

    public StatisticsAnalyzer(final Collector collector){
        this.collector = collector;
    }
    public void startListening(){

        collector.start()
                .flatMapIterable(lists -> lists)
                .map(logLines -> {
                    final Statistics statistics = new Statistics();
                    enrichStatistics(statistics,logLines);
                    return statistics;
                })
                .window(10,TimeUnit.SECONDS)
                .flatMap(statisticsObservable -> statisticsObservable.map(statistics -> statistics))
                .retry()
                .onErrorReturn(throwable -> {
                    logger.error("Exception occurred during parsing",throwable);
                    return new Statistics();
                })
                .subscribeOn(Schedulers.from(exceutor))
                .subscribe(statistics -> statistics.printStats(5));

    }

    private void enrichStatistics(Statistics statistics,List<String> logLines) throws ParseException, MalformedURLException, UnsupportedEncodingException {

        for(String logLine : logLines){
            LogEntry logEntry = LogParser.convert(logLine);
            statistics.addHit(logEntry);
        }


    }

}
