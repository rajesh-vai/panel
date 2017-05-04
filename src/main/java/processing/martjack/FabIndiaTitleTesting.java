package processing.martjack;

import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Arrays;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

public class FabIndiaTitleTesting {

	public static void main(String[] args) throws Exception{
		FabIndiaTitleTesting extract = new FabIndiaTitleTesting();
		extract.testTitles();
//		extract.testListTitles();
	}
	
	public void testListTitles() throws Exception{
		JsonArray fabIndiaFeed = Json.parse(new FileReader("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\martJackFabIndiaFeed.json")).asArray();
		
		try(PrintWriter pw = new PrintWriter("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\fabIndiaTitles.txt") ){
			for(int i=0; i < fabIndiaFeed.size() ; i++) {
				String title = fabIndiaFeed.get(i).asObject().getString("ns1_title", "");
				pw.println(title);
			}
		}
	}
	
	public void testTitles() throws Exception{
		JsonArray fabIndiaFeed = Json.parse(new FileReader("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\martJackFabIndiaFeed.json")).asArray();
		
		try(PrintWriter pw = new PrintWriter("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\nonworkingTitles.txt")){
			int total_success=0;
			for(int i=0; i < fabIndiaFeed.size() ; i++) {
				String title = fabIndiaFeed.get(i).asObject().getString("ns1_title", "");
//				Document doc = Jsoup.connect("http://52.163.59.249:8081/search/martjack/fabindia?query="+title).get();
				Document doc = Jsoup.connect("http://localhost:9200/_sql?sql=select * from martjack_fabindia where ns1_id='11179996'")
						.ignoreContentType(true).get();
				JsonObject jsonResult = Json.parse(doc.select("body").text()).asObject();

				JsonArray resultsArray = jsonResult.get("hits").asObject().get("hits").asArray();
				boolean found_title=false;
				for(int j=0; j < 5 ; j++) {
					String r_title = resultsArray.get(j).asObject().get("_source").asObject().getString("ns1_title", "");
					if(r_title.equals(title)) {
						total_success++;
						found_title = true;
						break;
					}
				}	
				if(found_title)
					pw.println(title);
			}
			System.out.println(Arrays.asList(total_success));
		}

		
	}
}
