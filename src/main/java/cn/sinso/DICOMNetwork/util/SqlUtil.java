package cn.sinso.DICOMNetwork.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlUtil {


   public static List<Map<String, Object>> getBySql(String sql) {

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            Class.forName(PropertiesUtils.getValue("driverClassName"));
            conn = DriverManager.getConnection(
                    PropertiesUtils.getValue("db_url"),
                    PropertiesUtils.getValue("db_username"), PropertiesUtils.getValue("db_password"));
            // conn.setAutoCommit(false);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();

            while (rs.next()) {
                Map<String, Object> rowData = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), rs.getObject(i));
                }
                list.add(rowData);
            }


            // conn.commit();      conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
                if (!stmt.isClosed()) {
                    stmt.close();
                }
                if (!rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
//        System.out.println("查询结果行数:"+list.size());
        return  list;
    }


    public static void updateBySql(String sql) {

        Connection conn = null;
        Statement stmt = null;
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            Class.forName(PropertiesUtils.getValue("driverClassName"));
            conn = DriverManager.getConnection(
                    PropertiesUtils.getValue("db_url"),
                    PropertiesUtils.getValue("db_username"), PropertiesUtils.getValue("db_password"));
            // conn.setAutoCommit(false);
            stmt = conn.createStatement();
            stmt.execute(sql);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
                if (!stmt.isClosed()) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
