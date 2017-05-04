package processing.martjack;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Vector;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

public class ResetPopularity {

	public static void main(String[] args) throws Exception{
		
		Vector<String> data = new Vector<>();
		try(LineNumberReader r = new LineNumberReader(new FileReader("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\FashionFeed_indexed_popularity.json"))){
			String line = null;
			
			while((line=r.readLine())!=null) {
				data.add(line);
			}
			
		}
		
		for(int i=1; i<data.size() ; i+=2) {
			JsonObject obj = Json.parse(data.get(i)).asObject();
			obj.set("popularity", 0.0);
			
			data.set(i, obj.toString());
		}
		
		try(PrintWriter pw = new PrintWriter("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\FashionFeed_indexed_popularity.json")){
			for(int i=0; i < data.size() ; i++)
				pw.println(data.get(i));
		}
		
	}

}
