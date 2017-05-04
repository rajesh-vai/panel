package processing.martjack;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Vector;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

public class Data {

	public static void main(String[] args) throws Exception{

		Vector<String> data = new Vector<>();
		try(LineNumberReader r = new LineNumberReader(new FileReader("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\FashionFeed_Data.json"))){
			String line = null;

			while((line=r.readLine())!=null) {
				data.add(line);
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


		try(PrintWriter pw = new PrintWriter("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\FashionFeed_Data_Sections.json")){
			for(String s : headers) {
				if(s.equals("ns1_price")) continue;
				pw.println("[ "+s+" ]");
				pw.println("==========");

				TreeSet<String> set = new TreeSet<>();
				for(int i=0; i<data.size() ; i+=1) {
					JsonObject obj = Json.parse(data.get(i)).asObject();
					set.add(obj.get(s).toString());
				}
				for(String ss : set)
					pw.println(ss);
				pw.println("**********");
			}
		}
		try(PrintWriter pw = new PrintWriter("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\FashionFeed_Data_Reference.json")){
			for(int i=0; i<data.size() ; i+=1) {
				JsonObject obj = Json.parse(data.get(i)).asObject();
				for(String s : headers) {
					pw.print(obj.get(s)+"\t\t\t");
				}
				pw.println();
			}
		}

	}

}
