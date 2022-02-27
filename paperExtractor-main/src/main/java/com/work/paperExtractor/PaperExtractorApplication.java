package com.work.paperExtractor;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaperExtractorApplication {
	String oldTilte = null;
	ArrayList<String> titleList = new ArrayList<>();

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(PaperExtractorApplication.class, args);

		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");

		Map<String, Object> prefs = new HashMap<String, Object>();

		// Use File.separator as it will work on any OS
		prefs.put("download.default_directory",
				System.getProperty("user.dir") + File.separator + "externalFiles" + File.separator + "downloadFiles");
		prefs.put("plugins.always_open_pdf_externally", true);
		prefs.put("download.prompt_for_download", false);

		// Adding cpabilities to ChromeOptions
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", prefs);

		ArrayList<String> qArr = new ArrayList<String>();
		List<Map<String, String>> paperData = new ArrayList<Map<String, String>>();

		// qArr.add("Relational+Join+Strategies"); no values
		// qArr.add("Hashing+techniques+cpu");
		// qArr.add("Hashing+techniques+on+single+core+cpu");
		// qArr.add("hash");
		// qArr.add("join");
		// qArr.add("Hashing");
		// qArr.add("relational+join+techniques");no values
//		qArr.add("join+algorithms+sql");
//		qArr.add("hash+join+algorithm");
		qArr.add("Non+exhaustive+Join+Ordering+Search+Algorithms+for+LJQO");
		// qArr.add("Hashing+techniques");
		// qArr.add("hashing+algorithms");
		//
		//
		// qArr.add("Relational+database+Join");
		// qArr.add("Hash+Join");
		// qArr.add("general+hashing+techniques+join+relational");
		// qArr.add("hash+techniques+dbms");
		// qArr.add("hash+dbms");
		//
		// qArr.add("relational+join");
		// qArr.add("join+extensions");
		// qArr.add("relational+join+dbms");
		//
		// qArr.add("join+algorithms");
		// qArr.add("relation+join+extension");
		// qArr.add("relational+join+algorithm");

		// qArr.add("nested+loop+join");
		// qArr.add("nested+loop+join+algorithm");
		// qArr.add("block+nested+loop+join+algorithm");
		// qArr.add("block+nested+loop+join");
		// qArr.add("sort+merge+join+algorithm");
		// qArr.add("sort+merge+join");

		PaperExtractorApplication demo = new PaperExtractorApplication();
		// demo.hitSciHub(doiListdblp);
		// demo.getFromScholar();

		// if (false) {
		for (String qVal : qArr) {
			try {
				URL dblpurl = new URL("https://dblp.org/search/publ/api?q=" + qVal + "&h=1000&format=json");
				HttpURLConnection conn = (HttpURLConnection) dblpurl.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP Error code : " + conn.getResponseCode());
				}

				String inline = "";
				Scanner scanner = new Scanner(dblpurl.openStream());

				// Write all the JSON data into a string using a scanner
				while (scanner.hasNext()) {
					inline += scanner.nextLine();
				}

				// Close the scanner
				scanner.close();

				// Using the JSON simple library parse the string into a json object

				JSONParser parser = new JSONParser();
				JSONObject data_obj = (JSONObject) parser.parse(inline);
				// Get the required object from the above created object
				JSONObject obj = (JSONObject) data_obj.get("result");
				JSONObject hit = (JSONObject) obj.get("hits");
				JSONArray arr = (JSONArray) hit.get("hit");

				for (int i = 0; i < arr.size(); i++) {

					JSONObject new_obj = (JSONObject) arr.get(i);
					JSONObject info = (JSONObject) new_obj.get("info");

					if (Integer.parseInt((String) info.get("year")) >= 1990) {
						Map<String, String> values = new HashMap<String, String>();

						values.put("year", ((String) info.get("year")));
						values.put("venue", ((String) info.get("venue")));
						values.put("title", ((String) info.get("title")));
						try {
							values.put("doi", ((String) info.get("doi")));
						} catch (Exception e) {
							values.put("doi", "");
							System.out.println("Exception in NetClientGet:- " + e);
						}

						try {
							// if (((String) info.get("ee")).toLowerCase().contains(".pdf")) {
							values.put("ee", ((String) info.get("ee")));

						} catch (Exception e) {
							values.put("ee", "");
							System.out.println("Exception in NetClientGet:- " + e);
						}
						paperData.add(values);
						System.out.println("hello");
					}
				}

				WebDriver driver = new ChromeDriver(options);
				for (Map<String, String> map : paperData) {
					// System.out.println(map.get("title"));

					if (map.get("ee") != null && map.get("ee").toLowerCase().contains(".pdf")) {
						demo.getPDFfiles(map, driver);
					}

					else if (map.get("doi") != null) {
						demo.hitSciHub(map, driver);
					} else {
						demo.getFromScholar(map, driver);
					}
				}

			} catch (Exception e) {
				System.out.println("Exception in NetClientGet:- " + e);
			}

		}
	}

	// }

	public void getFromScholar(Map<String, String> data, WebDriver driver) throws InterruptedException {

			Map<String, Object> prefs = new HashMap<String, Object>();
		PaperExtractorApplication demo = new PaperExtractorApplication();

//		 Use File.separator as it will work on any OS
				prefs.put("download.default_directory", System.getProperty("user.dir") + File.separator + "ResearchPapers" + File.separator+ data.get("venue")
		+ File.separator + data.get("year"));
			prefs.put("plugins.always_open_pdf_externally", true);
				prefs.put("download.prompt_for_download", false);
		
		//		// Adding cpabilities to ChromeOptions
			ChromeOptions options = new ChromeOptions();
			options.setExperimentalOption("prefs", prefs);

				driver = new ChromeDriver(options);
		driver.get("https://scholar.google.com/scholar?start=10hl=en&as_sdt=0%2C5&q=" + data.get("title"));
		Thread.sleep(2000);
		List<WebElement> allLinks = driver.findElements(By.tagName("a"));
		for (WebElement link : allLinks) {

		if (!link.getText().contentEquals("Save") && !link.getText().contentEquals("Cite")
				&& !link.getAttribute("href").contains("https://scholar.google.com")) {

			if (link.getText().length() > 15) {
				link.getAttribute("href");
				if (link.getAttribute("href").contains("http://doi.org")) {
					data.put("doi", link.getAttribute("href"));
					demo.hitSciHub(data,driver);
				}
				else if (link.getAttribute("href") != null && link.getText().contains("PDF")) {
					data.put("ee", link.getAttribute("href"));
					demo.getPDFfiles(data, driver);
				} 
				else {
					titleList.add(data.get("title"));
				}
			}
			else {
				titleList.add(data.get("title"));
			}
		}
		}
		//		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");

		//		WebDriver driver = new ChromeDriver();

		//		List<String> resultsTitle = new ArrayList<>();
		//		List<String> resultsLink = new ArrayList<>();
//		ArrayList<String> doiList = new ArrayList<>();
//		ArrayList<String> pdfList = new ArrayList<>();
//		List<String> directList = new ArrayList<>();
//		PaperExtractorApplication demo = new PaperExtractorApplication();

		//			for (int i = 0; i < 1; i++) {
		//
		//				driver.get("https://scholar.google.com/scholar?start=" + pageNo + "&q=" + qVal
		//						+ "&hl=en&as_sdt=0%2C5&as_ylo=2015&as_yhi=2022");
		//
		//				Thread.sleep(2000);
		//
		//				List<WebElement> allLinks = driver.findElements(By.tagName("a"));
		//				WebElement link = allLinks.get(0);

		// if(!link.getText().contentEquals("Save") &&
		// !link.getText().contentEquals("Cite")) {

		// System.out.println(link.getText() + " - " + link.getAttribute("href"));
		// }
		//					if (!link.getText().contentEquals("Save") && !link.getText().contentEquals("Cite")
		//							&& !link.getAttribute("href").contains("https://scholar.google.com")) {
		//
		//						if (link.getText().length() > 15) {
		//
		//							System.out.println(link.getText() + " - " + link.getAttribute("href"));
		//							resultsLink.add(link.getAttribute("href"));
		//							resultsTitle.add(link.getText());
		//
		//						}
		//					}


		//			}

		//		}
//		for (String link : resultsLink) {
//			if (link.contains("http://doi.org")) {
//				doiList.add(link);
//			}
//
//			else {
//				driver.get(link);
//
//				Thread.sleep(2000);
//
//				List<WebElement> allLinks = driver.findElements(By.tagName("a"));
//				for (WebElement weblink : allLinks) {
//
//					if (weblink.getAttribute("href") != null
//							&& weblink.getAttribute("href").startsWith("https://doi.org")
//							&& !weblink.getText().contains("CrossRef")) {
//						System.out.println(weblink.getAttribute("href"));
//						doiList.add(weblink.getAttribute("href"));
//					} else if (link.startsWith("https://arxiv.org/") && weblink.getAttribute("href") != null
//							&& weblink.getText().contains("PDF")) {
//						pdfList.add(weblink.getAttribute("href"));
//					} else {
//						directList.add(link);
//					}
//				}
//			}
//
//		}

		driver.quit();

	}

	public void hitSciHub(Map<String, String> data, WebDriver driver) throws InterruptedException {

		Map<String, Object> prefs = new HashMap<String, Object>();
		String url = null;

		// Use File.separator as it will work on any OS
		prefs.put("download.default_directory", System.getProperty("user.dir") + File.separator + "ResearchPapers" + File.separator+ data.get("venue")
		+ File.separator + data.get("year"));
		prefs.put("plugins.always_open_pdf_externally", true);
		prefs.put("download.prompt_for_download", false);

		// Adding cpabilities to ChromeOptions
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", prefs);

		driver = new ChromeDriver(options);
		driver.get("https://sci-hub.se/");

		Thread.sleep(2000);

		if (oldTilte != null && !oldTilte.equals(data.get("title"))) {
			url = data.get("doi");
			// driver.quit();
			oldTilte = data.get("title");

		} else if (oldTilte == null) {
			url = data.get("doi");
			// driver.quit();
			oldTilte = data.get("title");
		}

		if (url != null) {
			WebElement searchBox = driver.findElement(By.id("request"));
			searchBox.clear();
			searchBox.sendKeys(data.get("doi"));
			searchBox.submit();
			Thread.sleep(2000);
			try {
				WebElement pdf = driver.findElement(By.id("pdf"));
				WebElement buttons = driver.findElement(By.id("buttons"));
				if (buttons.isDisplayed()) {
					List<WebElement> c = buttons.findElements(By.xpath("./child::*"));
					// iterate child nodes
					for (WebElement i : c) {
						i.click();
						// getText() to get text for child nodes
						System.out.println(i.getText());
					}

				}
			} catch (Exception e) {
				System.out.println("Not found in Sci Hub:" + data.get("doi"));
			}
			driver.navigate().back();

			// System.out.println(pdf.getText());
			Thread.sleep(2000);
			driver.quit();
		}

	}

	public void getPDFfiles(Map<String, String> data, WebDriver driver) {
		Map<String, Object> prefs = new HashMap<String, Object>();

		// Use File.separator as it will work on any OS
		prefs.put("download.default_directory", System.getProperty("user.dir") + File.separator +"ResearchPapers" + File.separator+ data.get("venue")
		+ File.separator + data.get("year"));
		prefs.put("plugins.always_open_pdf_externally", true);
		prefs.put("download.prompt_for_download", false);

		// Adding cpabilities to ChromeOptions
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", prefs);

		driver = new ChromeDriver(options);

		if (oldTilte != null && !oldTilte.equals(data.get("title"))) {
			driver.get(data.get("ee"));
			// driver.quit();
			oldTilte = data.get("title");

		} else if (oldTilte == null) {
			driver.get(data.get("ee"));
			// driver.quit();
			oldTilte = data.get("title");
		}

	}
}
