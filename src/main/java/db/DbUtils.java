package db;

import org.springframework.stereotype.Component;
import search.ConfigMainController;
import search.ReadProperties;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rvairamani on 24-04-2017.
 */

@Component
public class DbUtils {
    Connection c = null;
    Statement stmt = null;
    ReadProperties properties = new ReadProperties();

    public String dataBase = properties.getPropValues("DATABASE");
    public DbUtils() throws IOException {

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:"+ dataBase);
            c.setAutoCommit(false);
            stmt = c.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public ResultSet selectOutput(String qry) {
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(qry);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public boolean InsertUpdateData(String qry) {
        Statement stmt1 = null;
        try {
            stmt = c.createStatement();
            stmt.executeUpdate(qry);
            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        //	System.out.println(insertDataQuery);

        return true;
    }

    public Map convertResultSetToHashMap(ResultSet rs, String column1, String column2) {
        Map<String, String> map = new HashMap();
        try {
            while (rs.next()) {
                String key = rs.getString(column1);
                String value = rs.getString(column2);
                map.put(key, value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public String convertMapToQueryForUpdate(Map<String, String> update,int companyid, String table, String column1, String column2) {
        String qry = "update " + table + " set " + column2 + " = '%s' where " + column1 + " = '%s' and companyid = " + companyid;

        for (Map.Entry<String, String> entry : update.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            qry = String.format(qry, value, key);
        }
        return qry;
    }

    public String convertMapToQueryForAdd(Map<String, String> update,int companyid, String table, String column1, String column2) {
        String qry = "INSERT INTO " + table + "(CompanyID, " + column1 + "," + column2 + " ) values (" + companyid + ",'%s','%s')";

        for (Map.Entry<String, String> entry : update.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            qry = String.format(qry, key, value);
        }
        return qry;
    }

    public String queryForDelete(String key,int companyid, String table, String column1) {
        String qry = "DELETE FROM " + table + " where companyid = " + companyid + " and " + column1 + "='%s' ";
        qry = String.format(qry, key);
        return qry;
    }

    public String getCompanyLogo(int companyid) {
        String qry = "Select logourl FROM " + " company " + " where companyid = " + companyid;
        qry = String.format(qry);
        ResultSet rs = selectOutput(qry);
        String url = "";
        try {
            url = rs.getString("logourl");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public ArrayList<String> getCategoryList(int companyid) {
        String qry = "Select category FROM " + " category " + " where companyid = " + companyid;
        qry = String.format(qry);
        ResultSet rs = selectOutput(qry);
        ArrayList<String> category = new ArrayList<>();
        String url = "";
        try {
            while (rs.next()) {
                category.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }

    public Map<String, Boolean> getAutoSuggestDetails(int companyid) {
        String qry = "Select autosuggest,topqueries,keywordsuggestions,searchscope,template FROM " + " autosuggestconfig " + " where companyid = " + companyid;
        qry = String.format(qry);
        ResultSet rs = selectOutput(qry);
        Map<String, Boolean> map = new HashMap();
        try {
            while (rs.next()) {
                map.put("autosuggest", rs.getBoolean("autosuggest"));
                map.put("topqueries", rs.getBoolean("topqueries"));
                map.put("keywordsuggestions", rs.getBoolean("keywordsuggestions"));
                map.put("searchscope", rs.getBoolean("searchscope"));
                map.put("template", rs.getBoolean("template"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, String> isValidCompany(String companyName) {
        String qry = "Select companyid,securitykey FROM " + " company " + " where companyname = '" + companyName + "'";
        ResultSet rs = selectOutput(qry);
        Map<String, String> map = new HashMap();
        try {
            while (rs.next()) {
                map.put("isValid", "true");
                map.put("companyid", rs.getString("companyid"));
                map.put("securitykey", rs.getString("securitykey"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}
