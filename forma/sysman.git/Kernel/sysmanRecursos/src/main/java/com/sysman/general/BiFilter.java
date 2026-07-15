package com.sysman.general;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtro servlet que intercepta las peticiones al tablero Power BI.
 * Recupera la URL real desde la sesion HTTP usando el UUID temporal
 * generado por FrmTableroPowerBIControlador y redirige al navegador
 * sin exponer la URL real en ningun momento.
 *
 *
 * @version 1.0, 28/04/2026
 * @author User 1
 */
@WebFilter("/bi/view")
public class BiFilter implements Filter {

    private static final org.slf4j.Logger LOG =
            org.slf4j.LoggerFactory.getLogger(BiFilter.class);

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uuid = req.getParameter("ref");
        if (uuid == null || uuid.trim().isEmpty()) {
            LOG.warn("BiFilter: peticion sin parametro 'ref'");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Referencia requerida");
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null) {
            LOG.warn("BiFilter: sesion nula para ref={}", uuid);
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Sesión no válida");
            return;
        }

        String urlReal = (String) session.getAttribute("BI_URL_" + uuid);
        if (urlReal == null) {
            LOG.warn("BiFilter: UUID no encontrado o ya utilizado ref={}", uuid);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Referencia inválida o ya utilizada");
            return;
        }

        session.removeAttribute("BI_URL_" + uuid);
        LOG.info("BiFilter: redirigiendo tablero BI ref={}", uuid);

        resp.sendRedirect(urlReal);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.info("BiFilter inicializado");
    }

    @Override
    public void destroy() {
        LOG.info("BiFilter destruido");
    }
}
