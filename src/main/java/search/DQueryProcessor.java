package search;

import com.eclipsesource.json.Json;
import com.google.gson.*;

import db.DbUtils;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import org.springframework.stereotype.Component;

import ru.lanwen.verbalregex.VerbalExpression;

import javax.inject.Inject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class DQueryProcessor {
	int companyId=0;
	ReadProperties properties = new ReadProperties();
	public String companyName = properties.getPropValues("COMPANYNAME");

	public DbUtils dbUtils;

	///////////
	String fieldQuery = "{\"filter\":{\"match\":{\"%s\":\"%s\"}},\"weight\":%s}";
	String rangeQueryFormat = "\"query\":{ \"bool\":{" + "\"should\":[%s]}}";
	String newQuery = "";
	String configurations = "";
	private String query = "";
	boolean hasTailString = true;
	JsonObject configurationJson =new JsonObject();
	private Hashtable<String, String> synonyms = new Hashtable<>();
	public Hashtable<String, Integer> precision = new Hashtable<>();
	Gson gson = new GsonBuilder().create();
	HashMap<String,Integer> searchScore = new HashMap<String,Integer>();

	Vector<String> noise = null;

	///////////
	HashMap<String, HashSet<String>> stringFields = new HashMap<>();
	HashMap<String, TreeSet<Double>> doubleFields = new HashMap<>();

	VerbalExpression.Builder _andORto = VerbalExpression.regex().then(" and ").or(" to ");
	VerbalExpression.Builder _betweenORfrom = VerbalExpression.regex().then("between ").or("from ");
	VerbalExpression.Builder _above = VerbalExpression.regex().then("above ").or("more than ");
	VerbalExpression.Builder _below = VerbalExpression.regex().then("below ").or("less than ").or("under ");
	VerbalExpression.Builder _around = VerbalExpression.regex().then("around ").or("about ");
	VerbalExpression.Builder _space = VerbalExpression.regex().capt().space().endCapt();
	VerbalExpression.Builder _number = VerbalExpression.regex().capt().digit().oneOrMore().endCapt();
	VerbalExpression.Builder _decimalpart = VerbalExpression.regex().then(".").digit().oneOrMore();
	VerbalExpression.Builder _decimalNumber = VerbalExpression.regex().capt().add(_number).maybe(_decimalpart).endCapt();

	VerbalExpression.Builder _amount = VerbalExpression.regex().space().capt().add(_decimalNumber).maybe("k").endCapt();
	VerbalExpression.Builder _amount_range = VerbalExpression.regex().add(_betweenORfrom).capt().add(_decimalNumber).maybe("k").endCapt().add(_andORto).capt().add(_decimalNumber).maybe("k").endCapt();
	VerbalExpression.Builder _amount_range_without_and = VerbalExpression.regex().add(_betweenORfrom).capt().add(_decimalNumber).maybe("k").endCapt().space().capt().add(_decimalNumber).maybe("k").endCapt();
	VerbalExpression.Builder _amount_above = VerbalExpression.regex().add(_above).capt().add(_decimalNumber).maybe("k").endCapt().maybe(_space);
	VerbalExpression.Builder _amount_below = VerbalExpression.regex().add(_below).capt().add(_decimalNumber).maybe("k").endCapt().maybe(_space);
	VerbalExpression.Builder _amount_around = VerbalExpression.regex().add(_around).capt().add(_decimalNumber).maybe("k").endCapt().maybe(_space);


	ArrayList<String> queryParts = new ArrayList<>();
	private com.eclipsesource.json.JsonObject feed_data_field_map;
	private com.eclipsesource.json.JsonObject martJackLulu_field_map;
	private Vector<String> _bow = new Vector<>();
	private String inputQry;
	///////////
	private static String filesPath = "";
	private static DQueryProcessor instance = null;

	///////////
	DQueryProcessor() throws Exception {
		this.dbUtils= new DbUtils();
		init();
	}

	public static DQueryProcessor instance(String inputPath) throws Exception {
		filesPath = inputPath;
		if (instance == null)
			instance = new DQueryProcessor();
		return instance;
	}

	public static void main(String[] args) throws Exception {
		DQueryProcessor queryProcessor = DQueryProcessor.instance("D:\\Data\\ClientFeed\\MartJack\\Lulu\\JSON\\processed\\");
		//        System.out.println(queryProcessor.process("show me 13mp 16gb 4g dual sim samsung karbonn apple mobiles under 25000 with ram greater than 2gb"));
		System.out.println(queryProcessor.process("","smart phones"));
		//         String query = "above 1gb ram mobiles with 32gb internal memory below 3500 mah below 5inch 5mp";

		queryProcessor.debugPrint();
	}

	public void debugPrint() {
		//        System.out.println(stringFields);
		//        System.out.println(doubleFields);
		//        System.out.println(queryFields);
	}

	private void init() throws Exception {
		try {
			getCompanyId();
			loadFieldData(filesPath + "/FieldObjects.d");
			loadSynonyms(filesPath);
			readNoiseList(filesPath);
			loadPrecision();
			loadFieldMap("flyrobe",filesPath + "/feed_data_field_map.json");
			//			loadFieldMap("martjack_lulu",filesPath + "/martJackFabIndia_field_map.json");

			configurations = new String(Files.readAllBytes(Paths.get(filesPath + "/configurations.json")));
			configurationJson = gson.fromJson(configurations, JsonObject.class);

			searchScore.put("category",5);
			searchScore.put("category_path",1);
			searchScore.put("brand",5);
			searchScore.put("model",1);
			searchScore.put("title",5);
			searchScore.put("specs",1);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadFieldMap(String index_name,String filePath) throws Exception{
		JSONParser parser = new JSONParser();
		feed_data_field_map = Json.parse(new FileReader(filePath)).asObject();
		org.json.simple.JSONObject set_o = (org.json.simple.JSONObject)parser.parse(feed_data_field_map.toString());
		for(Object f : set_o.keySet()) {
			String val = feed_data_field_map.get((String)f).toString().toLowerCase();
			String w[] = val.split("\\W+");
			for(String s : w) {
				if(!_bow.contains(s))
					_bow.add(s);
			}
		}
	}

	private HashMap<String,HashSet<String>> probableFields(String index_name, String q) throws Exception{
		HashMap<String,HashSet<String>> field_set = new HashMap<>();

		String[] w = q.split("\\W+");
		JSONParser parser = new JSONParser();
		com.eclipsesource.json.JsonObject field_map = new com.eclipsesource.json.JsonObject();

		field_map = feed_data_field_map;

		org.json.simple.JSONObject set_o = (org.json.simple.JSONObject)parser.parse(field_map.toString());
		for(Object f : set_o.keySet()) {
			String val = field_map.get((String)f).toString().toLowerCase();
			for(int i=0 ; i < w.length ; i++) {
				if(val.contains(w[i])) {
					HashSet<String> sset = field_set.get(f);
					if(sset == null) {
						sset = new HashSet<>();
						field_set.put((String)f, sset);
					}
					sset.add(w[i]);
				}
			}
		}
		return field_set;
	}

	public void readNoiseList(String folder) throws Exception {
		Vector<String> list = new Vector<>();
		String stopwordQuery = "Select noise from noise where companyid=" + companyId;
		ResultSet rs = dbUtils.selectOutput(stopwordQuery);
		while (rs.next()) {
			String stopword[] = rs.getString("noise").split(",");
			for (int i = 0; i < stopword.length; i++) {
				if (stopword[i].length() > 0) {
					list.add(stopword[i]);
		}

		}

		}
		noise=list;
	}


	public String process(String index_name, String _query) throws Exception {
		System.out.println(_query);
		//		newQuery = "{\"query\":{\"bool\":{\"should\":[{\"function_score\":{\"functions\":[_and_part_],\"score_mode\":\"sum\",\"boost_mode\":\"max\"}},{\"function_score\":{\"functions\":[_or_part_],\"score_mode\":\"sum\",\"boost_mode\":\"max\"}}_doubleQuery_]}}}";
		newQuery = "{\"from\":0,\"size\":200,\"query\":{\"bool\":{\"should\":[{\"function_score\":{\"functions\":[_and_part_],\"score_mode\":\"sum\",\"boost_mode\":\"max\"}},{\"function_score\":{\"functions\":[_or_part_],\"score_mode\":\"sum\",\"boost_mode\":\"max\"}}],\"must\":[_doubleQuery_]}},\"_source\":{\"includes\":[],\"excludes\":[\"s_n_s_tags\"]},\"sort\":[{\"_score\":{\"order\":\"desc\"}}]}";
//		newQuery = "{\"from\":0,\"size\":200,\"query\":{\"bool\":{\"should\":[{\"function_score\":{\"functions\":[_and_part_],\"score_mode\":\"sum\",\"boost_mode\":\"max\"}},{\"function_score\":{\"functions\":[_or_part_],\"score_mode\":\"sum\",\"boost_mode\":\"max\"}}],\"must\":[_doubleQuery_]}},\"_source\":{\"includes\":[],\"excludes\":[\"s_n_s_tags\"]},\"sort\":[{\"_score\":{\"order\":\"desc\"}},{\"popularity\":{\"order\":\"desc\"}}]}";

		//        System.out.println(newQuery);
		inputQry = _query.trim().toLowerCase();
		//        newQuery=prepareQuery(new StringBuilder(inputQry));
		//		checkStringFields(inputQry);
		hasTailString =  false;
		StringBuilder finalQuery = new StringBuilder("");
		String priceQuery = "";
		inputQry=inputQry.replaceAll(",","");
		String doubleQuery = checkDoubleFields(inputQry);
		priceQuery = priceQuery(inputQry);
		inputQry = replaceSynonyms(" " + inputQry + " ");
		doubleQuery = doubleQuery.length() > 0 ? doubleQuery+ "," + priceQuery : priceQuery;
		doubleQuery = doubleQuery.length() > 0 ?  "," + doubleQuery : "";
		if(doubleQuery.toString().endsWith(",")){
			doubleQuery = doubleQuery.substring(0,doubleQuery.length()-1);
		}
		if(doubleQuery.length()>0)
			newQuery = newQuery.replaceAll("_doubleQuery_",doubleQuery.substring(1));
		else
			newQuery = newQuery.replaceAll("_doubleQuery_",doubleQuery);

		//		inputQry = removeNumbers(inputQry);
		inputQry = removeNoise(inputQry);
		inputQry = removePunctuations(inputQry);
		//inputQry = replaceSynonyms(" " + inputQry + " ");

		///////////
		//////////
		//////////
		String _andPART = "{\"filter\":{\"multi_match\":{\"query\":\"_query_\",\"type\":\"best_fields\",\"fields\":[\"_field_\"],\"operator\":\"and\"}},\"weight\":1}"; 
		//		String _andPART = "{\"filter\":{\"match\":{\"_field_\":{\"query\":\"_query_\",\"operator\":\"and\"}}},\"weight\":1}"; 
		String _orPART = "{\"filter\":{\"multi_match\":{\"query\":\"_query_\",\"type\":\"best_fields\",\"fields\":[\"_field_\"],\"operator\":\"or\"}},\"weight\":1}"; 

		String _and_Q = "";
		String _or_Q = "";

		HashMap<String,HashSet<String>> fields = probableFields(index_name,inputQry);

		/////
		HashMap<String,String> w_map_flyrobe = new HashMap<>();
		w_map_flyrobe.put("title","\"weight\":6");
		w_map_flyrobe.put("category","\"weight\":15");
		w_map_flyrobe.put("vendor","\"weight\":6");
		w_map_flyrobe.put("brand","\"weight\":8");
		w_map_flyrobe.put("color","\"weight\":8");
		w_map_flyrobe.put("cod","\"weight\":2");
		w_map_flyrobe.put("in_stock","\"weight\":2");
		w_map_flyrobe.put("description","\"weight\":8");
		w_map_flyrobe.put("customizable","\"weight\":2");
		w_map_flyrobe.put("s_n_s_tags","\"weight\":2");
		w_map_flyrobe.put("currency","\"weight\":2");
		w_map_flyrobe.put("model","\"weight\":2");

		HashMap<String,String> w_map_lulu = new HashMap<>();
		/////

		for(String s : fields.keySet())
		{
			String words = "";
			for(String word : fields.get(s))
				words += word +" ";

			if(index_name.equals("flyrobe")) {
				String weight = w_map_flyrobe.get(s);
				//				System.out.println(s);
				if(s.equals("title")) {
					_and_Q += _andPART.replace("_field_", "s_n_s_tags").replaceAll("_query_", words).replace("\"weight\":1", weight)+",";
					_or_Q += _orPART.replace("_field_", "s_n_s_tags").replaceAll("_query_", words).replace("\"weight\":1", weight)+",";
				}

				System.out.println(s+":"+words+":"+weight);
				_and_Q += _andPART.replace("_field_", s).replaceAll("_query_", words).replace("\"weight\":1", weight)+",";
				_or_Q += _orPART.replace("_field_", s).replaceAll("_query_", words).replace("\"weight\":1", weight)+",";
			}
			else if(index_name.equals("flyrobe")) {

			}

		}
		if(_and_Q.endsWith(",")) _and_Q = _and_Q.substring(0, _and_Q.length()-1);
		if(_or_Q.endsWith(",")) _or_Q = _or_Q.substring(0, _or_Q.length()-1);

		newQuery = newQuery.replaceAll("_and_part_", _and_Q);
		newQuery = newQuery.replaceAll("_or_part_", _or_Q);

		newQuery=newQuery.replaceAll("_query_",inputQry);

		///////////
		//////////
		//////////
		getTailString(inputQry);
		return newQuery;
	}

	void getTailString(String qry){
		String[] words = qry.split(" ");
		for(int i =0 ; i<words.length;i++){
		}
	}
	private String prepareQuery(StringBuilder finalQuery) {
		String qry =  finalQuery.toString();
		if(finalQuery.toString().endsWith(",")){
			qry = qry.substring(0,qry.length()-1);
		}
		String sortOrder = "";
		String sortField = configurationJson.getAsJsonObject("configurations").getAsJsonArray("sort_fields").get(0).toString();
		sortOrder = "desc";
		/*if(hasTailString) {
            sortField = configurationJson.getAsJsonObject("configurations").getAsJsonArray("sort_fields").get(1).toString();
            sortOrder = "asc";
        }*/
		//        String qryFormat = "{\"sort\":[{\"_score\":{\"order\":\"desc\"}}],\"query\":{ \"bool\":{" +"\"should\":[%s]}}}";
		String qryFormat = "{ \"query\":{ \"function_score\":{ \"query\":{\"match_all\":{}},%s, \"score_mode\":\"sum\", \"boost_mode\":\"max\"}}}";
		// TODO Auto-generated method stub

		return String.format(qryFormat, qry);
	}

	private String checkDoubleFields(String query) {
		String rangeQry = "";
		JsonElement range = configurationJson.getAsJsonObject("configurations").getAsJsonObject("range_mappings");
		final Map<String, ArrayList> map = (Map<String, ArrayList>) gson.fromJson(range.toString(), HashMap.class);

		List<String> queries = map.keySet().stream().map(key -> buildDoubleQuery(key, map.get(key),query)).collect(Collectors.toList());
		for (int i = 0; i < queries.size(); i++) {
			if (queries.get(i).length() > 1) {
				rangeQry = rangeQry.length() > 0 ? rangeQry + ',' + queries.get(i) : queries.get(i);
			}
		}

		return rangeQry;
	}

	public String buildDoubleQuery(String field, ArrayList mappings, String query) {
		String result = "";
		Double delta = Double.parseDouble(mappings.get(0).toString());
		String maxMin = mappings.get(1).toString();

		VerbalExpression.Builder fieldIndicator = VerbalExpression.regex().capt().maybe(_space).then(mappings.get(2).toString()).endCapt();

		VerbalExpression.Builder fieldExpr = VerbalExpression.regex().add(_decimalNumber).add(fieldIndicator);
		VerbalExpression.Builder field_range = VerbalExpression.regex().add(_betweenORfrom).add(fieldExpr).add(_andORto).add(fieldExpr);
		VerbalExpression.Builder field_above = VerbalExpression.regex().add(_above).add(fieldExpr).maybe(_space);
		VerbalExpression.Builder field_below = VerbalExpression.regex().add(_below).add(fieldExpr).maybe(_space);
		VerbalExpression.Builder field_around = VerbalExpression.regex().add(_around).add(fieldExpr).maybe(_space);

		VerbalExpression vb = fieldExpr.build();

		if (vb.test(query)) {
			String sssss = vb.getText(query);
			newQuery = newQuery.replaceAll(sssss, "");
			List<String> list = vb.getTextGroups(query, 1);
			List<Double> values = list.stream().map(value -> Double.parseDouble(value)).collect(Collectors.toList());

			Double maxValue = values.stream().collect(Collectors.reducing(Double::max)).get();
			Double minValue = values.stream().collect(Collectors.reducing(Double::min)).get();
			String inputValue = (maxMin.equals("max")) ? maxValue.toString() : minValue.toString();

			vb=field_range.build();
			if (vb.test(query)) {
				String ssss = vb.getText(query);
				newQuery = newQuery.replaceAll(ssss, "");
				return String.format("{\"range\": {\"" + field + "\": {%s}}}", "\"gte\": " + (minValue - delta) + ", \"lte\": " + (maxValue + delta) + "");
			}

			vb = field_below.build();
			if (vb.test(query)) {
				String ssss = vb.getText(query);
				newQuery = newQuery.replaceAll(ssss, "");
				return String.format("{\"range\": {\"" + field + "\": {%s}}}", "\"lte\": " + inputValue + "");
			}

			vb = field_above.build();
			if (vb.test(query)) {
				String ssss = vb.getText(query);
				newQuery = newQuery.replaceAll(ssss, "");
				return String.format("{\"range\": {\"" + field + "\": {%s}}}", "\"gte\": " + inputValue + "");
			}

			vb = field_around.build();
			if (vb.test(query)) {
				String ssss = vb.getText(query);
				newQuery = newQuery.replaceAll(ssss, "");
				return String.format("{\"range\": {\"" + field + "\": {%s}}}", "\"gte\": " + (Double.parseDouble(inputValue) - delta) + ", \"lte\": " + (Double.parseDouble(inputValue) + delta) + "");
			}

			return String.format("{\"range\": {\"" + field + "\": {%s}}}", "\"gte\": " + (Double.parseDouble(inputValue) - delta) + ", \"lte\": " + (Double.parseDouble(inputValue) + delta) + "");
		}

		return result;

	}

	private String checkStringFields(String query) {
		String words[] = query.split("\\W+");
		HashMap<String, String> queryFields = new HashMap<>();
		for (int i = 0; i < words.length; i++) {
			for (String field : stringFields.keySet()) {
				if (stringFields.get(field).contains(words[i])) {
					String fieldValue = queryFields.get(field);
					if (fieldValue != null) {
						fieldValue += " " + words[i];
						queryFields.put(field, fieldValue);
					} else {
						fieldValue = words[i];
						queryFields.put(field, fieldValue);
					}
				}
			}
		}

		String _and_Q = "";
		String _or_Q = "";

		String _andPART = "{\"filter\":{\"match\":{\"_field_\":{\"query\":\"_query_\",\"operator\":\"and\"}}},\"weight\":1}"; 
		String _orPART = "{\"filter\":{\"match\":{\"_field_\":{\"query\":\"_query_\",\"operator\":\"or\",\"minimum_should_match\":\"50%\"}}},\"weight\":1}";

		String fieldValue="";
		for (String field : stringFields.keySet()) {
			fieldValue="";
			if(queryFields.containsKey(field)){
				fieldValue = queryFields.get(field);
			}

			if(fieldValue.trim().length()>0) {
				String _and_ = _andPART.replaceAll("_field_",field);
				_and_ = _and_.replaceAll("_query_", fieldValue);

				String _or_ = _orPART.replaceAll("_field_",field);
				_or_ = _or_.replaceAll("_query_", fieldValue);

				if(field.contains("title")) {
					_and_ = _and_.replace(":1", ":16");
					_or_ = _or_.replace(":1", ":16");
				}
				if(field.contains("category")) {
					_and_ = _and_.replace(":1", ":10");
					_or_ = _or_.replace(":1", ":10");
				}
				if(field.contains("brand")) {
					_and_ = _and_.replace(":1", ":10");
					_or_ = _or_.replace(":1", ":10");
				}
				if(field.contains("model") || field.contains("modal")) {
					_and_ = _and_.replace(":1", ":6");
					_or_ = _or_.replace(":1", ":6");
				}

				_and_Q += _and_ + ",";
				_or_Q += _or_ + ",";
			}
		}

		if(_and_Q.endsWith(",")) _and_Q = _and_Q.substring(0, _and_Q.length()-1);
		if(_or_Q.endsWith(",")) _or_Q = _or_Q.substring(0, _or_Q.length()-1);

		newQuery = newQuery.replace("_and_part_", _and_Q);
		newQuery = newQuery.replace("_or_part_", _or_Q);
		return "";

	}

	public String splitAndFormMatchQuery(String fieldName, String value){
		StringBuffer matchQuery = new StringBuffer();
		String[] valueArray= value.split(" ");
		String eachWordQuery="";
		for(int i=0;i<valueArray.length;i++){
			eachWordQuery = String.format(fieldQuery, fieldName, valueArray[i], searchScore.get(fieldName));
			matchQuery.append(eachWordQuery + ",");
		}

		return matchQuery.toString();
	}

	public boolean isIgnoreField(String field){
		JsonArray ignore_fields = configurationJson.getAsJsonObject("configurations").getAsJsonArray("ignore_fields");
		for(int i=0 ; i< ignore_fields.size() ;i++){
			if(ignore_fields.get(i).getAsString().equalsIgnoreCase(field)){
				return true;
			}
		}
		return false;
	}
	private void loadFieldData(String fileName) throws Exception {
		ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(fileName));
		stringFields = (HashMap<String, HashSet<String>>) objectInputStream.readObject();
		doubleFields = (HashMap<String, TreeSet<Double>>) objectInputStream.readObject();
		objectInputStream.close();
	}

	private String removeNumbers(String queryStr) {
		String temp = "";
		String w[] = queryStr.split("\\W+");
		for (int i = 0; i < w.length; i++) {
			try {
				Integer.parseInt(w[i]);
			} catch (Exception e) {
				temp += w[i] + " ";
			}
		}
		if (temp.length() > 0)
			queryStr = temp;
		return queryStr;
	}

	public String removePunctuations(String query) {
		StringBuilder res = new StringBuilder("");
		for (Character c : query.toCharArray()) {
			if (Character.isLetterOrDigit(c) || c.equals(' ') || c.equals('.')|| c.equals('_'))
				res.append(c);
		}
		return res.toString();
	}

	public void getCompanyId() {
		String qry = "Select companyid,securitykey FROM " + " company " + " where companyname = '" + companyName + "'";
		ResultSet rs = dbUtils.selectOutput(qry);
					try {
			while (rs.next()) {
				companyId = rs.getInt("companyid");
					}
		} catch (SQLException s) {
			s.printStackTrace();
				}
			}

	public void loadPrecision() throws Exception {
		String precisionQuery = "Select categoryname,precision from precision where companyid=" + companyId;
		ResultSet rs = dbUtils.selectOutput(precisionQuery);
		while (rs.next()) {
			precision.put(rs.getString("categoryname"), rs.getInt("precision"));
		}
	}
	public void loadSynonyms(String folderName) throws Exception {
		String synonymQuery = "Select keyword,synonyms from synonyms where companyid="+companyId;
		ResultSet rs = dbUtils.selectOutput(synonymQuery);
		while (rs.next()) {
			String syn[] = rs.getString("synonyms").split(",");
			for (int i=0;i<syn.length;i++){
				if(syn[i].length()>0){
					synonyms.put(syn[i], rs.getString("keyword"));
		}
				}
			}

		String spellingQuery = "Select keyword,spellings from spellcheck where companyid="+companyId;
		rs = dbUtils.selectOutput(spellingQuery);
		while (rs.next()) {
			String spel[] = rs.getString("spellings").split(",");
			for (int i=0;i<spel.length;i++){
				if(spel[i].length()>0){
					synonyms.put(spel[i], rs.getString("keyword"));
				}
			}

		}
	}

	public String replaceSynonyms(String query) {
		StringUtils su = new StringUtils();

		;;
		String _w[] = query.split(" ");
		String _q = "";
		for(String _s : _w) {
			_s = _s.trim();
			if(_s.length()>2 && !_bow.contains(_s)) {
				for(String _qs : _bow)
					if(su.getLevenshteinDistance(_s, _qs) < 2)
						_q += _qs+" ";
			}else {
				_q += _s+" ";
			}
		}
		query = _q;

		;;

		Set<String> keys = synonyms.keySet();
		String temp = "";

		Set<String> k = synonyms.keySet();
		for (String s : k) {
			if (query.contains(" " + s + " ")) {
				String v = synonyms.get(s);
				if (!query.contains(" " + v + " "))
					query = query.replace(" " + s + " ", " " + v + " ");
			}

		}
		if (temp.length() > 0)
			query = temp;

		query = query.trim().replaceAll(" +", " ");
		;

		return query;
	}

	private String amountK(String s) {
		query = query.replace(s, "");
		double kk = 0;
		if (s.endsWith("k")) {
			s = s.replace("k", "");
			kk = (Double.parseDouble(s) * 1000);
		} else {
			kk = Double.parseDouble(s);
		}
		return "" + kk;
	}

	private String removeNoise(String query) {
		String w[] = query.split("\\W+");
		for (int i = 0; i < w.length; i++) {
			if (noise.contains(w[i]))
				query = query.replace(w[i] + " ", " ");
		}
		return query;
	}


	private String priceQuery(String query){
		String result = "";
		Matcher m = null;
		int count =0;

		VerbalExpression vb = _amount_range.build();
		if(vb.test(query)){
			String ssss = vb.getText(query);
			inputQry = inputQry.replaceAll(ssss, "");
			List<String> list = vb.getTextGroups(query,1);
			list.addAll(vb.getTextGroups(query,2));
			list.addAll(vb.getTextGroups(query,4));
			list.remove(1);
			TreeSet<Double> ts = new TreeSet<Double>();
			for(int i=0;i<list.size();i++)
				ts.add(Double.parseDouble(amountK(list.get(i))));

			return String.format("{\"range\": {\"price\": {\"gte\": "+ts.first()+", \"lte\": "+ts.last()+"}}}");
		}

		vb = _amount_range_without_and.build();
		if(vb.test(query)){
			String ssss = vb.getText(query);
			inputQry = inputQry.replaceAll(ssss, "");
			List<String> list = vb.getTextGroups(query,1);
			list.addAll(vb.getTextGroups(query,4));
			TreeSet<Double> ts = new TreeSet<Double>();
			for(int i=0;i<list.size();i++)
				ts.add(Double.parseDouble(amountK(list.get(i))));

			return String.format("{\"range\": {\"price\": {\"gte\": "+ts.first()+", \"lte\": "+ts.last()+"}}}");
		}

		vb = _amount_above.build();
		if(vb.test(query)){
			String ssss = vb.getText(query);
			inputQry = inputQry.replaceAll(ssss, "");
			List<String> list = vb.getTextGroups(query,1);

			return String.format("{\"range\": {\"price\": {\"gte\": "+amountK(list.get(0))+"}}}");
		}

		vb = _amount_below.build();
		if(vb.test(query)){
			String ssss = vb.getText(query);
			inputQry = inputQry.replaceAll(ssss, "");
			List<String> list = vb.getTextGroups(query,1);
			return String.format("{\"range\": {\"price\": {\"lte\": "+amountK(list.get(0))+"}}}");
		}

		vb = _amount_around.build();
		if(vb.test(query)){
			String ssss = vb.getText(query);
			inputQry = inputQry.replaceAll(ssss, "");
			List<String> list = vb.getTextGroups(query,1);
			double val = Double.parseDouble(amountK(list.get(0)));
			return String.format("{\"range\": {\"price\": {\"gte\": "+(val-(val*0.05))+", \"lte\": "+(val+(val*0.05))+"}}}");
		}

		return result;
	}

	private String martJackLuluQuery() {
		String query = ""
				+ "{\"query\":{\"bool\":{\"should\":[{\"function_score\":"
				+ "{\"functions\":["
				+ "{\"filter\":{\"match\":{\"product_title\":{\"query\":\"_query_\",\"operator\":\"and\",\"fuzziness\": 2}}},\"weight\":16},"
				+ "{\"filter\":{\"match\":{\"category_name\":{\"query\":\"_query_\",\"operator\":\"and\",\"fuzziness\": 2}}},\"weight\":10},"
				+ "{\"filter\":{\"match\":{\"sub_category\":{\"query\":\"_query_\",\"operator\":\"and\",\"fuzziness\": 2}}},\"weight\":10},"
				+ "{\"filter\":{\"match\":{\"product_category\":{\"query\":\"_query_\",\"operator\":\"and\",\"fuzziness\": 2}}},\"weight\":10},"
				+ "{\"filter\":{\"match\":{\"brand\":{\"query\":\"_query_\",\"operator\":\"and\",\"fuzziness\": 2}}},\"weight\":10},"
				+ "{\"filter\":{\"match\":{\"s_n_s_tags\":{\"query\":\"_query_\",\"operator\":\"and\",\"fuzziness\": 2}}},\"weight\":10}"
				+ ""
				+ "],\"score_mode\":\"sum\",\"boost_mode\":\"max\"}},"
				+ ""
				+ "{\"function_score\":{\"functions\":["
				+ "{\"filter\":{\"match\":{\"product_title\":{\"query\":\"_query_\",\"operator\":\"or\",\"minimum_should_match\":\"50%\",\"fuzziness\": 2}}},\"weight\":16},"
				+ "{\"filter\":{\"match\":{\"category_name\":{\"query\":\"_query_\",\"operator\":\"or\",\"minimum_should_match\":\"50%\",\"fuzziness\": 2}}},\"weight\":10},"
				+ "{\"filter\":{\"match\":{\"sub_category\":{\"query\":\"_query_\",\"operator\":\"or\",\"minimum_should_match\":\"50%\",\"fuzziness\": 2}}},\"weight\":10},"
				+ "{\"filter\":{\"match\":{\"product_category\":{\"query\":\"_query_\",\"operator\":\"or\",\"minimum_should_match\":\"50%\",\"fuzziness\": 2}}},\"weight\":10},"
				+ "{\"filter\":{\"match\":{\"brand\":{\"query\":\"_query_\",\"operator\":\"or\",\"minimum_should_match\":\"50%\",\"fuzziness\": 2}}},\"weight\":10},"
				+ "{\"filter\":{\"match\":{\"s_n_s_tags\":{\"query\":\"_query_\",\"operator\":\"or\",\"minimum_should_match\":\"50%\",\"fuzziness\": 2}}},\"weight\":10}"
				+ ""
				+ "],\"score_mode\":\"sum\",\"boost_mode\":\"max\"}}"
				+ ""
				+ "_doubleQuery_]}}}" + 
				"";

		return query;
	}
	private String martJackFabIndiaQuery() {
		String query = ""
				+ "{\"query\":{\"bool\":{\"should\":[{\"function_score\":{"
				+ ""
				+ "\"functions\":["
				+ "{\"filter\":{\"match\":{\"ns1_product_type\":{\"query\":\"_query_\",\"operator\":\"and\",\"fuzziness\": 1}}},\"weight\":10},"
				+ "{\"filter\":{\"match\":{\"Sleeves(Predefined)\":{\"query\":\"_query_\",\"operator\":\"and\",\"fuzziness\": 1}}},\"weight\":10},"
				+ "{\"filter\":{\"match\":{\"s_n_s_tags\":{\"query\":\"_query_\",\"operator\":\"and\",\"fuzziness\": 1}}},\"weight\":5}"
				+ ""
				+ "],\"score_mode\":\"sum\",\"boost_mode\":\"max\"}},"
				+ ""
				+ "{\"function_score\":{\"functions\":["
				+ "{\"filter\":{\"match\":{\"ns1_product_type\":{\"query\":\"_query_\",\"operator\":\"or\",\"minimum_should_match\":\"50%\",\"fuzziness\": 1}}},\"weight\":2},"
				+ "{\"filter\":{\"match\":{\"Sleeves(Predefined)\":{\"query\":\"_query_\",\"operator\":\"or\",\"minimum_should_match\":\"50%\",\"fuzziness\": 1}}},\"weight\":10},"
				+ "{\"filter\":{\"match\":{\"s_n_s_tags\":{\"query\":\"_query_\",\"operator\":\"or\",\"minimum_should_match\":\"100%\",\"fuzziness\": 1}}},\"weight\":1}"
				+ ""
				+ "],\"score_mode\":\"sum\",\"boost_mode\":\"max\"}}"
				+ ""
				+ "_doubleQuery_]}}}" + 
				"";

		return query;
	}
}
