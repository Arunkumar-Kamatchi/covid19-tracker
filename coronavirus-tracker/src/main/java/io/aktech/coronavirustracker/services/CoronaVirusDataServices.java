package io.aktech.coronavirustracker.services;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.aktech.coronavirustracker.models.LocationStats;

@Service
public class CoronaVirusDataServices {
	
	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	private List<LocationStats> allStats = new ArrayList<>();
	
	public List<LocationStats> getAllStats() {
		return allStats;
	}

	@PostConstruct
	@Scheduled(cron="* * 1 * * *")
	public void fetchVirusData() throws IOException, InterruptedException {
		
		List<LocationStats> newStats = new ArrayList<>();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request =  HttpRequest.newBuilder()
				.uri(URI.create(VIRUS_DATA_URL)).build();
		HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
		StringReader stringReader = new StringReader(response.body());
		Iterable<CSVRecord> records =  CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(stringReader);
		
		for (CSVRecord csvRecord : records) {

			LocationStats locationStats = new LocationStats();
			locationStats.setState(csvRecord.get("Province/State"));
			locationStats.setCountry(csvRecord.get("Country/Region"));
			int latestCases = Integer.parseInt(csvRecord.get(csvRecord.size() - 1));
			int prevCases = Integer.parseInt(csvRecord.get(csvRecord.size() - 2));
			locationStats.setLatestTotalCases(latestCases);
			locationStats.setDiffFromPrevDay(latestCases - prevCases);
			newStats.add(locationStats);
		}
		this.allStats = newStats;
	}
}
