package io.aktech.coronavirustracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.aktech.coronavirustracker.models.LocationStats;
import io.aktech.coronavirustracker.services.CoronaVirusDataServices;

@Controller
public class HomeController {
	
	@Autowired
	CoronaVirusDataServices coronaVirusDataServices;
	
	@GetMapping("/")
	public String home(Model model) {
		
		List<LocationStats> allStats = coronaVirusDataServices.getAllStats();
		int totalReporedCases = allStats.stream().mapToInt(stat->stat.getLatestTotalCases()).sum();
		int totalNewCases = allStats.stream().mapToInt(stat->stat.getDiffFromPrevDay()).sum();
		model.addAttribute("locationStats",allStats);
		model.addAttribute("totalReportedCases",totalReporedCases);
		model.addAttribute("totalNewCases",totalNewCases);
		return "home";
	}
}
