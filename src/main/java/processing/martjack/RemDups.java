package processing.martjack;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Vector;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

public class RemDups {

	public static void main(String[] args) throws Exception{
		Vector<Integer> ids = new Vector<>();
		Vector<Double> prices = new Vector<>();

		try(LineNumberReader r = new LineNumberReader(new FileReader("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\pids.txt"))){
			String line = null;
			
			while((line=r.readLine())!=null) {
				ids.add(Integer.parseInt(line));
			}
		}
		try(LineNumberReader r = new LineNumberReader(new FileReader("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\prices.txt"))){
			String line = null;
			
			while((line=r.readLine())!=null) {
				prices.add(Double.parseDouble(line));
			}
		}
		
		Vector<String> data = new Vector<>();
		try(LineNumberReader r = new LineNumberReader(new FileReader("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\martJackFabIndiaFeed_indexed_popularity.json"))){
			String line = null;
			
			while((line=r.readLine())!=null) {
				data.add(line);
			}
			
		}
		
		for(int i=1; i<data.size() ; i+=2) {
			JsonObject obj = Json.parse(data.get(i)).asObject();
			int pid = obj.getInt("ns1_id", 0);
			obj.set("ns1_price", prices.get(ids.indexOf(pid)));
			
			data.set(i, obj.toString());
		}
		
		try(PrintWriter pw = new PrintWriter("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\martJackFabIndiaFeed_indexed_popularity.json")){
			for(int i=0; i < data.size() ; i++)
				pw.println(data.get(i));
		}
		
	}

}
