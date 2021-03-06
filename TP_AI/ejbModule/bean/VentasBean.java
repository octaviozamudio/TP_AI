package bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.PersistenceException;

import entity.Articulo;
import entity.Coordenada;
import entity.Despacho;
import entity.ItemArticulo;
import entity.OrdenDespacho;
import entity.Reporte;
import entity.Usuario;
import entity.Venta;
import exception.NoExisteException;
import exception.PersistException;
import exception.VentaException;
import util.ItemArticuloViewUtils;
import view.ItemArticuloView;
import view.PortalView;
import view.ReporteView;
import view.VentaDespachoRecomendadoView;
import view.VentaSinArticulosView;
import view.VentaView;

@Stateless
@SuppressWarnings("unchecked")
public class VentasBean extends GenericBean<Venta> {

	@EJB
	private DespachosBean despachosBean;
	@EJB
	private OrdenesDespachoBean ordenesBean;

	public VentasBean() {
		super(Venta.class);
	}

	public Integer guardarVenta(VentaView view) throws PersistException {
		Venta venta = new Venta();
		venta.setPortal(view.getPortal());
		venta.setTotal(view.getTotal());
		venta.setCodigo(view.getCodigo());
		venta.setFecha(view.getFecha());
		venta.setUsuario(view.getUsuario() != null ? new Usuario(view.getUsuario()) : null);
		venta.setDestino(view.getDestino() != null ? new Coordenada(view.getDestino()) : null);
		venta.setOrden(view.getOrden() != null ? new OrdenDespacho(view.getOrden()) : null);
		
		ItemArticuloViewUtils utils = new ItemArticuloViewUtils();
		List<ItemArticulo> articulos = new ArrayList<ItemArticulo>();
		for (ItemArticuloView iv : view.getArticulos()) {
			ItemArticulo item = utils.fromView(iv);
			Articulo a = item.getArticulo();
			a.setId(save(a));
			articulos.add(item);
		}
		venta.setArticulos(articulos);

		save(venta);
		return venta.getId();
	}
	
	private Integer save(Articulo obj) throws PersistException {
		try {
			em.persist((Articulo) obj);
			em.flush();
		} catch (Exception e) {
			throw new PersistenceException("No se pudo persistir la entidad. Excepcion: " + e.getMessage());
		}

		Integer id = obj.getId();
		if (id == null) {
			throw new PersistException("No se pudo persistir la entidad");
		}
		return id;
	}

	public Venta obtenerVenta(Integer idVenta) throws NoExisteException {
		return null;
	}

	public VentaView asignarDespachoAVenta(Integer idVenta, Integer idDespacho) throws NoExisteException, PersistException, VentaException {
		Venta venta = get(idVenta);
		Despacho despacho = despachosBean.get(idDespacho);

		if (venta.getOrden() != null) {
			throw new VentaException("La venta ya tiene asignada una orden.");
		} else {
			venta.setOrden(ordenesBean.generarOrdenDespacho(despacho));
			em.merge(venta);
		}

		return venta.getView();
	}

	public List<VentaDespachoRecomendadoView> obtenerVentasSinOrdenDespacho() {
		return despachosBean
				.getVentasDespachoRecomendado(executeQuery("select v from Venta v left join v.orden od where od is null and v.destino is not null"));
	}

	public List<VentaSinArticulosView> getAllSinArticulosViews() {
		List<VentaSinArticulosView> ventasView = new ArrayList<>();
		for (Venta v : getAll()) {
			ventasView.add(v.getVentaSinArticulosView());
		}

		return ventasView;
	}

	public List<PortalView> getReportePortales() {
		Reporte reporte = new Reporte();
		for (Venta v : getAll()) {
			reporte.agregarVenta(v);
		}
		reporte.ordenar();

		return reporte.getPortales();
	}

	public List<ReporteView> getReportes() {

		List<ReporteView> reportes = new ArrayList<ReporteView>();
		List<Object[]> lista = em.createQuery("select v.portal, sum(v.total) from Venta v group by v.portal order by sum(v.total) desc")
				.getResultList();

		for (int i = 0; i < lista.size(); i++) {
			reportes.add(new ReporteView((String) lista.get(i)[0], (double) lista.get(i)[1]));
		}

		return reportes;
	}

	public VentaView getView(String codigo) throws NoExisteException {
		List<Venta> results = em.createQuery("from Venta where codigo = :codigo").setParameter("codigo", codigo).getResultList();
		if (!results.isEmpty()) {
			return results.get(0).getView();
		}
		throw new NoExisteException(String.format("No se encontro venta con el codigo %s.", codigo));
	}
}
