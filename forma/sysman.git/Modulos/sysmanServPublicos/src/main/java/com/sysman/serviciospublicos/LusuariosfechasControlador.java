/*-
 * LusuariosfechasControlador.java
 *
 * 1.0
 *
 * 04/11/2016
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
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LusuariosfechasControladorEnum;
import com.sysman.serviciospublicos.enums.LusuariosfechasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario LusuariosfechasControlador
 *
 * @version 1.0, 04/11/2016
 * @author cperez
 *
 * @author eamaya
 * @version 2.0, 08/06/2017 Proceso de Refactoring y Manejo de EJBs
 *
 * @version 3, 13/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped

public class LusuariosfechasControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Obtiene el estado si es false o true del checkAgrupado
     */
    private boolean checkAgrupado;
    /**
     * Obtiene el id del banco Inicial de la consulta
     */
    private String bancoInicial;
    /**
     * Obtiene el id del banco Final de la consulta
     */
    private String bancoFinal;
    /**
     * Obtiene el id del operario Inicial del formulario
     */
    private String usuarioInicial;
    /**
     * Obtiene el id del operario Final del formulario
     */
    private String usuarioFinal;
    /**
     * Obtiene la fecha Inicial de la consulta
     */
    private Date fechaInicial;
    /**
     * Obtiene el nombre del Inicial del banco del informe
     */
    private String nombreBancoInicial;
    /**
     * Obtiene el nombre del Final banco del informe
     */
    private String nombreBancoFinal;
    /**
     * Obtiene el fecha Inicial de la consulta
     */
    private Date fechaFinal;
    /*
     * Nombre constante para el "CODIGO"
     */
    private String codigo;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Necesario para obtener y mandar la lista del Banco Inicial
     */
    private RegistroDataModelImpl listaBancoInicial;
    /**
     * Necesario para obtener y mandar la lista del Banco Final
     */
    private RegistroDataModelImpl listaBancoFinal;
    /**
     * Necesario para obtener y mandar la lista del Operario Inicial
     */
    private RegistroDataModelImpl listausuarioInicial;
    /**
     * Necesario para obtener y mandar la lista del Operario Final
     */
    private RegistroDataModelImpl listausuarioFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LusuariosfechasControlador
     */
    public LusuariosfechasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.LUSUARIOSFECHAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            codigo = "CODIGO";
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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaBancoInicial();

        cargarListausuarioInicial();

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
        fechaInicial = new Date();
        fechaFinal = new Date();
        usuarioInicial = "0";
        usuarioFinal = "zz";
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaBancoInicial
     */
    public void cargarListaBancoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LusuariosfechasControladorUrlEnum.URL6272
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaBancoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    /**
     *
     * Carga la lista listaBancoFinal
     */
    public void cargarListaBancoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LusuariosfechasControladorUrlEnum.URL7046
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(LusuariosfechasControladorEnum.PARAM0.getValue(),
                        bancoInicial);

        listaBancoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    /**
     *
     * Carga la lista listausuarioInicial
     */
    public void cargarListausuarioInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LusuariosfechasControladorUrlEnum.URL7826
                                                        .getValue());
        listausuarioInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, codigo);
    }

    /**
     *
     * Carga la lista listausuarioFinal
     */
    public void cargarListausuarioFinal() {

        Map<String, Object> param = new TreeMap<>();

        param.put(LusuariosfechasControladorEnum.PARAM1.getValue(),
                        usuarioInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LusuariosfechasControladorUrlEnum.URL8360
                                                        .getValue());
        listausuarioFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>

        determinarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>

        determinarReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void determinarReporte(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        long bancoIni = Long.parseLong(bancoInicial);
        long bancoFin = Long.parseLong(bancoFinal);
        if (bancoIni > bancoFin) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1792"));
        }
        else {
            if (!checkAgrupado) {
                genInforme(formato, "001221UsuariosPagoBancosFechas");
            }
            else {
                genInforme(formato, "001219UsuariosPagoBancosFechasAGRUP");
            }
        }
    }

    /*
     * Metodo para generar el informe ya sea de pdf o excel
     */
    public void genInforme(ReportesBean.FORMATOS formato, String reporte) {
        try {
            archivoDescarga = null;
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("usuarioInicial", usuarioInicial);
            reemplazar.put("usuarioFinal", usuarioFinal);
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazar.put("bancoInicial", bancoInicial);
            reemplazar.put("bancoFinal", bancoFinal);
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_FORMS_LUSUARIOSFECHAS_BANCOINICIAL",
                            bancoInicial);
            parametros.put("PR_FORMS_LUSUARIOSFECHAS_BANCOFINAL", bancoFinal);
            parametros.put("PR_FORMS_LUSUARIOSFECHAS_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FORMS_LUSUARIOSFECHAS_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
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
                        registroAux.getCampos().get("NOMBRE").toString(), "");
        bancoFinal = "";
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
        bancoFinal = registroAux.getCampos().get(codigo).toString();
        nombreBancoFinal = registroAux.getCampos().get("NOMBRE").toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listausuarioInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilausuarioInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        usuarioInicial = registroAux.getCampos().get(codigo).toString();
        usuarioFinal = " ";
        cargarListausuarioFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listausuarioFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilausuarioFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        usuarioFinal = registroAux.getCampos().get(codigo).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable checkAgrupado
     *
     * @return checkAgrupado
     */
    public boolean getCheckAgrupado() {
        return checkAgrupado;
    }

    /**
     * Asigna la variable checkAgrupado
     *
     * @param checkAgrupado
     * Variable a asignar en checkAgrupado
     */
    public void setCheckAgrupado(boolean checkAgrupado) {
        this.checkAgrupado = checkAgrupado;
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
     * Retorna la variable usuarioInicial
     *
     * @return usuarioInicial
     */
    public String getUsuarioInicial() {
        return usuarioInicial;
    }

    /**
     * Asigna la variable usuarioInicial
     *
     * @param usuarioInicial
     * Variable a asignar en usuarioInicial
     */
    public void setUsuarioInicial(String usuarioInicial) {
        this.usuarioInicial = usuarioInicial;
    }

    /**
     * Retorna la variable usuarioFinal
     *
     * @return usuarioFinal
     */
    public String getUsuarioFinal() {
        return usuarioFinal;
    }

    /**
     * Asigna la variable usuarioFinal
     *
     * @param usuarioFinal
     * Variable a asignar en usuarioFinal
     */
    public void setUsuarioFinal(String usuarioFinal) {
        this.usuarioFinal = usuarioFinal;
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

    /**
     * Retorna la lista listausuarioInicial
     *
     * @return listausuarioInicial
     */
    public RegistroDataModelImpl getListausuarioInicial() {
        return listausuarioInicial;
    }

    /**
     * Asigna la lista listausuarioInicial
     *
     * @param listausuarioInicial
     * Variable a asignar en listausuarioInicial
     */
    public void setListausuarioInicial(
        RegistroDataModelImpl listausuarioInicial) {
        this.listausuarioInicial = listausuarioInicial;
    }

    /**
     * Retorna la lista listausuarioFinal
     *
     * @return listausuarioFinal
     */
    public RegistroDataModelImpl getListausuarioFinal() {
        return listausuarioFinal;
    }

    /**
     * Asigna la lista listausuarioFinal
     *
     * @param listausuarioFinal
     * Variable a asignar en listausuarioFinal
     */
    public void setListausuarioFinal(RegistroDataModelImpl listausuarioFinal) {
        this.listausuarioFinal = listausuarioFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
