package cavani.endorfina.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet("/Service")
public class ServiceServlet extends HttpServlet
{

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
	{
		final String principal = request.getUserPrincipal().getName();
		final String sslSession = (String) request.getAttribute("javax.servlet.request.ssl_session");

		response.setCharacterEncoding("UTF-8");

		final PrintWriter writer = response.getWriter();
		writer.println("<html><head></head><body>");
		writer.println("<h1>Olá, " + principal + "!</h1>");
		writer.println("<p>" + String.format("%1$tF %1$tH:%1$tM:%1$tS", new Date()) + "</p>");
		writer.println("<p>" + (sslSession == null ? "(no ssl)" : sslSession) + "</p>");
		writer.println("</body></html>");
		writer.close();
	}

}
