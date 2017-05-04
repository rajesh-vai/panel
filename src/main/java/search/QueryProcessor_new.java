package search;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import ru.lanwen.verbalregex.VerbalExpression;

public class QueryProcessor_new {
	private String resourcePath="";
	private static QueryProcessor_new instance = null;
	HashMap<String, String> brand_model_map = new HashMap<String, String>(); 
	HashMap<String, String> matchQuery = null;
	HashMap<String, String> regexList = null;
	HashMap<String, Pattern> regexPatterns = null;
	private String query = "";
	Vector<String> brands = null;
	String mobile = "mobile mobiles";
	Vector<String> noise = null;
	Vector<String> models = null;
	Vector<String> specs = null;
	Properties regex = new Properties();
	Properties synonyms = new Properties();
	Vector<String> brand_vector = new Vector<String>();
	Vector<String> model_vector = new Vector<String>();
	Vector<String> specs_vector = new Vector<String>();

	String specs_string = "";

	VerbalExpression.Builder _space = VerbalExpression.regex().capt().space().endCapt();
	VerbalExpression.Builder _number = VerbalExpression.regex().capt().digit().oneOrMore().endCapt();
	VerbalExpression.Builder _decimalpart = VerbalExpression.regex().then(".").digit().oneOrMore();
	VerbalExpression.Builder _decimalNumber = VerbalExpression.regex().capt().add(_number).maybe(_decimalpart).endCapt();
	VerbalExpression.Builder _gb = VerbalExpression.regex().maybe(_space).then("gb");
	VerbalExpression.Builder _mah = VerbalExpression.regex().capt().maybe(_space).then("mah").endCapt();
	VerbalExpression.Builder _mp = VerbalExpression.regex().capt().maybe(_space).then("mp").endCapt();
	VerbalExpression.Builder _inch = VerbalExpression.regex().maybe(_space).then("inch").or("\"").endCapt();
	VerbalExpression.Builder _andORto = VerbalExpression.regex().then(" and ").or(" to ");
	VerbalExpression.Builder _betweenORfrom = VerbalExpression.regex().then("between ").or("from ");
	VerbalExpression.Builder _above = VerbalExpression.regex().then("above ").or("more than ");
	VerbalExpression.Builder _below = VerbalExpression.regex().then("below ").or("less than ").or("under ");
	VerbalExpression.Builder _around = VerbalExpression.regex().then("around ").or("about ");
	VerbalExpression.Builder _price = VerbalExpression.regex().then("price").or("cost").or("costing");
	VerbalExpression.Builder _display = VerbalExpression.regex().add(_number).add(_inch);

	VerbalExpression.Builder _amount = VerbalExpression.regex().space().add(_number).maybe(_space);
	VerbalExpression.Builder _amount_range = VerbalExpression.regex().add(_betweenORfrom).add(_number).add(_andORto).add(_number);
	VerbalExpression.Builder _amount_above = VerbalExpression.regex().add(_above).add(_number).maybe(_space);
	VerbalExpression.Builder _amount_below = VerbalExpression.regex().add(_below).add(_number).maybe(_space);
	VerbalExpression.Builder _amount_around = VerbalExpression.regex().add(_around).add(_number).maybe(_space);

	VerbalExpression.Builder _camera = VerbalExpression.regex().add(_number).add(_mp);
	VerbalExpression.Builder _camera_range = VerbalExpression.regex().add(_betweenORfrom).add(_camera).add(_andORto).add(_camera);
	VerbalExpression.Builder _camera_above = VerbalExpression.regex().add(_above).add(_camera).maybe(_space);
	VerbalExpression.Builder _camera_below = VerbalExpression.regex().add(_below).add(_camera).maybe(_space);
	VerbalExpression.Builder _camera_around = VerbalExpression.regex().add(_around).add(_camera).maybe(_space);


	VerbalExpression.Builder _ram = VerbalExpression.regex().add(_number).add(_gb);
	VerbalExpression.Builder _ram_range = VerbalExpression.regex().maybe(_betweenORfrom).space().add(_ram).maybe("and").or("to").space().add(_ram);
	VerbalExpression.Builder _ram_above = VerbalExpression.regex().then("above").space().add(_ram);
	VerbalExpression.Builder _ram_below = VerbalExpression.regex().then("below").space().add(_number).add(_gb);
	VerbalExpression.Builder _ram_around = VerbalExpression.regex().then("around").space().add(_number).add(_gb);

	VerbalExpression.Builder _battery = VerbalExpression.regex().add(_number).add(_mah);
	VerbalExpression.Builder _battery_range = VerbalExpression.regex().maybe(_betweenORfrom).space().add(_battery).maybe(" and ").or(" to ").add(_battery);
	VerbalExpression.Builder _battery_above = VerbalExpression.regex().then("above").add(_number).add(_mah);
	VerbalExpression.Builder _battery_below = VerbalExpression.regex().then("below").add(_number).add(_mah);
	VerbalExpression.Builder _battery_around = VerbalExpression.regex().then("around").add(_number).add(_mah);

	VerbalExpression.Builder _display_range = VerbalExpression.regex().maybe(_betweenORfrom).space().add(_display).maybe(" and ").or(" to ").add(_display);
	VerbalExpression.Builder _display_above = VerbalExpression.regex().then("above").add(_display);
	VerbalExpression.Builder _display_below = VerbalExpression.regex().then("below").add(_display);
	VerbalExpression.Builder _display_around = VerbalExpression.regex().then("around").add(_display);


	public void setResourcePath(String path){
		this.resourcePath = path;
	}
	QueryProcessor_new() throws Exception {
		init();
	}
	public static QueryProcessor_new instance() throws Exception{
		if(instance == null) 
			instance = new QueryProcessor_new();
		return instance;
	}
	public static void main(String[] args) throws Exception {

		System.out.println(QueryProcessor_new.instance().process("Show me 12mp 64gb 4gb ram dual sim 5inch mobils in htc below 15000 with 4g lte and 2500 mAH"));

	}
	private Vector<String> readList(String file) throws Exception{
		Vector<String> list = new Vector<String>();
		try(LineNumberReader reader = new LineNumberReader(new FileReader(file))){
			String line = null;
			while((line = reader.readLine())!=null)
				list.add(line);
		}
		return list;
	}
	public String process(String _query) throws Exception{
		query = _query;
		StringBuilder finalQuery = new StringBuilder("");
		Pattern pattern = null;
		Matcher m = null;
		query = query.toLowerCase();
		query=replaceSynonyms(" "+query+" ");
		//		finalQuery.append(String.format(matchQuery.get("url")+",",query));
		//		finalQuery.append(String.format(matchQuery.get("title")+",",query));
		//		finalQuery.append(String.format(matchQuery.get("brand")+",",query));
		//		finalQuery.append(String.format(matchQuery.get("model")+",",query));
		//		finalQuery.append(String.format(matchQuery.get("category")+",",query));
		//		finalQuery.append(String.format(matchQuery.get("category_path")+",",query));
		//		finalQuery.append(String.format(matchQuery.get("specs"),query));

		StringBuilder rangeQuery = new StringBuilder("");
		rangeQuery.append(priceQuery()+",");
		rangeQuery.append(primaryCameraQuery()+",");
		rangeQuery.append(secondaryCameraQuery()+",");
		rangeQuery.append(batteryQuery()+",");
		rangeQuery.append(displayQuery()+",");
		rangeQuery.append(internalMemoryQuery()+",");
		rangeQuery.append(ramQuery());

		String temp = rangeQuery.toString();

		while(temp.toString().endsWith(","))
			temp = temp.substring(0,temp.length()-1);

		//		StringBuilder filters = new StringBuilder("{\"terms\":{\"url\":[%s]}},{\"terms\":{\"title\":[%s]}},{\"terms\":{\"brand\":[%s]}},{\"terms\":{\"model\":[%s]}},{\"terms\":{\"specs\":[%s]}},{\"terms\":{\"category\":[%s]}},{\"terms\":{\"category_path\":[%s]}}");
		StringBuilder filters = new StringBuilder("{\"terms\":{\"brand\":[%s]}}");

		String w[] = query.trim().replaceAll("\"", " ").split("\\W+");
		
		removeNoise();
		trimQuery();
		w = query.trim().split("\\W+");

		for(int i=0; i<w.length ; i++)
			if(mobile.indexOf(w[i])>0)
				w[i]="";

		String prep="";
		for(int i=0; i<w.length-1 ; i++){
			if(w[i].length()>0)
				prep += "\""+w[i]+"\",";
		}
		if(w[w.length-1].length()>0)
			prep += "\""+w[w.length-1]+"\"";
		else
		{
			if(prep.length()>0)
				prep = prep.substring(0, prep.length()-1);
		}

		if(prep.length()>0)
			prep = String.format(filters.toString(), prep);

		
		/////////////////
		prep = "";
		if(brand_vector.size()>0) 
			prep += brandIsPresent()+",";
		else if(model_vector.size()>0) 
			prep += onlyModelIsPresent()+",";
		else if(specs_string.length()>0){
			String qTemplateSpecs = String.format("{\"terms\":{\"specs\":[%s]}}", specs_string);
			prep += qTemplateSpecs;
		}
		if(prep.endsWith(",")) 
			prep = prep.substring(0,prep.length()-1);
		////////////////
		finalQuery.append(matchQuery.get("new"));
		
		prep += ","+temp.toString();

		prep = prep.replaceAll(",{2,}", ",");

		while(prep.endsWith(","))
			prep = prep.substring(0, prep.length()-1);

		while(prep.startsWith(",") && prep.length()>1)
			prep = prep.substring(1);
		
		if( prep.length() ==0){
			prep = String.format("{\"wildcard\":{\"specs\":\"*%s*\"}}", query.trim());
		}
		
		prep = String.format(finalQuery.toString(), prep);
		

		return prep;
	}
	private void removeNoise(){
		String w[] = query.split("\\W+");
		for(int i=0; i < w.length ; i++){
			if(noise.contains(w[i]))
				query = query.replace(" "+w[i]+" ", " ");
		}
	}
	private String onlyModelIsPresent() {
		String qTemplateModel = "";
		String qTemplateSpecs = "";
		String Terms = "";

		String qMOdel="";

		if(specs_string.length()>0)
			qTemplateSpecs = String.format("{\"terms\":{\"specs\":[%s]}}", specs_string);

		for(int j=0; j < model_vector.size() ; j++){
			qMOdel+= "\""+model_vector.get(j)+"\",";
		}

		if(qMOdel.endsWith(","))
			qMOdel = qMOdel.substring(0, qMOdel.length()-1);

		qTemplateModel = String.format("{\"terms\":{\"model\":[%s]}}", qMOdel);

		Terms += qTemplateModel;
		if(qTemplateSpecs.length()>0)
			Terms +=","+qTemplateSpecs;

		return "{\"and\":["+Terms+"]}";
	}
	public String brandIsPresent(){
		String qTemplateBrand = "{\"terms\":{\"brand\":[\"%s\"]}}";
		String qTemplateModel = "";
		String qTemplateSpecs = "";
		String Terms = "";

		Vector<String> term_Queries = new Vector<String>();
		
		if(specs_string.length()>0)
			qTemplateSpecs = String.format("{\"terms\":{\"specs\":[%s]}}", specs_string);

		for(int i=0; i < brand_vector.size() ; i++){
			qTemplateBrand = "{\"terms\":{\"brand\":[\"%s\"]}}";
			qTemplateModel = "";
			Terms="";
			String models = brand_model_map.get(brand_vector.get(i));

			String qMOdel = "";

			for(int j=0; j < model_vector.size() ; j++){
				if((models!=null)&&(models.indexOf(model_vector.get(j))>-1)){
					qMOdel+= "\""+model_vector.get(j)+"\",";
				}
			}

			if(qMOdel.endsWith(","))
				qMOdel = qMOdel.substring(0, qMOdel.length()-1);

			qTemplateBrand = String.format(qTemplateBrand, brand_vector.get(i));
			if(qMOdel.length()>0)
				qTemplateModel = String.format("{\"terms\":{\"model\":[%s]}}", qMOdel);

			Terms+=qTemplateBrand;
			if(qTemplateModel.length()>0)
				Terms += ","+qTemplateModel;
			if(qTemplateSpecs.length()>0)
				Terms +=","+qTemplateSpecs;
			
			term_Queries.add("{\"and\":["+Terms+"]}");
		}

		Terms = term_Queries.toString();
		Terms = Terms.substring(1,Terms.length()-1);
		return Terms;
	}
	private void trimQuery() {
		String a="",b="",c="";

		brand_vector.clear();
		model_vector.clear();
		specs_string="";
		
		String rm[] = query.split("\\W+");
		for(int i=0; i < rm.length ; i++){
			if(brands.contains(rm[i]) && rm[i].length()>0)
				brand_vector.add(rm[i]);
			if(models.contains(rm[i]) && rm[i].length()>0)
				model_vector.add(rm[i]);
			if(specs.contains(rm[i]) && rm[i].length()>0){
				try{
					Integer.parseInt(rm[i]);
				}catch(Exception e){
					specs_string += "\""+rm[i]+"\",";
				}
			}
		}
		if(specs_string.endsWith(","))
			specs_string = specs_string.substring(0, specs_string.length()-1);
	}
	public String replaceSynonyms(String query){
		Set<Object> keys = synonyms.keySet();
		String temp="";

		for(Object k : keys){
			String value = synonyms.getProperty(k.toString());
			String vs[] = value.split(",");
			for(int j=0; j < vs.length ;j++){
				if(query.contains(" "+vs[j]+" ") && vs[j].trim().length()>0)
					query = query.replace(vs[j], k.toString());
			}
		}

		if(temp.length() > 0)
			query = temp;

		return query;
	}
	private String priceQuery(){
		String result = "";
		Matcher m = null;
		int count =0;

		VerbalExpression vb = _amount_range.build();
		if(vb.test(query)){
			List<String> list = vb.getTextGroups(query,1);
			list.addAll(vb.getTextGroups(query,2));
			TreeSet<Integer> ts = new TreeSet<Integer>();
			for(int i=0;i<list.size();i++) 
				ts.add(Integer.parseInt(list.get(i)));

			return String.format(matchQuery.get("price"), "\"gte\": "+ts.first()+", \"lte\": "+ts.last()+"");
		}

		vb = _amount_above.build();
		if(vb.test(query)){
			List<String> list = vb.getTextGroups(query,1);

			return String.format(matchQuery.get("price"), "\"gte\": "+list.get(0)+"");
		}

		vb = _amount_below.build();
		if(vb.test(query)){
			List<String> list = vb.getTextGroups(query,1);

			return String.format(matchQuery.get("price"), "\"lte\": "+list.get(0)+"");
		}

		vb = _amount_around.build();
		if(vb.test(query)){
			List<String> list = vb.getTextGroups(query,1);

			return String.format(matchQuery.get("price"), "\"gte\": "+(Integer.parseInt(list.get(0))-500)+", \"lte\": "+(Integer.parseInt(list.get(0))+500)+"");
		}

		return result;
	}
	private String primaryCameraQuery(){
		String result = "";
		Matcher m = null;
		int count =0;

		VerbalExpression vb = _camera_range.build();
		if(vb.test(query)){
			List<String> list = vb.getTextGroups(query,1);
			list.addAll(vb.getTextGroups(query,2));
			TreeSet<Integer> ts = new TreeSet<Integer>();
			for(int i=0;i<list.size();i++) 
				ts.add(Integer.parseInt(list.get(i)));

			return String.format(matchQuery.get("primary_camera"), "\"gte\": "+ts.first()+", \"lte\": "+ts.last()+"");
		}

		vb = _camera_above.build();
		if(vb.test(query)){
			List<String> list = vb.getTextGroups(query,1);

			return String.format(matchQuery.get("primary_camera"), "\"gte\": "+list.get(0)+"");
		}

		vb = _camera_below.build();
		if(vb.test(query)){
			List<String> list = vb.getTextGroups(query,1);

			return String.format(matchQuery.get("primary_camera"), "\"lte\": "+list.get(0)+"");
		}

		vb = _camera_around.build();
		if(vb.test(query)){
			List<String> list = vb.getTextGroups(query,1);

			return String.format(matchQuery.get("primary_camera"), "\"gte\": "+(Integer.parseInt(list.get(0))-1)+", \"lte\": "+(Integer.parseInt(list.get(0))+1)+"");
		}

		vb = _camera.build();
		if(vb.test(query)){
			List<String> list = vb.getTextGroups(query,1);
			TreeSet<Integer> ts = new TreeSet<Integer>();
			for(int i=0;i<list.size();i++) 
				ts.add(Integer.parseInt(list.get(i)));


			return String.format(matchQuery.get("primary_camera"), "\"gte\": "+(ts.first()-1)+", \"lte\": "+(ts.last()+1)+"");
		}

		return result;
	}
	private String secondaryCameraQuery(){
		String result = "";
		Matcher m = null;
		int count =0;

		VerbalExpression vb = _camera_range.build();
		if(vb.test(query)){
			List<String> list = vb.getTextGroups(query,1);
			list.addAll(vb.getTextGroups(query,2));
			TreeSet<Integer> ts = new TreeSet<Integer>();
			for(int i=0;i<list.size();i++) 
				ts.add(Integer.parseInt(list.get(i)));

			return String.format(matchQuery.get("secondary_camera"), "\"gte\": "+ts.first()+", \"lte\": "+ts.last()+"");
		}

		vb = _camera_above.build();
		if(vb.test(query)){
			List<String> list = vb.getTextGroups(query,1);

			return String.format(matchQuery.get("secondary_camera"), "\"gte\": "+list.get(0)+"");
		}

		vb = _camera_below.build();
		if(vb.test(query)){
			List<String> list = vb.getTextGroups(query,1);

			return String.format(matchQuery.get("secondary_camera"), "\"lte\": "+list.get(0)+"");
		}

		vb = _camera_around.build();
		if(vb.test(query)){
			List<String> list = vb.getTextGroups(query,1);

			return String.format(matchQuery.get("secondary_camera"), "\"gte\": "+(Integer.parseInt(list.get(0))-1)+", \"lte\": "+(Integer.parseInt(list.get(0))+1)+"");
		}

		vb = _camera.build();
		if(vb.test(query)){
			List<String> list = vb.getTextGroups(query,1);
			TreeSet<Integer> ts = new TreeSet<Integer>();
			for(int i=0;i<list.size();i++) 
				ts.add(Integer.parseInt(list.get(i)));

			if(list.size()>1)
				return String.format(matchQuery.get("secondary_camera"), "\"gte\": "+(ts.first()-1)+", \"lte\": "+(ts.first()+1)+"");
		}

		return result;
	}
	private String batteryQuery(){
		String result = "";
		Matcher m = null;
		int count =0;

		VerbalExpression vb = _battery.build();
		if(vb.test(query)){
			List<String> list = vb.getTextGroups(query,1);
			TreeSet<Integer> ts = new TreeSet<Integer>();
			for(int i=0;i<list.size();i++) 
				ts.add(Integer.parseInt(list.get(i)));

			vb = _battery_below.build();
			if(vb.test(query)){
				list = vb.getTextGroups(query,1);

				return String.format(matchQuery.get("battery"), "\"lte\": "+ts.first()+"");
			}

			vb = _battery_around.build();
			if(vb.test(query)){
				list = vb.getTextGroups(query,1);

				return String.format(matchQuery.get("battery"), "\"gte\": "+(ts.first()-500)+", \"lte\": "+(ts.first()+500)+"");
			}

			if(list.size()>1)
				return String.format(matchQuery.get("battery"), "\"gte\": "+ts.first()+", \"lte\": "+ts.last()+"");
			else
				return String.format(matchQuery.get("battery"), "\"gte\": "+ts.first()+"");
		}

		return result;
	}
	private String displayQuery(){
		String result = "";
		Matcher m = null;
		int count =0;

		VerbalExpression vb = _display.build();
		if(vb.test(query)){
			List<String> list = vb.getTextGroups(query,1);
			TreeSet<Integer> ts = new TreeSet<Integer>();
			for(int i=0;i<list.size();i++) 
				ts.add(Integer.parseInt(list.get(i)));

			vb = _display_below.build();
			if(vb.test(query)){
				list = vb.getTextGroups(query,1);

				return String.format(matchQuery.get("display"), "\"lte\": "+ts.first()+"");
			}

			vb = _display_around.build();
			if(vb.test(query)){
				list = vb.getTextGroups(query,1);

				return String.format(matchQuery.get("display"), "\"gte\": "+(ts.first()-500)+", \"lte\": "+(ts.first()+500)+"");
			}

			if(list.size()>1)
				return String.format(matchQuery.get("display"), "\"gte\": "+ts.first()+", \"lte\": "+ts.last()+"");
			else
				return String.format(matchQuery.get("display"), "\"gte\": "+ts.first()+"");
		}
		return result;
	}
	private String internalMemoryQuery(){
		String result = "";
		Matcher m = null;
		int count =0;

		VerbalExpression vb = _ram.build();
		if(vb.test(query)){
			List<String> list = vb.getTextGroups(query,1);
			TreeSet<Integer> ts = new TreeSet<Integer>();
			for(int i=0;i<list.size();i++) 
				ts.add(Integer.parseInt(list.get(i)));

			vb = _ram_below.build();
			if(vb.test(query)){
				list = vb.getTextGroups(query,1);

				return String.format(matchQuery.get("internal_memory"), "\"lte\": "+ts.first()+"");
			}

			vb = _ram_around.build();
			if(vb.test(query)){
				list = vb.getTextGroups(query,1);

				return String.format(matchQuery.get("internal_memory"), "\"gte\": "+(ts.first()-500)+", \"lte\": "+(ts.first()+500)+"");
			}

			if(list.size()>1)
				return String.format(matchQuery.get("internal_memory"), "\"gte\": "+(ts.last()-1)+", \"lte\": "+(ts.last()+1)+"");
			else
				if(ts.first() > 8)
					return String.format(matchQuery.get("internal_memory"), "\"gte\": "+ts.last()+"");
		}

		return result;
	}
	private String ramQuery(){
		String result = "";
		Matcher m = null;
		int count =0;

		VerbalExpression vb = _ram.build();
		if(vb.test(query)){
			List<String> list = vb.getTextGroups(query,1);
			TreeSet<Integer> ts = new TreeSet<Integer>();
			for(int i=0;i<list.size();i++) 
				ts.add(Integer.parseInt(list.get(i)));

			vb = _ram_below.build();
			if(vb.test(query)){
				list = vb.getTextGroups(query,1);

				return String.format(matchQuery.get("ram"), "\"lte\": "+ts.first()+"");
			}

			vb = _ram_around.build();
			if(vb.test(query)){
				list = vb.getTextGroups(query,1);

				return String.format(matchQuery.get("ram"), "\"gte\": "+(ts.first()-500)+", \"lte\": "+(ts.first()+500)+"");
			}

			if(list.size()>1)
				return String.format(matchQuery.get("ram"), "\"gte\": "+(ts.first()-1)+", \"lte\": "+(ts.first()+1)+"");
			else
				return String.format(matchQuery.get("ram"), "\"gte\": "+ts.first()+"");
		}

		return result;
	}
	private void init() throws Exception{
		brands = readList(resourcePath+"/brands.txt");
		models = readList("src/main/resources/models.txt");
		specs = readList("src/main/resources/specs.txt");
		noise = readList("src/main/resources/noise.txt");
		try(LineNumberReader reader = new LineNumberReader(new FileReader("D:/Data/Client Feed/PriceRaja/brand-model-map.json"))){
			brand_model_map = new Gson().fromJson(reader.readLine().toLowerCase(), new TypeToken<HashMap<String, String>>() {}.getType());
		}
		//		regex.load(new FileInputStream("properties.regex"));
		synonyms.load(new FileInputStream("src/main/resources/synonyms.txt"));
		//====================
		matchQuery = new HashMap<String, String>();
		//		matchQuery.put("simplified", "{\"query\":{\"bool\":{\"should\":[{\"multi_match\":{\"query\":\"%s\",\"type\":\"cross_fields\",\"fields\":[\"url\",\"title\",\"brand^3\",\"model\",\"category\",\"category_path\",\"specs\"],\"operator\":\"and\",\"boost\":2}},{\"bool\":{\"should\":[{\"multi_match\":{\"query\":\"%s\",\"type\":\"cross_fields\",\"fields\":[\"url\",\"title\",\"brand\",\"model\",\"category\",\"category_path\",\"specs\"],\"operator\":\"or\"}}]}}]}}}");
		//		matchQuery.put("simplified", "{\"query\":{\"bool\":{\"should\":[{\"multi_match\":{\"query\":\"%s\",\"type\":\"cross_fields\",\"fields\":[\"url\",\"title\",\"brand\",\"model\",\"category\",\"category_path\",\"specs\"],\"operator\":\"and\",\"boost\":2}},{\"bool\":{\"should\":[{\"multi_match\":{\"query\":\"%s\",\"type\":\"cross_fields\",\"fields\":[\"url\",\"title\",\"brand\",\"model\",\"category\",\"category_path\",\"specs\"],\"operator\":\"or\"}}]}}],\"filter\":[{\"terms\":{\"brand\":[%s]}}],,\"filter\":[{\"terms\":{\"model\":[%s]}}]}}}");
		//		matchQuery.put("simplified", "{\"query\": {\"dis_max\": {\"queries\": [{ \"match\": { \"url\": \"%s\" }},{ \"match\": { \"title\": \"%s\" }},{ \"match\": { \"brand\": \"%s\" }},{ \"match\": { \"model\": \"%s\" }},{ \"match\": { \"specs\": \"%s\" }},{ \"match\": { \"category\": \"%s\" }},{ \"match\": { \"category_path\": \"%s\" }}]}}}");
		//		matchQuery.put("simplified", "{\"query\": {\"bool\":{\"dis_max\": {\"queries\": [{ \"match\": { \"url\": \"%s\" }},{ \"match\": { \"title\": \"%s\" }},{ \"match\": { \"brand\": \"%s\" }},{ \"match\": { \"model\": \"%s\" }},{ \"match\": { \"specs\": \"%s\" }},{ \"match\": { \"category\": \"%s\" }},{ \"match\": { \"category_path\": \"%s\" }},{\"range\":{\"price\":{\"gte\":10000,\"lte\":15000}}}]}}}}");
		//		matchQuery.put("simplified", "{\"query\":{\"bool\":{\"should\":[{\"dis_max\":{\"queries\":[{\"match\":{\"url\":\"%s\"}},{\"match\":{\"title\":\"%s\"}},{\"match\":{\"brand\":\"%s\"}},{\"match\":{\"model\":\"%s\"}},{\"match\":{\"specs\":\"%s\"}},{\"match\":{\"category\":\"%s\"}},{\"match\":{\"category_path\":\"%s\"}}]}}]%s,\"minimum_should_match\": 1,\"boost\":1.0}}}");
		matchQuery.put("simplified", "{\"sort\":[{\"popularity\":{\"order\":\"desc\"}},{\"_score\":{\"order\":\"desc\"}}],\"query\":{\"bool\":{\"should\":[{\"dis_max\":{\"queries\":[{\"match\":{\"url\":\"%s\"}},{\"match\":{\"title\":\"%s\"}},{\"match\":{\"brand\":\"%s\"}},{\"match\":{\"model\":\"%s\"}},{\"match\":{\"specs\":\"%s\"}},{\"match\":{\"category\":\"%s\"}},{\"match\":{\"category_path\":\"%s\"}}]}}%s]%s,\"minimum_should_match\": 0,\"boost\":1.0}}}");
		matchQuery.put("new", "{\"sort\":[{\"popularity\":{\"order\":\"asc\"}},{\"_score\":{\"order\":\"desc\"}}],\"query\":{\"filtered\":{\"query\":{\"match_all\":{}},\"filter\":{\"bool\":{\"should\":[%s]}}}}}");
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
