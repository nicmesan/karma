package com.myapp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.hibernate.FbData;
import com.myapp.dao.ServerDAO;
import com.myapp.exceptions.FbDataRequestException;
import com.myapp.server.Server;

public class ServerService {
	Logger logger = Logger.getLogger(ServerService.class);
	ServerDAO dao = new ServerDAO();
	
	 public List<FbData> getDataDB(String idPage,List<FbData> dbData){
		PropertyConfigurator.configure("log4j.properties");
		logger.info("Ingresa metodo getDataDB(), para acceder a la DB y obtener los datos");
		
		
		dbData = dao.getDataDB(idPage, dbData);
		
		return dbData;
	 }
	 
	 public void insertFbData(String code, String sinceDate, boolean requesUrlWithDate) throws FbDataRequestException{
		 PropertyConfigurator.configure("log4j.properties");
		 logger.info("Ingresa metodo insertFbData(), para grabar los datos en la DB");
		 
		 
		 try {
			 
			dao.insertFbData(code,  sinceDate,  requesUrlWithDate);
	
		} catch (FbDataRequestException e) {
//			e.printStackTrace();
			
			throw new FbDataRequestException(e.getMessage(), e.getCode());
			
		}
		 
		}
	
	 	
}
