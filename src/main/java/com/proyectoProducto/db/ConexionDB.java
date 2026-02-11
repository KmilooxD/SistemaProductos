package com.proyectoProducto.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConexionDB {
    public static Connection getConection(){
        try(InputStream input = ConexionDB.class.getClassLoader().getResourceAsStream("db.properties");) {
            if(input == null){
                    throw new RuntimeException("No se encontro db.properties");
            }
        Properties prop = new Properties();
        prop.load(input);
        String url=prop.getProperty("db.url");
        String user=prop.getProperty("db.user");
        String password=prop.getProperty("db.password");
        return  DriverManager.getConnection(url,user,password);
        }catch(Exception e){
            throw new RuntimeException("Error al conectar a la BD", e);
        }
    }
}
