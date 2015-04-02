/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adam6806.mediarequest;

import java.io.IOException;
import java.sql.Connection;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.json.JSONArray;

/**
 *
 * @author adam
 */
@WebServlet(asyncSupported = false, name = "StoreRequest", urlPatterns = {"/StoreRequest"})
public class StoreRequest extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    
    @Resource(lookup = "java:jboss/datasources/SpaceriskDB")
    private DataSource datasource;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
       
        try {
            connection = datasource.getConnection();
            if (request.getParameter("request").equalsIgnoreCase("data-request")) {
           
                response.getWriter().write(getResultJSON(connection));
            } else {
                String name = request.getParameter("medianame");
                String sqlStatement = "INSERT INTO earlynotify.\"addresses\" VALUES ('"+name+"')";
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                statement.execute(sqlStatement);
                response.getWriter().write(getResultJSON(connection));
            }
            response.setStatus(200);
        } catch (SQLException | IOException ex) {
            Logger.getLogger(StoreRequest.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(500);
        }
        
    }
    
    private String getResultJSON(Connection connection) {
        String output = "";
        try {
            String sqlStatement = "SELECT * FROM earlynotify.\"addresses\"";
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery(sqlStatement);
            JSONArray jSONArray = new JSONArray();
            while(resultSet.next()) {
                jSONArray.put(resultSet.getString(1));
            }
            output = jSONArray.toString();
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(StoreRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }
}
