package com.sysman.rest.negocio;
/*-
 * RegistroAutoservicio.java
 *
 * 1.0
 * 
 * 21/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.rest.excepcion.NegocioExcepcion;
import com.sysman.rest.negocio.enums.GeneraArchivoEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;

/**
 * Registro de operaciones de autoservicio. Creaci&oacute;n de
 * solicitudes, consultas y actualizaci&oacute;n de datos personales y
 * familiares.
 * 
 * @version 1.0, 21/05/2018
 * @author jrodrigueza
 *
 */
public class RegistroAutoservicio {

    /**
     * Campo CLASE_SOLICITUD.
     */
    private static final String CLASE_SOLICITUD = "CLASE_SOLICITUD";
    /**
     * Campo TIPO_PERMISO.
     */
    private static final String TIPO_PERMISO = "TIPO_PERMISO";
    /**
     * Campo HORA_INICIO.
     */
    private static final String HORA_INICIO = "HORA_INICIO";
    /**
     * Campo HORA_FINAL.
     */
    private static final String HORA_FINAL = "HORA_FINAL";
    /**
     * Campo FECHA_SOLICITUD.
     */
    private static final String FECHA_SOLICITUD = "FECHA_SOLICITUD";
    /**
     * Campo CEDULA.
     */
    private static final String CEDULA = GeneralParameterEnum.CEDULA.getName();
    /**
     * Campo ID_DEMPLEADO.
     */
    private static final String ID_DEMPLEADO = "ID_DEMPLEADO";
    /**
     * Campo USUARIO_SISTEMA.
     */
    private static final String USUARIO_SISTEMA = GeneraArchivoEnum.USUARIO_SISTEMA
                    .getValue();
    /**
     * Campo SUCURSAL_DESTINO.
     */
    private static final String SUCURSAL_DESTINO = "SUCURSAL_DESTINO";

    /**
     * Par$aacute;metro JEFE_DIRECTO.
     */
    private static final String JEFE_DIRECTO = "JEFE_DIRECTO";
    /**
     * Par$aacute;metro SUCURSAL_JEFE_DIRECTO.
     */
    private static final Object SUCURSAL_JEFE_DIRECTO = "SUCURSAL_JEFE_DIRECTO";

    /**
     * Valor para el estado de solicitud <b>En Tr&aacute;mite</b>.
     */
    private static final String ESTADO_EN_TRAMITE = "T";

    /**
     * Constante que representa la instancia del Log
     */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
                    .getLogger(GestionAutoservicio.class);

    private RequestManager requestManager = new RequestManager();
    /**
     * URL al servicio DSS para permite crear solicitudes de
     * autoservicio.
     */
    private UrlBean urlCreacionSolicitudes = new UrlBean();
    /**
     * URL al servicio DSS para permite crear solicitudes de
     * actualizacion de datos en personal.
     */
    private UrlBean urlCreacionPersonal = new UrlBean();
    /**
     * URL al servicio DSS para permite crear solicitudes de
     * actualizacion de datos familiares.
     */
    private UrlBean urlCreacionFamiliares = new UrlBean();
    /**
     * Referencia a los enumerados con las operaciones a la tabla de
     * la solicitudes de autoservicio.
     */
    protected GenericUrlEnum enumSolicitudes;
    /**
     * Referencia a los enumerados con las operaciones a la tabla de
     * personal de autoservicio.
     */
    private GenericUrlEnum enumPersonal;
    /**
     * Referencia a los enumerados con las operaciones a la tabla de
     * familiares de autoservicio.
     */
    private GenericUrlEnum enumFamiliares;

    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private static EjbSysmanUtilRemote ejbSysmanUtil = new EjbSysmanUtil();

    /**
     * Inicializa los enumerados y URLs para operar con las tablas de
     * autoservicio.
     */
    public RegistroAutoservicio() {
        enumSolicitudes = GenericUrlEnum.AUT_SOLICITUDES;
        urlCreacionSolicitudes = enumSolicitudes.getCreateKey() != null
            ? UrlServiceUtil.getUrlBeanById(enumSolicitudes.getCreateKey())
            : null;
        enumPersonal = GenericUrlEnum.AUT_PERSONAL;
        urlCreacionPersonal = enumPersonal.getCreateKey() != null
            ? UrlServiceUtil.getUrlBeanById(enumPersonal.getCreateKey())
            : null;
        enumFamiliares = GenericUrlEnum.AUT_FAMILIARES;
        urlCreacionFamiliares = enumFamiliares.getCreateKey() != null
            ? UrlServiceUtil.getUrlBeanById(enumFamiliares.getCreateKey())
            : null;
    }

    /**
     * Registro de solicitudes y consultas de autoservicio.
     * 
     * @param campos
     * @return map con la llave primaria del registro insertado
     * @throws NegocioExcepcion
     */
    public Map<String, Object> registrarSolicitud(Map<String, Object> campos)
                    throws NegocioExcepcion {
        Map<String, Object> parameters = new HashMap<>();
        String compania = SysmanFunciones.toString(
                        campos.get(GeneralParameterEnum.COMPANIA.getName()));
        parameters.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parameters.put(FECHA_SOLICITUD, new Date());
        parameters.put(CLASE_SOLICITUD, campos.get(CLASE_SOLICITUD));
        parameters.put(TIPO_PERMISO, campos.get(TIPO_PERMISO));
        parameters.put(GeneralParameterEnum.FECHA_INICIO.getName(), campos
                        .get(GeneralParameterEnum.FECHA_INICIO.getName()));
        parameters.put(HORA_INICIO, campos.get(HORA_INICIO));
        parameters.put(GeneralParameterEnum.FECHA_FINAL.getName(),
                        campos.get(GeneralParameterEnum.FECHA_FINAL
                                        .getName()));
        parameters.put(HORA_FINAL, campos.get(HORA_FINAL));
        parameters.put(CEDULA, campos.get(CEDULA));
        parameters.put(GeneralParameterEnum.MES.getName(),
                        campos.get(GeneralParameterEnum.MES.getName()));
        parameters.put(GeneralParameterEnum.OBSERVACIONES.getName(), campos
                        .get(GeneralParameterEnum.OBSERVACIONES.getName()));
        parameters.put(GeneralParameterEnum.PERIODO.getName(),
                        campos.get(GeneralParameterEnum.PERIODO.getName()));
        parameters.put(ID_DEMPLEADO, campos.get(ID_DEMPLEADO));
        parameters.put(GeneralParameterEnum.SUCURSAL.getName(),
                        campos.get(GeneralParameterEnum.SUCURSAL.getName()));
        parameters.put(GeneralParameterEnum.CARGO.getName(),
                        campos.get(GeneraArchivoEnum.ID_DE_CARGO.getValue()));
        parameters.put(GeneralParameterEnum.ANO.getName(),
                        campos.get(GeneralParameterEnum.ANO.getName()));
        parameters.put(GeneralParameterEnum.ESTADO.getName(),
                        ESTADO_EN_TRAMITE);
        parameters.put(USUARIO_SISTEMA, campos.get(USUARIO_SISTEMA));

        String cedulaJefeDirecto = SysmanFunciones
                        .toString(campos.get(JEFE_DIRECTO));
        String sucursalJefeDirecto = SysmanFunciones
                        .toString(campos.get(SUCURSAL_JEFE_DIRECTO));

        parameters.put(GeneralParameterEnum.DESTINO.getName(),
                        cedulaJefeDirecto == null
                            ? SysmanConstantes.CONS_TERCERO
                            : cedulaJefeDirecto);
        parameters.put(SUCURSAL_DESTINO, sucursalJefeDirecto == null
            ? SysmanConstantes.CONS_SUCURSAL
            : sucursalJefeDirecto);

        parameters.put(GeneralParameterEnum.CREATED_BY.getName(),
                        campos.get(USUARIO_SISTEMA));
        parameters.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

        Long consecutivo;
        try {
            consecutivo = generarConsecutivo(enumSolicitudes.getTable(),
                            "COMPANIA=''" + compania + "''");
            parameters.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            consecutivo);

            Parameter parameter = new Parameter();
            parameter.setFields(parameters);
            return requestManager.save(urlCreacionSolicitudes.getUrl(),
                            urlCreacionSolicitudes.getMetodo(), parameter);
        }
        catch (SystemException e) {
            LOG.error("Error al registrar la solicitud de Autoservicio. ->> mensaje ->> {} / causa ->> {}",
                            e.getMessage(),
                            e.getCause());
            throw new NegocioExcepcion(e);
        }
    }

    /**
     * Realiza el llamado a la funcion
     * PCK_SYSMAN_UTL.FC_GENCONSECUTIVO para obtener el consecutivo de
     * la tabla especificada
     * 
     * @tabla nombre de la tabla
     * @param criterio
     * filtro de consulta
     *
     * @return Valor a ser asignado como consecutivo en la tabla
     * AUT_SOLICITUDES
     * @throws SystemException
     */
    private long generarConsecutivo(String tabla, String criterio)
                    throws SystemException {
        return ejbSysmanUtil.generarConsecutivoConValorInicial(tabla, criterio,
                        "CONSECUTIVO", "1");
    }

    /**
     * Crea el registro de para actualizaci&oacute;n de datos
     * personales.
     * 
     * @param parametros
     * @return map con la llave primaria del registro insertado
     * @throws NegocioExcepcion
     */
    public Map<String, Object> postAutPersonal(Map<String, Object> parametros)
                    throws NegocioExcepcion {
        Map<String, Object> parameters = parametros;
        parameters.put(GeneralParameterEnum.ESTADO.getName(),
                        ESTADO_EN_TRAMITE);
        parameters.put("ENVIADO", -1);

        String compania = SysmanFunciones.toString(parametros
                        .get(GeneralParameterEnum.COMPANIA.getName()));
        int idEmpleado = (Integer) parametros
                        .get(GeneralParameterEnum.ID_DE_EMPLEADO.getName());
        String criterio = "COMPANIA = ''" + compania
            + "'' AND ID_DE_EMPLEADO = ''" + idEmpleado + "''";
        try {
            long consecutivo = generarConsecutivo(enumPersonal.getTable(),
                            criterio);
            parameters.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            consecutivo);

            Parameter parameter = new Parameter();
            parameter.setFields(parameters);
            return requestManager.save(urlCreacionPersonal.getUrl(),
                            urlCreacionPersonal.getMetodo(), parameter);
        }
        catch (SystemException e) {
            LOG.error("Error al registrar la solicitud de actualización de datos personales."
                + " ->> mensaje ->> {} / causa ->> {}",
                            e.getMessage(),
                            e.getCause());
            throw new NegocioExcepcion(e);
        }
    }

    /**
     * Crea el registro de actualizaci&oacute;n de datos de
     * familiares.
     * 
     * @param parametros
     * @return map con la llave primaria del registro insertado
     * @throws NegocioExcepcion
     */
    public Map<String, Object> postAutFamiliares(Map<String, Object> parametros)
                    throws NegocioExcepcion {
        Map<String, Object> parameters = parametros;
        parameters.put(GeneralParameterEnum.ESTADO.getName(),
                        ESTADO_EN_TRAMITE);

        String compania = SysmanFunciones.toString(parametros
                        .get(GeneralParameterEnum.COMPANIA.getName()));
        String cedulaEmpleado = SysmanFunciones
                        .toString(parametros.get("DCTO_EMPLEADO"));
        String sucursalEmpleado = SysmanFunciones
                        .toString(parametros.get("SUCURSAL_EMPLEADO"));
        String criterio = "COMPANIA = ''" + compania
            + "'' AND DCTO_EMPLEADO = ''" + cedulaEmpleado + "''"
            + " AND SUCURSAL_EMPLEADO = ''" + sucursalEmpleado + "''";

        try {
            long consecutivo = generarConsecutivo(enumFamiliares.getTable(),
                            criterio);
            parameters.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            consecutivo);

            Parameter parameter = new Parameter();
            parameter.setFields(parameters);
            return requestManager.save(urlCreacionFamiliares.getUrl(),
                            urlCreacionFamiliares.getMetodo(), parameter);
        }
        catch (SystemException e) {
            LOG.error("Error al registrar la solicitud de actualización de datos familiares."
                + " ->> mensaje ->> {} / causa ->> {}",
                            e.getMessage(),
                            e.getCause());
            throw new NegocioExcepcion(e);
        }
    }
}