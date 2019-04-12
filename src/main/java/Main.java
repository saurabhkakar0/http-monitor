import com.http.monitor.alerts.StatisticsAnalyzer;
import com.http.monitor.collector.Collector;
import com.http.monitor.reader.FileReader;

public class Main {

    public static void main(String[] args) {


        FileReader fileReader = new FileReader();
        Collector collector = new Collector(fileReader);
        StatisticsAnalyzer statisticsAnalyzer = new StatisticsAnalyzer(collector);
        statisticsAnalyzer.startListening();
    }
}
