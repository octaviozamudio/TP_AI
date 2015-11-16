package jms;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.auth.login.LoginException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import bean.GenericBean;
import entity.Log;
import entity.LogMail;

@MessageDriven(name = "RecepcionInformesEmailMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/MailQueue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class RecepcionInformesEmailMDB implements MessageListener {

	@PersistenceContext(unitName = "CRM")
	private EntityManager em; 
	
	public RecepcionInformesEmailMDB() {
		
	}

	public void onMessage(Message message) {

		TextMessage json = (TextMessage) message;
		try {
			// Convert JSON String to List
			Type type = new TypeToken<List<LogMail>>() {
			}.getType();
			List<LogMail> logs = new Gson().fromJson(json.getText(), type);
			Logger.getAnonymousLogger().info("Informes recibidos: " + logs.size());

			for (LogMail l : logs) {
				em.merge(l);
				em.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
