package com.myapp.server;

import static spark.Spark.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;





import javax.swing.JFrame;
import javax.swing.JLabel;





import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hibernate.Session;
import org.hibernate.query.Query;





import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hibernate.FbData;
import com.hibernate.HibernateUtil;
import com.myapp.exceptions.FbDataRequestException;
import com.myapp.service.ServerService;import com.restfb.exception.ResponseErrorJsonParsingException;





public class Server {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
    	Logger logger = Logger.getLogger(Server.class);
    	PropertyConfigurator.configure("log4j.properties");
    	JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(280,200);
		JLabel label = new JLabel("Cerrar la ventana para terminar el proceso");
		f.add(label);
        f.setVisible(true);
        
        ServerService service = new ServerService();
        Gson gson = new Gson();
       
        
    
    	
    	
        port(8081);
    	get("/getData/:code", (request, response) -> {
    	Session session = HibernateUtil.getSessionFactory().openSession();
    	session.beginTransaction();
    	
    	String idPage = request.params(":code").trim().toString();
    	
    	idPage = "noodiascuando";    	
    	

    	List<FbData> dbData	= null;

    		
    	dbData = service.getDataDB(idPage, dbData);
    	
		if(dbData.size()==0){
			
    	try{
    		
    		service.insertFbData(String.valueOf(idPage),"", false);
    		
    	}catch(FbDataRequestException e){
    		
    		logger.error(e.getMessage(), e);
    		response.status(e.getCode());
    		
    	}
    	
    	
		if(response.status()==200){

//			Query query = session.createQuery("from FbData where idPage = :idPage order by date desc ");
//			query.setParameter("idPage", "546183928849302");
//			dbData = query.list();
//			dbData = service.getDataDB(idPage, dbData);
			
			Query query = session.createQuery("from FbData where idPage = :idPage order by date desc");
			query.setParameter("idPage", String.valueOf(idPage));
			

			dbData = query.list();
		}
		
		}else{
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("d-MMMM-y");
			String sinceDate = dateFormat.format(dbData.get(0).getDate());
			
			sinceDate = sinceDate.replace("-", "%20");
			
			try{
				
				
	    		service.insertFbData(idPage, sinceDate, true);
	    		
	    	}catch(FbDataRequestException e){
	    		
	    		logger.error(e.getMessage(), e);
	    		response.status(e.getCode());
	    		
	    	}
			
			
			
			
			
		}
		
		
		

		session.clear();
		session.close();
		
		
		
		
		String json = gson.toJson(dbData);
		
	    return json;
	});
		
    	
    		


    }

  
}