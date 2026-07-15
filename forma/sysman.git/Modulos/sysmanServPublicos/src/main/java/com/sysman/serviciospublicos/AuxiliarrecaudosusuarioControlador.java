/*-
 * AuxiliarrecaudosusuarioControlador.java
 *
 * 1.0
 * 
 * 22/11/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.AuxiliarrecaudosusuarioControladorEnum;
import com.sysman.serviciospublicos.enums.AuxiliarrecaudosusuarioControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlarod del formulario Auxiliarrecaudosusuario
 *
 * @version 1.0, 22/11/2016
 * @author cperez
 * 
 * @author eamaya
 * @version 2, 16/05/2017 Proceso de Refactoring y Manejo de EJBs
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped

public class AuxiliarrecaudosusuarioControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Obtiene el ciclo de la consulta
     */
    private String ciclo;
    /**
     * Obtiene el id del combo seleccionado por el usiario banco
     * Inicial de la consulta
     */
    private String bancoInicial;
    /**
     * Obtiene el id del combo seleccionado por el usiario banco Final
     * de la consulta
     */
    private String bancoFinal;
    /**
     * Obtiene la fecha Inicial seleccionada por el usuario
     */
    private Date fechaInicial;
    /**
     * variable para obtener el nombre del Banco Inicial de la
     * consulta
     */
    private String nombreBancoInicial;
    /**
     * variable para obtener el nombre del Banco Final de la consulta
     */
    private String nombreBancoFinal;
    /*
     * Obtiene la fecha Final seleccionada por el usuario
     */
    private Date fechaFinal;
    /*
     * variable constante para el nombre de "CODIGO"
     */
    private String codigo;
    /*
     * variable constante para el nombre de SP_BANCOS.NOMBRE
     */
    private String nombre;
    /*
     * variable para asiganar el nombre del tipo de error
     * msm_Trans_Interrumpida
     */
    private String msmTransInterrumpida;
    /*
     * variable para asiganar el nombre zzzzzzzzzzzz en el banco Final
     */
    private String zzzzzzzzzzzz;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private static final String VALOR_MAXIMO = "99999999999999";

    @EJB
    private EjbSysmanUtilRemote ejbParametro;

    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Necesario para obtener mandar la lista del ciclo
     */
    private List<Registro> listaCiclo;
    /**
     * Necesario para obtener mandar la lista el banco Inicial
     */
    private RegistroDataModelImpl listaBancoFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Necesario para obtener mandar la lista el Banco Inicial
     */
    private RegistroDataModelImpl listaBancoInicial;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de AuxiliarrecaudosusuarioControlador
     */
    public AuxiliarrecaudosusuarioControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigo = "CODIGO";
        nombre = "NOMBRE";
        zzzzzzzzzzzz = "ZZZZZZZZZZZZZZZZ";
        msmTransInterrumpida = "MSM_TRANS_INTERRUMPIDA";
        try {
            numFormulario = GeneralCodigoFormaEnum.AUXILIARRECAUDOSUSUARIO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
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
        cargarListaCiclo();
        cargarListaBancoFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaBancoInicial();
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
        ciclo = "T";
        bancoInicial = "0";
        bancoFinal = zzzzzzzzzzzz;
        fechaInicial = new Date();
        fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AuxiliarrecaudosusuarioControladorUrlEnum.URL6268
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaBancoFinal
     */
    public void cargarListaBancoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxiliarrecaudosusuarioControladorUrlEnum.URL6961
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(AuxiliarrecaudosusuarioControladorEnum.PARAM0.getValue(),
                        bancoInicial);

        listaBancoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, codigo);

    }

    /**
     * 
     * Carga la lista listaBancoInicial
     *
     */
    public void cargarListaBancoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxiliarrecaudosusuarioControladorUrlEnum.URL7692
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaBancoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, codigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /*
     * muesta el mensaje de error del campo Banco
     */
    public void mensajeBanco() {
        if (zzzzzzzzzzzz.equals(bancoFinal)) {
            bancoFinal = VALOR_MAXIMO;
        }
        long bancoIni = Long.parseLong(bancoInicial);
        long bancoFin = Long.parseLong(bancoFinal);
        if (bancoIni > bancoFin) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2073"));
            bancoFinal = "";
            return;
        }
    }

    /*
     * valida si el vanco Inicial es menor que el banco Final
     * 
     * @return compribarBanco
     */
    public String validarBanco() {
        if (zzzzzzzzzzzz.equals(bancoFinal)) {
            bancoFinal = VALOR_MAXIMO;
        }
        long bancoIni = Long.parseLong(bancoInicial);
        long bancoFin = Long.parseLong(bancoFinal);
        if (bancoIni > bancoFin) {
            if (VALOR_MAXIMO.equals(bancoFinal)) {
                bancoFinal = zzzzzzzzzzzz;
            }
            return "";
        }
        else {
            if (VALOR_MAXIMO.equals(bancoFinal)) {
                bancoFinal = zzzzzzzzzzzz;
            }
            return "1";
        }
    }

    /*
     * Retorna la variable compribarBanco
     * 
     * @return compribarBanco
     */
    public String comprobarBanco() {
        if ("1".equals(validarBanco())) {
            return "1";
        }
        else {
            return "";
        }

    }

    /*
     * Para saber si el parámetro “FORMATO CALIDAD”es igual a si o a
     * no
     */
    public String valorParametro() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String valorParametro = null;
        try {
            valorParametro = ejbParametro.consultarParametro(compania,
                            "INFORMES DE RECAUDO ORDENADOS POR FECHA Y HORA",
                            SessionUtil.getModulo(), new Date(), false);

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(idioma.getString(msmTransInterrumpida)
                + ex.getMessage());
            Logger.getLogger(AuxiliarrecaudosusuarioControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
        return valorParametro;
    }

    /*
     * funcion para decidir que informe imprime el pdf
     */
    public void decisionPdf() {
        if ("SI".equals(valorParametro())) {
            // LAuxiliarRecaudosUsuarioFECHA
            genInforme(FORMATOS.PDF, "001268LAuxiliarRecaudosUsuarioFECHA");
        }
        else {
            // LAuxiliarRecaudosUsuario
            genInforme(FORMATOS.PDF, "001269LAuxiliarRecaudosUsuario");
        }
    }

    /*
     * funcion para decidir que informe imprime el pdf
     */
    public void decisionExcel() {
        if ("SI".equals(valorParametro())) {
            genInforme(FORMATOS.EXCEL, "001268LAuxiliarRecaudosUsuarioFECHA");
        }
        else {
            genInforme(FORMATOS.EXCEL, "001269LAuxiliarRecaudosUsuario");
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     * 
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if ("1".equals(comprobarBanco())) {
            decisionPdf();
        }
        else {
            mensajeBanco();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if ("1".equals(comprobarBanco())) {
            decisionExcel();
        }
        else {
            mensajeBanco();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera el reporte de Excel y de Pdf
     */
    public void genInforme(ReportesBean.FORMATOS formato, String reporte) {
        try {

            archivoDescarga = null;
            String nombreCiclo;
            String cicloInicial;
            String cicloFinal;
            String bancoFinalFinal;
            if ("T".equals(ciclo)) {
                nombreCiclo = "Todos";
                cicloInicial = "0";
                cicloFinal = "999999999999";
            }
            else {
                nombreCiclo = ciclo;
                cicloInicial = ciclo;
                cicloFinal = ciclo;
            }
            if (zzzzzzzzzzzz.equals(bancoFinal)) {
                bancoFinalFinal = VALOR_MAXIMO;

            }
            else {
                bancoFinalFinal = bancoFinal;
            }
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("cicloInicial", cicloInicial);
            reemplazar.put("cicloFinal", cicloFinal);
            reemplazar.put("bancoInicial", bancoInicial);
            reemplazar.put("bancoFinal", bancoFinalFinal);
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_AUXILIARRECAUDOSUSUARIO_CICLO",
                            nombreCiclo);
            parametros.put("PR_FORMS_AUXILIARRECAUDOSUSUARIO_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FORMS_AUXILIARRECAUDOSUSUARIO_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_FORMS_AUXILIARRECAUDOSUSUARIO_BANCOINICIAL",
                            bancoInicial);
            parametros.put("PR_FORMS_AUXILIARRECAUDOSUSUARIO_BANCOFINAL",
                            bancoFinal);
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | ParseException
                        | SysmanException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(msmTransInterrumpida) + " "
                                + ex.getMessage());
            Logger.getLogger(AuxiliarrecaudosusuarioControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (OutOfMemoryError ex) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB814"));
            Logger.getLogger(AuxiliarrecaudosusuarioControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ciclo
     * 
     */
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        if ("T".equals(ciclo)) {
            bancoInicial = "0";
            bancoFinal = zzzzzzzzzzzz;
        }
        else {
            bancoInicial = null;
            bancoFinal = null;
        }
        nombreBancoFinal = "";
        nombreBancoInicial = "";
        cargarListaBancoInicial();
        cargarListaBancoFinal();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaInicial
     * 
     */
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        fechaFinal = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBancoInicial
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        bancoInicial = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(codigo).toString(), "");
        nombreBancoInicial = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(nombre).toString(), "");
        cargarListaBancoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBancoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        bancoFinal = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(codigo).toString(), "");
        nombreBancoFinal = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(nombre).toString(), "");
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ciclo
     * 
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     * 
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable bancoInicial
     * 
     * @return bancoInicial
     */
    public String getBancoInicial() {
        return bancoInicial;
    }

    /**
     * Asigna la variable bancoInicial
     * 
     * @param bancoInicial
     * Variable a asignar en bancoInicial
     */
    public void setBancoInicial(String bancoInicial) {
        this.bancoInicial = bancoInicial;
    }

    /**
     * Retorna la variable fechaFinal
     * 
     * @return fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Retorna la variable bancoFinal
     * 
     * @return bancoFinal
     */
    public String getBancoFinal() {
        return bancoFinal;
    }

    /**
     * Asigna la variable bancoFinal
     * 
     * @param bancoFinal
     * Variable a asignar en bancoFinal
     */
    public void setBancoFinal(String bancoFinal) {
        this.bancoFinal = bancoFinal;
    }

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable nombreBancoInicial
     * 
     * @return nombreBancoInicial
     */
    public String getNombreBancoInicial() {
        return nombreBancoInicial;
    }

    /**
     * Asigna la variable nombreBancoInicial
     * 
     * @param nombreBancoInicial
     * Variable a asignar en nombreBancoInicial
     */
    public void setNombreBancoInicial(String nombreBancoInicial) {
        this.nombreBancoInicial = nombreBancoInicial;
    }

    /**
     * Retorna la variable nombreBancoFinal
     * 
     * @return nombreBancoFinal
     */
    public String getNombreBancoFinal() {
        return nombreBancoFinal;
    }

    /**
     * Asigna la variable nombreBancoFinal
     * 
     * @param nombreBancoFinal
     * Variable a asignar en nombreBancoFinal
     */
    public void setNombreBancoFinal(String nombreBancoFinal) {
        this.nombreBancoFinal = nombreBancoFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCiclo
     * 
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     * 
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Retorna la lista listaBancoFinal
     * 
     * @return listaBancoFinal
     */
    public RegistroDataModelImpl getListaBancoFinal() {
        return listaBancoFinal;
    }

    /**
     * Asigna la lista listaBancoFinal
     * 
     * @param listaBancoFinal
     * Variable a asignar en listaBancoFinal
     */
    public void setListaBancoFinal(RegistroDataModelImpl listaBancoFinal) {
        this.listaBancoFinal = listaBancoFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaBancoInicial
     * 
     * @return listaBancoInicial
     */
    public RegistroDataModelImpl getListaBancoInicial() {
        return listaBancoInicial;
    }

    /**
     * Asigna la lista listaBancoInicial
     * 
     * @param listaBancoInicial
     * Variable a asignar en listaBancoInicial
     */
    public void setListaBancoInicial(RegistroDataModelImpl listaBancoInicial) {
        this.listaBancoInicial = listaBancoInicial;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
