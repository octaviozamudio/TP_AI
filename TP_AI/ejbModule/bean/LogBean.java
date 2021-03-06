package bean;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import entity.Log;
import exception.PersistException;

@Stateless
@SuppressWarnings("unchecked")
public class LogBean extends GenericBean<Log> {

	public LogBean() {
		super(Log.class);
	}

	public void log(String modulo, String descripcion, Date fecha) throws PersistException {
		Log log = new Log();
		log.setModulo(modulo);
		log.setDescripcion(descripcion);
		log.setFecha(fecha);
		save(log);
	}

	public void log(String modulo, String descripcion) throws PersistException {
		log(modulo, descripcion, new Date());
	}

	public String getLogsMonitor() {

		String logs = null;
		try {
			List<Log> lista = (List<Log>) em.createQuery("select m from Log m").getResultList();
			logs = toJson(lista);
		} catch (Exception e) {
			logException(e);
		}

		return logs;
	}
	
	public List<Log> getListLogsMonitor() {

		List<Log> logs = null;
		try {
			logs = (List<Log>) em.createQuery("select m from Log m").getResultList();
		} catch (Exception e) {
			logException(e);
		}

		return logs;
	}
	
	public List<Log> getListLogsMail() {

		List<Log> logs = null;
		try {
			logs = (List<Log>) em.createQuery("select m from LogMail m").getResultList();
		} catch (Exception e) {
			logException(e);
		}

		return logs;
	}
	
	public String getLogsMail() {

		String logs = null;
		try {
			List<Log> lista = (List<Log>) em.createQuery("select m from LogMail m").getResultList();
			logs = toJson(lista);
		} catch (Exception e) {
			logException(e);
		}

		return logs;
	}

	public String getLogsPorModulo(String modulo) {

		String logs = null;
		try {
			List<Log> lista = (List<Log>) em.createQuery("select m from Log m where m.modulo = ?")
					.setParameter(1, modulo).getResultList();
			toJson(lista);
		} catch (Exception e) {
			logException(e);
		}

		return logs;
	}

}
