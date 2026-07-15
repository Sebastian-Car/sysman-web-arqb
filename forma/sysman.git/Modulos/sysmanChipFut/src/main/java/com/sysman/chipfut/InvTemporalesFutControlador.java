/*-
 * InvTemporalesFutControlador.java
 *
 * 1.0
 * 
 * 28/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.enums.InvTemporalesFutControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 28/03/2017
 * @author jsforero
 */
@ManagedBean
@ViewScoped
public class InvTemporalesFutControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String consError;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Variable que controla el ano seleccionado en el formulario
     */
    private int ano;
    /**
     * Variable que controla el trimestre seleccionado en el
     * formulario
     */
    private int trimestre;
    /**
     * Variable en la que se almacena el parametro de redondeo
     */
    private int redondeo;
    /**
     * Variable que controla el indicador de pesos seleccionado en el
     * formulario
     */
    private boolean pesos;
    /**
     * Variable que almacena el archivo generado
     */
    private StreamedContent archivoDescarga;
    /**
     * Variable lee el codigo de la entidad digitado en el formulario
     */
    private String entradaCodigo;
    /**
     * Variable que contiene una lista de los a�os de trabajo
     */
    private List<Registro> listaAnoTrabajo;
    /**
     * Variable que almacena el mes inicial del trimestre
     */
    private int mesInicial;
    /**
     * Variable que almacena el mes final del trimestre
     */
    private int mesFinal;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InvTemporalesFutControlador
     */
    public InvTemporalesFutControlador() {
        super();
        compania = SessionUtil.getCompania();
        consError = "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS";
        entradaCodigo = SessionUtil.getCompaniaIngreso().getCodigoContaduria();
        try {
            redondeo = Integer.parseInt(
                            Acciones.getParametro(ConectorPool.ESQUEMA_SYSMAN,
                                            SessionUtil.getCompania(),
                                            "DIGITO REDONDEO DE INFORMES FUT",
                                            "99", "SYSDATE"));
            numFormulario = GeneralCodigoFormaEnum.INV_TEMPORALES_FUT_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaAnoTrabajo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnoTrabajo
     *
     */
    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InvTemporalesFutControladorUrlEnum.URL5362
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;
        try {
            HashMap<String, Object> variables = new HashMap<>();
            variables.put("redondeo", redondeo);
            variables.put("ano", ano);
            variables.put("pesos", pesos ? "/1000" : " ");
            variables.put("compania", compania);
            variables.put("fechaCorte", "TO_DATE('01/" + mesInicial + "/" + ano
                + "','DD/MM/YYYY')");// ------------------

            String strSql = Reporteador.resuelveConsulta(
                            "800105inversionestemporalesfut", 99, variables);
            List<Registro> listaPrincipal = service
                            .getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);
            if (listaPrincipal.isEmpty()) {
                JsfUtil.agregarMensajeError(
                                idioma.getString(
                                                consError));
                Logger.getLogger(InvTemporalesFutControlador.class.getName());
            }
            else {
                Clob texto;
                strSql = strSql.replace("'", "''");
                String aux;
                String parametros = " '" + compania + "' , '" + strSql + "' ,'"
                    + entradaCodigo + "'" + ",'" + cambiarTRIMESTRE() + "',"
                    + ano
                    + "";

                texto = (Clob) Acciones.ejecutarFuncion(
                                ConectorPool.ESQUEMA_SYSMAN,
                                "PCK_CHIPFUT.FC_INVERSIONES_TEMPORALES",
                                parametros, Types.CLOB);
                aux = Acciones.clobToStringSalto(texto);
                archivoDescarga = JsfUtil.getArchivoDescarga(
                                JsfUtil.serializarPlano(aux),
                                SessionUtil.getCompaniaIngreso().getNombre()
                                    + " INVERSIONES_TEMPORALES_FUT.txt");

            }

        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException | IOException | JRException e) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(
                                            consError));
            Logger.getLogger(InvTemporalesFutControlador.class.getName())
                            .log(Level.SEVERE, null, e);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Comando76 en la vista
     *
     *
     */
    public void oprimirComando76() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try {
            HashMap<String, Object> variables = new HashMap<>();
            variables.put("redondeo", redondeo);
            variables.put("ano", ano);
            variables.put("pesos", pesos ? "/1000" : " ");
            variables.put("compania", compania);
            variables.put("fechaCorte", "TO_DATE('01/" + mesInicial + "/" + ano
                + "','DD/MM/YYYY')");// ------------------

            String strSql = Reporteador.resuelveConsulta(
                            "800105inversionestemporalesfut", 99, variables);
            List<Registro> listaPrincipal = service
                            .getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);
            if (listaPrincipal.isEmpty()) {
                JsfUtil.agregarMensajeError(
                                idioma.getString(
                                                consError));
                Logger.getLogger(InvTemporalesFutControlador.class.getName());
            }
            else {

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL97,
                                SessionUtil.getCompaniaIngreso().getNombre()
                                    + " INVERSIONES_TEMPORALES_FUT");

            }
        }
        catch (SQLException | DRException
                        | IOException | JRException
                        | SysmanException e) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(
                                            consError));
            Logger.getLogger(InvTemporalesFutControlador.class.getName())
                            .log(Level.SEVERE, null, e);
        }
    }

    /**
     * Crea una cadena con el mes inicial y el mes final del trimestre
     * para enviarla al archivo plano
     * 
     * @return cadena con el mes inicial y el mes final concatenado
     */
    public String cambiarTRIMESTRE() {

        mesInicial = (trimestre * 3) - 2;
        mesFinal = trimestre * 3;
        String aux = mesInicial < 10 ? "0" + mesInicial
            : String.valueOf(mesInicial);
        String aux2 = mesFinal < 10 ? "0" + mesFinal : String.valueOf(mesFinal);
        return aux + aux2;

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnoTrabajo
     * 
     * @return listaAnoTrabajo
     */
    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    /**
     * Asigna la lista listaAnoTrabajo
     * 
     * @param listaAnoTrabajo
     * Variable a asignar en listaAnoTrabajo
     */
    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getTrimestre() {
        return trimestre;
    }

    public void setTrimestre(int trimestre) {
        this.trimestre = trimestre;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isPesos() {
        return pesos;
    }

    public void setPesos(boolean pesos) {
        this.pesos = pesos;
    }

    public String getEntradaCodigo() {
        return entradaCodigo;
    }

    public void setEntradaCodigo(String entradaCodigo) {
        this.entradaCodigo = entradaCodigo;
    }

    public int getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(int mesInicial) {
        this.mesInicial = mesInicial;
    }

    public int getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(int mesFinal) {
        this.mesFinal = mesFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
