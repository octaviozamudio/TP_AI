package rest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import bean.ModulosBean;
import bean.OrdenesDespachoBean;
import bean.VentasBean;
import entity.Modulos;
import exception.NoExisteException;
import response.RecibirCambioEstadoResponse;
import util.Utilities;
import view.ItemArticuloView;
import view.VentaView;
import ws.orden.IRecibirOrdenDespachoWs;
import ws.orden.IRecibirOrdenDespachoWsProxy;
import ws.orden.Item;
import ws.orden.OrdenDespacho;

@Stateless
@Path("/OrdenDeDespacho")
public class OrdenDespachoRESTService {

	@EJB
	private OrdenesDespachoBean ordenesDespachoBean;
	@EJB
	private VentasBean ventasBean;
	@EJB
	private ModulosBean modulosBean;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response recibirCambioEstado(OrdenesDespachoArray ordenes) {
		int status = 200;
		List<RecibirCambioEstadoResponse> response = new ArrayList<>();

		for (OrdenDespachoJson orden : ordenes.getOrdenes()) {
			try {
				ordenesDespachoBean.actualizarOrden(orden.getIdOrdenDeDespacho());
				response.add(new RecibirCambioEstadoResponse(orden.getIdOrdenDeDespacho().toString(),
						"Orden de despacho actualizada correctamente."));
			} catch (NoExisteException e) {
				status = 400;
				response.add(new RecibirCambioEstadoResponse(orden.getIdOrdenDeDespacho().toString(),
						"No existe la orden de despacho enviada."));
				ordenesDespachoBean.logException(e);
			}
		}

		return Response.status(status).entity(response).build();
	}

	@POST
	@Path("/enviar")
	@Produces(MediaType.APPLICATION_JSON)
	public Response enviarOrden(@FormParam("idVenta") int idVenta, @FormParam("idDespacho") int idDespacho) {
		try {
			String url = Utilities.normalizarUrl(modulosBean.getUrlModulo(Integer.toString(idDespacho), Modulos.Despacho))
					+ "DespachoWeb/RecibirOrdenDespachoWs";
			IRecibirOrdenDespachoWs ws = new IRecibirOrdenDespachoWsProxy(url);

			VentaView venta = ventasBean.asignarDespachoAVenta(idVenta, idDespacho);

			List<Item> wsItems = new ArrayList<Item>();
			for (ItemArticuloView ia : venta.getArticulos()) {
				Item i = new Item(String.valueOf(ia.getArticulo().getCodigo()), ia.getCantidad());
				wsItems.add(i);
			}

			OrdenDespacho wsOrden = new OrdenDespacho(venta.getOrden().getId().toString(), venta.getId(), "16",
					Calendar.getInstance(), wsItems.toArray(new Item[wsItems.size()]));

			return Response.status(200).entity(ws.recibirOrdenDespacho(wsOrden)).build();
		} catch (Exception e) {
			return Response.status(400).entity(Utilities.generarMensajeError(e)).build();
		}
	}

}
