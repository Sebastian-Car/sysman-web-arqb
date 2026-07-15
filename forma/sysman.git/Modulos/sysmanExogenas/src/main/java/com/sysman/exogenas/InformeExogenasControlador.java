/*-
 * InformeExogenasControlador.java
 *
 * 1.0
 * 
 * 19/12/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.exogenas;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.exogenas.enums.InformeExogenasControladorEnum;
import com.sysman.exogenas.enums.InformeExogenasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
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

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para generar el informe de exgenas por formato
 *
 * @version 1.0, 19/12/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class InformeExogenasControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor del codigo del formato
     * seleccioando
     */
    private String formato;
    /**
     * Atributo que almacena el valor del nombre del formato
     * seleccioando
     */
    private boolean esContribuyente;
    
    private boolean bloqContribuyente;
    
    private boolean abonoCuentaSinIva;
    
    private String nombreFormato;
    /**
     * Atributo que almacena el valor del ano seleccionado en el combo
     */
    private String ano;
    /**
     * Atributo que almacena el valor del numero del mes inicial
     * seleccionado en el combo
     */
    private String mesInicial;
    /**
     * Atributo que almacena el valor del numero del mes final
     * seleccionado en el combo
     */
    private String mesFinal;

    /**
     * Combo que carga los tipos de formato
     */
    private String tipo;

    /**
     * Atributo que almacena el codigo de la consulta del registro
     * seleccionado en el combo formato
     */
    private String consulta;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     *  Atributo que almacena el formato del codigo seleccionado 
     */
    private String formatoAux;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de la tabla ano
     */
    private List<Registro> listaAno;
    /**
     * Lista de registros de la tabla mes
     */
    private List<Registro> listaMesInicial;
    /**
     * Lista de registros de la tabla mes
     */
    private List<Registro> listaMesFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de la tabla formatos
     */
    private RegistroDataModelImpl listaFormato;
    /**
     * 
     */
    @EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InformeExogenasControlador
     */
    public InformeExogenasControlador() {
        super();
        compania = SessionUtil.getCompania();
        esContribuyente = false;
        abonoCuentaSinIva = false;
        bloqContribuyente = true;
        try {
            // 2015
            numFormulario = GeneralCodigoFormaEnum.INFORME_EXOGENAS_CONTROLADOR
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
        cargarListaMesInicial();

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
        ano = String.valueOf(SysmanFunciones.ano(new Date()));
        mesInicial = "1";
        cargarListaMesFinal();
        mesFinal = "12";
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
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
                                                            InformeExogenasControladorUrlEnum.URL169
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaMesInicial
     */
    public void cargarListaMesInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));

        try {
            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeExogenasControladorUrlEnum.URL195
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaMesFinal
     *
     */
    public void cargarListaMesFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));
        param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);

        try {
            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeExogenasControladorUrlEnum.URL221
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo ejecutado al cambiar el control Tipo
     * 
     */
    public void cambiarTipo() {
        listaFormato = null;
        formato = null;
        nombreFormato = null;
        cargarListaFormato();

    }

    /**
     * 
     * Carga la lista listaFormato
     *
     */
    public void cargarListaFormato() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeExogenasControladorUrlEnum.URL253
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPO.getName(), "4");
        param.put(InformeExogenasControladorEnum.SUBTIPO.getValue(), tipo);
        listaFormato = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton GenerarInforme en la vista
     *
     *
     * 
     */
    public void oprimirGenerarInforme() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo invocado al ejecutar el comando remoto Alerta en la
     * vista
     *
     *
     */
    public void ejecutarAlerta() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.agregarMensajeError(idioma.getString(
                        "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control MesInicial
     * 
     * 
     */
    public void cambiarMesInicial() {
        // <CODIGO_DESARROLLADO>
        mesFinal = null;
        cargarListaMesFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    /**
     * Metodo que se ejecuta en el llamado del evento del boton excel
     */
    private void generarInforme() {
        try {
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ano", ano);
            reemplazar.put("formato", formatoAux);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesFinal", mesFinal);
            
            if (formatoAux.equals("1001")) {
                if (esContribuyente && abonoCuentaSinIva) {
                    reemplazar.put("ckPago", -1);
                    reemplazar.put("contribuyente", -1);
                } else if (esContribuyente) {
                    reemplazar.put("ckPago", 0);
                    reemplazar.put("contribuyente", -1); 
                } else if (abonoCuentaSinIva) {
                    reemplazar.put("ckPago", -1); 
                    reemplazar.put("contribuyente", 0);
                } else {
                    reemplazar.put("ckPago", 0);
                    reemplazar.put("contribuyente", 0);
                }
            }
            
            String sql = Reporteador.resuelveConsulta(consulta,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            List<Registro> existeDatos;
            existeDatos = service.getListado(ConectorPool.ESQUEMA_SYSMAN, sql);
            if (!existeDatos.isEmpty()) {

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.EXCEL, nombreFormato);

            }
            else {
                ejecutarAlerta();

            }
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
    }

    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFormato
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     * @throws SystemException 
     */
    public void seleccionarFilaFormato(SelectEvent event) throws SystemException {
        Registro registroAux = (Registro) event.getObject();
        formato = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        nombreFormato = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();
        consulta = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        InformeExogenasControladorEnum.CONSULTA
                                                        .getValue()),
                        "").toString();
        
        formatoAux =  SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        InformeExogenasControladorEnum.FORMATO
                                                        .getValue()),
                        "").toString();
        String parametroForma1001 = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
			        "INFORME_1001_ESPECIAL", SessionUtil.getModulo(), new Date(), false), "NO").toString();
		
        if(formatoAux.equals("1001")){
        	bloqContribuyente = false;
        	if(parametroForma1001.equals("SI")) {
        	consulta = "800563Formato1001_especial";
        	}
        }else {
        	bloqContribuyente = true;
        }
     }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable formato
     * 
     * @return formato
     */
    public String getFormato() {
        return formato;
    }

    /**
     * Asigna la variable formato
     * 
     * @param formato
     * Variable a asignar en formato
     */
    public void setFormato(String formato) {
        this.formato = formato;
    }

    /**
     * Retorna la variable nombreFormato
     * 
     * @return nombreFormato
     */
    public String getNombreFormato() {
        return nombreFormato;
    }

    /**
     * Asigna la variable nombreFormato
     * 
     * @param nombreFormato
     * Variable a asignar en nombreFormato
     */
    public void setNombreFormato(String nombreFormato) {
        this.nombreFormato = nombreFormato;
    }

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
     * Retorna la lista listaMesInicial
     * 
     * @return listaMesInicial
     */
    public List<Registro> getListaMesInicial() {
        return listaMesInicial;
    }

    /**
     * Asigna la lista listaMesInicial
     * 
     * @param listaMesInicial
     * Variable a asignar en listaMesInicial
     */
    public void setListaMesInicial(List<Registro> listaMesInicial) {
        this.listaMesInicial = listaMesInicial;
    }

    /**
     * Retorna la lista listaMesFinal
     * 
     * @return listaMesFinal
     */
    public List<Registro> getListaMesFinal() {
        return listaMesFinal;
    }

    /**
     * Asigna la lista listaMesFinal
     * 
     * @param listaMesFinal
     * Variable a asignar en listaMesFinal
     */
    public void setListaMesFinal(List<Registro> listaMesFinal) {
        this.listaMesFinal = listaMesFinal;
    }

    /**
     * Retorna la variable tipo
     * 
     * @return tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Asigna la variable tipo
     * 
     * @param tipo
     * Variable a asignar en tipo
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaFormato
     * 
     * @return listaFormato
     */
    public RegistroDataModelImpl getListaFormato() {
        return listaFormato;
    }

    /**
     * Asigna la lista listaFormato
     * 
     * @param listaFormato
     * Variable a asignar en listaFormato
     */
    public void setListaFormato(RegistroDataModelImpl listaFormato) {
        this.listaFormato = listaFormato;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

	/**
	 * @return the esContribuyente
	 */
	public boolean isEsContribuyente() {
		return esContribuyente;
	}

	/**
	 * @param esContribuyente the esContribuyente to set
	 */
	public void setEsContribuyente(boolean esContribuyente) {
		this.esContribuyente = esContribuyente;
	}

	/**
	 * @return the bloqContribuyente
	 */
	public boolean isBloqContribuyente() {
		return bloqContribuyente;
	}

	/**
	 * @return the abonoCuentaSinIva
	 */
	public boolean isAbonoCuentaSinIva() {
		return abonoCuentaSinIva;
	}

	/**
	 * @param abonoCuentaSinIva the abonoCuentaSinIva to set
	 */
	public void setAbonoCuentaSinIva(boolean abonoCuentaSinIva) {
		this.abonoCuentaSinIva = abonoCuentaSinIva;
	}

	/**
	 * @param bloqContribuyente the bloqContribuyente to set
	 */
	public void setBloqContribuyente(boolean bloqContribuyente) {
		this.bloqContribuyente = bloqContribuyente;
	}
    
    
}
