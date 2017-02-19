package com.myapp.server;

import static spark.Spark.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hibernate.Session;
import org.hibernate.query.Query;
import com.google.gson.Gson;
import com.hibernate.FbData;
import com.hibernate.HibernateUtil;
import com.myapp.exceptions.*;
import com.myapp.service.ServerService;


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
        HTTPErrorJson httpError = new HTTPErrorJson();
        ServerService service = new ServerService();
        Gson gson = new Gson();
       
        
        port(8081);
    	get("/getData/:code", (request, response) -> {
    	Session session = HibernateUtil.getSessionFactory().openSession();
    	session.beginTransaction();
    	
    	String idPage = request.params(":code").trim().toString();

    	

    	List<FbData> dbData	= null;

    		
    	dbData = service.getDataDB(idPage, dbData);
    	
		if(dbData.size()==0){
			
    	try{
    		
    		service.insertFbData(String.valueOf(idPage),"", false);
    		
    	}catch(FbDataRequestException e){    		
    		logger.error(e.getMessage(), e);
    		httpError.setCode("1");
    		httpError.setMessage("Bad Request");
    		response.status(e.getCode());
    		
    	}
    	
    	
		if(response.status()==200){

			dbData = service.getDataDB(idPage, dbData);
		}
		
		}else{
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("d-MMMM-y");
			String sinceDate = dateFormat.format(dbData.get(0).getDate());
			
			sinceDate = sinceDate.replace("-", "%20");
			
			try{
				
				
	    		service.insertFbData(idPage, sinceDate, true);
	    		
	    	}catch(FbDataRequestException e){
	    		httpError.setCode("2");
	    		httpError.setMessage("Server Error");
	    		logger.error(e.getMessage(), e);
	    		response.status(e.getCode());
	    		
	    	}
			
			
			
			
			
		}
		
		
		

		session.clear();
		session.close();
		
		
		String json = "";
		
		if(response.status()==200){
			json = gson.toJson(dbData);
		}else {
			
			json = gson.toJson(httpError);
		}
		
		
	    return json;
	});
		
    	
    		


    }

  
}