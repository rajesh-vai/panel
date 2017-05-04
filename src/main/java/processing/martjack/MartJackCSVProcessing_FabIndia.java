package processing.martjack;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.math.NumberUtils;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import au.com.bytecode.opencsv.CSVReader;

public class MartJackCSVProcessing_FabIndia {

	public static void main(String[] args) throws Exception {
		MartJackCSVProcessing_FabIndia martJack = new MartJackCSVProcessing_FabIndia();
		//		martJack.processAllCSVs("D:\\Data\\ClientFeed\\MartJack\\FabIndia");
		//		martJack.re_process("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\martJackFabIndia_generic.json");
		//		martJack.preparedIndexedFeed();
		//		martJack.addPopularitySignals("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\FabIndia_SortRanking.csv");
		martJack.calculatePopularity();
	}
	private void calculatePopularity() throws Exception{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Vector<String> data = new Vector<>();
		Vector<Double> page_views = new Vector<>();
		Vector<Double> orders = new Vector<>();
		Vector<Double> added_to_cart_count = new Vector<>();
		Vector<Double> number_of_reviews = new Vector<>();
		Vector<Double> average_rating = new Vector<>();
		Vector<Date> product_published_date = new Vector<>();
		Vector<Double> popularity = new Vector<>();

		try(LineNumberReader reader = new LineNumberReader(new FileReader("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\martJackFabIndiaFeed_indexed.json"))){
			String line=null;
			while((line=reader.readLine())!=null) {
				data.add(line);
			}
		}

		Vector<Integer> idx = new Vector<>(); 
		for(int i=1; i < data.size() ; i+=2) {
			try {
				JsonObject p = Json.parse(data.get(i)).asObject();
				page_views.add(p.get("page_views").asDouble());
				orders.add(p.get("orders").asDouble());
				added_to_cart_count.add(p.get("added_to_cart_count").asDouble());
				number_of_reviews.add(p.get("number_of_reviews").asDouble());
				average_rating.add(p.get("average_rating").asDouble());
				product_published_date.add(dateFormat.parse(p.get("product_published_date").asString()));
			}catch(Exception e) {
				data.set(i, "");
				data.set(i-1,"");
				idx.add(i);
				idx.add(i-1);
			}
		}
		for(int i=0; i < idx.size() ;i++)
			data.remove(idx.get(i));

		Date cur_date = Calendar.getInstance().getTime();
		for(int i=0; i < page_views.size() ; i+=1) {
			double numerator = page_views.get(i)
					+orders.get(i)
					+added_to_cart_count.get(i)
					+number_of_reviews.get(i)
					+average_rating.get(i);

			long denominator = cur_date.getTime() - product_published_date.get(i).getTime();
			denominator = TimeUnit.DAYS.convert(denominator, TimeUnit.MILLISECONDS);
			double D = denominator;

			popularity.add(numerator/D);
		}		

		for(int i=1,j=0; i < data.size() ; i+=2,j++) {
			try {
				JsonObject p = Json.parse(data.get(i)).asObject();
				p.set("popularity", popularity.get(j));
				data.set(i, p.toString());
				System.out.println(data.get(i));
			}catch(Exception e) {

			}
		}

		try(PrintWriter pw = new PrintWriter("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\martJackFabIndiaFeed_indexed_popularity.json")){
			for(int i=0; i < data.size() ; i++) {
				String line = data.get(i);
				if(line.length()>0)
					pw.println(line);
			}
		}

	}
	private void addPopularitySignals(String popularity_file_name) throws Exception{
		JsonArray prods = new JsonArray();
		try(LineNumberReader reader = new LineNumberReader(new FileReader("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\martJackFabIndia_interim.json"))){
			String line = null;
			while((line=reader.readLine())!=null) {
				JsonObject p = Json.parse(line).asObject();
				prods.add(p);
			}
		}
		try(PrintWriter pw = new PrintWriter("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\martJackFabIndiaFeed_indexed.json")){
			try(CSVReader csv_reader = new CSVReader(new FileReader(popularity_file_name))){
				String[] headers = csv_reader.readNext();
				String[] field_values = null;

				while((field_values = csv_reader.readNext()) != null) {
					long f_id = Long.parseLong(field_values[0]);
					for(int i=0; i < prods.size() ; i++) {
						JsonObject p = prods.get(i).asObject();
						long p_id = p.get("ns1_id").asLong();
						if(p_id == f_id) {
							for(int m=1; m < headers.length-1 ; m++) {
								if(field_values[m].length()>0)
									p.add(headers[m], Double.parseDouble(field_values[m]));
								else
									p.add(headers[m], 0);
							}
							p.add(headers[headers.length-1], field_values[headers.length-1]);
							p.add("rank", 0);
							p.add("popularity", 0);
						}
					}
				}
			}
			HashSet<Long> ids = new HashSet<>();
			for(int i=0; i < prods.size() ; i++) {
				long pid = prods.get(i).asObject().get("ns1_id").asLong();
				if(ids.contains(pid)) continue;
				ids.add(pid);
				pw.println("{\"index\":{\"_index\":\"martjack_fabindia\",\"_type\":\"product\", \"_id\": "+ pid +" }}");
				pw.println(prods.get(i));
			}
		}
	}
	public void preparedIndexedFeed() throws Exception{
		try(PrintWriter writer = new PrintWriter("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\martJackFabIndiaFeed_indexed.json")){
			try(LineNumberReader reader = new LineNumberReader(new FileReader("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\martJackFabIndia_Interim.json"))){
				String line = null;
				long i =0;
				while((line=reader.readLine()) != null) {
					writer.println("{\"index\":{\"_index\":\"martjack_fabindia\",\"_type\":\"product\"}}");
					writer.println(line);
					writer.println();
				}
			}
		}
	}
	public void processAllCSVs(String csv_directory) throws Exception{
		File myDirectory = new File(csv_directory);
		String[] containingFileNames = myDirectory.list();

		for (String fileName : containingFileNames) {
			if (fileName.endsWith(".csv") && !fileName.contains("PredefinedValues")) {
				processCSV(csv_directory+"\\"+fileName);
			}
		}
		re_process("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\martJackFabIndia_generic.json");

		try(PrintWriter writer = new PrintWriter("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\martJackFabIndiaFeed.json")){
			writer.println("[");
			try(LineNumberReader reader = new LineNumberReader(new FileReader("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\martJackFabIndia_interim.json"))){
				String line = null;
				long i =0;
				while(true) {
					if(line != null) writer.print(line);
					line = reader.readLine();
					if(line == null) break;
					else {
						if(i!=0)
							writer.println(",");
						if(i==0) i=1;
					}
				}
			}
			writer.println("]");
		}
	}
	public void processCSV(String csv_file_path) throws Exception{
		try(PrintWriter writer = new PrintWriter(new FileWriter("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\martJackFabIndia_generic.json",true))){
			try(CSVReader reader = new CSVReader(new FileReader(csv_file_path))){
				String[] headers = reader.readNext();

				String[] fieldValues = null;
				while((fieldValues = reader.readNext())!=null){
					JsonObject product = new JsonObject();
					for(int i=0 ; i < headers.length ; i++)
					{
						if(headers[i].trim().length()>0) {
							try {
								if(NumberUtils.isNumber(fieldValues[i].toString()))
									product.add(headers[i].replaceAll(" ", "_").replaceAll(":", "_"),Double.parseDouble(fieldValues[i]));
								else
									product.add(headers[i].replaceAll(" ", "_").replaceAll(":", "_"),fieldValues[i]);
							}catch(Exception exp) {
								product.add(headers[i].replaceAll(" ", "_").replaceAll(":", "_"),fieldValues[i]);
							}
						}
					}

					String extr = product.get("ns1_product_type").toString();
					System.out.println(extr);
					String extr2 = extr.substring(extr.indexOf('>')+1);
					extr2 = extr2.substring(0,extr2.indexOf('>'));
					extr = extr.substring(extr.lastIndexOf('>')+1).trim();
					product.add("ns1_product_type_extr", extr2+" "+extr);
					writer.println(product);
					//					System.out.println(product);
					//					break;
				}
			}
		}
	}
	public void re_process(String generic_feed_file) throws Exception{
		HashMap<String, HashSet<String>> field_map = new HashMap<>();
		try(PrintWriter writer = new PrintWriter(new FileWriter("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\martJackFabIndia_interim.json"))){
			try(LineNumberReader reader = new LineNumberReader(new FileReader(generic_feed_file))){
				String[] headers = {
						"ns1_material",
						"ns1_title",
						"Style(Predefined)",
						"Fabric(Predefined)",
						"ns1_color",
						"ns1_product_type_extr",
						"ns1_product_type",
						"Neck(Predefined)",
						"Craft(Predefined)",
						"Fit(Predefined)",
						"ns1_pattern",
						"Category(Predefined)",
				"Sleeves(Predefined)"};

				String line = null;

				while((line = reader.readLine())!=null){
					HashSet<String> bow = new HashSet<>();
					JsonObject product = Json.parse(line).asObject();
					for(int i=0 ; i < headers.length ; i++)
					{
						HashSet<String> set = field_map.get(headers[i]);
						if(set == null) {
							set = new HashSet<String>();
							field_map.put(headers[i], set);
						}
						String val = product.get(headers[i]).toString();
						if(headers[i].equals("ns1_title"))
							bow.addAll(Arrays.asList(val.split("\\W+")));
						set.addAll(Arrays.asList(val.split("\\W+")));
					}
					product.add("s_n_s_tags", bow.toString());
					writer.println(product);
					//					System.out.println(product);
					//					break;
				}
			}
		}
		try(PrintWriter pw = new PrintWriter("D:\\Data\\ClientFeed\\MartJack\\FabIndia\\JSON\\martJackFabIndia_field_map.json")){
			JsonObject obj = new JsonObject();
			for(String key : field_map.keySet()) {
				obj.add(key, field_map.get(key).toString());
			}
			pw.println(obj);
		}
	}

}
