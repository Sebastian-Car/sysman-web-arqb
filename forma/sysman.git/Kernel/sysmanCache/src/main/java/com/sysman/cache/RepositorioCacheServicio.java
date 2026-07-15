package com.sysman.cache;

import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.exception.SystemException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

/**
 * Session Bean implementation class RepositorioCache
 */
public class RepositorioCacheServicio {

    private static RepositorioCacheServicio instancia;

    private Map<String, String[]> llavesTablas;

    /**
     * Default constructor.
     * 
     * @throws SQLException
     * @throws NamingException
     */
    private RepositorioCacheServicio() {
        llavesTablas = new HashMap<>();
    }

    public static RepositorioCacheServicio getInstance() {
        if (instancia == null) {
            instancia = new RepositorioCacheServicio();
        }

        return instancia;
    }

    public String[] getLlave(UrlServiceCache esquema, String tabla)
                    throws SystemException {
        String[] rta = llavesTablas.get(esquema + "." + tabla);
        if (rta == null) {
            RequestManager rm = new RequestManager();
            UrlBean url = UrlServiceUtil.getUrlBeanById(esquema.getCodeUrl());
            Map<String, Object> pars = new HashMap<>();
            pars.put("TABLA", tabla);
            rta = rm.get(url.getUrl(), pars).getFields().get("LLAVE").toString()
                            .split(",");
            llavesTablas.put(esquema + "." + tabla, rta);

        }
        return rta;
    }

}
