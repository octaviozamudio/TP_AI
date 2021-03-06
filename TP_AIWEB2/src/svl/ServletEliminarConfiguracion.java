package svl;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bean.ModulosBean;

/**
 * Servlet implementation class ServletEliminarConfiguracion
 */
@WebServlet("/eliminarConfiguracion")
public class ServletEliminarConfiguracion extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	private ModulosBean modulosBean;

	public ServletEliminarConfiguracion() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String ip = request.getParameter("ip");
		
		if(ip != null)
			modulosBean.deleteMiConfiguracion(ip);
		
	}

}
