package search;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import javax.inject.Inject;

import db.DbUtils;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.google.gson.Gson;

@RestController
@RequestMapping(value = "/rest")
public class ConfigMainController {

    @Inject
    ApplicationContext context;
    ReadProperties properties = new ReadProperties();

    @Autowired
    MailSender mailSender;

    @Autowired
    private JavaMailSender javaMailSender;

    //	@Value("${resourceLocation}")
    public String resourceLocation = properties.getPropValues("RESOURCES_LOCATION");

    RestTemplate restTemplate = new RestTemplate();
    public String warName = properties.getPropValues("WARNAME");
    String elasticDbUrl = properties.getPropValues("ELASTIC_URL");
    public String searchUrl = properties.getPropValues("SEARCH_URL");
    public String dataBase = properties.getPropValues("DATABASE");
    public String indexname = properties.getPropValues("INDEXNAME");

    public void setSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
    }

    public DbUtils dbUtils;

    @Inject
    public ConfigMainController(DbUtils dbUtils) throws Exception {
        this.dbUtils = dbUtils;
    }


    @RequestMapping(value = "config/filter/{filterText}/{selectedCategory}", method = RequestMethod.GET)
    public List<String> filterById(@PathVariable String filterText,@PathVariable String selectedCategory) throws IOException, URISyntaxException {

        //		Document doc = Jsoup.connect(
        //				elasticDbUrl + "_sql?sql=select * from martjack_fabindia where ns1_id='" + filterText + "' LIMIT 25")
        //				.ignoreContentType(true).get();
        String qry = "Select productid,rank from rankbyproduct where companyid=" + 3 +" order by rank asc";
        ResultSet rs = dbUtils.selectOutput(qry);
        Hashtable<String,Integer> ranking=new Hashtable<String,Integer>();
        try {
            while(rs.next()){
                ranking.put(rs.getString("productid"),rs.getInt("rank"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<String> outputLines = new ArrayList<>();
        if(filterText.length() > 0) {
            Document doc = Jsoup.connect(
                    elasticDbUrl + indexname + "/_search?q=pid:" + filterText)
                    .ignoreContentType(true).get();
            JsonObject jsonResult = Json.parse(doc.select("body").text()).asObject();

            JsonArray resultsArray = jsonResult.get("hits").asObject().get("hits").asArray();



            for (int j = 0; j < resultsArray.size(); j++) {
                JsonValue record = resultsArray.get(j).asObject().get("_source");
                if (ranking.containsKey(record.asObject().get("pid").asString())) {
                    record.asObject().add("rank", ranking.get(record.asObject().get("pid").asString()));
                }
                outputLines.add(record.toString());
            }
        }
        if(selectedCategory.length() > 0) {
            Document doc = Jsoup.connect(
                    elasticDbUrl + indexname + "/_search?q=category:" + selectedCategory)
                    .ignoreContentType(true).get();
            JsonObject jsonResult = Json.parse(doc.select("body").text()).asObject();

            JsonArray resultsArray = jsonResult.get("hits").asObject().get("hits").asArray();



            for (int j = 0; j < resultsArray.size(); j++) {
                JsonValue record = resultsArray.get(j).asObject().get("_source");
                if (ranking.containsKey(record.asObject().get("pid").asString())) {
                    record.asObject().add("rank", ranking.get(record.asObject().get("pid").asString()));
                }
                outputLines.add(record.toString());
            }
        }

        return outputLines;
    }

    @RequestMapping(value = "config/filterByKey/{filterText}", method = RequestMethod.GET)
    public List<String> filterByKey(@PathVariable String filterText) throws IOException, URISyntaxException {

        String qry = "Select productid,rank from rankbykeyword where companyid=" + 3 +" order by rank asc";
        ResultSet rs = dbUtils.selectOutput(qry);
        Hashtable<String,Integer> ranking=new Hashtable<String,Integer>();
        try {
            while(rs.next()){
                ranking.put(rs.getString("productid"),rs.getInt("rank"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Document doc = Jsoup.connect(searchUrl + "flyrobe2" + "/" + "search?query=" + filterText).ignoreContentType(true)
                .get();
        JsonObject jsonResult = Json.parse(doc.select("body").text()).asObject();

        JsonArray resultsArray = jsonResult.get("hits").asObject().get("hits").asArray();

        List<String> outputLines = new ArrayList<>();

        for (int j = 0; j < resultsArray.size(); j++) {
            if (resultsArray.get(j).asObject().get("_score").asDouble() > 1.2) {
                JsonValue record = resultsArray.get(j).asObject().get("_source");
                if (ranking.containsKey(record.asObject().get("pid").asString())) {
                    record.asObject().add("rank", ranking.get(record.asObject().get("pid").asString()));
                }
                outputLines.add(record.toString());
            }
            if (j >= 24)
                break;
        }

        return outputLines;
    }

    //Synonyms code
    @RequestMapping(value = {"config/synonyms/{companyid}"}, method = {
            RequestMethod.GET})
    public Map getSynonyms(@PathVariable int companyid) throws IOException, URISyntaxException {
        String qry = "Select keyword,synonyms from synonyms where companyid=" + companyid;
        ResultSet rs = dbUtils.selectOutput(qry);
        Map map = dbUtils.convertResultSetToHashMap(rs, "keyword", "synonyms");
        return map;
    }

    @RequestMapping(value = {"config/add/synonyms/{companyid}"}, method = {
            RequestMethod.POST})
    public void addSynonmys(@PathVariable int companyid,@RequestBody Map<String, String> data) throws IOException, URISyntaxException {
        String insertQry = dbUtils.convertMapToQueryForAdd(data,companyid, "synonyms", "keyword", "synonyms");
        dbUtils.InsertUpdateData(insertQry);
        Jsoup.connect(searchUrl+warName+"/" + "watcher/update").ignoreContentType(true).get();

    }

    @RequestMapping(value = {"config/update/synonyms/{companyid}"}, method = {
            RequestMethod.POST})
    public void updateSynonmys(@PathVariable int companyid,@RequestBody Map<String, String> data) throws IOException, URISyntaxException {
        String updateQry = dbUtils.convertMapToQueryForUpdate(data,companyid, "synonyms", "keyword", "synonyms");
        dbUtils.InsertUpdateData(updateQry);
		Jsoup.connect(searchUrl+warName+"/" + "watcher/update").ignoreContentType(true).get();

    }

    @RequestMapping(value = {"config/delete/synonyms/{companyid}"}, method = {
            RequestMethod.POST})
    public void deleteSynonmys(@PathVariable int companyid,@RequestBody String data) throws IOException, URISyntaxException {
        String updateQry = dbUtils.queryForDelete(data,companyid, "synonyms", "keyword");
        dbUtils.InsertUpdateData(updateQry);
		Jsoup.connect(searchUrl+warName+"/" + "watcher/update").ignoreContentType(true).get();

    }

    //Synonyms code ends

    //spelling code
    @RequestMapping(value = {"config/spellings/{companyid}"}, method = {
            RequestMethod.GET})
    public Map getSpellings(@PathVariable int companyid) throws IOException, URISyntaxException {
        String qry = "Select keyword,spellings from spellcheck where companyid=" + companyid;
        ResultSet rs = dbUtils.selectOutput(qry);
        Map map = dbUtils.convertResultSetToHashMap(rs, "keyword", "spellings");
        return map;
    }

    @RequestMapping(value = {"config/add/spellings/{companyid}"}, method = {
            RequestMethod.POST})
    public void addSpellings(@PathVariable int companyid,@RequestBody Map<String, String> data) throws IOException, URISyntaxException {
        String insertQry = dbUtils.convertMapToQueryForAdd(data,companyid, "spellcheck", "keyword", "spellings");
        dbUtils.InsertUpdateData(insertQry);
		Jsoup.connect(searchUrl+warName+"/" + "watcher/update").ignoreContentType(true).get();

    }

    @RequestMapping(value = {"config/update/spellings/{companyid}"}, method = {
            RequestMethod.POST})
    public void updatesSpellings(@PathVariable int companyid,@RequestBody Map<String, String> data) throws IOException, URISyntaxException {
        String updateQry = dbUtils.convertMapToQueryForUpdate(data,companyid, "spellcheck", "keyword", "spellings");
        dbUtils.InsertUpdateData(updateQry);
		Jsoup.connect(searchUrl+warName+"/" + "watcher/update").ignoreContentType(true).get();

    }

    @RequestMapping(value = {"config/delete/spellings/{companyid}"}, method = {
            RequestMethod.POST})
    public void deleteSpellings(@PathVariable int companyid,@RequestBody String data) throws IOException, URISyntaxException {
        String updateQry = dbUtils.queryForDelete(data,companyid, "spellcheck", "keyword");
        dbUtils.InsertUpdateData(updateQry);
		Jsoup.connect(searchUrl+warName+"/" + "watcher/update").ignoreContentType(true).get();

    }

    //spelling code ends


    //query code
    @RequestMapping(value = {"config/links/{companyid}"}, method = {RequestMethod.GET})
    public Map getLinks(@PathVariable int companyid) throws IOException, URISyntaxException {
        String qry = "Select keyword,url from querytoredirect where companyid=" + companyid;
        ResultSet rs = dbUtils.selectOutput(qry);
        Map map = dbUtils.convertResultSetToHashMap(rs, "keyword", "url");
        return map;
    }

    @RequestMapping(value = {"config/add/links/{companyid}"}, method = {RequestMethod.POST})
    public void addLinks(@PathVariable int companyid,@RequestBody Map<String, String> data) throws IOException, URISyntaxException {
        String insertQry = dbUtils.convertMapToQueryForAdd(data,companyid, "querytoredirect", "keyword", "url");
        dbUtils.InsertUpdateData(insertQry);
		Jsoup.connect(searchUrl+warName+"/" + "watcher/update").ignoreContentType(true).get();

    }

    @RequestMapping(value = {"config/update/links/{companyid}"}, method = {
            RequestMethod.POST})
    public void updateLinks(@PathVariable int companyid,@RequestBody Map<String, String> data) throws IOException, URISyntaxException {
        String updateQry = dbUtils.convertMapToQueryForUpdate(data,companyid, "querytoredirect", "keyword", "url");
        dbUtils.InsertUpdateData(updateQry);

        Jsoup.connect(searchUrl + warName + "/" + "watcher/update").ignoreContentType(true).get();

    }

    @RequestMapping(value = {"config/delete/links/{companyid}"}, method = {
            RequestMethod.POST})
    public void deleteLinks(@PathVariable int companyid,@RequestBody String data) throws IOException, URISyntaxException {
        String updateQry = dbUtils.queryForDelete(data,companyid, "querytoredirect", "keyword");
        dbUtils.InsertUpdateData(updateQry);
		Jsoup.connect(searchUrl+warName+"/" + "watcher/update").ignoreContentType(true).get();
    }

    //query code ends

    //noise code

    @RequestMapping(value = {"config/update/stopwords/{companyid}"}, method = {
            RequestMethod.POST})
    public void updateStopwords(@PathVariable int companyid,@RequestBody String data) throws IOException, URISyntaxException {
        dbUtils.InsertUpdateData("delete from noise where companyid="+companyid);
        String qry = "INSERT INTO noise (CompanyID, noise ) values (" + companyid + ",'%s')";
        qry = String.format(qry, data);
        dbUtils.InsertUpdateData(qry);
        Jsoup.connect(searchUrl + warName + "/" + "watcher/update").ignoreContentType(true).get();

    }

    @RequestMapping(value = {"config/stopwords/{companyid}"}, method = {
            RequestMethod.GET})
    public List<String> getStopwords(@PathVariable int companyid) throws IOException, URISyntaxException, SQLException {
        String qry = "Select noise from noise where companyid=" +companyid;
        ResultSet rs = dbUtils.selectOutput(qry);
        return Arrays.asList(rs.getString("noise").split(","));
    }

    //noise code ends

    // Sorting order

    @RequestMapping(value = {"config/update/sortorder/{companyid}"}, method = {
            RequestMethod.POST})
    public void updateSortOrder(@PathVariable int companyid,@RequestBody String data) throws IOException, URISyntaxException {
        JSONObject uobject = new JSONObject(data);
        String uCategory = uobject.get("category").toString();
        String uSort = uobject.get("sort").toString();

        File file = new File(resourceLocation, "/sortconfig.json");
        List<String> allLines = FileUtils.readLines(file);

        List<String> outputLines = new ArrayList<>();
        boolean found = false;
        for (String line : allLines) {
            JSONObject object = new JSONObject(line);
            String category = object.get("category").toString();
            String sort = object.get("sort").toString();

            if (category.equals(uCategory) && sort.equals(uSort)) {
                outputLines.add(data);
                found = true;
            } else {
                outputLines.add(line);
            }
        }
        if (!found)
            outputLines.add(data);

        FileUtils.writeLines(file, outputLines);

        Jsoup.connect(searchUrl + warName + "/" + "watcher/update").ignoreContentType(true).get();

    }

    @RequestMapping(value = {"config/sortorder/{companyid}"}, method = {
            RequestMethod.GET})
    public List<String> getSortOrder(@PathVariable int companyid) throws IOException, URISyntaxException {
        File file = new File(resourceLocation, "/sortconfig.json");
        List<String> lines = FileUtils.readLines(file);
        return lines;
    }
    // sorting Configuration
    @RequestMapping(value = {"config/sorting/{companyid}"}, method = {
            RequestMethod.GET})
    public Map<String, String> getSorting(@PathVariable int companyid) throws IOException, URISyntaxException {
        Map<String, String> sorting = new HashMap<>();
        String qry = "Select sortorder,rank from sortconfig where companyid=" +companyid;
        ResultSet rs = dbUtils.selectOutput(qry);
        try {
            while (rs.next()) {
                sorting.put(rs.getString("sortorder"), rs.getString("rank"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sorting;
    }

    // Precision Configuration
    @RequestMapping(value = {"config/precision/{companyid}"}, method = {
            RequestMethod.GET})
    public Map<String, String> getPrecision(@PathVariable int companyid) throws IOException, URISyntaxException {
        Map<String, String> precision = new HashMap<>();
        String qry = "Select categoryname,precision from precision where companyid=" +companyid;
        ResultSet rs = dbUtils.selectOutput(qry);
        try {
            while (rs.next()) {
                precision.put(rs.getString("categoryname"), rs.getString("precision"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return precision;
    }

@RequestMapping(value = {"config/update/precision/{companyid}/{category}/{precision}"}, method = {
            RequestMethod.POST})
    public void updatePrecision(@PathVariable int companyid, @PathVariable String category, @PathVariable String precision) throws IOException, URISyntaxException {

        dbUtils.InsertUpdateData("delete from precision where companyid=" + companyid + " and categoryname = '" + category + "'");
        String qry = "INSERT INTO precision (CompanyID, categoryname, precision) values (" + companyid + ",'%s'," + Integer.parseInt(precision) + ")";
        qry = String.format(qry, category);
        dbUtils.InsertUpdateData(qry);

        Jsoup.connect(searchUrl + warName + "/" + "watcher/update").ignoreContentType(true).get();
//        Jsoup.connect(searchUrl + warName + "/" + "rest/config/precision/"+companyid+"").ignoreContentType(true).get();

    }
    // End of Precision Configuration

    @RequestMapping(value = {"config/update/rankbykey/{companyid}/{rank}"}, method = {
            RequestMethod.POST})
    public void updateRankByKey(@PathVariable int companyid,@PathVariable String rank,@RequestBody String data) throws IOException, URISyntaxException {
        dbUtils.InsertUpdateData("delete from rankbykeyword where companyid="+companyid + " and productid ='" +data +"'");
        String qry = "INSERT INTO rankbykeyword (CompanyID, productid,rank ) values (" + companyid + ",'" + data + "'," + Integer.parseInt(rank) + ")";
        dbUtils.InsertUpdateData(qry);

//        Jsoup.connect(searchUrl + warName + "/" + "watcher/update").ignoreContentType(true).get();

    }

    @RequestMapping(value = {"config/update/rankbyproduct/{companyid}/{rank}/{productid}"}, method = {
            RequestMethod.POST})
    public void updateRankByProduct(@PathVariable int companyid, @PathVariable String rank, @PathVariable String productid, @RequestBody String category) throws IOException, URISyntaxException {
        dbUtils.InsertUpdateData("delete from rankbyproduct where companyid=" + companyid + " and productid =" + productid + " and categoryname ='" + category + "'");
        String qry = "INSERT INTO rankbyproduct (CompanyID, productid,rank,categoryname ) values (" + companyid + ",'" + productid + "'," + Integer.parseInt(rank) + ",'%s')";
        qry = String.format(qry, category);
        dbUtils.InsertUpdateData(qry);
//        Jsoup.connect(searchUrl + warName + "/" + "watcher/update").ignoreContentType(true).get();

    }

    //Rank code starts

    //Rank code ends
    @RequestMapping(value = {"config/update/filters/{companyid}"}, method = {
            RequestMethod.POST})
    public void updateFilters(@PathVariable int companyid,@RequestBody String data) throws IOException, URISyntaxException {
        File file = new File(resourceLocation, "/configurations.json");
        FileUtils.writeStringToFile(file, data);

        Jsoup.connect(searchUrl + warName + "/" + "watcher/update").ignoreContentType(true).get();

    }

    @RequestMapping(value = {"config/filters/{companyid}"}, method = {
            RequestMethod.GET})
    public String getFilters(@PathVariable int companyid) throws IOException, URISyntaxException {
        File file = new File(resourceLocation, "/configurations.json");
        return FileUtils.readFileToString(file);
    }


    @RequestMapping(value = {"logourl/{companyid}"}, method = {RequestMethod.GET})
    public Map<String, String> grtLogUrl(@PathVariable int companyid) throws IOException, URISyntaxException {
        Map<String, String> url = new HashMap<>();
        url.put("logo", dbUtils.getCompanyLogo(companyid));
        return url;
    }

    @RequestMapping(value = {"categories/{companyid}"}, method = {RequestMethod.GET})
    public ArrayList<String> getCategoryList(@PathVariable int companyid) throws IOException, URISyntaxException {
        ArrayList<String> category = new ArrayList<String>();
        category = dbUtils.getCategoryList(companyid);
        return category;
    }

    @RequestMapping(value = {"config/autosuggestdetails/{companyid}"}, method = {RequestMethod.GET})
    public Map<String, Boolean> getAutoSuggstDetails(@PathVariable int companyid) throws IOException, URISyntaxException {
        Map<String, Boolean> autoSug = new HashMap<>();
        autoSug = dbUtils.getAutoSuggestDetails(companyid);
        return autoSug;
    }

    @RequestMapping(value = {"config/update/autosuggestconfig/{companyid}/{autosuggest}/{topqueries}/{keywordsuggestions}/{searchscope}/{selectedtemplate}"}, method = {
            RequestMethod.POST})
    public void addUpdateAutoSuggest(@PathVariable int companyid, @PathVariable int autosuggest, @PathVariable int topqueries, @PathVariable int keywordsuggestions, @PathVariable int searchscope, @PathVariable String selectedtemplate) throws IOException, URISyntaxException {
        dbUtils.InsertUpdateData("delete from autosuggestconfig where companyid=" + companyid);
        String qry = "Insert into autosuggestconfig (companyid,autosuggest,topqueries,keywordsuggestions,searchscope,template) values (%s,%s,%s,%s,%s" + ",'%s')";
        qry = String.format(qry, companyid, autosuggest, topqueries, keywordsuggestions, searchscope, selectedtemplate);
        dbUtils.InsertUpdateData(qry);
    }




    @RequestMapping(value = {"validate/companyName/{companyName}"}, method = {RequestMethod.GET})
    public Map<String, String> isUserValid(@PathVariable String companyName) throws IOException, URISyntaxException {
        // valiate logic
        Map<String, String> validationResult = new HashMap<>();
        validationResult = dbUtils.isValidCompany(companyName);
        return validationResult;
    }


    @RequestMapping(value = {"config/panel/{companyid}"}, method = {RequestMethod.GET})
    public Map<String, Boolean> configPanelSetting(@PathVariable int companyid) throws IOException, URISyntaxException {
        Map<String, Boolean> companySettings = new HashMap<>();
        if(!StringUtils.isEmpty(companyid)){
            companySettings = dbUtils.getCompanySettings(companyid);
        }

        return companySettings;
    }


    //Email support
    @RequestMapping(value = {"send/email/{companyid}"}, method = {
            RequestMethod.POST})
    public void sendMail(@PathVariable int companyid,@RequestBody Map<String, String> data) throws IOException, URISyntaxException {
        SimpleMailMessage message = new SimpleMailMessage();
        String subject ="",body="";
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String key = entry.getKey();
            if (entry.getKey().equals("subject")) {
                subject = entry.getValue();
            }
            if (entry.getKey().equals("message")) {
                body = entry.getValue();
            }
        }

        message.setSubject(subject);
        message.setText(body);
        message.setTo("info@seeknshop.io");
        message.setFrom("query@seeknshop.io");
        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
