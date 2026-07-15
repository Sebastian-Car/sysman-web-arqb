/*-
 * RegistrovigenciasfutschipsControlador.java
 *
 * 1.0
 *
 * 08/03/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.cgr.enums.RegistrovigenciasfutschipsControladorEnum;
import com.sysman.cgr.enums.RegistrovigenciasfutschipsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador para la vista del formulario Libro registro de
 * vigencias futuras Ruta UtilidadesChip\Configuraciï¿½n /
 * Informes\Resoluciï¿½n 6224\Libro registro de vigencias futuras
 *
 * @version 1.0, 08/03/2017
 * @author ybecerra
 * @version 2.0 16/08/2017
 * @modifiedby jrodriguezr Se elimina la conexion y se ajusta el
 * manejo de excepciones
 * @version 3, 30/08/2017
 * @modifiedby <strong>jrodriguezr </strong>Se refactoriza el código
 * SQL de las listas para utilizar DSS. También los llamados a
 * funciones, procedimientos y métodos de la clase Acciones a llamados
 * a EJB. Textos al archivo properties. Cambio el numero del
 * formulario al enumerado.
 */
@ManagedBean
@ViewScoped
public class RegistrovigenciasfutschipsControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo
     * ingresado en la aplicacion
     */
    private final String modulo;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que define si el reporte se visualiza con Cï¿½digo
     * Interno
     */
    private boolean indicador;
    /**
     * Atributo que almacena el codigo seleccionado en el combo cuenta
     * inicial
     */
    private String cuentaInicial;
    /**
     * Atributo que almacena el codigo seleccionado en el combo cuenta
     * final
     */
    private String cuentaFinal;
    /**
     * Atributo que almacena el numero de mes seleccionado en el combo
     * mes inicial
     */
    private String mesInicial;
    /**
     * Atributo que almacena el numero de mes seleccionado en el combo
     * mes final
     */
    private String mesFinal;
    /**
     * Atributo que almacena el numero de ano seleccionado en el combo
     * ano
     */
    private String ano;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros del combo mes inicial del formulario
     */
    private List<Registro> listamesInicial;
    /**
     * Lista de registros del combo mes final del formulario
     */
    private List<Registro> listamesFinal;
    /**
     * Lista de registros del combo ano del formulario
     */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros del combo cuenta inicial del formulario
     */
    private RegistroDataModelImpl listaCuentaInicial;
    /**
     * Lista de registros del combo cuenta final del formulario
     */
    private RegistroDataModelImpl listaCuentaFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * RegistrovigenciasfutschipsControlador
     */
    public RegistrovigenciasfutschipsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.REGISTROVIGENCIASFUTSCHIPS_CONTROLADOR
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
        cargarListaAno();
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listamesInicial
     */
    public void cargarListamesInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        try {
            listamesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistrovigenciasfutschipsControladorUrlEnum.URL6537
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
     * Carga la lista listamesFinal
     *
     */
    public void cargarListamesFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);
        try {
            listamesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistrovigenciasfutschipsControladorUrlEnum.URL7112
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
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistrovigenciasfutschipsControladorUrlEnum.URL7757
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
     * Carga la lista listaCuentaInicial
     *
     */
    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegistrovigenciasfutschipsControladorUrlEnum.URL8389
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     *
     * Carga la lista listaCuentaFinal
     *
     */
    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegistrovigenciasfutschipsControladorUrlEnum.URL9367
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(RegistrovigenciasfutschipsControladorEnum.CUENTAINICIAL
                        .getValue(), cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton presentar en la vista
     *
     *
     */
    public void oprimirpresentar() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton excel en la vista
     *
     */
    public void oprimirexcel() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        cargarListamesInicial();
        cargarListamesFinal();
        cargarListaCuentaInicial();

    }

    public void cambiarmesInicial() {
        mesFinal = null;
        cargarListamesFinal();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get("ID").toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get("ID").toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>

    /**
     * Metodo que valida si los parametros usados en el reporte, son
     * nulos o no, si son nulos ejecuta un mensaje de error
     *
     * @param nombreParametro
     * @param mensaje
     * @return true o false
     */
    private boolean validarParametro(String nombreParametro, String mensaje) {
        try {
            String parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, modulo, new Date(), true);
            if (parametro == null) {
                JsfUtil.agregarMensajeError(idioma.getString(mensaje));
                return true;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return false;
    }

    /**
     * Se llama en el metodo generarInforme
     *
     * @return true o false
     */
    private boolean validarMensajeParametro() {
        return validarParametro("NIVEL 1F", "TB_TB2918")
            || validarParametro("NIVEL 2F", "TB_TB2919")
            || validarParametro("NIVEL 3F", "TB_TB2920");
    }

    /**
     * Se llama en el metodo generarInforme
     *
     * @return true o false
     */
    private boolean validarMensajeParametroUno() {
        return validarParametro("NIVEL 4F", "TB_TB2921")
            || validarParametro("NIVEL 5F", "TB_TB2922")
            || validarParametro("NIVEL 1I", "TB_TB2923");
    }

    /**
     * Se llama en el metodo generarInforme
     *
     * @return true o false
     */
    private boolean validarMensajeParametroDos() {
        return validarParametro("NIVEL 2I", "TB_TB2924")
            || validarParametro("NIVEL 3I", "TB_TB2925")
            || validarParametro("NIVEL 4I", "TB_TB2926");
    }

    /**
     * Metodo ejecutado al darle clic al boton presentar o excel del
     * formulario
     *
     * @param formato
     */
    private void generarInforme(FORMATOS formato) {
        archivoDescarga = null;
        String reporte = "001441REGISTROVIGENCIASFUT6224";

        Map<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("ano", ano);
        reemplazar.put("mesInicial", mesInicial);
        reemplazar.put("mesFinal", mesFinal);
        reemplazar.put("cuentaInicial",
                        SysmanFunciones.colocarComillas(cuentaInicial));
        reemplazar.put("cuentaFinal",
                        SysmanFunciones.colocarComillas(cuentaFinal));

        Map<String, Object> parametro = new HashMap<>();

        if (validarMensajeParametro() || validarMensajeParametroUno()
            || validarMensajeParametroDos()) {
            return;
        }
        try {
            String nivel1 = ejbSysmanUtil.consultarParametro(
                            compania,
                            "NIVEL 1F", modulo, new Date(), true);
            String nivel2 = ejbSysmanUtil.consultarParametro(
                            compania, "NIVEL 2F", modulo, new Date(), true);
            String nivel3 = ejbSysmanUtil.consultarParametro(
                            compania, "NIVEL 3F", modulo, new Date(), true);
            String nivel4 = ejbSysmanUtil.consultarParametro(
                            compania, "NIVEL 4F", modulo, new Date(), true);
            String nivel5 = ejbSysmanUtil.consultarParametro(
                            compania, "NIVEL 5F", modulo, new Date(), true);
            String nivel1I = ejbSysmanUtil.consultarParametro(
                            compania, "NIVEL 1I", modulo, new Date(), true);
            String nivel2I = ejbSysmanUtil.consultarParametro(
                            compania, "NIVEL 2I", modulo, new Date(), true);
            String nivel3I = ejbSysmanUtil.consultarParametro(
                            compania, "NIVEL 3I", modulo, new Date(), true);
            String nivel4I = ejbSysmanUtil.consultarParametro(
                            compania, "NIVEL 4I", modulo, new Date(), true);

            parametro.put("PR_NIVEL_1F", nivel1);
            parametro.put("PR_NIVEL_2F", nivel2);
            parametro.put("PR_NIVEL_3F", nivel3);
            parametro.put("PR_NIVEL_4F", nivel4);
            parametro.put("PR_NIVEL_5F", nivel5);
            parametro.put("PR_NIVEL_1I", nivel1I);
            parametro.put("PR_NIVEL_2I", nivel2I);
            parametro.put("PR_NIVEL_3I", nivel3I);
            parametro.put("PR_NIVEL_4I", nivel4I);
            parametro.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametro.put("PR_INDICADOR", indicador ? 1 : 0);
            parametro.put("PR_MESINICIAL",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mesInicial)]);
            parametro.put("PR_MESFINAL",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mesFinal)]);

            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametro);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametro,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable indicador
     *
     * @return indicador
     */
    public boolean getIndicador() {
        return indicador;
    }

    /**
     * Asigna la variable indicador
     *
     * @param indicador
     * Variable a asignar en indicador
     */
    public void setIndicador(boolean indicador) {
        this.indicador = indicador;
    }

    /**
     * Retorna la variable cuentaInicial
     *
     * @return cuentaInicial
     */
    public String getCuentaInicial() {
        return cuentaInicial;
    }

    /**
     * Asigna la variable cuentaInicial
     *
     * @param cuentaInicial
     * Variable a asignar en cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    /**
     * Retorna la variable cuentaFinal
     *
     * @return cuentaFinal
     */
    public String getCuentaFinal() {
        return cuentaFinal;
    }

    /**
     * Asigna la variable cuentaFinal
     *
     * @param cuentaFinal
     * Variable a asignar en cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }

    /**
     * Retorna la variable mesInicial
     *
     * @return mesInicial
     */
    public String getMesInicial() {
        return mesInicial;
    }

    /**
     * Asigna la variable mesInicial
     *
     * @param mesInicial
     * Variable a asignar en mesInicial
     */
    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    /**
     * Retorna la variable mesFinal
     *
     * @return mesFinal
     */
    public String getMesFinal() {
        return mesFinal;
    }

    /**
     * Asigna la variable mesFinal
     *
     * @param mesFinal
     * Variable a asignar en mesFinal
     */
    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    /**
     * Retorna la variable ano AND ANO_NUMERO NOT IN(0)
     *
     * @return ano AND ANO_NUMERO NOT IN(0)
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano AND ANO_NUMERO NOT IN(0)
     *
     * @param ano
     * AND ANO_NUMERO NOT IN(0) Variable a asignar en ano AND
     * ANO_NUMERO NOT IN(0)
     */
    public void setAno(String ano) {
        this.ano = ano;
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
     * Retorna la lista listamesInicial
     *
     * @return listamesInicial
     */
    public List<Registro> getListamesInicial() {
        return listamesInicial;
    }

    /**
     * Asigna la lista listamesInicial
     *
     * @param listamesInicial
     * Variable a asignar en listamesInicial
     */
    public void setListamesInicial(List<Registro> listamesInicial) {
        this.listamesInicial = listamesInicial;
    }

    /**
     * Retorna la lista listamesFinal
     *
     * @return listamesFinal
     */
    public List<Registro> getListamesFinal() {
        return listamesFinal;
    }

    /**
     * Asigna la lista listamesFinal
     *
     * @param listamesFinal
     * Variable a asignar en listamesFinal
     */
    public void setListamesFinal(List<Registro> listamesFinal) {
        this.listamesFinal = listamesFinal;
    }

    /**
     * Retorna la lista listaAno
     *
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     *
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCuentaInicial
     *
     * @return listaCuentaInicial
     */
    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    /**
     * Asigna la lista listaCuentaInicial
     *
     * @param listaCuentaInicial
     * Variable a asignar en listaCuentaInicial
     */
    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    /**
     * Retorna la lista listaCuentaFinal
     *
     * @return listaCuentaFinal
     */
    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    /**
     * Asigna la lista listaCuentaFinal
     *
     * @param listaCuentaFinal
     * Variable a asignar en listaCuentaFinal
     */
    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
