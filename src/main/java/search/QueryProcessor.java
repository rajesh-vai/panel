package search;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryProcessor {
	private static QueryProcessor instance = null;
	HashMap<String, String> matchQuery = null;
	HashMap<String, String> regexList = null;
	HashMap<String, Pattern> regexPatterns = null;
	private QueryProcessor() {
		init();
	}
	public static QueryProcessor instance(){
		if(instance == null) 
			instance = new QueryProcessor();
		return instance;
	}
	public static void main(String[] args) {

		System.out.println(QueryProcessor.instance().process("lenovo price between 10000 and 20000 "));

	}
	public String process(String query){
		StringBuilder finalQuery = new StringBuilder("{\"query\":{\"bool\":{\"should\": [");
		Pattern pattern = null;
		Matcher m = null;
		query = query.toLowerCase();
//		finalQuery.append(String.format(matchQuery.get("url")+",",query));
//		finalQuery.append(String.format(matchQuery.get("title")+",",query));
//		finalQuery.append(String.format(matchQuery.get("brand")+",",query));
//		finalQuery.append(String.format(matchQuery.get("model")+",",query));
//		finalQuery.append(String.format(matchQuery.get("category")+",",query));
//		finalQuery.append(String.format(matchQuery.get("category_path")+",",query));
//		finalQuery.append(String.format(matchQuery.get("specs"),query));
		finalQuery.append(String.format(matchQuery.get("simplified"),query));
		finalQuery.append(priceQuery(query));
		finalQuery.append(primaryCameraQuery(query));
		finalQuery.append(secondaryCameraQuery(query));
		finalQuery.append(batteryQuery(query));
		finalQuery.append(displayQuery(query));
		finalQuery.append(internalMemoryQuery(query));
		finalQuery.append(ramQuery(query));

		return finalQuery.append("]}}}").toString();
	}
	private String priceQuery(String query){
		String result = "";
		Matcher m = null;
		int count =0;
		
		m = regexPatterns.get("priceRegexBetween").matcher(query);
		count = m.groupCount();
		if(count==8 && m.find()){
			return String.format(","+matchQuery.get("price"), "\"gte\": "+m.group(count-4)+", \"lte\": "+m.group(count-1)+"");
		}
		
		m = regexPatterns.get("priceRegexAbove").matcher(query);
		count = m.groupCount();
		if(count==5 && m.find()){
			return String.format(","+matchQuery.get("price"), "\"gte\": "+m.group(count-1)+"");
		}

		m = regexPatterns.get("priceRegexBelow").matcher(query);
		count = m.groupCount();
		if(count==5 && m.find()){
			return String.format(","+matchQuery.get("price"), "\"lte\": "+m.group(count-1)+"");
		}

		m = regexPatterns.get("priceRegexAround").matcher(query);
		count = m.groupCount();
		if(count==5 && m.find()){
			return String.format(","+matchQuery.get("price"), "\"gte\": "+(Double.parseDouble(m.group(count-1))-500)+", \"lte\": "+(Double.parseDouble(m.group(count-1))+500)+"");
		}

		return result;
	}
	private String primaryCameraQuery(String query){
		String result = "";
		Matcher m = null;
		int count =0;
		
		m = regexPatterns.get("primaryCameraRegexBetween").matcher(query);
		count = m.groupCount();
		if(count==13 && m.find()){
			return String.format(","+matchQuery.get("primary_camera"), "\"gte\": "+m.group(count-6)+", \"lte\": "+m.group(count-2)+"");
		}
		
		m = regexPatterns.get("primaryCameraRegexAbove").matcher(query);
		count = m.groupCount();
		if(count==9 && m.find()){
			return String.format(","+matchQuery.get("primary_camera"), "\"gte\": "+m.group(count-2)+"");
		}

		m = regexPatterns.get("primaryCameraRegexBelow").matcher(query);
		count = m.groupCount();
		if(count==9 && m.find()){
			return String.format(","+matchQuery.get("primary_camera"), "\"lte\": "+m.group(count-2)+"");
		}

		m = regexPatterns.get("primaryCameraRegexAround").matcher(query);
		count = m.groupCount();
		if(count==9 && m.find()){
			return String.format(","+matchQuery.get("primary_camera"), "\"gte\": "+(Double.parseDouble(m.group(count-2))- 0.5)+", \"lte\": "+(Double.parseDouble(m.group(count-2))+ 0.5)+"");
		}

		return result;
	}
	private String secondaryCameraQuery(String query){
		String result = "";
		Matcher m = null;
		int count =0;
		
		m = regexPatterns.get("secondaryCameraRegexBetween").matcher(query);
		count = m.groupCount();
		if(count==13 && m.find()){
			return String.format(","+matchQuery.get("secondary_camera"), "\"gte\": "+m.group(count-6)+", \"lte\": "+m.group(count-2)+"");
		}
		
		m = regexPatterns.get("secondaryCameraRegexAbove").matcher(query);
		count = m.groupCount();
		if(count==9 && m.find()){
			return String.format(","+matchQuery.get("secondary_camera"), "\"gte\": "+m.group(count-2)+"");
		}

		m = regexPatterns.get("secondaryCameraRegexBelow").matcher(query);
		count = m.groupCount();
		if(count==9 && m.find()){
			return String.format(","+matchQuery.get("secondary_camera"), "\"lte\": "+m.group(count-2)+"");
		}

		m = regexPatterns.get("secondaryCameraRegexAround").matcher(query);
		count = m.groupCount();
		if(count==9 && m.find()){
			return String.format(","+matchQuery.get("secondary_camera"), "\"gte\": "+(Double.parseDouble(m.group(count-2))- 0.5)+", \"lte\": "+(Double.parseDouble(m.group(count-2))+ 0.5)+"");
		}

		return result;
	}
	private String batteryQuery(String query){
		String result = "";
		Matcher m = null;
		int count =0;
		
		m = regexPatterns.get("batteryRegexBetween").matcher(query);
		count = m.groupCount();
		if(count==9 && m.find()){
			return String.format(","+matchQuery.get("battery"), "\"gte\": "+m.group(count-6)+", \"lte\": "+m.group(count-2)+"");
		}
		
		m = regexPatterns.get("batteryRegexAbove").matcher(query);
		count = m.groupCount();
		if(count==5 && m.find()){
			return String.format(","+matchQuery.get("battery"), "\"gte\": "+m.group(count-2)+"");
		}

		m = regexPatterns.get("batteryRegexBelow").matcher(query);
		count = m.groupCount();
		if(count==5 && m.find()){
			return String.format(","+matchQuery.get("battery"), "\"lte\": "+m.group(count-2)+"");
		}

		m = regexPatterns.get("batteryRegexAround").matcher(query);
		count = m.groupCount();
		if(count==5 && m.find()){
			return String.format(","+matchQuery.get("battery"), "\"gte\": "+(Double.parseDouble(m.group(count-2))- 500)+", \"lte\": "+(Double.parseDouble(m.group(count-2))+ 500)+"");
		}
		return result;
	}
	private String displayQuery(String query){
		String result = "";
		Matcher m = null;
		int count =0;
		
		m = regexPatterns.get("displayRegexBetween").matcher(query);
		count = m.groupCount();
		if(count==14 && m.find()){
			return String.format(","+matchQuery.get("display"), "\"gte\": "+m.group(count-6)+", \"lte\": "+m.group(count-2)+"");
		}
		
		m = regexPatterns.get("displayRegexAbove").matcher(query);
		count = m.groupCount();
		if(count==10 && m.find()){
			return String.format(","+matchQuery.get("display"), "\"gte\": "+m.group(count-2)+"");
		}

		m = regexPatterns.get("displayRegexBelow").matcher(query);
		count = m.groupCount();
		if(count==10 && m.find()){
			return String.format(","+matchQuery.get("display"), "\"lte\": "+m.group(count-2)+"");
		}

		m = regexPatterns.get("displayRegexAround").matcher(query);
		count = m.groupCount();
		if(count==10 && m.find()){
			return String.format(","+matchQuery.get("display"), "\"gte\": "+(Double.parseDouble(m.group(count-2))- 0.5)+", \"lte\": "+(Double.parseDouble(m.group(count-2))+ 0.5)+"");
		}
		return result;
	}
	private String internalMemoryQuery(String query){
		String result = "";
		Matcher m = null;
		int count =0;
		
		m = regexPatterns.get("internalMemoryRegexBetween").matcher(query);
		count = m.groupCount();
		if(count==13 && m.find()){
			return String.format(","+matchQuery.get("internal_memory"), "\"gte\": "+m.group(count-6)+", \"lte\": "+m.group(count-2)+"");
		}
		
		m = regexPatterns.get("internalMemoryRegexAbove").matcher(query);
		count = m.groupCount();
		if(count==9 && m.find()){
			return String.format(","+matchQuery.get("internal_memory"), "\"gte\": "+m.group(count-2)+"");
		}

		m = regexPatterns.get("internalMemoryRegexBelow").matcher(query);
		count = m.groupCount();
		if(count==9 && m.find()){
			return String.format(","+matchQuery.get("internal_memory"), "\"lte\": "+m.group(count-2)+"");
		}

		m = regexPatterns.get("internalMemoryRegexAround").matcher(query);
		count = m.groupCount();
		if(count==9 && m.find()){
			return String.format(","+matchQuery.get("internal_memory"), "\"gte\": "+(Double.parseDouble(m.group(count-2))- 1)+", \"lte\": "+(Double.parseDouble(m.group(count-2))+ 1)+"");
		}
		return result;
	}
	private String ramQuery(String query){
		String result = "";
		Matcher m = null;
		int count =0;
		
		m = regexPatterns.get("ramRegexBetween").matcher(query);
		count = m.groupCount();
		if(count==10 && m.find()){
			return String.format(","+matchQuery.get("ram"), "\"gte\": "+m.group(count-6)+", \"lte\": "+m.group(count-2)+"");
		}
		
		m = regexPatterns.get("ramRegexAbove").matcher(query);
		count = m.groupCount();
		if(count==6 && m.find()){
			return String.format(","+matchQuery.get("ram"), "\"gte\": "+m.group(count-2)+"");
		}

		m = regexPatterns.get("ramRegexBelow").matcher(query);
		count = m.groupCount();
		if(count==6 && m.find()){
			return String.format(","+matchQuery.get("ram"), "\"lte\": "+m.group(count-2)+"");
		}

		m = regexPatterns.get("ramRegexAround").matcher(query);
		count = m.groupCount();
		if(count==6 && m.find()){
			return String.format(","+matchQuery.get("ram"), "\"gte\": "+(Double.parseDouble(m.group(count-2))- 1)+", \"lte\": "+(Double.parseDouble(m.group(count-2))+ 1)+"");
		}
		return result;
	}
	private void init(){
		//====================
		matchQuery = new HashMap<String, String>();
		matchQuery.put("simplified", "{\"query\":{\"bool\":{\"should\":[{\"multi_match\":{\"query\":\"%s\",\"type\":\"best_fields\",\"fields\":[\"url\",\"title\",\"brand^11\",\"model\",\"category\",\"category_path\",\"specs\"]}}]}}}");
		matchQuery.put("url", "{ \"match\": { \"url\": \"%s\" }}");
		matchQuery.put("title", "{ \"match\": { \"title\": \"%s\" }}");
		matchQuery.put("brand", "{ \"match\": { \"brand\": \"%s\" }}");
		matchQuery.put("model", "{ \"match\": { \"model\": \"%s\" }}");
		matchQuery.put("category", "{ \"match\": { \"category\": \"%s\" }}");
		matchQuery.put("category_path", "{ \"match\": { \"category_path\": \"%s\" }}");
		matchQuery.put("specs", "{ \"match\": { \"specs\": \"%s\" }}");
		matchQuery.put("price", "{\"range\": {\"price\": {%s}}}");
		matchQuery.put("primary_camera", "{\"range\": {\"primary_camera\": {%s}}}");
		matchQuery.put("secondary_camera", "{\"range\": {\"secondary_camera\": {%s}}}");
		matchQuery.put("battery", "{\"range\": {\"battery\": {%s}}}");
		matchQuery.put("display", "{\"range\": {\"display\": {%s}}}");
		matchQuery.put("internal_memory", "{\"range\": {\"internal_memory\": {%s}}}");
		matchQuery.put("ram", "{\"range\": {\"ram\": {%s}}}");
		//====================
		regexList = new HashMap<String, String>();
		//price
		regexList.put("priceRegexBetween", "(?i)((price |cost |costing )(between |from )(\\d+(\\.\\d+)?)( to | and )(\\d+(\\.\\d+)?))");
		regexList.put("priceRegexAbove", "(?i)((price |cost |costing )(above |more than )(\\d+(\\.\\d+)?))");
		regexList.put("priceRegexBelow", "(?i)((price |cost |costing )(below |under |less than ))(\\d+(\\.\\d+)?)");
		regexList.put("priceRegexAround", "(?i)((price |cost |costing )(around |about )?)(\\d+(\\.\\d+)?)");
		//primary camera
		regexList.put("primaryCameraRegexBetween", "(?i)((cam |camera )|((primary |front )(cam |camera )))(between |from )(\\d+(\\.\\d+)?)(mp | mp )?(to | to |and | and )(\\d+(\\.\\d+)?)(mp | mp )?");
		regexList.put("primaryCameraRegexAbove", "(?i)((cam |camera )|((primary |front )(cam |camera )))(above |more than )(\\d+(\\.\\d+)?)(mp | mp )?");
		regexList.put("primaryCameraRegexBelow", "(?i)((cam |camera )|((primary |front )(cam |camera )))(below |under |less than )(\\d+(\\.\\d+)?)(mp | mp )?");
		regexList.put("primaryCameraRegexAround", "(?i)((cam |camera )|((primary |front )(cam |camera )))(around |about )(\\d+(\\.\\d+)?)(mp | mp )?");
		//secondary camera
		regexList.put("secondaryCameraRegexBetween", "(?i)((cam |camera )|((second |secondary |back |rear )(cam |camera )))(between |from )(\\d+(\\.\\d+)?)(mp | mp )?(to | to |and | and )(\\d+(\\.\\d+)?)(mp | mp )?");
		regexList.put("secondaryCameraRegexAbove", "(?i)((cam |camera )|((second |secondary |back |rear )(cam |camera )))(above |more than )(\\d+(\\.\\d+)?)(mp | mp )?");
		regexList.put("secondaryCameraRegexBelow", "(?i)((cam |camera )|((second |secondary |back |rear )(cam |camera )))(below |under |less than )(\\d+(\\.\\d+)?)(mp | mp )?");
		regexList.put("secondaryCameraRegexAround", "(?i)((cam |camera )|((second |secondary |back |rear )(cam |camera )))(around |about )(\\d+(\\.\\d+)?)(mp | mp )?");
		//battery
		regexList.put("batteryRegexBetween", "(?i)(battery |battery capacity )(between |from )(\\d+(\\.\\d+)?)(mah | mah )?(to | to |and | and )(\\d+(\\.\\d+)?)(mah | mah )?");
		regexList.put("batteryRegexAbove", "(?i)(battery |battery capacity )(above |more than )(\\d+(\\.\\d+)?)(mah | mah )?");
		regexList.put("batteryRegexBelow", "(?i)(battery |battery capacity )(below |under |less than )(\\d+(\\.\\d+)?)(mah | mah )?");
		regexList.put("batteryRegexAround", "(?i)(battery |battery capacity )(around |about )(\\d+(\\.\\d+)?)(mah | mah )?");
		//display
		regexList.put("displayRegexBetween", "(?i)((display |screen )|(resolution )|((display |screen )(size )))(between |from )(\\d+(\\.\\d+)?)(in | inch|\" )?(to | to |and | and )(\\d+(\\.\\d+)?)(in | inch|\" )?");
		regexList.put("displayRegexAbove", "(?i)((display |screen )|(resolution )|((display |screen )(size )))(above |more than )(\\d+(\\.\\d+)?)(in | inch|\" )?");
		regexList.put("displayRegexBelow", "(?i)((display |screen )|(resolution )|((display |screen )(size )))(below |under |less than )(\\d+(\\.\\d+)?)(in | inch|\" )?");
		regexList.put("displayRegexAround", "(?i)((display |screen )|(resolution )|((display |screen )(size )))(around |about )(\\d+(\\.\\d+)?)(in | inch|\" )?");
		//internal memory
		regexList.put("internalMemoryRegexBetween", "(?i)((memory |storage )|(internal )|((memory |storage )))(between |from )(\\d+(\\.\\d+)?)(g | g |gb | gb )?(to | to |and | and )(\\d+(\\.\\d+)?)(g | g |gb | gb )?");
		regexList.put("internalMemoryRegexAbove", "(?i)((memory |storage )|(internal )|((memory |storage )))(above |more than )(\\d+(\\.\\d+)?)(g | g |gb | gb )?");
		regexList.put("internalMemoryRegexBelow", "(?i)((memory |storage )|(internal )|((memory |storage )))(below |under |less than )(\\d+(\\.\\d+)?)(g | g |gb | gb )?");
		regexList.put("internalMemoryRegexAround", "(?i)((memory |storage )|(internal )|((memory |storage )))(around |about )?(\\d+(\\.\\d+)?)(g | g |gb | gb )?");
		//ram
		regexList.put("ramRegexBetween", "(?i)((memory |ram ))(between |from )(\\d+(\\.\\d+)?)(g | g |gb | gb )?(to | to |and | and )(\\d+(\\.\\d+)?)(g | g |gb | gb )?");
		regexList.put("ramRegexAbove", "(?i)((memory |ram ))(above |more than )(\\d+(\\.\\d+)?)(g | g |gb | gb )?");
		regexList.put("ramRegexBelow", "(?i)((memory |ram ))(below |under |less than )(\\d+(\\.\\d+)?)(g | g |gb | gb )?");
		regexList.put("ramRegexAround", "(?i)((memory |ram ))(around |about )?(\\d+(\\.\\d+)?)(g | g |gb | gb )?");
		//====================
		regexPatterns = new HashMap<String, Pattern>();
		for(String regexKey : regexList.keySet()){
			regexPatterns.put(regexKey, Pattern.compile(regexList.get(regexKey)));
		}
	}
}
