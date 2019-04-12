package com.http.monitor.model;

import com.http.monitor.parser.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Statistics {

    private static Logger logger = LoggerFactory.getLogger(Statistics.class);

    private final Map<String,Integer> sectionHits = new TreeMap<>();
    private final Map<String, HashMap<String, Integer>> hitsPerSection = new HashMap<>();
    private Long byteTransferred = 0L;
    private Date startWindow;
    private Date endWindow;



    public void addHit(LogEntry logEntry){

        try {

            final String hostName = logEntry.getHostName();
            final String resource = logEntry.getResource();
            final Date date = logEntry.getDateTime();
            if(startWindow == null)
                startWindow = date;
            endWindow = date;

            Integer count = sectionHits.getOrDefault(resource, 0);
            sectionHits.put(resource,count+1);
            byteTransferred += Long.valueOf(logEntry.getResponseSize());

            HashMap<String, Integer> countBySections = hitsPerSection.getOrDefault(hostName, new HashMap<>());
            int sectionCount = countBySections.getOrDefault(resource,0);
            countBySections.put(resource, sectionCount+1);
            hitsPerSection.put(hostName, countBySections);

        } catch (Throwable throwable){
            logger.error("Something went wrong.");

        }
    }

    public void printStats(int k){

        try {

            System.out.println("Statistics period From : "+startWindow + " , To : "+endWindow);

            System.out.println("            Top Hits for each website are :");

            for (String website : hitsPerSection.keySet()) {
                final List<Map.Entry<String, Integer>> sortedMap = new ArrayList<>();
                System.out.println("                          Website Name : " + website);
                HashMap<String, Integer> sectionCounts = hitsPerSection.get(website);
                sectionCounts.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .forEachOrdered(x -> sortedMap.add(x));
                k = k < sortedMap.size() ? k : sortedMap.size() - 1;
                for (int i = 0; i < k; i++) {
                    System.out.print("                                                    ");
                    System.out.println(sortedMap.get(i).getKey() + ":" + sortedMap.get(i).getValue());
                }

            }
        }catch (Throwable throwable){

        }

    }

    public void setStartWindow(Date startWindow) {
        this.startWindow = startWindow;
    }

    public void setEndWindow(Date endWindow) {
        this.endWindow = endWindow;
    }
}
