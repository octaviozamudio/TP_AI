package bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import entity.Despacho;
import entity.Estado;
import entity.OrdenDespacho;
import exception.NoExisteException;
import exception.PersistException;
import response.EnviarOrdenesResponse;
import view.OrdenDespachoActivaView;

@Stateless
public class OrdenesDespachoBean extends GenericBean<OrdenDespacho> {

	@EJB
	private OrdenesDespachoBean ordenes;

	public OrdenesDespachoBean() {
		super(OrdenDespacho.class);
	}

	public void actualizarOrden(Integer id, Estado estado) throws NoExisteException {
		OrdenDespacho orden = get(id);
		orden.setEstado(estado);
		em.merge(orden);
	}

	public OrdenDespacho generarOrdenDespacho(Despacho despacho) throws PersistException {
		OrdenDespacho orden = new OrdenDespacho(despacho, Estado.ACTIVO);
		save(orden);
		return orden;
	}

	private List<OrdenDespacho> getOrdenesSinEnviar() {
		return executeQuery(
				"from OrdenDespacho od join fetch od.venta join fetch od.despacho where enviada is null or enviada = false");
	}

	public EnviarOrdenesResponse enviarOrdenesActivas() {
		List<OrdenDespacho> ordenesActivas = getOrdenesSinEnviar();
		List<OrdenDespachoActivaView> ordenesActivasView = new ArrayList<>();
		String mensaje = "Ordenes activas enviadas correctamente.";

		for (OrdenDespacho o : ordenesActivas) {
			ordenesActivasView.add(o.getOrdenDespachoActivaView());
			// enviar por ws a portal, setear mensaje
			o.setEnviada(true);
			em.merge(o);
		}

		if (ordenesActivasView.isEmpty()) {
			mensaje = "No hay ordenes activas para enviar en este momento.";
		}
		return new EnviarOrdenesResponse(ordenesActivasView, mensaje);
	}

	public List<OrdenDespachoActivaView> getOrdenesActivasView() {
		List<OrdenDespacho> ordenesActivas = getOrdenesSinEnviar();
		List<OrdenDespachoActivaView> ordenesActivasView = new ArrayList<>();

		for (OrdenDespacho o : ordenesActivas) {
			ordenesActivasView.add(o.getOrdenDespachoActivaView());
		}

		return ordenesActivasView;
	}

}
