package com.leadauto.jms;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;

@Component
public class Listener {
	
	@Autowired
	private Producer producer;

	@JmsListener(destination = "inbound.queue")
	public void receiveMessage(final Message jsonMessage) throws JMSException {
		String messageData = null;
		System.out.println("Received message " + jsonMessage);
		if(jsonMessage instanceof TextMessage) {
			TextMessage textMessage = (TextMessage)jsonMessage;
			messageData = textMessage.getText();
		}
		producer.sendMessage("outbound.queue", messageData);
	}

	/*@JmsListener(destination = "inbound.topic")
	@SendTo("outbound.topic")
	public String receiveMessageFromTopic(final Message jsonMessage) throws JMSException {
		String messageData = null;
		System.out.println("Received message " + jsonMessage);
		String response = null;
		if(jsonMessage instanceof TextMessage) {
			TextMessage textMessage = (TextMessage)jsonMessage;
			messageData = textMessage.getText();
			Map map = new Gson().fromJson(messageData, Map.class);
			response  = "Hello " + map.get("name");
		}
		return response;
	}*/
}