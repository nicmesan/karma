package com.server.dao;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hibernate.FbData;
import com.hibernate.HibernateUtil;
import com.server.exceptions.FbDataRequestException;



public class ServerDAO {
	Logger logger = Logger.getLogger(ServerDAO.class);
	Session session = null;
	
	public List<FbData> getDataDB(String idPage, List<FbData> dbData){
		PropertyConfigurator.configure("log4j.properties");
		logger.info("Ingresa metodo getDataDB(), para acceder a la DB y obtener los datos");
		
		Date day = new Date();
		SimpleDateFormat date3 = new SimpleDateFormat("y-MM-d k:mm:ss");
		Long amountDaysBefore = 10L;
		Date daysBefore = new Date(day.getTime() - amountDaysBefore * 24 * 3600 * 1000);
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
		    session.beginTransaction();
		    Query query = null;
		    
			
			query = session.createQuery("from FbData where idPage = :idPage order by date desc");
			query.setParameter("idPage", idPage);

			dbData = query.list();
		}catch(HibernateException e){
			
			System.out.println("Error en obtencion de datos: " + e);
			
		}finally{
			session.clear();
			session.close();
	
		}
    	
		
		return dbData;
		
	}
	
	public void insertFbData(String code, String sinceDate, boolean requesUrlWithDate) throws FbDataRequestException{
		PropertyConfigurator.configure("log4j.properties");
		logger.info("Ingresa metodo insertFbData(), para grabar los datos en la DB");
		String url= requesUrlWithDate==false ?  "https://graph.facebook.com/" + code +"/feed?fields=shares,likes.limit(0).summary(true),from"
				
		: "https://graph.facebook.com/" + code +"/feed?fields=shares,likes.limit(0).summary(true),from&since=" + sinceDate + "&until=now" ;
		
//		if(option==1){
//			
//		 url = "https://graph.facebook.com/" + code +"/feed?fields=shares,likes.limit(0).summary(true),from";
//		}else {
//			 url = "https://graph.facebook.com/" + code +"/feed?fields=shares,likes.limit(0).summary(true),from&since=" + sinceDate + "&until=now";
//		}
		Gson gson = new Gson();  	
    	JsonArray array = null;
    	JsonObject jsonFile = null;
    	
		String responseData;
		session = HibernateUtil.getSessionFactory().openSession();
    	session.beginTransaction();
    	
		try{
			
		
			do{
				responseData = facebookRequest(url);
				jsonFile = gson.fromJson(responseData, JsonObject.class);
				array = jsonFile.get("data").getAsJsonArray();
				url = mapDataToInsert(responseData, array, jsonFile, session);
    		
    		 
    			
			   }while(array.size()!=0);
    	
			session.getTransaction().commit();
    	
		}catch(HibernateException e){
			System.out.println("Error al insertar los datos: " + e);
			session.getTransaction().rollback();
			
		} catch (FbDataRequestException e) {
		
//			e.printStackTrace();
			throw new FbDataRequestException(e.getMessage(), e.getCode());
		}finally{
			session.clear();
			session.close();
		}
		
    	
	}
	public String mapDataToInsert(String responseData, JsonArray array, JsonObject jsonFile, Session session ){
		PropertyConfigurator.configure("log4j.properties");
		logger.info("Ingresa metodo mapDataToInsert(), para mapear los datos obtenidos de la consulta con Facebook");
		
		String url = "";
		JsonElement jsonElement;
		String element;
		  try {
		for(int i = 0 ; i < array.size(); i++){
			 FbData fbData = new FbData();
			 Date date = new Date();
			 fbData.setDate(date);
			 
			if(array.get(i).getAsJsonObject().get("shares")!= null){
				jsonElement = array.get(i).getAsJsonObject().get("shares").getAsJsonObject().get("count");
				element = jsonElement.toString();
				
				fbData.setShares(Long.parseLong(element));
				
			  }
			else{
				  fbData.setShares(0L);
			  }
			
			if(array.get(i).getAsJsonObject().get("likes").getAsJsonObject()!= null){
				jsonElement = array.get(i).getAsJsonObject().get("likes").getAsJsonObject().get("summary").getAsJsonObject().get("total_count");
				element = jsonElement.toString();
				fbData.setLikes(Long.parseLong(element));
				
			  }
			else{
				  fbData.setLikes(0L);
			  }
			
			if(array.get(i).getAsJsonObject().get("created_time")!= null){
				jsonElement = array.get(i).getAsJsonObject().get("created_time");
				element = jsonElement.toString().substring(12,14);
				fbData.setTime(element);
				
			  }
			
			if(array.get(i).getAsJsonObject().get("from")!= null){
				jsonElement = array.get(i).getAsJsonObject().get("from").getAsJsonObject().get("name");
				element = jsonElement.toString();
				element = element.substring(1,element.length()-1);
				element= element.replace(" ","%20");
				element = element.replace("%20", "").toLowerCase();				
				fbData.setIdPage(element);
				
			  }
			
			
			session.save(fbData);
			
		}
		if(jsonFile.getAsJsonObject().get("paging")!= null){
			jsonElement = jsonFile.getAsJsonObject().get("paging").getAsJsonObject().get("next");
			element = jsonElement.toString();
			url = element.substring(1,element.length()-1);
			
			
		  } 
		} catch (JsonParseException  e) {
			System.out.println("Error al parsear el archivo JSON: " + e);
			
		}
		return url;
			
	}

	public String facebookRequest(String url) throws FbDataRequestException {
		  PropertyConfigurator.configure("log4j.properties");
		  logger.info("Ingresa metodo facebookRequest(), para hacer la consulta con Facebook");
		  
		  String jsonData = "";
		  String inputLine;
		  StringBuffer response = new StringBuffer();
		  HttpURLConnection con = null;
		  URL obj;
		
				try {
					obj = new URL(url);
					con = (HttpURLConnection) obj.openConnection();
				
					con.setRequestMethod("GET");
					con.setRequestProperty("Authorization", "Bearer EAAPrxZAXpX9wBAGZBNKfDpkn1mg00qBoIzAbZCGzZCa8Fj1ao3sxcxzmQhJPmXEeAzoOhtakLS9FsxZAqQ6xCuQ2vX0ZBN9AVwI4ChxxhYdQrnIwLAAvpMsUTDCDITNnxvlH2F7ZBdAMbEcXUoHhLZAuzqoKfy7i28EidaQD6iARDQZDZD");
					
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					
					}
					in.close();
					jsonData = response.toString();
				} catch (MalformedURLException e) {
					
					throw new FbDataRequestException("URL mal formada de facebook", 500);
				} catch(FileNotFoundException e) {

				try {
						throw new FbDataRequestException("No existe ese codigo para hacer una request", con.getResponseCode());
					} catch (IOException e1) {

						/*ACA LE PONGO EL CON.GETRESPONSECODE() y devuelve un 404 cuando pongo mal el codigo de la pgina a buscar, la preugnta es se harcodea el 400 o se
						 * deja ese ?*/
					}
				}catch (IOException e) {

					throw new FbDataRequestException("Problemas para conectarse al servidor de facebook", 500);
					
				}
				
		 con.disconnect();
		 
		  
		  return jsonData;
		  
		  
	  }
	
}
