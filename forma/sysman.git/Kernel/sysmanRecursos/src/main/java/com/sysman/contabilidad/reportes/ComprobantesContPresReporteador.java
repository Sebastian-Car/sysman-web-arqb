/*-
 * ReportesContablesReporteador.java
 *
 * 1.0
 *
 * 8/11/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad.reportes;

import com.sysman.controladores.SessionUtil;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.logica.DatosSesion;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 8/11/2017
 * @author spina
 *
 */
public class ComprobantesContPresReporteador {

    /**
     * Constante a nivel de clase que aloja el nombre de la compania
     * desde la que el usuario inicio sesion.
     */
    private String nombreCompania;
    /**
     * codigo de la compa&ntilde;ia
     */
    private String compania;
    private String modulo;
    private StreamedContent archivoDescarga;
    private String cFirma3;
    private ResourceBundle idioma;
    private List<String> mensajesParametros;
    private String cFormato = "formato";
    private String cFormatoNombre = "formatoNombre";
    private String cInforme = "informe";
    /**
     * Indica si la generaci&oacute;n se realiza con datos de sessi&oacute;n o de json
     */
    private boolean conDatosSession = true;
    /**
     * guardar el c&oacute;digo del usuario
     */
    private String codigoUsuario;
    /**
     * guarda el nit de la compa&ntilde;ia de la sessi&oacute;n o el json
     */
    private String nitCompania;
    /**
     * guarda la direci&oacute;n de la compa&ntilde;ia de la sessi&oacute;n o el json
     */
    private String direccionCompania;
    /**
     * guarda la ciudad de la compa&ntilde;ia
     */
    private String ciudadCompania;
    /**
      * guarda el departamento de la compa&ntilde;ia
     */
    private String departamentoCompania;
    /**
     * guarda el primer nombre del usuario de la sessi&oacute;n o el json
     */
    private String nombreCompleto;
    
    /**
     * conjunto de datos relacionados con la sesi&oacute;n
     */
    private DatosSesion datosSesion;

    private final Log logger = LogFactory.getLog(this.getClass());
    private EjbSysmanUtilRemote ejbSysmanUtil;

    public ComprobantesContPresReporteador(EjbSysmanUtilRemote ejbSysmanUtil) {
        this.ejbSysmanUtil = ejbSysmanUtil;
        compania = SessionUtil.getCompania();
        nombreCompania = SessionUtil.getCompaniaIngreso()
                        .getNombre();
        nitCompania = SessionUtil.getCompaniaIngreso().getNit();
        direccionCompania = SessionUtil.getCompaniaIngreso().getDireccion();
        ciudadCompania =SessionUtil.getCompaniaIngreso().getCiudad();
        departamentoCompania =SessionUtil.getCompaniaIngreso().getDepartamento();
        codigoUsuario = SessionUtil.getUser().getCodigo();
        nombreCompleto = SysmanFunciones.concatenar(
                        SessionUtil.getUser().getNombre1(), " ",
                        SessionUtil.getUser().getNombre2(), " ",
                        SessionUtil.getUser().getApellido1(), " ",
                        SessionUtil.getUser().getApellido2());
        
        if ("98".equals(SessionUtil.getModulo())) {
            conDatosSession = true;
            modulo = "1";
            datosSesion = new DatosSesion();
            datosSesion.setCompania(compania);
            datosSesion.setCompaniaIngreso(SessionUtil.getCompaniaIngreso());
            datosSesion.setModulo(modulo);
            datosSesion.setUser(SessionUtil.getUser());
            datosSesion.setExcelPlano(SessionUtil.getExcePlano());
            datosSesion.setUsuario(codigoUsuario);
        } else {
            conDatosSession = false;
            modulo = SessionUtil.getModulo();
        }
        cFirma3 = "firmaTres";
        idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
        mensajesParametros = new ArrayList<>();
    }
    
    /**
     * Constructor para los comprobantes generados por sysmanAPI
     * @param ejbSysmanUtil
     */
    public ComprobantesContPresReporteador(EjbSysmanUtilRemote ejbSysmanUtil, DatosSesion session) {
        this.ejbSysmanUtil = ejbSysmanUtil;
        this.datosSesion = session;
        modulo = session.getModulo();
        conDatosSession = true;
        
        compania = session.getCompania();
        nombreCompania= session.getCompaniaIngreso().getNombre();
        nitCompania = session.getCompaniaIngreso().getNit();
        direccionCompania = session.getCompaniaIngreso().getDireccion();
        ciudadCompania =session.getCompaniaIngreso().getCiudad();
        departamentoCompania =session.getCompaniaIngreso().getDepartamento();
        codigoUsuario = session.getUser().getCodigo();        
        nombreCompleto = SysmanFunciones.concatenar(
                        session.getUser().getNombre1(), " ",
                        session.getUser().getNombre2(), " ",
                        session.getUser().getApellido1(), " ",
                        session.getUser().getApellido2());
        
        cFirma3 = "firmaTres";
        idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
        mensajesParametros = new ArrayList<>();
    }
    
    /**
     * SE genera por separado para que sirva para descargar los datos desde la forma o desde un servicio
     * @param valores
     * @param parametros
     * @param reemplazar
     * @throws com.sysman.util.SysmanException 
     */
    private void PreparaInforme(Map<String, Object> valores,
        Map<String, Object> parametros, Map<String, Object> reemplazar) throws com.sysman.util.SysmanException {
        archivoDescarga = null;
                // modulo actual contabilidad o presupuesto
        reemplazar.put("modulo", modulo);
        reemplazar.put("nombreCompania", SysmanFunciones.concatenar("'",
                        nombreCompania, "'"));

        String nombreAprobo;
        try {
            nombreAprobo = cargaParametroConAlerta(
                            compania,
                            "NOMBRE APROBO EN FORMATO CDC");


            String firmasCDC = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "FIRMAS CDC CAJA DE RETIRO",
                                            modulo,
                                            new Date(), true),
                            "NO");

            reemplazar.put(cFirma3, idioma.getString("SI".equals(nombreAprobo)
                            ? "TB_TB1459"
                                : "TB_TB1460"));

            if ("SI".equals(firmasCDC)) {
                reemplazar.put("firmaUno", idioma.getString("TG_PREPARO2"));
                reemplazar.put("firmaDos", idioma.getString("TG_REVISO3"));
                reemplazar.put(cFirma3, idioma.getString("TB_TB1459"));
            } else {
                reemplazar.put("firmaUno", idioma.getString("TG_ELABORO3"));
                reemplazar.put("firmaDos", idioma.getString("TB_TB1461"));
            }

            // reporte 1505

            if ("SI".equals(ejbSysmanUtil.consultarParametro(
                            compania,
                            "PERMITE RESTRINGIR CODIGOS DE RETENCIONES EN EGRESO",
                            modulo,
                            new Date(), true))) {

                String codigos = ejbSysmanUtil.consultarParametro(
                                compania,
                                "CODIGOS DE RETENCIONES NO REPORTADOS EN EGRESO",
                                modulo,
                                new Date(), true);
                reemplazar.put("condicion",
                                " AND INSTR(PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS('"
                                                + codigos
                                                + "'), TIPORETENCION.CODIGO , 1) < 0 ");
            } else {
                reemplazar.put("condicion", "");
            }

            // Modulo de contabilidad
            agregarParametrosCont(parametros);

            // Modulo de Presupuesto
            agregarParametrosPres(parametros);

            // parametro de contabilidad
            parametros.put("PR_CODIGO_FORMATO_CONSIGNACION",
                            cargaParametroConAlerta(compania,
                                            "CODIGO FORMATO CONSIGNACION"));

            // parametros comunes en contabilidad y presupuesto
            String manejaFormato = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA FORMATO SIN CUADROS EN COM",
                                            modulo, new Date(), true), "NO");

            String firmasCom = SysmanFunciones.nvlStr(ejbSysmanUtil
                            .consultarParametro(compania, "FIRMAS EN COM",
                                            modulo, new Date(), true),
                            "NO");
            String verAuxiliar = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "VER AUXILIAR EN FORMATO COM_SO",
                                            modulo, new Date(), true), "NO");
            String mostrarCargo = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MOSTRAR CARGO TESORERO EN FORMATO COM_SO",
                                            modulo, new Date(), true), "NO");
            String cargoOrdenador = SysmanFunciones.nvlStr(ejbSysmanUtil
                            .consultarParametro(compania, "CARGO ORDENADOR",
                                            modulo, new Date(), true),
                            "CARGO ORDENADOR");
            String cargoSecretario = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO SECRETARIO", modulo,
                                            new Date(), true),
                            "CARGO SECRETARIO");
            
            String nombreGerente = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "NOMBRE GERENTE", modulo,
                                    new Date(), true),
                    "NOMBRE GERENTE");
            
            /* Inicio Daniel Niño */
            String nombreContador = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE CONTADOR", modulo,
                                            new Date(), true),
                            "NOMBRE CONTADOR");
            String cargoContador = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO CONTADOR", modulo,
                                            new Date(), true),
                            "CARGO CONTADOR");
            String nombreAdminFinanzas = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE ADMINISTRADOR Y FINANZAS",
                                            modulo,
                                            new Date(), true),
                            "NOMBRE ADMINISTRADOR Y FINANZAS");
            String cargoAdminFinanzas = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO ADMINISTRADOR Y FINANZAS",
                                            modulo,
                                            new Date(), true),
                            "CARGO ADMINISTRADOR Y FINANZAS");
            String ordenadorPago = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "ORDENADOR DEL PAGO", modulo,
                                            new Date(), true),
                            "ORDENADOR DEL PAGO");
            String firmaOrdenadorPago = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA2 EN ORDEN DE PAGO", modulo,
                                            new Date(), true),
                            "FIRMA2 EN ORDEN DE PAGO");
            /* Fin Daniel Niño */
            
            /*7741299 JDSALCEDO*/
            
            String firmaOrdenadorPago1 = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "FIRMA1_EN_ORDEN_DE_PAGO", modulo,
                                    new Date(), true),
                    "FIRMA1_EN_ORDEN_DE_PAGO");
            
            String firmaOrdenadorPago3 = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "FIRMA3_EN_ORDEN_DE_PAGO", modulo,
                                    new Date(), true),
                    "FIRMA3_EN_ORDEN_DE_PAGO");
            
            String firmaOrdenadorPago4 = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "FIRMA4_EN_ORDEN_DE_PAGO", modulo,
                                    new Date(), true),
                    "FIRMA4_EN_ORDEN_DE_PAGO");
            
            String firmaOrdenadorPago5 = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "FIRMA5_EN_ORDEN_DE_PAGO", modulo,
                                    new Date(), true),
                    "FIRMA5_EN_ORDEN_DE_PAGO");
            /*Fin 7741299 JDSAL*/
            
            String accionUnoOrden = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "ACCION1.1 EN ORDEN DE PAGO",
                                            modulo, new Date(), true),
                            "ACCION1.1 EN ORDEN DE PAGO");
            String accionOrdenPago = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "ACCION EN ORDEN DE PAGO", modulo,
                                            new Date(), true),
                            "ACCION EN ORDEN DE PAGO");
            String accionUnoOrdenPago = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "ACCION1 EN ORDEN DE PAGO", modulo,
                                            new Date(), true),
                            "ACCION1 EN ORDEN DE PAGO");
            String nombreOrdenador = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE ORDENADOR", modulo,
                                            new Date(), true),
                            "NOMBRE ORDENADOR");
            String nombreTesorero = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE TESORERO", modulo,
                                            new Date(), true),
                            "NOMBRE TESORERO");
            /* Agrego Miguel Venegas */
            String numeroResolucion = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NÚMERO RESOLUCIÓN EN FORMATO FAC_EDB",
                                            modulo,
                                            new Date(), true),
                            "NÚMERO RESOLUCIÓN EN FORMATO FAC_EDB");

            String nombre1 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE1 EN FAC_EDB",
                                            modulo,
                                            new Date(), true),
                            "NOMBRE1 EN FAC_EDB");

            String fechaResolucionFAC = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "FECHA RESOLUCIÓN EN FORMATO FAC_EDB",
                                            modulo,
                                            new Date(), true),
                            "FECHA RESOLUCIÓN EN FORMATO FAC_EDB");

            String descripcionPie = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "DESCRIPCION EN PIE FORMATO FAC_EDB",
                                            modulo,
                                            new Date(), true),
                            "DESCRIPCION EN PIE FORMATO FAC_EDB");

            /* Fin Miguel Venegas */
            
            /*Inicio Diana Castiblanco*/
            String leyenda2 = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "LEYENDA2 EN ORDEN DE PAGO",
                                    modulo,
                                    new Date(), true),
                    "LEYENDA2 EN ORDEN DE PAGO");
            
            String leyenda1 = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "LEYENDA1 EN ORDEN DE PAGO",
                                    modulo,
                                    new Date(), true),
                    "LEYENDA1 EN ORDEN DE PAGO");
            
            String leyenda3 = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "LEYENDA3 EN ORDEN DE PAGO",
                                    modulo,
                                    new Date(), true),
                    "LEYENDA3 EN ORDEN DE PAGO");
            
            String comprobanteC = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "CARGO 1 COMPROBANTE CONTABLE",
                                    modulo,
                                    new Date(), true),
                    "CARGO 1 COMPROBANTE CONTABLE");     
            /*Fin Diana Castiblanco*/
            
            String leyendaEncabezado = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "LEYENDA ENCABEZADO DE PAGINA FORMATO REG_DU",
                                    modulo,
                                    new Date(), true),
                    "LEYENDA ENCABEZADO DE PAGINA FORMATO REG_DU");

            boolean manejaFirmasEspecialesEgr = "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA FIRMAS ESPECIALES EN EGR_AZ",
                                            modulo,
                                            new Date(),
                                            true),
                                            "NO"));
            parametros.put("PR_MANEJA_FIRMAS_ESPECIALES",
                            manejaFirmasEspecialesEgr);

            asignarParametros(parametros);
            // Parametros 001469COMSO
            parametros.put("PR_FIRMASCOM", firmasCom);
            parametros.put("PR_AUXILIARENCOM", verAuxiliar);
            parametros.put("PR_MOSTRARTESORERO", mostrarCargo);
            parametros.put("PR_MANEJAFORMATO", manejaFormato);
            parametros.put("PR_CARGO_ORDENADOR", cargoOrdenador);
            parametros.put("PR_CARGO_SECRETARIO", cargoSecretario);
            /* Inicio Daniel Niño */
            parametros.put("PR_CARGO_CONTADOR", cargoContador);
            parametros.put("PR_NOMBRE_CONTADOR", nombreContador);
            parametros.put("PR_NOMBRE_ADMINISTRADOR_Y_FINANZAS",
                            nombreAdminFinanzas);
            parametros.put("PR_CARGO_ADMINISTRADOR_Y_FINANZAS",
                            cargoAdminFinanzas);
            parametros.put("PR_ORDENADOR_DEL_PAGO", ordenadorPago);
            parametros.put("PR_FIRMA2_EN_ORDEN_DE_PAGO",
                            firmaOrdenadorPago);
            /* Fin Daniel Niño */
            
            /*Inicio 7741299 JDSAL*/
            parametros.put("PR_FIRMA1_EN_ORDEN_DE_PAGO",
                    firmaOrdenadorPago1);
            parametros.put("PR_FIRMA3_EN_ORDEN_DE_PAGO",
                    firmaOrdenadorPago3);
            parametros.put("PR_FIRMA4_EN_ORDEN_DE_PAGO",
                    firmaOrdenadorPago4);
            parametros.put("PR_FIRMA5_EN_ORDEN_DE_PAGO",
                    firmaOrdenadorPago5);
            /*Fin 7741299 JDSAL*/
            parametros.put("PR_ACCION1.1_EN_ORDEN_DE_PAGO", accionUnoOrden);
            parametros.put("PR_ACCION_EN_ORDEN_DE_PAGO", accionOrdenPago);

            parametros.put("PR_ACCION1_EN_ORDEN_DE_PAGO", accionUnoOrdenPago);
            parametros.put("PR_NOMBRE_ORDENADOR", nombreOrdenador);
            parametros.put("PR_NOMBRE_TESORERO", nombreTesorero);
            parametros.put("PR_NOMBRE_GERENTE", nombreGerente);
            parametros.put("PR_GETUSER", codigoUsuario);
            parametros.put("PR_NÚMERO_RESOLUCIÓN_EN_FORMATO_FAC_EDB",
                            numeroResolucion);
            parametros.put("PR_NOMBRE1_EN_FAC_EDB",
                            nombre1);
            parametros.put("PR_FECHA_RESOLUCIÓN_EN_FORMATO_FAC_EDB",
                            fechaResolucionFAC);
            parametros.put("PR_COMPANIA", nombreCompania);
            parametros.put("PR_DESCRIPCION_EN_PIE_FORMATO_FAC_EDB",
                            descripcionPie);
            parametros.put("PR_LEYENDA1_EN_ORDEN_DE_PAGO",
            				leyenda1);
            parametros.put("PR_LEYENDA2_EN_ORDEN_DE_PAGO",
    						leyenda2);
            parametros.put("PR_LEYENDA3_EN_ORDEN_DE_PAGO",
    						leyenda3);
            
            parametros.put("PR_CARGO_1_COMPROBANTE_CONTABLE",
					comprobanteC);
            
            String observacionFormatos = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "OBSERVACION EN FORMATOS DIS_CB Y CB_REG",
                                    modulo,
                                    new Date(), true),
                    "OBSERVACION EN FORMATOS DIS_CB Y CB_REG");
            
            String cargoDivision = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "CARGO DIVISION FINANCIERA Y DE PRESUPUESTO",
                                    modulo,
                                    new Date(), true),
                    "CARGO DIVISION FINANCIERA Y DE PRESUPUESTO");
            
            String seccionPrincipal = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "PN SECCION PRINCIPAL",
                                    modulo,
                                    new Date(), true),
                    "PN SECCION PRINCIPAL");
            
            String unidadEjecutora = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "PN UNIDAD EJECUTORA",
                                    modulo,
                                    new Date(), true),
                    "PN UNIDAD EJECUTORA");
            
            String decretoComp = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "DECRETO DEL COMPROMISO",
                                    modulo,
                                    new Date(), true),
                    "DECRETO DEL COMPROMISO");
            
            String elaboroPresupuesto = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "ELABORO EN PRESUPUESTO",
                                    modulo,
                                    new Date(), true),
                    "ELABORO EN PRESUPUESTO");
            String profesionalDefensa = SysmanFunciones.nvlStr
            		(ejbSysmanUtil.consultarParametro(compania, 
            				"PROFEISONAL DE DEFENSA", 
            				modulo, new Date(), true), "PROFESIONAL DE DEFENSA"); 
            
            String logoimagen = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "IMAGEN LOGO SOGAMOSO",
                                    modulo,
                                    new Date(), false),
                    "IMAGEN LOGO SOGAMOSO");
            
            String escudoimagen = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "IMAGEN ESCUDO SOGAMOSO",
                                    modulo,
                                    new Date(), false),
                    "IMAGEN ESCUDO SOGAMOSO");
            //INI_1397_PRESUPUESTO
            String cargoElaboroPresupuesto = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "CARGO ELABORO EN PRESUPUESTO",
                                    modulo,
                                    new Date(), true),
                    "CARGO ELABORO EN PRESUPUESTO");
            
            String aproboPresupuesto = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "APROBO EN PRESUPUESTO",
                                    modulo,
                                    new Date(), true),
                    "APROBO EN PRESUPUESTO");
            
            String cargoAproboPresupuesto = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "CARGO APROBO EN PRESUPUESTO",
                                    modulo,
                                    new Date(), true),
                    "CARGO APROBO EN PRESUPUESTO");
            
            String revisoPresupuesto = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "REVISO EN PRESUPUESTO",
                                    modulo,
                                    new Date(), true),
                    "REVISO EN PRESUPUESTO");
            
            String cargoRevisoPresupuesto = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "CARGO REVISO EN PRESUPUESTO",
                                    modulo,
                                    new Date(), true),
                    "CARGO REVISO EN PRESUPUESTO");
            
            //FIN_1397_PRESUPUESTO
            
            //INI dcastiblanco
            
           parametros.put("PR_NOMBRE_QUIEN_APROBO_DOCUMENTO",
					ejbSysmanUtil.consultarParametro(compania, "NOMBRE QUIEN APROBO DOCUMENTO", modulo, new Date(), true));
			
			parametros.put("PR_NOMBRE_QUIEN_REVISO_DOCUMENTO",
					ejbSysmanUtil.consultarParametro(compania, "NOMBRE QUIEN REVISO DOCUMENTO", modulo, new Date(), true));

			//FIN dcastiblanco
			
            parametros.put("PR_OBSERVACION_EN_FORMATOS_DIS_CB_Y_CB_REG",observacionFormatos);
            parametros.put("PR_CARGO_DIVISION_FINANCIERA_Y_DE_PRESUPUESTO",cargoDivision);
            parametros.put("PR_SECCION_PRINCIPAL",seccionPrincipal);
            parametros.put("PR_UNIDAD_EJECUTORA",unidadEjecutora);
            parametros.put("PR_DECRETO_DEL_COMPROMISO",decretoComp);
            parametros.put("PR_ELABORO_EN_PRESUPUESTO",elaboroPresupuesto);
            parametros.put("PR_IMAGEN_LOGO_SOGAMOSO",logoimagen);
            parametros.put("PR_IMAGEN_ESCUDO_SOGAMOSO",escudoimagen);
            parametros.put("PR_PROFESIONAL_DEFENSA", profesionalDefensa);
            parametros.put("PR_LEYENDA_ENCABEZADO_PAGINA_FORMATO_REG_DU", leyendaEncabezado);
            //INI_1397_PRESUPUESTO
            parametros.put("PR_CARGO_ELABORO_EN_PRESUPUESTO",cargoElaboroPresupuesto);
            parametros.put("PR_APROBO_EN_PRESUPUESTO",aproboPresupuesto);
            parametros.put("PR_CARGO_APROBO_EN_PRESUPUESTO",cargoAproboPresupuesto);
            parametros.put("PR_REVISO_EN_PRESUPUESTO",revisoPresupuesto);
            parametros.put("PR_CARGO_REVISO_EN_PRESUPUESTO",cargoRevisoPresupuesto);
            //FIN_1397_PRESUPUESTO

            String informeFinal=valores.get(cInforme).toString();
            if ("001495EGRSOCNT".equals(valores.get(cInforme).toString())
                            || "001496INGSOCNT".equals(valores.get(cInforme).toString())
                            || "001499EGRUPCCNT".equals(valores.get(cInforme).toString())
                            || "002542INGCNT_TELEPA".equals(valores.get(cInforme).toString())) {
                informeFinal = "001495EGRSOCNT";
                
                Reporteador.resuelveConsulta("002542INGCNT_TELEPA".equals(valores.get(cInforme).toString())?"002542INGCNT_TELEPA":"001495EGRSOCNT",
                                Integer.valueOf(modulo),
                                reemplazar, parametros);
            }
            if ("001519ADIUPC".equals(valores.get(cInforme).toString())
                            || "001520REOUPC".equals(valores.get(cInforme).toString())
                            || "001521REGUPC".equals(valores.get(cInforme).toString())) {
                parametros.put("PR_VISIBLECUADROS",
                                FORMATOS.PDF.equals(valores.get(cFormato))
                                ? true
                                    : false);
                informeFinal = "001519ADIUPC";
            }
            if(conDatosSession) {
                Reporteador.resuelveConsulta(informeFinal,
                                Integer.valueOf(modulo),
                                reemplazar, parametros,datosSesion);
            }else {
                Reporteador.resuelveConsulta(informeFinal,
                                Integer.valueOf(modulo),
                                reemplazar, parametros);
            }
        }
        catch (NamingException | SQLException | SystemException | NumberFormatException | SysmanException e) {
            String mensaje =e.getMessage();
            String causa = e.getCause().toString();
            logger.error(e.getMessage(), e);
            com.sysman.util.SysmanException error = new com.sysman.util.SysmanException(mensaje);
            error.initCause(new Exception(causa));
            throw error;
        }
    }

    /**
     * Prepara datos para generar los reportes de comprobantes contables y presupuestales
     * 
     * @param valores
     * @param parametros
     * @param reemplazar
     * @return archivo en formato StreamedContent
     */
    public StreamedContent generarInforme(Map<String, Object> valores,
        Map<String, Object> parametros, Map<String, Object> reemplazar) {
        try {
            PreparaInforme(valores, parametros, reemplazar);
            if (!(boolean) valores.get("lote")) {
                if ("001356FAC".equals(valores.get(cInforme).toString())) {
                    parametros.put("PR_FECHAS",
                                    valores.get("fechas").toString());
                }
            } else {

                if (parametroTxt()) {
                    ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];
                    salidas[0] = JsfUtil.serializarPlano(
                                    valores.get("strPlano").toString());
                    salidas[1] = JsfUtil.serializarReporte(
                                    valores.get(cFormatoNombre).toString(),
                                    parametros,
                                    ConectorPool.ESQUEMA_SYSMAN,
                                    (FORMATOS) valores.get(cFormato));

                    String[] nombresArchivos = new String[2];
                    nombresArchivos[0] = valores.get("tituloPlano").toString();

                    nombresArchivos[1] = (FORMATOS) valores
                                    .get(cFormato) == FORMATOS.PDF
                                    ? SysmanFunciones.concatenar(valores
                                                    .get(cFormatoNombre)
                                                    .toString(), ".pdf")
                                        : SysmanFunciones.concatenar(valores
                                                        .get(cFormatoNombre)
                                                        .toString(), ".xlsx");

                                    return JsfUtil.exportarComprimidoGeneralStreamed(
                                                    salidas, nombresArchivos);
                }
            }
            if (conDatosSession) {
                archivoDescarga = JsfUtil.exportarStreamed(
                                valores.get(cInforme).toString(),
                                parametros,
                                ConectorPool.ESQUEMA_SYSMAN,
                                (FORMATOS) valores.get(cFormato), datosSesion);
            } else {
            	if(valores.get(cInforme).toString().equals("002592COM_CREMIL")) {
            		parametros.replace("PR_CARGO_ORDENADOR", valores.get("ordenador").toString().toUpperCase());
            	}
            	
                archivoDescarga = JsfUtil.exportarStreamed(
                                valores.get(cInforme).toString(),
                                parametros,
                                ConectorPool.ESQUEMA_SYSMAN,
                                (FORMATOS) valores.get(cFormato));
            }
        }
        catch (JRException | IOException | SysmanException | SQLException | DRException | com.sysman.util.SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return archivoDescarga;
    }
    
    
    /**
     * Generar los reportes de comprobantes contables
     * desde el sysmanAPI  
     *  
     * @param valores
     * @param parametros
     * @param reemplazar
     * @return archivo en formato generarInformeBase64
     * @throws com.sysman.util.SysmanException 
     */
    public  byte[] generarInformeBase64(Map<String, Object> valores,
        Map<String, Object> parametros, Map<String, Object> reemplazar) throws com.sysman.util.SysmanException {
        byte[] base64 = null;
        try {
            PreparaInforme(valores, parametros, reemplazar);
            if ("001356FAC".equals(valores.get(cInforme).toString())) {
                parametros.put("PR_FECHAS",
                                valores.get("fechas").toString());
            }
            if (conDatosSession) {
                base64 = JsfUtil.serializarReporteBase64(valores.get(cInforme).toString(),
                                parametros,
                                ConectorPool.ESQUEMA_SYSMAN,
                                (FORMATOS) valores.get(cFormato), datosSesion);           
            }
        }
        catch (JRException | IOException | SysmanException | com.sysman.util.SysmanException e) {
            String mensaje =e.getMessage();
            String causa = e.getCause().toString();
            logger.error(e.getMessage(), e);
            com.sysman.util.SysmanException error = new com.sysman.util.SysmanException(mensaje);
            error.initCause(new Exception(causa));
            throw error;
        }
        return base64;
    }
    
    private void agregarParametrosPres(Map<String, Object> parametros)
                    throws SystemException {
        String parametro;

        // individual pptal
        parametro = SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "CARGO ENCARGADO DE TESORERIA", modulo,
                                        new Date(),
                                        true),
                                        "");

        parametros.put("PR_CARGO_ENCARGADO_DE_TESORERIA", parametro);

        parametro = SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "NOMBRE ENCARGADO DE TESORERIA", modulo,
                                        new Date(), false),
                                        "");

        parametros.put("PR_NOMBRE_ENCARGADO_DE_TESORERIA", parametro);

        parametro = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(compania,
                                        "TITULO VALOR EN DISP. PAC", modulo,
                                        new Date(), true),
                        "");

        parametros.put("PR_TITULO_VALOR_EN_DISP._PAC", parametro);

        parametro = SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "TITULO NUEVO SALDO EN DISP. PAC",
                                        modulo, new Date(), true),
                                        "");

        parametros.put("PR_TITULO_NUEVO_SALDO_EN_DISP._PAC", parametro);

        parametro = SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "TITULO SALDO ANTERIOR EN DISP. PAC",
                                        modulo, new Date(), true),
                                        "");

        parametros.put("PR_TITULO_SALDO_ANTERIOR_EN_DISP._PAC", parametro);

        // en lotes
        parametro = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(
                                        compania,
                                        "CARGO PRESUPUESTO",
                                        modulo,
                                        new Date(), true),
                        " ");

        parametros.put("PR_CARGO_PRESUPUESTO",
                        SysmanFunciones.nvl(parametro, " "));

        parametro = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(
                                        compania,
                                        "NOMBRE DE JEFE DE PRESUPUESTO",
                                        modulo,
                                        new Date(), true),
                        " ");

        parametros.put("PR_NOMBRE_DE_JEFE_DE_PRESUPUESTO", parametro);

        parametro = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(
                                        compania,
                                        "OCULTAR DATOS ELABORADOR EN CDP",
                                        modulo,
                                        new Date(), true),
                        "NO");

        parametros.put("PR_ELABORO",
                        "NO".equals(parametro)
                        ? idioma.getString("TG_ELABORO3")
                            : " ");

        parametros.put("PR_NOMBREUSUARIO", "NO".equals(parametro)
                        ? codigoUsuario
                            : " ");

        parametro = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(
                                        compania,
                                        "MOSTRAR CONCEPTO DEL REGISTRO AFECTADO",
                                        modulo,
                                        new Date(), true),
                        "NO");

        parametros.put("PR_MOSTRARCONCEPTO", "SI".equals(parametro));

        parametro = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(
                                        compania,
                                        "MANEJA NOMBRE EN MOVIMIENTOS PRESUPUESTALES",
                                        modulo,
                                        new Date(), true),
                        "NO");

        parametros.put("PR_MANEJANOMBRE", "SI".equals(parametro));

        parametro = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(
                                        compania,
                                        "MOSTRAR FIRMA DE SUBDIRECTORA EN FORMATO CDP",
                                        modulo,
                                        new Date(), true),
                        "NO");

        parametros.put("PR_MOSTRARFIRMA", "SI".equals(parametro));

        parametro = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(
                                        compania,
                                        "TEXTO DE VENCIMIENTO EN FORMATO CDP",
                                        modulo,
                                        new Date(), true),
                        " ");

        parametros.put("PR_TEXTO_DE_VENCIMIENTO_EN_FORMATO_CDP", parametro);

        parametro = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(
                                        compania,
                                        "NOMBRE SUBDIRECTOR ADMINISTRATIVO Y FINANCIERO",
                                        modulo,
                                        new Date(), true),
                        " ");

        parametros.put("PR_NOMBRE_SUBDIRECTOR_ADMINISTRATIVO_Y_FINANCIERO",
                        parametro);

        parametro = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(
                                        compania,
                                        "NOMBRE DEL CARGO SUBDIRECTOR ADMINISTRATIVO",
                                        modulo,
                                        new Date(), true),
                        " ");

        parametros.put("PR_NOMBRE_DEL_CARGO_SUBDIRECTOR_ADMINISTRATIVO",
                        parametro);

        // rep 1592
        parametros.put("PR_CARGO_FINANCIERO1", ejbSysmanUtil.consultarParametro(
                        compania,
                        "CARGO FINANCIERO1", modulo,
                        new Date(), true));

        // rep 1503
        parametro = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(
                                        compania,
                                        "MANEJA FIRMAS EN CERTIFICADO DISPONIBILIDAD",
                                        modulo,
                                        new Date(), true),
                        " ");

        parametros.put("PR_MANEJA_FIRMAS_EN_CERTIFICADO_DIS", parametro);

        parametro = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(
                                        compania,
                                        "MENSAJE DISPONIBILIDAD",
                                        modulo,
                                        new Date(), true),
                        "AFECTACION PRESUPUESTAL: ACUERDO 082/99. DECRETO 202/99");

        parametros.put("PR_MENSAJE_DISPONIBILIDAD", parametro);

        // rep 1500
        String formatoDisSo1 = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(compania,
                                        "RESOLUCION EN FORMATO DE DISPONIBILIDAD DIS_SO1",
                                        modulo,
                                        new Date(), true),
                        "RESOLUCION EN FORMATO DE DISPONIBILIDAD DIS_SO1");

        parametros.put("PR_RESOLUCION_EN_FORMATO_DE_DISPONIBILIDAD_DIS_SO1",
                        formatoDisSo1);

        // rep 001494REGSOPPTAL
        String valor = SysmanFunciones
                        .nvl(ejbSysmanUtil.consultarParametro(compania,
                                        "NORMA PRESUPUESTAL", modulo,
                                        new Date(), false), "")
                        .toString();

        parametros.put("PR_NORMA_PRESUPUESTAL", valor);

        valor = SysmanFunciones
                        .nvl(ejbSysmanUtil.consultarParametro(compania,
                                        "MANEJA FIRMAS EN REGISTROPPTAL",
                                        modulo,
                                        new Date(), false), "NO")
                        .toString();

        parametros.put("PR_MANEJA_FIRMAS_REGISTROPPTAL", "SI".equals(valor));

        // Parametros Reporte 001508DISCORPPTAL
        parametro = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(
                                        compania,
                                        "CARGO EJECUCION 2",
                                        modulo,
                                        new Date(), true),
                        " ");

        parametros.put("PR_CARGO_EJECUCION_2", parametro);

        parametro = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(
                                        compania,
                                        "MOSTRAR PRORROGA Y FIRMA EN FORMATO DIS_COR",
                                        modulo,
                                        new Date(), true),
                        "NO");

        parametros.put("PR_MOSTRAR_PRORROGA", "SI".equals(parametro));

        parametro = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(compania,
                                        idioma.getString("TB_TB3782"), modulo,
                                        new Date(), true),
                        "NO");

        parametros.put("PR_MOSTRAR_NOMBRE_COMPANIA", "SI".equals(parametro));

        parametro = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(
                                        compania,
                                        "RUTA IMAGEN LEMA CAQUETA",
                                        modulo,
                                        new Date(), false),
                        " ");

        parametros.put("PR_IMAGEN_LEMA", parametro);
        
        parametro = SysmanFunciones.nvlStr( ejbSysmanUtil.consultarParametro(
                                compania, "CERTIFICADOR FORMATO RES_UES", modulo, new Date(), false), " ");

        parametros.put("PR_CERTIFICADOR_FORMATO", parametro);
    }

    private void agregarParametrosCont(Map<String, Object> parametros)
                    throws SystemException, NamingException, SQLException {
        String tituloRbCnt = ejbSysmanUtil.consultarParametro(compania,
                        "MANEJA FORMATO ESPECIAL EN ING_COR",
                        modulo, new Date(), true);
        if ("SI".equals(tituloRbCnt)) {
            parametros.put("PR_TITULO_RB_CNT",
                            idioma.getString("TB_TB3378"));

            parametros.put("PR_TITULO_CER_REG",
                            idioma.getString("TG_REGISTRO_PRESUPUESTAL"));
            parametros.put("PR_TITULO_VLR", idioma.getString("TB_TB3380"));
            parametros.put("PR_TITULO_RP_RO",
                            idioma.getString("TB_TB3381"));
        } else {
            parametros.put("PR_TITULO_RB_CNT",
                            idioma.getString("TB_TB3382"));

            parametros.put("PR_TITULO_CER_REG",
                            idioma.getString("TB_TB3383"));
            parametros.put("PR_TITULO_VLR", idioma.getString("TB_TB3384"));
            parametros.put("PR_TITULO_RP_RO",
                            idioma.getString("TG_REGISTRO_PRESUPUESTAL"));

        }
        parametros.put("PR_NOMBRE_USUARIO_SESION",nombreCompleto);

        parametros.put("PR_NOTA_AL_PIE", SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "TEXTO EN NOTA AL PIE EN FORMATO ING_COR",
                                        modulo, new Date(), true), ""));

        String tituloRpRo = ejbSysmanUtil.consultarParametro(compania,
                        "MOSTRAR CERT. DISPONIB. Y REG. PPTAL EN ING_COR",
                        modulo, new Date(), true);
        parametros.put("PR_OCULTAR", "SI".equals(tituloRpRo) ? -1 : 0);
        parametros.put("PR_CARGO_TESORERO", cargaParametroConAlerta(
                        compania,
                        "CARGO TESORERO"));

        parametros.put("PR_CARGO_CONTABILIDAD", cargaParametroConAlerta(
                        compania,
                        "CARGO CONTABILIDAD"));

        parametros.put("PR_CARGO_FINANCIERO", cargaParametroConAlerta(
                        compania,
                        "CARGO FINANCIERO"));
        parametros.put("PR_CARGO_GERENTE", cargaParametroConAlerta(
                        compania,
                        "CARGO GERENTE"));

        parametros.put("PR_NOMBRECOMPANIA",nombreCompania);

        parametros.put("PR_NITCOMPANIA",nitCompania);
        /*Inicio Diana Castiblanco 002201*/
          String nota = SysmanFunciones
                        .nvl(ejbSysmanUtil.consultarParametro(compania,
                                        "TEXTO EN NOTA AL PIE EN FORMATO EGR_SO", modulo,
                                        new Date(), false), "")
                        .toString();

        parametros.put("PR_TEXTO_EN_NOTA_AL_PIE_EN_FORMATO_EGR_SO", nota);
         //Diana Castiblanco

        // 1499
        parametros.put("PR_ACCION_EN_EGRESO", cargaParametroConAlerta(compania,
                        "ACCION EN EGRESO"));

        parametros.put("PR_FIRMA_EN_EGRESO", cargaParametroConAlerta(compania,
                        "FIRMA EN EGRESO"));
        
        parametros.put("PR_ACCION_EN_INGRESO", cargaParametroConAlerta(compania,
                		"ACCION EN INGRESO"));

        parametros.put("PR_FIRMA_EN_INGRESO", cargaParametroConAlerta(compania,
                		"FIRMA EN INGRESO"));
        
        parametros.put("PR_REVISADO_POR",
                ejbSysmanUtil.consultarParametro(compania,
                         "REVISADO POR",
                         modulo, new Date(), true));
        // 1496
        String manInfAdicionalIngSo = ejbSysmanUtil.consultarParametro(compania,
                        "MANEJA INFORMACION ADICIONAL EN ING_SO",
                        modulo,
                        new Date(), true);

        String elimCreadorEgrSo = SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "ELIMINAR CREADOR EN EGR_SO", modulo,
                                        new Date(), true), "NO");
        String codFormatoRecCaja = SysmanFunciones.nvlStr(
                        ejbSysmanUtil.consultarParametro(compania,
                                        "CODIGO FORMATO RECIBOS DE CAJA",
                                        modulo,
                                        new Date(), true),
                        " ");

        parametros.put("PR_VISIBLESCOMP",
                        "SI".equals(manInfAdicionalIngSo) ? true : false);
        parametros.put("PR_VISIBLECREADOR",
                        "SI".equals(elimCreadorEgrSo) ? false : true);
        parametros.put("PR_CODIGO_FORMATO_RECIBOS_DE_CAJA", codFormatoRecCaja);
        parametros.put("PR_DIRECCIONCOMPANIA",direccionCompania);

        // reporte 1476
        parametros.put("PR_NOMBRECOMPLETO", nombreCompleto);

        // Parámetros Informe 001495EGRSOCNT contabilidad

        String firmasEgr = SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "FIRMAS EN EGR",
                                        modulo, new Date(), true), "NO");

        String firmasEpeciales = SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "MANEJA FIRMAS ESPECIALES EN EGR_SO",
                                        modulo, new Date(), true), "NO");
        String verNota = SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "VER NOTA AL PIE EN FORMATO EGR_SO",
                                        modulo, new Date(), true), "NO");

        String fechaRecibido = SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "MANEJA FECHA RECIBIDO EN EGR_SO",
                                        modulo, new Date(), true), "NO");
        String eliminarSuperior = SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "ELIMINAR ELABORO SUPERIOR EN EGR_SO",
                                        modulo, new Date(), true), "NO");
        String manejaPago = SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "MANEJA PAGO ELECTRONICO EN EGR_SO",
                                        modulo, new Date(), true), "NO");
        String colocarTitulos = SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "COLOCAR TITULOS EN FORMATO COM_SM",
                                        modulo, new Date(), true), "NO");
        
        parametros.put("PR_CARGO_FINANCIERO_PARA_PASTO", SysmanFunciones
        				.nvlStr(ejbSysmanUtil.consultarParametro(compania,
		                        		"CARGO FINANCIERO PARA PASTO",
		                        		modulo, new Date(),true),""));

		parametros.put("PR_CARGO_TESORERO_PARA_PASTO", SysmanFunciones
						.nvlStr(ejbSysmanUtil.consultarParametro(compania,
		                        		"CARGO Tesorero PARA PASTO",
		                        		modulo, new Date(),true),""));
        
        parametros.put("PR_FIRMASENEGR", firmasEgr);
        parametros.put("PR_MANEJAFIRMASESPECIALES", firmasEpeciales);
        parametros.put("PR_VERNOTA", verNota);
        parametros.put("PR_FECHARECIBIDO", fechaRecibido);
        parametros.put("PR_ELIMINARELABORO", eliminarSuperior);
        parametros.put("PR_MANEJAPAGO", manejaPago);
        parametros.put("PR_COLOCARTITULOS", colocarTitulos);
        parametros.put("PR_ELIMINARCREADOR", elimCreadorEgrSo);

        String codigoFormatoEgreso = SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "CODIGO FORMATO EGRESO",
                                        modulo, new Date(), true), "");
        String textoNota = SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "TEXTO EN NOTA AL PIE EN FORMATO EGR_SO",
                                        modulo, new Date(), true), "");
        String elaboroEgreso = SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "ELABORO EN EGRESO",
                                        modulo, new Date(), true), "");
        String revisoEgreso = SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                        "REVISO EN EGRESO",
                                        modulo, new Date(), true), "");
        parametros.put("PR_CODIGO_FORMATO_EGRESO", codigoFormatoEgreso);
        parametros.put("PR_FORMATOEGRSOCNT", textoNota);
        parametros.put("PR_ELABORO_EN_EGRESO", elaboroEgreso);
        parametros.put("PR_REVISO_EN_EGRESO", revisoEgreso);

        // reporte 1505
        String eslogan = ejbSysmanUtil.consultarParametro(
                        compania,
                        "ESLOGAN",
                        modulo,
                        new Date(), true);
        parametros.put("PR_ESLOGAN", eslogan);

        String tituloRpt = ejbSysmanUtil.consultarParametro(compania,
                        "TITULO CUENTAS POR PAGAR", modulo, new Date(), true);
        parametros.put("PR_TITULO_REPORTE", "SI".equals(tituloRpt)
                        ? "CUENTAS POR PAGAR"
                            : "COMPROBANTE DE PAGO");
        
        parametros.put("PR_NOMBRE_DE_JEFE_DE_CONTABILIDAD", SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania, "NOMBRE JEFE DE CONTABILIDAD",
                                        modulo, new Date(), true), ""));
        parametros.put("PR_CARGO_DE_JEFE_DE_CONTABILIDAD", SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,"CARGO JEFE DE CONTABILIDAD",
                                        modulo, new Date(), true), ""));
        parametros.put("PR_CARGO_1_COMPROBANTE_CONTABLE", SysmanFunciones
                        .nvlStr(ejbSysmanUtil.consultarParametro(compania,"CARGO1 COMPROBANTE CONTABLE",
                                        modulo, new Date(), true), ""));
        
        parametros.put("PR_TEXTO_EN_NOTA_AL_PIE_EN_FORMATO_ING_COR", SysmanFunciones
                .nvlStr(ejbSysmanUtil.consultarParametro(compania,"TEXTO EN NOTA AL PIE EN FORMATO ING COR",
                                modulo, new Date(), false), ""));
        
        //Reporte SINCHE
        parametros.put("PR_ELABORO_CONTABILIZO_FIRMA", SysmanFunciones
                .nvlStr(ejbSysmanUtil.consultarParametro(compania,"ELABORO_CONTABILIZO",
                                modulo, new Date(), true), ""));
        parametros.put("PR_REVISO_FIRMA", SysmanFunciones
                .nvlStr(ejbSysmanUtil.consultarParametro(compania,"REVISO FIRMA",
                                modulo, new Date(), true), ""));
        parametros.put("PR_TESORERO_FIRMA", SysmanFunciones
                .nvlStr(ejbSysmanUtil.consultarParametro(compania,"TESORERO FIRMA",
                                modulo, new Date(), true), ""));
        
        parametros.put("PR_FIRMA_FORMATO_INGCHIA", SysmanFunciones
                .nvlStr(ejbSysmanUtil.consultarParametro(compania,"FIRMA FORMATO INGCHIA",
                                modulo, new Date(), true), ""));
        parametros.put("PR_CARGO_FIRMA_FORMATO_INGCHIA", SysmanFunciones
                .nvlStr(ejbSysmanUtil.consultarParametro(compania,"CARGO FIRMA FORMATO INGCHIA",
                                modulo, new Date(), true), ""));


        // fin modulo contabilidad
    }

    public boolean parametroTxt() {
        boolean parametroTx = false;
        try {

            parametroTx = "SI".equalsIgnoreCase(
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "GENERAR PLANO FACTURA MULTIPLE DE INGRESOS",
                                                            modulo,
                                                            new Date(), true),
                                            "NO")
                            .toString());
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
        }
        return parametroTx;
    }

    /**
     * Asigna los parametros de los resportes en relacion al modulo
     * desde el cual se abre el formulario.
     */
    private void asignarParametros(Map<String, Object> parametros) {
        switch (Integer.parseInt(modulo)) {
        /* Asignar parametros presupesto */
        case SysmanConstantes.MODULO_PRESUPUESTO:
            parametros.put("PR_CIUDADCOMPANIA",ciudadCompania);
            parametros.put("PR_DEPARTAMENTOCOMPANIA",departamentoCompania);
            break;
        case SysmanConstantes.MODULO_CONTABILIDAD:
        default:
            break;
        }
    }

    /**
     * Este metodo se hace con el fin de validar si el parametro
     * existe en la base de datos. Si el parametro no existe, arroja
     * un mensaje informando al usuario.
     *
     * @param nombreParametro
     * Nombre del parametro a cargar.
     * @return Valor del parametro.
     * @throws SQLException
     * @throws NamingException
     * @throws SystemException
     */
    private String cargaParametroConAlerta(String companiaPar,
        String nombreParametro)
                        throws NamingException, SQLException, SystemException {
        String parametro = ejbSysmanUtil.consultarParametro(companiaPar,
                        nombreParametro, modulo, new Date(), true);
        if (parametro == null) {
            String mensaje = idioma.getString("TB_TB1743");
            mensaje = mensaje.replace("s$nombreParametro$s", nombreParametro);
            mensaje = mensaje.replace("s$companiaInforme$s", companiaPar);
            mensajesParametros.add(mensaje);
            return "";
        } else {
            return parametro;
        }
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }
}
