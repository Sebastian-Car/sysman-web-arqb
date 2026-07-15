/*-
 * FejecucionPpptalsControlador.java
 *
 * 1.0
 * 
 * 30/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

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
import com.sysman.presupuesto.enums.FejecucionPpptalsControladorEnum;
import com.sysman.presupuesto.enums.FejecucionPpptalsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
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
 * clase migrada para generar los informes de ejecucion presupuestal
 *
 * @version 1.0, 30/11/2017
 * @author ybecerra
 * 
 * @author dcastiblanco
 * @version 2, 16/07/2021 Se ajusta para que el reporte por excel salga
 * plano
 */
@ManagedBean
@ViewScoped

public class FejecucionPpptalsControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por el
     * cual se ingresa en aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * variable que almacena el valor de la casilla de verificacion
     * auxiliar del formulario
     */
    private boolean auxiliar;
    /**
     * variable que almacena el valor del codigo de la cuenta inicial
     * seleccionado en el combo cuenta inicial del formulario
     */
    private String cuentaInicial;
    /**
     * variable que almacena el valor del codigo de la cuenta final
     * seleccionado en el combo cuenta final del formulario
     */
    private String cuentaFinal;
    /**
     * variable que almacena el valor del codigo seleccionado en el
     * combo ejecucion de del formulario
     */
    private String tipo;
    /**
     * variable que almacena el valor del anio seleccionado
     */
    private String ano;
    /**
     * variable que almacena el valor del mes seleccionado
     */
    private String mes;
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
     * lista de registros del combo ano
     */
    private List<Registro> listaAno;
    /**
     * lista de registros del combo mes
     */
    private List<Registro> listaMes;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * lista de registros del combo cuenta inicial del formulario
     */
    private RegistroDataModelImpl listaCuentaInicial;
    /**
     * lista de regidtros del combo cuenta final del formulario
     */
    private RegistroDataModelImpl listaCuentaFinal;
    
    /**
     * variable que almacena el valor de la casilla de verificacion
     * indFuenteCuipo del formulario
     */
    private boolean indFuenteCuipo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>



	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FejecucionPpptalsControlador
     */
    public FejecucionPpptalsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.F_EJECUCIONPPTAL_CONTROLADOR
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
        abrirFormulario();
        // <CARGAR_LISTA>
        cargarListaAno();
        cargarListaMes();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();

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
        ano = Integer.toString(SysmanFunciones.ano(new Date()));
        mes = Integer.toString(SysmanFunciones.mes(new Date()));
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     */
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FejecucionPpptalsControladorUrlEnum.URL5047
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
     * Carga la lista listaMes
     */
    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FejecucionPpptalsControladorUrlEnum.URL5447
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
     * Carga la lista listaCuentaInicial
     */
    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FejecucionPpptalsControladorUrlEnum.URL6064
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
     */
    public void cargarListaCuentaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FejecucionPpptalsControladorUrlEnum.URL7124
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(FejecucionPpptalsControladorEnum.CUENTAINICIAL
                        .getValue(), cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     */
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
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
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = null;
        cuentaFinal = null;
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        // </CODIGO_DESARROLLADO>
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
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "").toString();
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
        cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos().get("ID"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>

    /**
     * Metodo ejecutado al darle clic en el boton pdf o excel
     * 
     * @param formato
     */
    public void generarInforme(ReportesBean.FORMATOS formato) {
    	String reporte;
    	String excelSalida;
    	
    	if(indFuenteCuipo) {
    		reporte = "2713EjecucionPptalDRE";
    	}
    	else {
    		reporte = "001531FEjecucionPptalDRE";
    	}
  
        try {
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ano", ano);
            reemplazar.put("mes", mes);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);

            if (!auxiliar) {
                reemplazar.put("movimiento",
                                "AND V_RESUMENPPTO_BASE.MOVIMIENTO IN(0)");
            }
            else {
                reemplazar.put("movimiento", "");
            }
            String tituloInforme = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "TITULO INFORMES MUNICIPIO", modulo,
                                            new Date(),
                                            true),
                                            SessionUtil.getCompaniaIngreso()
                                                            .getNombre());

            String firmaUno = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA 1 EN PRESUPUESTO", modulo,
                                            new Date(), true), "");
            String firmaDos = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA 2 EN PRESUPUESTO", modulo,
                                            new Date(), true), "");

            String cargoUno = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO 1 EN PRESUPUESTO", modulo,
                                            new Date(), true), "");

            String cargoDos = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO 2 EN PRESUPUESTO", modulo,
                                            new Date(), true), "");
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_TITULOINFORMES", tituloInforme.toUpperCase());
            parametros.put("PR_NOMBRE_MES",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes)].toUpperCase());
            parametros.put("PR_FIRMA_UNO", firmaUno);
            parametros.put("PR_FIRMA_DOS", firmaDos);
            parametros.put("PR_CARGO_UNO", cargoUno);
            parametros.put("PR_CARGO_DOS", cargoDos);

            if ("D".equals(tipo)) {
                reemplazar.put(FejecucionPpptalsControladorEnum.TIPOEJECUCION
                                .getValue(), "DISPONIBILIDAD");
                parametros.put(FejecucionPpptalsControladorEnum.PR_PORDISPONIBILIDAD
                                .getValue(), true);
                parametros.put(FejecucionPpptalsControladorEnum.PR_POREJECUTADO
                                .getValue(), false);
                parametros.put(FejecucionPpptalsControladorEnum.PR_PORREGISTRO
                                .getValue(), false);
                parametros.put(FejecucionPpptalsControladorEnum.PR_TIPOTITULO
                                .getValue(), "- DISPONIBILIDADES");
            }
            else if ("R".equals(tipo)) {
                reemplazar.put(FejecucionPpptalsControladorEnum.TIPOEJECUCION
                                .getValue(), "REGISTRO");
                parametros.put(FejecucionPpptalsControladorEnum.PR_PORDISPONIBILIDAD
                                .getValue(), false);
                parametros.put(FejecucionPpptalsControladorEnum.PR_POREJECUTADO
                                .getValue(), false);
                parametros.put(FejecucionPpptalsControladorEnum.PR_PORREGISTRO
                                .getValue(), true);
                parametros.put(FejecucionPpptalsControladorEnum.PR_TIPOTITULO
                                .getValue(), "- REGISTROS");
            }
            else {
                reemplazar.put(FejecucionPpptalsControladorEnum.TIPOEJECUCION
                                .getValue(), "EJECUTADO");
                parametros.put(FejecucionPpptalsControladorEnum.PR_PORDISPONIBILIDAD
                                .getValue(), false);
                parametros.put(FejecucionPpptalsControladorEnum.PR_POREJECUTADO
                                .getValue(), true);
                parametros.put(FejecucionPpptalsControladorEnum.PR_PORREGISTRO
                                .getValue(), false);
                parametros.put(FejecucionPpptalsControladorEnum.PR_TIPOTITULO
                                .getValue(), "");
            }
            
          
            if(indFuenteCuipo) {
            	excelSalida = "800695FEjecucionPptalDRE_Excel";
        	}
        	else {
        		excelSalida = "800449FEjecucionPptalDRE_Excel";
        	}
            

            archivoDescarga = JsfUtil.exportarExcelPlano(reporte,
                            excelSalida,
                            ConectorPool.ESQUEMA_SYSMAN, formato, reemplazar,
                            parametros, Integer.valueOf(modulo));
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", reporte),
                            ex.getMessage()));
            logger.error(ex.getMessage(), ex);
        }
        catch (JRException | IOException | SysmanException | SQLException | DRException
                        | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        } 

    }

    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public boolean getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(boolean auxiliar) {
        this.auxiliar = auxiliar;
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
    
    public boolean getIndFuenteCuipo() {
		return indFuenteCuipo;
	}

	public void setIndFuenteCuipo(boolean indFuenteCuipo) {
		this.indFuenteCuipo = indFuenteCuipo;
	}
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
