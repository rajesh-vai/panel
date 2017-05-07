package search;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;

import java.util.HashSet;
import java.util.Map;

import javax.inject.Inject;


@RestController
@RequestMapping(value = "/")
public class SearchMainController {

	//	private static final String URL = "http://%s:%s/jabong/_search?size=250";
	private static final String URL = "http://%s:%s/%s/_search?";



	@Inject
	ApplicationContext context;
	ReadProperties properties = new ReadProperties();
//	@Value("${elastichost}")
	private String elastichost=properties.getPropValues("ELASTICHOST");

//	@Value("${elasticport}")
	private String elasticport=properties.getPropValues("ELASTICPORT");

//	@Value("${resourceLocation}")
	public String resourceLocation=properties.getPropValues("RESOURCES_LOCATION");

//	@Value("${indexName}")
	public String indexName=properties.getPropValues("INDEXNAME");

	RestTemplate restTemplate = new RestTemplate();

	DQueryProcessor qp = null;

	public SearchMainController() throws Exception{
	}
	
	@RequestMapping(value = "watcher/update", method = RequestMethod.GET)
	public void watcherUpdate()  throws Exception{
		DQueryProcessor.instance(resourceLocation).loadSynonyms(resourceLocation);
		DQueryProcessor.instance(resourceLocation).readNoiseList(resourceLocation);
	}
	
	@RequestMapping(value = "search", method = RequestMethod.GET)
	public String searchMartJack_FabIndia(@RequestParam(value="query") String query)  {
		String parsedQuery="",url="";
		try {
			qp = DQueryProcessor.instance(resourceLocation);
//			indexName = "martjack_fabindia";
			parsedQuery = qp.process(indexName,query);
			url = String.format(URL, elastichost,elasticport,indexName);
		} catch (Exception e) {
			e.printStackTrace();
		}

//		parsedQuery = parsedQuery.replaceAll("\"price\":", "\"ns1_price\":");
		System.out.println(parsedQuery);

		return restTemplate.postForObject(url, parsedQuery, String.class);
	}

	@RequestMapping(value = "search/ui", method = RequestMethod.GET)
	public String searchMartJack_FabIndia_UI(@RequestParam(value="query") String query) throws Exception{
		DQueryProcessor qp = null;
		String parsedQuery="",url="";
		try {
//			indexName = "martjack_fabindia";
			qp = DQueryProcessor.instance(resourceLocation);
			parsedQuery = qp.process(indexName,query);
			url = String.format(URL, elastichost,elasticport,indexName);
		} catch (Exception e) {
			e.printStackTrace();
		}

//		parsedQuery = parsedQuery.replaceAll("\"price\":", "\"ns1_price\":");
		System.out.println(parsedQuery);

		JsonObject results = Json.parse(restTemplate.postForObject(url, parsedQuery, String.class).toString()).asObject();
		
		JsonArray baseRes = results.get("hits").asObject().get("hits").asArray();
		JsonArray coreRes = new JsonArray();
		for(JsonValue v : baseRes) {
			double score = v.asObject().get("_score").asDouble();
			if(score > 2) {
				JsonObject eO = v.asObject().get("_source").asObject();
				eO.remove("s_n_s_tags");
				coreRes.add(eO);
			}
		}
		
		String[] headers = {
				"ns1_title",
				"ns1_price",
				"ns1_product_type",
				"ns1_material",
				"Fabric(Predefined)",
				"Style(Predefined)",
				"ns1_color",
				"Neck(Predefined)",
				"Craft(Predefined)",
				"Fit(Predefined)",
				"ns1_pattern",
				"Sleeves(Predefined)"};
		
		StringBuilder sb_table = new StringBuilder("<html><head><style>table {\n" + 
				"color: #333; " + 
				"font-family: Trebuchet MS; " + 
				"width: 640px; " + 
				"border-collapse: " + 
				"collapse; border-spacing: 5; " + 
				"} " + 
				" " + 
				"td, th { border: 1px solid #C2DFFF; height: 25px; } " + 
				"tr:nth-child(even) {background: #FFF}" + 
				"tr:nth-child(odd) {background: #F3F3F3}" + 
				"th {\n" + 
				"background: #7385AA;"
				+ "color:#FFF;"
				+ "padding:5px;" + 
				"font-weight: bold; " + 
				"}" + 
				"" + 
				"td {" + 
				"padding:5px;" + 
				"text-align: left; " + 
				"}" + 
				"</style></head><body><table id=products>");
		
		sb_table.append("<tr>");
		for(int i=0;i<headers.length;i++)
			sb_table.append("<th><p> "+headers[i]+" </p></th>");
		sb_table.append("</tr>");

		for(JsonValue value : coreRes) {
			JsonObject row = value.asObject();
			sb_table.append("<tr>");
			for(int i=0;i<headers.length;i++)
				sb_table.append("<td nowrap>"+row.get(headers[i]).toString()+"</td>");
			sb_table.append("</tr>");
		}
		sb_table.append("</table></body></html>");
		
		return Jsoup.parse(sb_table.toString()).toString();
	}

}
