package com.sysman.session;

import java.util.HashMap;

import javax.ejb.Remote;

@Remote
public interface SessionContainerRemoteSt {

    HashMap<String, Object> getSession(String id);

    void setSession(String id, HashMap<String, Object> variables);

    Object[] getDataLoad(String idSesion);

    void setSessionVar(String id, String nombre, Object valor);

    Object[] getDataNomina(String idSesion);

    Object[] getDataFacturacionGeneral(String idSesion);

    Object[] getDataTramite(String idSession);

    Object[] getDataClaseEvaluacion(String idSession);

    Object getSessionVar(String id, String nombre);

    void removeSessionVar(String id, String nombre);

    void removeSession(String id);

    boolean existSession(String idSession);

    Object getApplicationVar(String id);

    void setApplicationVar(String id, Object value);

    Object[] getDataGeneradorReportes(String idSession);

    Object[] getDataPlusvalia(String idSesion);

}
