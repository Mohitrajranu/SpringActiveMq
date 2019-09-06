package com.leadauto.jms;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

@Component
public class Producer {

	@Autowired
	JmsTemplate jmsTemplate;

	public void sendMessage(final String queueName, final String message) {
		Map map = new Gson().fromJson(message, Map.class);
		final String textMessage = "Hello" + map.get("name");
		
		//call freetrial pallavi node creation servlet here .
		ResponseEntity<String> responseG =null;
		 String serverUrl = null;
		 String shoppingcarturl = null;
		 String mailapiurl = null;
		 JSONObject request = null;
		 HttpHeaders headers = null;
		 HttpEntity<String> entity = null;
		 RestTemplate restGetTemplate = null;
		 RestTemplate restTemplate = null;
		 
		 ResponseEntity<String> loginResponse = null;
		 //shoppingcarturl mailapiurl
		 try {
			 shoppingcarturl = (String)map.get("shoppingcarturl");
			 mailapiurl = (String)map.get("mailapiurl");
			 request = new JSONObject();
			 request.put("mailTo", map.get("mailTo"));
			 request.put("mailSubject", map.get("mailSubject"));
			 request.put("mailContent", map.get("mailContent"));
			 request.put("contentType", map.get("contentType"));
			 request.put("link", map.get("link"));
			System.out.println("Generated Json Request is "+request.toString()); 
		    headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        entity = new HttpEntity<String>(request.toString(), headers);
			serverUrl = shoppingcarturl+(String)map.get("mailTo");
			restGetTemplate = new RestTemplate();
			responseG = restGetTemplate.getForEntity(serverUrl, String.class);
			System.out.println("Sending message " + textMessage + " to queue - " + queueName+" and User Node Created in Shopping Cart :: "+responseG.getBody());
			restTemplate = new RestTemplate();
			loginResponse = restTemplate
		        	  .exchange(mailapiurl, HttpMethod.POST, entity, String.class);
					
			if (loginResponse.getStatusCode() == HttpStatus.OK) {
        		System.out.println("Mail api called successfully");
        	} else if (loginResponse.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        		System.out.println("Unable to call Mail api");
        	}
		 } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jmsTemplate.send(queueName, new MessageCreator() {

			public Message createMessage(Session session) throws JMSException {
				TextMessage message = session.createTextMessage();
				return message;
			}
		});
	}

}
