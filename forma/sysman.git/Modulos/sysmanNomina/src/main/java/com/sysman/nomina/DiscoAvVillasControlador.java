/*-
 * DiscoAvVillasControlador.java
 *
 * 2.0
 * 
 * 04/10/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.ArchivosBean;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaBancosRemote;
import com.sysman.nomina.enums.DiscoAvVillasControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
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

/**
 * Gerenaci&oacute;n de archivo plano para el banco AvVillas.
 * 
 * @author acaceres
 * @version 1.0, 13/08/2015
 * 
 * @author jrodrigueza
 * @version 2.0, 04/10/2017 Proceso de refactoring.
 */
@ManagedBean
@ViewScoped
public class DiscoAvVillasControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    /**
     * A&ntilde;o con el que se desea generar el archivo.
     */
    private String ano;
    /**
     * Mes con el que se desea generar el archivo.
     */
    private String mes;
    /**
     * Periodo con el que se desea generar el archivo.
     */
    private String periodo;
    /**
     * Proceso de n&oacute;mina con el que se desea generar el
     * archivo.
     */
    private String proceso;
    /**
     * Banco seleccionado.
     */
    private String banco;
    /**
     * Fecha del reporte.
     */
    private Date fechaFormulario;
    /**
     * Nombre del banco seleccionado.
     */
    private String nombreBanco;
    // </DECLARAR_ATRIBUTOS>
    /**
     * EJB para hacer llamado a las funciones y/o procedimientos del
     * paquete PCK_NOMINA_BAN.
     */
    @EJB
    private EjbNominaBancosRemote ejbNominaBancos;
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>

    // <DECLARAR_LISTAS>
    /**
     * Lista de a&ntilde;os.
     */
    private List<Registro> listaAno;
    /**
     * Lista de meses.
     */
    private List<Registro> listaMes;
    /**
     * Listado de periodos de n&oacute;mina.
     */
    private List<Registro> listaPeriodo;
    /**
     * Lista de Procesos de n&oacute;mina.
     */
    private List<Registro> listaProceso;
    // </DECLARAR_LISTAS>

    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de bancos.
     */
    private RegistroDataModelImpl listaBanco;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia de DiscoAvVillasControlador
     */
    public DiscoAvVillasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.DISCO_AV_VILLAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
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
        abrirFormulario();
        // <CARGAR_LISTA>
        cargarListaAno();
        cargarListaMes();
        cargarListaPeriodo();
        cargarListaProceso();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaBanco();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        proceso = extraerString(SessionUtil.getSessionVar("procesoNomina"));
        ano = extraerString(SessionUtil.getSessionVar("anioNomina"));
        mes = extraerString(SessionUtil.getSessionVar("mesNomina"));
        periodo = extraerString(SessionUtil.getSessionVar("periodoNomina"));
        fechaFormulario = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaAno
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        String urlEnumId = DiscoAvVillasControladorUrlEnum.URL5654.getValue();
        String url = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
        try {
            listaAno = RegistroConverter
                            .toListRegistro(requestManager.getList(url, param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaMes
     */
    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        String urlEnumId = DiscoAvVillasControladorUrlEnum.URL6123.getValue();
        String url = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
        try {
            listaMes = RegistroConverter
                            .toListRegistro(requestManager.getList(url, param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaPeriodo
     */
    public void cargarListaPeriodo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.MES.getName(), mes);

        String urlEnumId = DiscoAvVillasControladorUrlEnum.URL24268.getValue();
        String url = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
        try {
            listaPeriodo = RegistroConverter
                            .toListRegistro(requestManager.getList(url, param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaProceso
     */
    public void cargarListaProceso() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        String urlEnumId = DiscoAvVillasControladorUrlEnum.URL7582.getValue();
        String url = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
        try {
            listaProceso = RegistroConverter
                            .toListRegistro(requestManager.getList(url, param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaBanco
     */
    public void cargarListaBanco() {
        String urlEnumId = DiscoAvVillasControladorUrlEnum.URL8420.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> params = new HashMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaBanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), params,
                        true, GeneralParameterEnum.BANCO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton GenerarDisco en la vista
     */
    public void oprimirGenerarDisco() {
        // <CODIGO_DESARROLLADO>
        if (validarCampos()) {
            return;
        }
        try {
            String salida = ejbNominaBancos.generarDiscoAvVillas(
                            compania,
                            Integer.parseInt(proceso),
                            Integer.parseInt(ano),
                            Integer.parseInt(mes),
                            periodo,
                            banco,
                            fechaFormulario);
            int posicion = salida.indexOf("\r\n");
            String codEntidad = salida.substring(0, posicion);
            salida = salida.substring(posicion + 2);
            ArchivosBean.generarPlano("Nomina" + codEntidad + ".pln", salida);
        }
        catch (NumberFormatException | SystemException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_BOTONES>

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        mes = null;
        periodo = null;
        cargarListaMes();
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Mes
     */
    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        periodo = null;
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Proceso
     */
    public void cambiarProceso() {
        // <CODIGO_DESARROLLADO>
        ano = null;
        mes = null;
        periodo = null;
        cargarListaAno();
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>

    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista listaBanco
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        banco = extraerString(registroAux.getCampos()
                        .get(GeneralParameterEnum.BANCO.getName()));
        nombreBanco = extraerString(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));
    }
    // </METODOS_COMBOS_GRANDES>

    // <METODOS_ARBOL>
    // </METODOS_ARBOL>

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(String mes) {
        this.mes = mes;
    }

    /**
     * Retorna la variable periodo
     * 
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     * 
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * Retorna la variable proceso
     * 
     * @return proceso
     */
    public String getProceso() {
        return proceso;
    }

    /**
     * Asigna la variable proceso
     * 
     * @param proceso
     * Variable a asignar en proceso
     */
    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    /**
     * Retorna la variable banco
     * 
     * @return banco
     */
    public String getBanco() {
        return banco;
    }

    /**
     * Asigna la variable banco
     * 
     * @param banco
     * Variable a asignar en banco
     */
    public void setBanco(String banco) {
        this.banco = banco;
    }

    /**
     * Retorna la variable fechaFormulario
     * 
     * @return fechaFormulario
     */
    public Date getFechaFormulario() {
        return fechaFormulario;
    }

    /**
     * Asigna la variable fechaFormulario
     * 
     * @param fechaFormulario
     * Variable a asignar en fechaFormulario
     */
    public void setFechaFormulario(Date fechaFormulario) {
        this.fechaFormulario = fechaFormulario;
    }

    /**
     * Retorna la variable nombreBanco
     * 
     * @return nombreBanco
     */
    public String getNombreBanco() {
        return nombreBanco;
    }

    /**
     * Asigna la variable nombreBanco
     * 
     * @param nombreBanco
     * Variable a asignar en nombreBanco
     */
    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }
    // </SET_GET_ATRIBUTOS>

    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>

    // <SET_GET_LISTAS>
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

    /**
     * Retorna la lista listaMes
     * 
     * @return listaMes
     */
    public List<Registro> getListaMes() {
        return listaMes;
    }

    /**
     * Asigna la lista listaMes
     * 
     * @param listaMes
     * Variable a asignar en listaMes
     */
    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    /**
     * Retorna la lista listaPeriodo
     * 
     * @return listaPeriodo
     */
    public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

    /**
     * Asigna la lista listaPeriodo
     * 
     * @param listaPeriodo
     * Variable a asignar en listaPeriodo
     */
    public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    /**
     * Retorna la lista listaProceso
     * 
     * @return listaProceso
     */
    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    /**
     * Asigna la lista listaProceso
     * 
     * @param listaProceso
     * Variable a asignar en listaProceso
     */
    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }
    // </SET_GET_LISTAS>

    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaBanco
     * 
     * @return listaBanco
     */
    public RegistroDataModelImpl getListaBanco() {
        return listaBanco;
    }

    /**
     * Asigna la lista listaBanco
     * 
     * @param listaBanco
     * Variable a asignar en listaBanco
     */
    public void setListaBanco(RegistroDataModelImpl listaBanco) {
        this.listaBanco = listaBanco;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    // <METODOS_ADICIONALES>
    /**
     * Extrae la cadena que representa al objeto, solo si es diferente
     * de nulo.
     * 
     * @param object
     * Un Objeto
     * @return String que representa al objeto
     */
    private String extraerString(Object object) {
        return object != null ? object.toString() : null;
    }

    /**
     * Validaci&oacute;n de campos obligatorios.
     * 
     * @return Verdadero si falta diligenciar campos obligatorios.
     */
    private boolean validarCampos() {
        boolean retorno = false;
        if (SysmanFunciones.validarVariableVacio(proceso)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2548"));
            retorno = true;
        }
        if (SysmanFunciones.validarVariableVacio(ano)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB729"));
            retorno = true;
        }
        if (SysmanFunciones.validarVariableVacio(mes)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB730"));
            retorno = true;
        }
        if (SysmanFunciones.validarVariableVacio(periodo)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB670"));
            retorno = true;
        }
        if (SysmanFunciones.validarVariableVacio(banco)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2556"));
            retorno = true;
        }
        if (fechaFormulario == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2557"));
            retorno = true;
        }
        return retorno;
    }

    // </METODOS_ADICIONALES>
}
