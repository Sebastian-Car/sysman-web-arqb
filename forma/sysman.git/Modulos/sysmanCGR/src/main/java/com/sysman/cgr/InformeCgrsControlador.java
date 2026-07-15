/*-
 * InformeCgrsControlador.java
 *
 * 1.0
 * 
 * 15/03/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.cgr.enums.InformeCgrsControladorEnum;
import com.sysman.cgr.enums.InformeCgrsControladorUrlEnum;
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
import com.sysman.nomina.ejb.impl.EjbNominaNueveGeneral;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para la generacion de los informes, segun lo configurado en nomina y contratos
 *
 * @version 1.0, 15/03/2019
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class InformeCgrsControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo que almacena el codigo del modulo por el cual se ingresa en la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el codigo del registro seleccionado en el combo de proceso
     */
    private String proceso;
    /**
     * Atributo que almacena el codigo del informe a generar
     */
    private String nombreProceso;
    /**
     * Atributo que almacena el numero de a�o seleccionado
     */
    private String ano;
    /**
     * Atributo que almacena el numero del mes inicial seleccionado
     */
    private String mesInicial;
    /**
     * Atributo que almacena el numero del mes final seleccionado
     */
    private String mesFinal;
    /**
     * Atributo que almacena el codigo de contaduria registado en el formulario de compania
     */
    private String codigoContaduria;
    /**
     * Atributo que almacena el codigo de la consulta a resolver, segun el registro seleccionado en el combo Informe a Generar
     */
    private String nombreConsulta;
    /**
     * Atributo que almacena el nombre del encabezado a generar seleccionado en el combo de formulario a generar
     */
    private String encabezadoInforme;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Atributo que identifica si el informe genera regsitro de totales
     */
    private boolean totales;
    
    private boolean verBtnEncargos = false;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de la tabla ANO
     */
    private List<Registro> listaAno;
    /**
     * Lista de registros de la tabla MES
     */
    private List<Registro> listaMesInicial;
    /**
     * Lista de registros de la tabla MES
     */
    private List<Registro> listaMesFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de la tabla INFORMES_ENTES
     */
    private RegistroDataModelImpl listaNombreProceso;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InformeCgrsControlador
     */
    private EjbNominaNueveGeneral ejbNominaNueve = new EjbNominaNueveGeneral();
    
    public InformeCgrsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.INFORME_CGR_CONTROLADOR.getCodigo();
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
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaAno();
        cargarListaMesInicial();
        cargarListaMesFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaNombreProceso();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        ano = String.valueOf(SysmanFunciones.ano(new Date()));
        mesInicial ="1";
        mesFinal = "12";
        codigoContaduria = SessionUtil.getCompaniaIngreso().getCodigoContaduria();
        cargarListaMesInicial();
        cargarListaMesFinal();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeCgrsControladorUrlEnum.URL182
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
     *
     */
    public void cargarListaMesInicial()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        try {
            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeCgrsControladorUrlEnum.URL215
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
    public void cargarListaMesFinal()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);

        try {
            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeCgrsControladorUrlEnum.URL240
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
     * Carga la lista listaNombreProceso
     *
     */
    public void cargarListaNombreProceso()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeCgrsControladorUrlEnum.URL270
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPO.getName(), "5");
        param.put(InformeCgrsControladorEnum.SUBTIPO.getValue(), SessionUtil.getCompaniaIngreso().getTipoEntidad());

        listaNombreProceso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     *
     */
    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.CSV);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>

    public void generarInforme(FORMATOS formato)
    {

        try {

            Map<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("ano", ano);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesFinal", mesFinal);

            archivoDescarga = JsfUtil.reportesFut(
                            nombreConsulta, reemplazar,
                            generarEncabezado(),
                            formato,
                            nombreProceso,
                            modulo, totales);

        }
        catch (JRException | IOException | SQLException | DRException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    public void oprimirRevEncargo(){
    	
    	
		try {
					           	
		    ejbNominaNueve.verificarcarencargospersonalhistorico(compania,ano, 0);
		           	 
		    JsfUtil.agregarMensajeInformativo(
		                     idioma.getString("MSM_PROCESO_EJECUTADO"));
		           	 
			 
		} catch (SystemException e) {
			 e.printStackTrace();
		}


    }

    /**
     * Metodo que arma el encabezado en el informe
     * 
     * @return
     */
    private String generarEncabezado()
    {
        String retorno = "";

        String mesIni = mesInicial.length() == 1 ? SysmanFunciones.concatenar("0", mesInicial) : mesInicial;
        String mesFin = mesFinal.length() == 1 ? SysmanFunciones.concatenar("0", mesFinal) : mesFinal;

        String separadorColumnas = "\t";
        try {
            retorno = SysmanFunciones.concatenar("S", separadorColumnas, codigoContaduria, separadorColumnas, "1", mesIni, mesFin,
                            separadorColumnas, ano, separadorColumnas, encabezadoInforme, separadorColumnas,
                            SysmanFunciones.convertirAFechaCadena(
                                            new Date(),
                                            "dd-MM-yyyy"));
        }
        catch (ParseException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        return retorno;

    }

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     * 
     */
    public void cambiarAno()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaMesInicial();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control MesInicial
     * 
     * 
     */
    public void cambiarMesInicial()
    {
        // <CODIGO_DESARROLLADO>
        mesFinal = null;
        cargarListaMesFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaNombreProceso
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNombreProceso(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        proceso = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        nombreProceso = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();
        nombreConsulta = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        InformeCgrsControladorEnum.CONSULTA
                                                        .getValue()),
                        "").toString();
        encabezadoInforme = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        InformeCgrsControladorEnum.ENCABEZADO
                                                        .getValue()),
                        "").toString();
        
        if("800611_PersonalyCostosPlantaEncargo".equalsIgnoreCase(nombreConsulta)) {
            verBtnEncargos = true;
            
            JsfUtil.agregarMensajeAlerta(
                    idioma.getString("TG_MSG_ENCARGO"));
            
        } else {
            verBtnEncargos = false;
        }

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable proceso
     * 
     * @return proceso
     */
    public String getProceso()
    {
        return proceso;
    }

    /**
     * Asigna la variable proceso
     * 
     * @param proceso
     * Variable a asignar en proceso
     */
    public void setProceso(String proceso)
    {
        this.proceso = proceso;
    }

    /**
     * Retorna la variable nombreProceso
     * 
     * @return nombreProceso
     */
    public String getNombreProceso()
    {
        return nombreProceso;
    }

    /**
     * Asigna la variable nombreProceso
     * 
     * @param nombreProceso
     * Variable a asignar en nombreProceso
     */
    public void setNombreProceso(String nombreProceso)
    {
        this.nombreProceso = nombreProceso;
    }

    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno()
    {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano)
    {
        this.ano = ano;
    }

    /**
     * Retorna la variable mesInicial
     * 
     * @return mesInicial
     */
    public String getMesInicial()
    {
        return mesInicial;
    }

    /**
     * Asigna la variable mesInicial
     * 
     * @param mesInicial
     * Variable a asignar en mesInicial
     */
    public void setMesInicial(String mesInicial)
    {
        this.mesInicial = mesInicial;
    }

    /**
     * Retorna la variable mesFinal
     * 
     * @return mesFinal
     */
    public String getMesFinal()
    {
        return mesFinal;
    }

    /**
     * Asigna la variable mesFinal
     * 
     * @param mesFinal
     * Variable a asignar en mesFinal
     */
    public void setMesFinal(String mesFinal)
    {
        this.mesFinal = mesFinal;
    }

    /**
     * Retorna la variable codigoContaduria
     * 
     * @return codigoContaduria
     */
    public String getCodigoContaduria()
    {
        return codigoContaduria;
    }

    /**
     * Asigna la variable codigoContaduria
     * 
     * @param codigoContaduria
     * Variable a asignar en codigoContaduria
     */
    public void setCodigoContaduria(String codigoContaduria)
    {
        this.codigoContaduria = codigoContaduria;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
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
    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    /**
     * Retorna la lista listaMesInicial
     * 
     * @return listaMesInicial
     */
    public List<Registro> getListaMesInicial()
    {
        return listaMesInicial;
    }

    /**
     * Asigna la lista listaMesInicial
     * 
     * @param listaMesInicial
     * Variable a asignar en listaMesInicial
     */
    public void setListaMesInicial(List<Registro> listaMesInicial)
    {
        this.listaMesInicial = listaMesInicial;
    }

    /**
     * Retorna la lista listaMesFinal
     * 
     * @return listaMesFinal
     */
    public List<Registro> getListaMesFinal()
    {
        return listaMesFinal;
    }

    /**
     * Asigna la lista listaMesFinal
     * 
     * @param listaMesFinal
     * Variable a asignar en listaMesFinal
     */
    public void setListaMesFinal(List<Registro> listaMesFinal)
    {
        this.listaMesFinal = listaMesFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaNombreProceso
     * 
     * @return listaNombreProceso
     */
    public RegistroDataModelImpl getListaNombreProceso()
    {
        return listaNombreProceso;
    }

    /**
     * Asigna la lista listaNombreProceso
     * 
     * @param listaNombreProceso
     * Variable a asignar en listaNombreProceso
     */
    public void setListaNombreProceso(RegistroDataModelImpl listaNombreProceso)
    {
        this.listaNombreProceso = listaNombreProceso;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    
	public boolean isVerBtnEncargos() {
		return verBtnEncargos;
	}

	public void setVerBtnEncargos(boolean verBtnEncargos) {
		this.verBtnEncargos = verBtnEncargos;
	}
}
