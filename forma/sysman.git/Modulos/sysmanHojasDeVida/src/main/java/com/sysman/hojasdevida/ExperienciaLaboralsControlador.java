/*-
 * ExperienciaLaboralsControlador.java
 *
 * 1.0
 * 
 * 16/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.ejb.impl.EjbHojasDeVidaCero;
import com.sysman.hojasdevida.enums.ExperienciaLaboralsControladorEnum;
import com.sysman.hojasdevida.enums.ExperienciaLaboralsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Esta clase es el controlador para el formulario
 * "Experiencia Laboral" en Access "NAT_EXPERIENCIA_LABORAL", el cual
 * es llamado desde Hojas De Vida \Datos Hoja De Vida\Datos Basicos
 * --> Botón Exp. Laboral
 *
 * 
 * @version 1.0, 05/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 * 
 */
@ManagedBean
@ViewScoped
public class ExperienciaLaboralsControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo FECHARETIRO en el formulario, almacena el
     * texto FECHARETIRO el cual es un campo del registro
     */
    private final String cFechaRetiro;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo ANOSERVICIO en el formulario, almacena el
     * texto ANOSERVICIO el cual es un campo del registro
     */
    private String cAnoServicio;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo MESESERVICIO en el formulario, almacena el
     * texto MESESERVICIO el cual es un campo del registro
     */
    private String cMesesServicio;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo DIASERVICIO en el formulario, almacena el
     * texto DIASERVICIO el cual es un campo del registro
     */
    private String cDiasServicio;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Numero de documento de identificacion perteneciente al empleado
     * con el que se esta trabajando
     */
    private String numeroDcto;
    /**
     * Sucursal definida para el empleado con el que se esta
     * trabajando
     */
    private String sucursal;
    /**
     * Codigo asignado al empleado con el que se esta trabajando
     */
    private String codigoPersona;
    /**
     * Atributo que permite definir la visibilidad del campo
     * "Otra Dedicacion" en el formulario
     */
    private boolean visibleOtraDedicacion;
    /**
     * Atributo que almacena la fecha de ingreso registrada en el
     * formulario
     */
    private Date fechaIngreso;
    /**
     * Atributo que almacena la fecha de retiro registrada en el
     * formulario
     */
    private Date fechaRetiro;
    /**
     * Estructura que almacena los campos llave del personal con el
     * que se eta trabajando
     */
    private Map<String, Object> ridDatosPersonales;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de registros para el combo de Departamento
     */
    private List<Registro> listaDepartamento;
    /**
     * Listado de registros para el combo de Municipio
     */
    private List<Registro> listaMunicipio;
    /**
     * Listado de registros para el combo de Pais
     */
    private List<Registro> listaPais;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
    /**
     * Implementacion del EJB de EjbHojasDeVidaCero para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_HOJAS_DE_VIDA
     */
    @EJB
    private EjbHojasDeVidaCero ejbHojasDeVidaCero;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de ExperienciaLaboralsControlador
     */
    public ExperienciaLaboralsControlador() {
        super();
        compania = SessionUtil.getCompania();
        cFechaRetiro = ExperienciaLaboralsControladorEnum.FECHARETIRO
                        .getValue();
        cAnoServicio = ExperienciaLaboralsControladorEnum.ANOSERVICIO
                        .getValue();
        cMesesServicio = ExperienciaLaboralsControladorEnum.MESESERVICIO
                        .getValue();
        cDiasServicio = ExperienciaLaboralsControladorEnum.DIASERVICIO
                        .getValue();

        try {
            numFormulario = GeneralCodigoFormaEnum.EXPERIENCIA_LABORALS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                ridDatosPersonales = (Map<String, Object>) parametrosEntrada
                                .get("rid");
                numeroDcto = parametrosEntrada.get("numeroDcto").toString();
                sucursal = parametrosEntrada.get("sucursal").toString();
                codigoPersona = parametrosEntrada.get("codigo").toString();
            }

            visibleOtraDedicacion = false;
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaPais();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaDepartamento();
        cargarListaMunicipio();
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        enumBase = GenericUrlEnum.NAT_EXPERIENCIA_LABORAL;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza el llamado al metodo buscarUrls() el cual obtiene
     * las Urls para realizar las operaciones CRUD en el formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        numeroDcto);
        parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                        codigoPersona);
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaDepartamento
     *
     */
    public void cargarListaDepartamento() {
        Map<String, Object> param = new TreeMap<>();
        param.put(ExperienciaLaboralsControladorEnum.PAIS.getValue(),
                        registro.getCampos()
                                        .get(ExperienciaLaboralsControladorEnum.PAIS
                                                        .getValue()));

        try {
            listaDepartamento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ExperienciaLaboralsControladorUrlEnum.URL001
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
     * Carga la lista listaMunicipio
     *
     */
    public void cargarListaMunicipio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(ExperienciaLaboralsControladorEnum.PAIS.getValue(),
                        registro.getCampos().get("PAIS"));
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),
                        registro.getCampos().get("DEPTO"));

        try {
            listaMunicipio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ExperienciaLaboralsControladorUrlEnum.URL002
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
     * Carga la lista listaPais
     *
     */
    public void cargarListaPais() {
        Map<String, Object> param = new TreeMap<>();
        try {
            listaPais = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ExperienciaLaboralsControladorUrlEnum.URL003
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Dedicacion
     * 
     * Asigna alor a la variable visibleOtraDedicacion de acuerdo a la
     * opcionseleccionada en el combo "Dedicacion(CB5066)"
     * 
     */
    public void cambiarDedicacion() {
        // <CODIGO_DESARROLLADO>
        visibleOtraDedicacion = "OD".equals(
                        registro.getCampos().get("DEDICACION")) ? true : false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Departamento
     * 
     * Actualiza el listado de registros para el combo de municipio,
     * de acuerdo al departamento seleccionado
     * 
     */
    public void cambiarDepartamento() {
        // <CODIGO_DESARROLLADO>
        cargarListaMunicipio();
        registro.getCampos().put("MUNICIPIO", null);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Pais
     * 
     * Actualiza el listado de registros para el combo de
     * departamento, de acuerdo al pais seleccionado
     * 
     */
    public void cambiarPais() {
        // <CODIGO_DESARROLLADO>
        cargarListaDepartamento();
        cargarListaMunicipio();
        registro.getCampos().put("DEPTO", null);
        registro.getCampos().put("MUNICIPIO", null);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaIngreso
     * 
     * Realiza el llamado al metodo calcularTiempoServicio() para
     * actualizar los campos de "Tiempo de Servicio" en el formulario
     * 
     */
    public void cambiarFechaIngreso() {
        // <CODIGO_DESARROLLADO>
        calcularTiempoServicio();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaRetiro
     * 
     * Realiza el llamado al metodo calcularTiempoServicio() para
     * actualizar los campos de "Tiempo de Servicio" en el formulario
     * 
     */
    public void cambiarFechaRetiro() {
        // <CODIGO_DESARROLLADO>
        calcularTiempoServicio();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    /**
     * Verifica que la fecha de ingreso registrada en el formulario
     * sea menor a la gfecfecha de retiro
     */
    private boolean validarFechas() {
        boolean fechasValidas = true;
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        "FECHAINGRESO")
            && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            cFechaRetiro)) {
            fechaIngreso = (Date) registro.getCampos().get("FECHAINGRESO");
            fechaRetiro = (Date) registro.getCampos().get(cFechaRetiro);
            if (fechaIngreso.after(fechaRetiro)) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3862"));
                fechasValidas = false;
            }
            else if (fechaRetiro.before(fechaIngreso)) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3863"));
                fechasValidas = false;
            }

            if (!fechasValidas) {
                registro.getCampos().put(cFechaRetiro, null);
                registro.getCampos().put(cAnoServicio, null);
                registro.getCampos().put(cMesesServicio, null);
                registro.getCampos().put(cDiasServicio, null);
            }
        }
        return fechasValidas;
    }

    /**
     * Calcula el tiempo de servicio en Anio-Meses-Dias, de acuerdo a
     * las fecha de ingreso y retiro registradas
     */
    private void calcularTiempoServicio() {
        try {
            if (validarFechas() && fechaIngreso != null
                && fechaRetiro != null) {
                String diferencia = ejbSysmanUtil.calcularDiferenciaEntreFechas(
                                fechaIngreso,
                                fechaRetiro,
                                1,
                                0);
                registro.getCampos().put(cAnoServicio,
                                diferencia.substring(0, 2));
                registro.getCampos().put(cMesesServicio,
                                diferencia.substring(3, 5));
                registro.getCampos().put(cDiasServicio,
                                diferencia.substring(6, 8));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
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

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * Genera el consecutivo para realizar la insercion del registro,
     * asigna los valores a los campos llave de la tabla
     * 
     * @return Si el proceso previo a la insercion fue exitoso
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        try {
            long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "NAT_EXPERIENCIA_LABORAL",
                            "    COMPANIA        = ''" + compania + "'' " +
                                "AND NUMERO_DCTO = ''" + numeroDcto + "'' " +
                                "AND SUCURSAL    = ''" + sucursal + "'' " +
                                "AND NEL_CODIGOPERSONA = ''" + codigoPersona
                                + "'' ",
                            "NUMERO",
                            "1");

            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos().put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                            numeroDcto);
            registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                            sucursal);
            registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                            consecutivo);
            registro.getCampos()
                            .put(ExperienciaLaboralsControladorEnum.NEL_CODIGOPERSONA
                                            .getValue(), codigoPersona);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return Si el proceso de insercion fue exitoso
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        fechaIngreso = fechaRetiro = null;
        try {
			ejbHojasDeVidaCero.actualizarExpLaboral(compania, numeroDcto, codigoPersona, sucursal,
					SessionUtil.getUser().getCodigo());
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     * @return Si el proceso previo a la actualizacion fue exitoso
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        fechaIngreso = fechaRetiro = null;
        visibleOtraDedicacion = false;
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.NUMERO_DCTO.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.SUCURSAL.getName());
            registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
            registro.getCampos()
                            .remove(ExperienciaLaboralsControladorEnum.NEL_CODIGOPERSONA
                                            .getValue());
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     * @return Si el proceso de actualizacion fue exitoso
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
    	try {
			ejbHojasDeVidaCero.actualizarExpLaboral(compania, numeroDcto, codigoPersona, sucursal,
					SessionUtil.getUser().getCodigo());
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * 
     * @return Si el proceso previo a la eliminacion fue exitoso
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * 
     * @return Si el proceso de eliminacion fue exitoso
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
    	try {
			ejbHojasDeVidaCero.actualizarExpLaboral(compania, numeroDcto, codigoPersona, sucursal,
					SessionUtil.getUser().getCodigo());
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     * Realiza la redireccion al formulario "Natdatospersonales"
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridDatosPersonales);
        parametros.put("numeroDcto", numeroDcto);
        parametros.put("sucursal", sucursal);
        parametros.put("codigo", codigoPersona);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public List<Registro> getListaDepartamento() {
        return listaDepartamento;
    }

    public boolean isVisibleOtraDedicacion() {
        return visibleOtraDedicacion;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaDepartamento
     * 
     * @return listaDepartamento
     */
    public void setVisibleOtraDedicacion(boolean visibleOtraDedicacion) {
        this.visibleOtraDedicacion = visibleOtraDedicacion;
    }

    /**
     * Asigna la lista listaDepartamento
     * 
     * @param listaDepartamento
     * Variable a asignar en listaDepartamento
     */
    public void setListaDepartamento(List<Registro> listaDepartamento) {
        this.listaDepartamento = listaDepartamento;
    }

    /**
     * Retorna la lista listaMunicipio
     * 
     * @return listaMunicipio
     */
    public List<Registro> getListaMunicipio() {
        return listaMunicipio;
    }

    /**
     * Asigna la lista listaMunicipio
     * 
     * @param listaMunicipio
     * Variable a asignar en listaMunicipio
     */
    public void setListaMunicipio(List<Registro> listaMunicipio) {
        this.listaMunicipio = listaMunicipio;
    }

    /**
     * Retorna la lista listaPais
     * 
     * @return listaPais
     */
    public List<Registro> getListaPais() {
        return listaPais;
    }

    /**
     * Asigna la lista listaPais
     * 
     * @param listaPais
     * Variable a asignar en listaPais
     */
    public void setListaPais(List<Registro> listaPais) {
        this.listaPais = listaPais;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
