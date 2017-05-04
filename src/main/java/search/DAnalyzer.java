package search;

import org.json.JSONObject;

import java.io.*;
import java.util.*;

import org.apache.commons.lang.math.NumberUtils;

public class DAnalyzer {

	private static String feedFileToAnalyze="D:\\Data\\ClientFeed\\MartJack\\Lulu\\JSON\\martJack_Interim.json";
	private static String fieldObjectsDestination="D:\\Data\\ClientFeed\\MartJack\\Lulu\\JSON\\processed\\FieldObjects.d";
	private static String indexFileLocation = "D:\\Data\\ClientFeed\\MartJack\\Lulu\\JSON\\processed\\index.json";
	
	public static void main(String[] args) throws Exception{
		
		DAnalyzer analyzer = new DAnalyzer();
		
		analyzer.analyze(feedFileToAnalyze);
	}
	
	private void analyze(String file_name) throws Exception{
		HashMap<String, HashSet<String>> stringFields = new HashMap<>();
		HashMap<String, TreeSet<Double>> doubleFields = new HashMap<>();
		try(LineNumberReader reader = new LineNumberReader(new FileReader(file_name))){
			String line = null;
			
			while((line=reader.readLine())!=null){
				line = line.toLowerCase();
				JSONObject object = new JSONObject(line);
				String[] keys = JSONObject.getNames(object);

				for (String key : keys)
				{
					Object value = object.get(key);
				    
					if(!(NumberUtils.isNumber(value.toString())) || stringFields.keySet().contains(key)){
						HashSet<String> pre = stringFields.get(key);
				    	if(pre!=null){
				    		String words[] = value.toString().split("\\W+");
				    		pre.addAll(Arrays.asList(words));
				    	}else{
				    		pre = new HashSet<String>();
				    		String words[] = value.toString().split("\\W+");
				    		pre.addAll(Arrays.asList(words));
				    		stringFields.put(key, pre);
				    	}
				    }else{
						TreeSet<Double> pre = doubleFields.get(key);
				    	if(pre!=null){
				    			pre.add(Double.parseDouble(value.toString()));
				    	}else{
				    		pre = new TreeSet<Double>();
				    		pre.add(Double.parseDouble(value.toString()));
				    		doubleFields.put(key, pre);
				    	}
				    }
				}


			}

			saveFieldData(stringFields, doubleFields,fieldObjectsDestination);
			createIndexFile(stringFields, doubleFields,indexFileLocation);
			
		}
	}

	private void saveFieldData(HashMap<String, HashSet<String>> stringFields,
			HashMap<String, TreeSet<Double>> doubleFields, String fileName) throws Exception{
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName));

		Map<String,HashSet<String>> orderedStringFields = new LinkedHashMap<>();
		HashSet<String> modelList = stringFields.get("model");
		if(modelList != null){
			orderedStringFields.put("model",modelList);
		}
		System.out.println(modelList);
		orderedStringFields.putAll(stringFields);

		objectOutputStream.writeObject(orderedStringFields);
		objectOutputStream.writeObject(doubleFields);
		objectOutputStream.flush();
		objectOutputStream.close();
	}

	private void createIndexFile(HashMap<String, HashSet<String>> stringFields,
			HashMap<String, TreeSet<Double>> doubleFields, String fileName) throws Exception{
		JSONObject index_file = new JSONObject();

		JSONObject index = new JSONObject();
		JSONObject product = new JSONObject();
		JSONObject _all = new JSONObject();
		JSONObject product_properties = new JSONObject();

		index.put("number_of_shards", 1);
		index.put("number_of_replicas", 1);
		
		_all.put("enabled", "true");
		
		for(String key : stringFields.keySet()){
			JSONObject field_type = new JSONObject();
			field_type.put("type", "string");
			product_properties.put(key, field_type);
		}

		for(String key : doubleFields.keySet()){
			JSONObject field_type = new JSONObject();
			field_type.put("type", "double");
			product_properties.put(key, field_type);
			
		}
		
		product.put("_all", _all);
		product.put("properties", product_properties);
		
		
		index_file.put("settings", index);
		index_file.put("mappings", product);
		
		PrintWriter pw = new PrintWriter(fileName);
		pw.println(index_file);
		pw.close();
		
	}

}
