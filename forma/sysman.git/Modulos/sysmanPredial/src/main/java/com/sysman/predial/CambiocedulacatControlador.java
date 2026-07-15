package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialDosRemote;
import com.sysman.predial.enums.CambiocedulacatControladorUrlEnum;
import com.sysman.util.SysmanConstantes;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author dsuesca
 * @version 1, 25/05/2016
 *
 * @version 2, 13/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 * 
 * @author eamaya
 * @version 3.0, 22/06/2017 Proceso de Refactoring DSS y Manejo de
 * EJBs, se elimin� el m�todo que generaba el reporte y el llamado el
 * mismo
 * 
 */
@ManagedBean
@ViewScoped

public class CambiocedulacatControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String predioAnterior;
    private String predioNuevo;
    private String resultado;
    private String tituloMensajes;
    private String etiquetaMensajes;
    private boolean visibleDialogo;
    private byte dialogo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private Object archivoDescarga;

    @EJB
    private EjbPredialDosRemote ejbPredialDos;

    /**
     * Creates a new instance of CambiocedulacatControlador
     */
    public CambiocedulacatControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CAMBIOCEDULACAT_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(CambiocedulacatControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdIniciar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        try {

            HashMap<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.PREDIO.getName(), predioAnterior);
            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);

            if (validarCampos()) {
                Registro rsUP = RegistroConverter
                                .toRegistro(requestManager
                                                .get(UrlServiceUtil
                                                                .getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                CambiocedulacatControladorUrlEnum.URL2892
                                                                                                .getValue())
                                                                .getUrl(),
                                                                param));

                if (rsUP != null) {
                    registrarUP(rsUP);
                }
                else {
                    resultado = idioma.getString("TB_TB1281");
                    JsfUtil.agregarMensajeInformativo(resultado);
                }

            }

        }
        catch (SystemException e) {
            Logger.getLogger(CambiocedulacatControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void registrarUP(Registro rsUP) {
        Registro rsAux;
        try {
            if (!(boolean) rsUP.getCampos().get("INDBORRADO")) {
                HashMap<String, Object> param = new HashMap<>();

                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.PREDIO.getName(),
                                predioNuevo);
                param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                                SysmanConstantes.NUMERO_ORDEN_PREDIAL);

                rsAux = RegistroConverter
                                .toRegistro(requestManager
                                                .get(UrlServiceUtil
                                                                .getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                CambiocedulacatControladorUrlEnum.URL2892
                                                                                                .getValue())
                                                                .getUrl(),
                                                                param));

                if (rsAux != null) {
                    registrarAuxiliar(rsAux);
                }
                else {
                    resultado = idioma.getString("TB_TB1279");
                    JsfUtil.agregarMensajeInformativo(resultado);
                }
            }
            else {
                resultado = idioma.getString("TB_TB1280");
                JsfUtil.agregarMensajeInformativo(resultado);
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void registrarAuxiliar(Registro rsAux) {
        try {
            if (!(boolean) rsAux.getCampos().get("INDBORRADO")) {

                HashMap<String, Object> param = new HashMap<>();

                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.PREDIO.getName(),
                                predioNuevo);
                param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                                SysmanConstantes.NUMERO_ORDEN_PREDIAL);

                Registro rsFac;

                rsFac = RegistroConverter
                                .toRegistro(requestManager
                                                .get(UrlServiceUtil
                                                                .getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                CambiocedulacatControladorUrlEnum.URL2895
                                                                                                .getValue())
                                                                .getUrl(),
                                                                param));

                if ("0".equals(rsFac.getCampos().get("CANT").toString())) {

                    ejbPredialDos.realizarTrasladoNuevoCodigo(
                                    compania, predioAnterior, predioNuevo,
                                    SessionUtil.getUser().getCodigo(), false);

                    resultado = idioma.getString("MSM_PROCESO_EJECUTADO");

                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString(resultado));

                }
                else {
                    tituloMensajes = idioma.getString("TG_SYSMAN_SOFTWARE");
                    tituloMensajes = tituloMensajes.replace("s$empresaparam$s", JsfUtil.obtenerParametroMarcaBlanca("TITULOMSJ"));
                    etiquetaMensajes = idioma.getString("TB_TB1277");
                    dialogo = 1;
                    visibleDialogo = true;
                }
            }
            else {
                resultado = idioma.getString("TB_TB1278");
                JsfUtil.agregarMensajeInformativo(resultado);
            }
        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
    }

    public boolean validarCampos() {

        if ((predioAnterior.length() != 15) || (predioNuevo.length() != 15)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1282"));
            return false;
        }
        if (predioAnterior.equals(predioNuevo)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1283"));

            return false;

        }

        return true;
    }

    public void aceptardialogo1() {
        try {

            if (dialogo == 1) {
                dialogo = 2;
                etiquetaMensajes = idioma.getString("TB_TB1284");
            }
            else if (dialogo == 2) {
                visibleDialogo = false;

                ejbPredialDos.realizarTrasladoNuevoCodigo(
                                compania, predioAnterior, predioNuevo,
                                SessionUtil.getUser().getCodigo(), true);

                resultado = idioma.getString("MSM_PROCESO_EJECUTADO");

                JsfUtil.agregarMensajeInformativo(resultado);
            }
        }
        catch (SystemException e1) {
            Logger.getLogger(CambiocedulacatControlador.class.getName())
                            .log(Level.SEVERE, null, e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

    }

    public void cancelardialogo1() {
        archivoDescarga = null;
        visibleDialogo = false;
        resultado = idioma.getString("TB_TB1285");
        JsfUtil.agregarMensajeInformativo(resultado);

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public Object getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(Object archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getPredioAnterior() {
        return predioAnterior;
    }

    public void setPredioAnterior(String predioAnterior) {
        this.predioAnterior = predioAnterior;
    }

    public String getPredioNuevo() {
        return predioNuevo;
    }

    public void setPredioNuevo(String predioNuevo) {
        this.predioNuevo = predioNuevo;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getTituloMensajes() {
        return tituloMensajes;
    }

    public void setTituloMensajes(String tituloMensajes) {
        this.tituloMensajes = tituloMensajes;
    }

    public String getEtiquetaMensajes() {
        return etiquetaMensajes;
    }

    public void setEtiquetaMensajes(String etiquetaMensajes) {
        this.etiquetaMensajes = etiquetaMensajes;
    }

    public boolean isVisibleDialogo() {
        return visibleDialogo;
    }

    public void setVisibleDialogo(boolean visibleDialogo) {
        this.visibleDialogo = visibleDialogo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
