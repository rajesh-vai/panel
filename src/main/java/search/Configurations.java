package search;

import com.cedarsoftware.util.io.JsonWriter;
import org.json.JSONObject;

import java.io.PrintWriter;

public class Configurations {

	JSONObject confObject = new JSONObject();
	
	public static void main(String[] args) throws Exception{
		Configurations configurations = new Configurations();
		configurations.process();
		configurations.saveConfigurations("D:/test/configurations.json");
	}
	
	public void process() throws Exception{
		JSONObject conf = new JSONObject();
		
		String ignore_fields[] = {"url","model"};
		String sort_fields[] = {"_score","popularity"};
		conf.put("ignore_fields", ignore_fields);
		conf.put("sort_fields", sort_fields);
		JSONObject range_mappings = new JSONObject();
		conf.put("range_mappings", range_mappings);
//        range_mappings.put("price", new String[]{"0.00","min",""});
        /*range_mappings.put("primary_camera", new String[]{"0.05","max","mp"});
		range_mappings.put("secondary_camera", new String[]{"0.05","min","mp"});
		range_mappings.put("ram", new String[]{"0.05","min","gb"});
		range_mappings.put("internal_memory", new String[]{"0.05","max","gb"});
		range_mappings.put("display", new String[]{"0.05","min","inch","\""});
		range_mappings.put("battery", new String[]{"100.0","min","mah"});

		range_mappings.put("dimension_height", new String[]{"0.05","min","inch"});
		range_mappings.put("dimension_length", new String[]{"0.05","min","inch"});
		range_mappings.put("dimension_breadth", new String[]{"0.05","min","inch"});*/

		range_mappings.put("primary_camera", new String[]{"0.05","max","mp"});
		range_mappings.put("hard_disk_capacity", new String[]{"0.05","min","gb"});
		range_mappings.put("internal_storage", new String[]{"0.05","min","gb"});
		range_mappings.put("optical_zoom", new String[]{"0.05","min","x"});
		range_mappings.put("mega_pixel", new String[]{"0.05","min","mp"});
		range_mappings.put("capacity", new String[]{"0.05","min","mp"});
		range_mappings.put("display_size", new String[]{"0.05","min","inch"});
		range_mappings.put("system_memory", new String[]{"0.05","min","gb"});
		range_mappings.put("screen_size", new String[]{"0.05","min","inch"});
		range_mappings.put("display_range", new String[]{"0.05","min","inch"});
		range_mappings.put("ram", new String[]{"0.05","min","gb"});
		confObject.put("configurations", conf);
	}
	
	public void saveConfigurations(String fileName) throws Exception{
		PrintWriter pw = new PrintWriter(fileName);
		pw.println(JsonWriter.formatJson(confObject.toString()));
		pw.close();
	}

}
