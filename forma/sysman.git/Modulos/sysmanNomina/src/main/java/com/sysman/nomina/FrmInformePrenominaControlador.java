/*-
 * FrmInformePrenominaControlador.java
 *
 * 1.0
 *
 * 5/01/2018
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
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.FrmInformePrenominaControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
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
 * Formulario que genera los reportes de prenomina de los empleados
 *
 * @version 1.0, 05/01/2018
 * @author eamaya
 */
/**
 * @version 2.0, 04/04/2018
 * @author dnino
 *
 * Se actualiza para que incluya el nuevo formato de reporte
 * PRENOMINA_ANE, el cual debe estar configurado en el parámetro
 * "FORMATO PRENOMINA". En caso contrario, se define como
 * PRENOMINA_EEB.
 * 
* @author obarragan
* @version 3, 10/06/2019 - Se agrego opcion de imprimir header con imagenes adicionales.    
 *  
 *  */
@ManagedBean
@ViewScoped
public class FrmInformePrenominaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el numero de modulo de
     * la por el cual accedio el usuario
     */
    private final String modulo;
    /**
     * Atributo que almacena el anio seleccionado antes de ingresar a
     * la nomina
     */
    private final String anio;
    /**
     * Atributo que almacena el proceso seleccionado antes de ingresar
     * a la nomina
     */
    private final String proceso;
    /**
     * Atributo que almacena el mes seleccionado antes de ingresar a
     * la nomina
     */
    private final String mes;
    /**
     * Atributo que almacena el periodo seleccionado antes de ingresar
     * a la nomina
     */
    private final String periodo;

    private final String nombreProceso;
    private final String nombrePeriodo;
    private final String nombreCompania;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor de la opcion seleccionada en la
     * vista
     */
    private String opcion;
    /**
     * Atributo que almacena el valor del indicador de prenomina en la
     * vista
     */
    private String informePrenomina;
    /**
     * Atributo que almacena el codigo del empleado seleccionado en la
     * vista
     */
    private String empleado;
    /**
     * Atributo que almacena el valor del centro del costo
     * seleccionado en la vista
     */
    private String centroCosto;

    /**
     * Atributo que almacena el nombre del centro de costo y del
     * empleado
     */
    private String nombreCampo;

    private boolean verInformePrenomina;

    /**
     * Atributo que se encagra de generar el titulo del formulario
     */
    private String titulo;
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
     * Lista que almacena los registros de todos los empleados
     */
    private RegistroDataModelImpl listaEmpleado;
    /**
     * Lista que almacena los registros de todos los centros de costo
     */
    private RegistroDataModelImpl listaCentroCosto;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    
    private String headerEspecial;

    public String getHeaderEspecial() {
		return headerEspecial;
	}

	public void setHeaderEspecial(String headerEspecial) {
		this.headerEspecial = headerEspecial;
	}

	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrmInformePrenominaControlador
     */
    public FrmInformePrenominaControlador() {
        super();

        compania = SessionUtil.getCompania();

        modulo = SessionUtil.getModulo();
        anio = (String) SessionUtil
                        .getSessionVar("anioNomina");
        proceso = (String) SessionUtil
                        .getSessionVar("procesoNomina");
        mes = (String) SessionUtil.getSessionVar("mesNomina");
        periodo = (String) SessionUtil
                        .getSessionVar("periodoNomina");
        nombreProceso = (String) SessionUtil
                        .getSessionVar("nombreProcesoNomina");
        nombrePeriodo = (String) SessionUtil
                        .getSessionVar("nombrePeriodoNomina");
        nombreCompania = SessionUtil.getCompaniaIngreso()
                        .getNombre();

        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_INFORMEPRENOMINA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            titulo = idioma.getString("TB_TB3710")
                            .replace("#$nombreProceso#$", nombreProceso)
                            .replace("#$nombreMes#$",
                                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                            .parseInt(mes)].toUpperCase())
                            .replace("#$anio#$", anio)
                            .replace("#$nombrePeriodo#$",
                                            nombrePeriodo.toUpperCase());

            opcion = "2";

            verInformePrenomina = false;
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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaEmpleado();
        cargarListaCentroCosto();
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

        try {
            String parametro = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA COMPARACION DE PRENOMINA", modulo,
                            new Date(),
                            false);

            verInformePrenomina = "SI".equals(parametro) ? true : false;

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaEmpleado
     *
     */
    public void cargarListaEmpleado() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmInformePrenominaControladorUrlEnum.URL7490
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID_DE_EMPLEADO");
    }

    /**
     *
     * Carga la lista listaCentroCosto
     *
     */
    public void cargarListaCentroCosto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmInformePrenominaControladorUrlEnum.URL8087
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton PDF en la vista
     *
     *
     */
    public void oprimirPDF() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarOpcion() {
        nombreCampo = "";
        centroCosto = "";
        empleado = "";
    }

    private void generarReporte(FORMATOS formato) {
        try {

            if (!validarVacios()) {
                return;
            }

            String condicion = "";
            if (("1").equals(opcion)) {
                condicion = " AND HISTORICOS.ID_DE_EMPLEADO=" + empleado + " ";
            }
            else if (("2").equals(opcion)) {
                condicion = "";
            }
            else if (("3").equals(opcion)) {
                condicion = " AND CENTRO_COSTO.CODIGO=" + centroCosto + " ";
            }
            String formatoRep = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "FORMATO PRENOMINA", modulo,
                                            new Date(), false),
                                            "001617Prenomina")
                            .toString();
            String consultaInforme = "001617Prenomina";
            boolean generarcuadros;
            if ("001617Prenomina".equals(formatoRep)) {

                generarcuadros = true;
            }
            else {
                generarcuadros = false;
            }

            String nombreRC = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE JEFE RECURSOS HUMANOS", modulo,
                            new Date(), false);

            String cargoRC = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO JEFE RECURSOS HUMANOS", modulo,
                            new Date(), false);

            String nombreTP = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DEL CARGO TESORERO PAGADOR", modulo,
                            new Date(), false);

            String cargoTP = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DEL TESORERO PAGADOR", modulo,
                            new Date(), false);

            String nombreGerente = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DEL GERENTE", modulo, new Date(), false);

            String cargoGerente = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DEL GERENTE", modulo, new Date(), false);

            String nombreJP = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DE JEFE DE PRESUPUESTO", modulo,
                            new Date(), false);

            String cargoJP = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DEL JEFE DE PRESUPUESTO", modulo,
                            new Date(), false);

            String nombreAutoriza = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DE QUIEN AUTORIZA NOMINA", modulo,
                            new Date(), false);

            String cargoAutoriza = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DE QUIEN AUTORIZA NOMINA", modulo,
                            new Date(), false);

            String nombreRevisa = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DE QUIEN REVISA NOMINA", modulo,
                            new Date(), false);

            String cargoRevisa = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DE QUIEN REVISA NOMINA", modulo,
                            new Date(), false);

            String elaboradoPor = ejbSysmanUtil.consultarParametro(compania,
                            "ELABORADO POR", modulo,
                            new Date(), false);
            
            headerEspecial = ejbSysmanUtil.consultarParametro(compania,
                    "FORMATOS ESPECIALES BUCARAMANGA", modulo,
                    new Date(),
                    true);
            
            String sticker = SessionUtil.getCompaniaIngreso().getRutaSticker();

            Map<String, Object> reemplazos = new TreeMap<>();
            Map<String, Object> parametros = new TreeMap<>();

            // reemplazos.put("compania", compania); - LA COMPANIA NO
            // SE ENVIA COMO PARAMETRO A LOS INFORMES,
            // AL REALIZAR LOS REEMPLAZOS SE TOMA LA COMPANIA CON LA
            // QUE SE LOGEA EL USUARIO
            reemplazos.put("proceso", proceso);
            reemplazos.put("ano", anio);
            reemplazos.put("mes", mes);
            reemplazos.put("periodo", periodo);
            reemplazos.put("condicion", condicion);

            parametros.put("PR_NOMBREEMPRESA",
                            nombreCompania);

            parametros.put("PR_NOMBRE_DEL_JEFE_DE_RECURSOS_HUMANOS",
                            nombreRC);
            parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS",
                            cargoRC);
            parametros.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR",
                            nombreTP);
            parametros.put("PR_CARGO_DEL_TESORERO_PAGADOR",
                            cargoTP);
            parametros.put("PR_NOMBRE_DEL_GERENTE",
                            nombreGerente);
            parametros.put("PR_CARGO_DEL_GERENTE",
                            cargoGerente);
            parametros.put("PR_NOMBRE_DE_JEFE_DE_PRESUPUESTO",
                            nombreJP);
            parametros.put("PR_CARGO_DEL_JEFE_DE_PRESUPUESTO",
                            cargoJP);
            parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA",
                            nombreAutoriza);
            parametros.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA",
                            cargoAutoriza);
            parametros.put("PR_NOMBRE_DE_QUIEN_REVISA_NOMINA",
                            nombreRevisa);
            parametros.put("PR_CARGO_DE_QUIEN_REVISA_NOMINA",
                            cargoRevisa);
            parametros.put("PR_ELABORADO_POR", elaboradoPor);

            parametros.put("PR_VISIBLE_FIRMAS", generarcuadros);
            
            parametros.put("PR_HEADER_ESPECIAL", headerEspecial.equals("SI")?true:false);
            parametros.put("PR_IMAGEN_ESPECIAL", sticker);
            
            Reporteador.resuelveConsulta(consultaInforme,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(formatoRep,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (SystemException | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarVacios() {
        if (("1").equals(opcion)
            && SysmanFunciones.validarVariableVacio(empleado)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2506"));
            return false;
        }
        else if (("3").equals(opcion)
            && SysmanFunciones.validarVariableVacio(centroCosto)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2774"));
            return false;

        }
        return true;

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEmpleado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleado = registroAux.getCampos().get("ID_DE_EMPLEADO").toString();

        nombreCampo = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName())
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCosto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCosto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCosto = registroAux.getCampos().get("CODIGO")
                        .toString();

        nombreCampo = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName())
                        .toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    public boolean isVerInformePrenomina() {
        return verInformePrenomina;
    }

    public void setVerInformePrenomina(boolean verInformePrenomina) {
        this.verInformePrenomina = verInformePrenomina;
    }

    /**
     * Retorna la variable opcion
     *
     * @return opcion
     */
    public String getOpcion() {
        return opcion;
    }

    /**
     * Asigna la variable opcion
     *
     * @param opcion
     * Variable a asignar en opcion
     */
    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    /**
     * Retorna la variable informePrenomina
     *
     * @return informePrenomina
     */
    public String getInformePrenomina() {
        return informePrenomina;
    }

    /**
     * Asigna la variable informePrenomina
     *
     * @param informePrenomina
     * Variable a asignar en informePrenomina
     */
    public void setInformePrenomina(String informePrenomina) {
        this.informePrenomina = informePrenomina;
    }

    /**
     * Retorna la variable empleado
     *
     * @return empleado
     */
    public String getEmpleado() {
        return empleado;
    }

    /**
     * Asigna la variable empleado
     *
     * @param empleado
     * Variable a asignar en empleado
     */
    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    /**
     * Retorna la variable centroCosto
     *
     * @return centroCosto
     */
    public String getCentroCosto() {
        return centroCosto;
    }

    /**
     * Asigna la variable centroCosto
     *
     * @param centroCosto
     * Variable a asignar en centroCosto
     */
    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
    }

    /**
     * Retorna la variable nombreCampo
     *
     * @return nombreCampo
     */
    public String getNombreCampo() {
        return nombreCampo;
    }

    /**
     * Asigna la variable nombreCampo
     *
     * @param nombreCampo
     * Variable a asignar en nombreCampo
     */
    public void setNombreCampo(String nombreCampo) {
        this.nombreCampo = nombreCampo;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaEmpleado
     *
     * @return listaEmpleado
     */
    public RegistroDataModelImpl getListaEmpleado() {
        return listaEmpleado;
    }

    /**
     * Asigna la lista listaEmpleado
     *
     * @param listaEmpleado
     * Variable a asignar en listaEmpleado
     */
    public void setListaEmpleado(RegistroDataModelImpl listaEmpleado) {
        this.listaEmpleado = listaEmpleado;
    }

    /**
     * Retorna la lista listaCentroCosto
     *
     * @return listaCentroCosto
     */
    public RegistroDataModelImpl getListaCentroCosto() {
        return listaCentroCosto;
    }

    /**
     * Asigna la lista listaCentroCosto
     *
     * @param listaCentroCosto
     * Variable a asignar en listaCentroCosto
     */
    public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto) {
        this.listaCentroCosto = listaCentroCosto;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
