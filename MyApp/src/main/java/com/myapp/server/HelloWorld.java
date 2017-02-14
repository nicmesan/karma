package com.myapp.server;

import static spark.Spark.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hibernate.FbData;
import com.hibernate.HibernateUtil;




public class HelloWorld {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
    	port(8081);
    	get("/hola", (request, response) -> {
    		JFrame f = new JFrame();
    		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		f.setSize(280,200);
    		JLabel label = new JLabel("Cerrar la ventana para terminar el proceso");
    		f.add(label);
            f.setVisible(true);
//        	Persona persona = new Persona();
//        	ArrayList<String> list = new ArrayList<String>();
//        	ArrayList<Persona> lista = new ArrayList<Persona>();
//    		
//    		String name = request.queryParams("name");
//    		String lastName = request.queryParams("lastName");
//    		persona.setName(name);
//    		persona.setLastName(lastName);
//    		list.add("Sergio");
//    		list.add("Kili");
//    		persona.setFamily(list);
//    		lista.add(persona);
//    		lista.add(persona);
//    		lista.add(persona);
    	
    	Session session = HibernateUtil.getSessionFactory().openSession();
    	session.beginTransaction();
    	
    	Query query = session.createQuery("from FbData where idPage = :idPage ");
		query.setParameter("idPage", "546183928849302");
		List<FbData> dbData = query.list();
		
		if(dbData.size()==0){
			
    	String responseData = "";
    	String url = "https://graph.facebook.com/546183928849302/feed?fields=shares,likes.limit(0).summary(true),from";
    	
    	Gson gson = new Gson();    	
    	JsonArray array = null;
    	JsonObject jsonFile = null;
    	JsonElement jsonElement;
		String element;
		
    	
    	
    	do{
    		 responseData = facebookRequest(url);
    		 jsonFile = gson.fromJson(responseData, JsonObject.class);
    		 array = jsonFile.get("data").getAsJsonArray();
    		 
    			for(int i = 0 ; i < array.size(); i++){
    				 FbData fbData = new FbData();
    				 Date date = new Date();    				 
    				 fbData.setDate(date);
    				 
    				if(array.get(i).getAsJsonObject().get("shares")!= null){
    					jsonElement = array.get(i).getAsJsonObject().get("shares").getAsJsonObject().get("count");
    					element = jsonElement.toString();
    					
    					fbData.setShares(Long.parseLong(element));
    					
    				  }else{
    					  fbData.setShares(0L);
    				  }
    				
    				if(array.get(i).getAsJsonObject().get("likes").getAsJsonObject()!= null){
    					jsonElement = array.get(i).getAsJsonObject().get("likes").getAsJsonObject().get("summary").getAsJsonObject().get("total_count");
    					element = jsonElement.toString();
    					fbData.setLikes(Long.parseLong(element));
    					
    				  }else{
    					  fbData.setLikes(0L);
    				  }
    				
    				if(array.get(i).getAsJsonObject().get("created_time")!= null){
    					jsonElement = array.get(i).getAsJsonObject().get("created_time");
    					element = jsonElement.toString().substring(12,14);
    					fbData.setTime(element);
    					
    				  }
    				
    				if(array.get(i).getAsJsonObject().get("from")!= null){
    					jsonElement = array.get(i).getAsJsonObject().get("from").getAsJsonObject().get("id");
    					element = jsonElement.toString();
    					fbData.setIdPage(element.substring(1,element.length()-1));
    					
    				  }
    				
    				
    				session.save(fbData);
    				
    			}
    			if(jsonFile.getAsJsonObject().get("paging")!= null){
					jsonElement = jsonFile.getAsJsonObject().get("paging").getAsJsonObject().get("next");
					element = jsonElement.toString();
					url = element.substring(1,element.length()-1);
					
					
				  }
    	}while(array.size()!=0);
	 
    	
		session.getTransaction().commit();
		
		
		query = session.createQuery("from FbData where idPage = :idPage ");
		query.setParameter("idPage", "546183928849302");
		dbData = query.list();
		
		}
		
		
		

		session.clear();
		session.close();
		
		
		Gson gson = new Gson();
		
		String json = gson.toJson(dbData);
		
	    return json;
	});
		
    	
    	
    		

		
//		------------------------------------------------------------CONEXION BASE DE DATOS------------------------------------------------------------------------------
//		try {
//
//			Class.forName("com.mysql.jdbc.Driver");     
//
//			Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/karma","root","maradona10");         
//			Statement stmt=con.createStatement();            
//			stmt = con.createStatement();
//			ResultSet rs = stmt.executeQuery("insert into dataresponse values" + "(" + "null,"+ dataList.get(0).getLikes() + ","+ dataList.get(0).getShares()+ ","+ dataList.get(0).getTime() +")");
//			
//
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  
		
		
	
		
//		---------------------------------------------------------------------CONEXION BASE DE DATOS--------------------------------------------------------------------

    	System.out.println("hola");
    }

  public static String facebookRequest(String url){
	  String jsonData = "";
	  String inputLine;
	  StringBuffer response = new StringBuffer();
	  URL obj;
	
			try {
				obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				
				con.setRequestMethod("GET");
				con.setRequestProperty("Authorization", "Bearer EAAPrxZAXpX9wBAGZBNKfDpkn1mg00qBoIzAbZCGzZCa8Fj1ao3sxcxzmQhJPmXEeAzoOhtakLS9FsxZAqQ6xCuQ2vX0ZBN9AVwI4ChxxhYdQrnIwLAAvpMsUTDCDITNnxvlH2F7ZBdAMbEcXUoHhLZAuzqoKfy7i28EidaQD6iARDQZDZD");
				
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				
				}
				in.close();
				jsonData = response.toString();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
	 
	  
	  return jsonData;
	  
	  
  }
}