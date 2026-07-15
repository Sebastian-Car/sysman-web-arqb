package com.sysman.session;

import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.session.utl.ConstantesHojasDeVidaEnum;
import com.sysman.session.utl.ConstantesWorkflowEnum;

import java.util.HashMap;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;

/**
 * Session Bean implementation class SessionContainer
 */
@Singleton
@LocalBean
public class SessionContainerSt implements SessionContainerRemoteSt {

    private HashMap<String, HashMap<String, Object>> sessions;
    private HashMap<String, Object> aplicationVars;

    /**
     * Default constructor.
     */
    public SessionContainerSt() {
        sessions = new HashMap<>();
        aplicationVars = new HashMap<>();
    }

    @Override
    public Object getApplicationVar(String id) {
        return aplicationVars.get(id);
    }

    @Override
    public void setApplicationVar(String id, Object value) {
        aplicationVars.put(id, value);
    }

    @Override
    public HashMap<String, Object> getSession(String id) {
        return sessions.get(id);
    }

    @Override
    public void setSession(String id, HashMap<String, Object> variables) {
        sessions.put(id, variables);
    }

    @Override
    public void removeSession(String id) {
        sessions.remove(id);
    }

    @Override
    public void setSessionVar(String id, String nombre, Object valor) {
        if (sessions.get(id) != null) {
            sessions.get(id).put(nombre, valor);
        }
    }

    @Override
    public void removeSessionVar(String id, String nombre) {
        sessions.get(id).remove(nombre);
    }

    @Override
    public Object getSessionVar(String id, String nombre) {
        return sessions.get(id).get(nombre);
    }

    @Override
    public Object[] getDataLoad(String idSession) {
        HashMap<String, Object> dataSession = getSession(idSession);

        Object data[] = { (String) dataSession.get("modulo"),
                          (String) dataSession.get("menu"),
                          (String) dataSession.get("menuActual") };
        return data;
    }

    @Override
    public Object[] getDataNomina(String idSession) {
        // TODO Auto-generated method stub
        HashMap<String, Object> dataSession = getSession(idSession);

        Object data[] = { (String) dataSession.get("procesoNomina"),
                          (String) dataSession.get("anioNomina"),
                          (String) dataSession.get("mesNomina"),
                          (String) dataSession.get("periodoNomina"),
                          (String) dataSession.get("nombreMesNomina"),
                          (String) dataSession.get("nombrePeriodoNomina"),
                          (String) dataSession.get("nombreProcesoNomina"),
                          (Boolean) dataSession.get("periodoActivo") };
        return data;
    }

    @Override
    public Object[] getDataFacturacionGeneral(String idSession) {
        HashMap<String, Object> dataSession = getSession(idSession);

        Object[] data = new Object[10];
        data[0] = dataSession.get(ConstantesFacturacionGenEnum.ANIO.getValue());

        data[1] = dataSession
                        .get(ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());

        data[2] = dataSession.get(ConstantesFacturacionGenEnum.NOMBRETIPOCOBRO
                        .getValue());

        data[3] = dataSession.get(ConstantesFacturacionGenEnum.MANEJA_INVENTARIO
                        .getValue());

        data[4] = dataSession.get(ConstantesFacturacionGenEnum.INTERFAZ_RECAUDO
                        .getValue());

        data[5] = dataSession
                        .get(ConstantesFacturacionGenEnum.TIPOCOBRO_NOFACTURADO
                                        .getValue());

        data[6] = dataSession.get(ConstantesFacturacionGenEnum.CPTE_RECAUDO
                        .getValue());

        data[7] = dataSession
                        .get(ConstantesFacturacionGenEnum.MANEJA_RECAUDOTERCEROS
                                        .getValue());

        data[8] = dataSession
                        .get(ConstantesFacturacionGenEnum.CLASE_CUENTASRECAUDO
                                        .getValue());

        data[9] = dataSession.get(ConstantesFacturacionGenEnum.INDPRELIQUIDACION
                        .getValue());

        return data;
    }

    @Override
    public Object[] getDataPlusvalia(String idSession) {
        HashMap<String, Object> dataSession = getSession(idSession);

        Object data[] = { (String) dataSession.get("claseVp"),
                          (String) dataSession.get("nombreClase") };
        return data;
    }

    @Override
    public Object[] getDataTramite(String idSession) {
        HashMap<String, Object> dataSession = getSession(idSession);

        Object[] data = new Object[1];

        data[0] = dataSession
                        .get(ConstantesWorkflowEnum.PR_RID_TRAMITE.getValue());

        return data;

    }

    @Override
    public Object[] getDataClaseEvaluacion(String idSession) {
        HashMap<String, Object> dataSession = getSession(idSession);
        Object[] data = new Object[1];

        data[0] = dataSession.get(
                        ConstantesHojasDeVidaEnum.CLASE_EVALUACION.getValue());
        return data;
    }

    @Override
    public Object[] getDataGeneradorReportes(String idSession) {
        HashMap<String, Object> dataSession = getSession(idSession);
        Object[] data = new Object[1];

        data[0] = dataSession.get("aplicacion");
        return data;
    }

    @Override
    public boolean existSession(String idSession) {
        return sessions.containsKey(idSession);
    }
}
