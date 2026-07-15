package com.sysman.hojasdevida.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroGeneralRemote;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroLocal;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroRemote;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class HojasDeVida
 */
@Stateless
@LocalBean
public class EjbHojasDeVidaCero
                implements EjbHojasDeVidaCeroRemote, EjbHojasDeVidaCeroLocal {

    @EJB
    private EjbHojasDeVidaCeroGeneralRemote ejbHojasDeVidaGeneralRemote;

    /**
     * Default constructor.
     */
    public EjbHojasDeVidaCero() {
    }

    @Override
    public void validarFiltrosImpresionHV(
        boolean indListado,
        boolean indFechas,
        boolean indEstado,
        boolean indConsolidado,
        boolean indHistorial,
        Date fechaInicial,
        Date fechaFinal,
        String empleadoIniNum,
        String empleadoFinNum,
        String informe,
        String estado)
                    throws SystemException {
        try {
            String[] parametros = { "UN_IND_LISTADO       =>",
                                    indListado ? "-1" : "0", ", ",
                                    "UN_IND_FECHAS        =>",
                                    indFechas ? "-1" : "0", ", ",
                                    "UN_IND_ESTADO        =>",
                                    indEstado ? "-1" : "0", ", ",
                                    "UN_IND_CONSOLIDADO   =>",
                                    indConsolidado ? "-1" : "0", ", ",
                                    "UN_IND_HISTORIAL     =>",
                                    indHistorial ? "-1" : "0", ", ",
                                    "UN_FECHAINICIAL      =>TO_DATE('",
                                    SysmanFunciones.nvl(
                                                    SysmanFunciones.convertirAFechaCadena(
                                                                    fechaInicial),
                                                    "").toString(),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFINAL        =>TO_DATE('",
                                    SysmanFunciones.nvl(
                                                    SysmanFunciones.convertirAFechaCadena(
                                                                    fechaFinal),
                                                    "").toString(),
                                    "','DD/MM/YYYY'), ",
                                    "UN_EMPLEADOINI_NUM   =>'", empleadoIniNum,
                                    "', ", "UN_EMPLEADOFIN_NUM   =>'",
                                    empleadoFinNum, "', ",
                                    "UN_INFORME           =>'", informe, "', ",
                                    "UN_ESTADO            =>'", estado, "'"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_HOJAS_DE_VIDA.PR_VALIDARFILTROSIMPRESIONHV",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void validarEstudiosSuperiores(
        String compania,
        String numeroDcto,
        String sucursal)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUMERO_DCTO       =>'", numeroDcto, "', ",
                                "UN_SUCURSAL          =>'", sucursal, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.PR_VALIDARESTUDIOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarConsecutivoEvaluacion(
        String compania,
        String numeroDctoInicial,
        String numeroDctoFinal,
        Date fechaInicial,
        Date fechaFinal,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_NUMERODCTOINICIAL =>'",
                                    numeroDctoInicial,
                                    "', ", "UN_NUMERODCTOFINAL   =>'",
                                    numeroDctoFinal, "', ",
                                    "UN_FECHAINICIAL      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaInicial),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFINAL        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaFinal),
                                    "','DD/MM/YYYY'),",
                                    "UN_USUARIO => '",
                                    usuario, "'"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_HOJAS_DE_VIDA.PR_CONSECEVAL",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String cerrarConvocatoria(
        String compania,
        String convocatoria,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CONVOCATORIA      =>'", convocatoria, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.FC_CERRARCONVOCATORIA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void calificarEvaluacion(
        String compania,
        int idEmpleado,
        BigInteger evaluacion,
        int claseEvaluacion,
        String usuario)
                    throws SystemException {

        ejbHojasDeVidaGeneralRemote.calificarEvaluacion(compania, idEmpleado,
                        evaluacion, claseEvaluacion, usuario);

    }

    @Override
    public void actualizarCamposNulosPersonal(
        String compania,
        String numeroDcto)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUMERO_DCTO       =>",
                                numeroDcto, ""
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.PR_ACT_CAMPOS_NULOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarCamposNuevos(
        String compania,
        String numeroDcto,
        String sucursal,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUMERO_DCTO       =>'", numeroDcto, "', ",
                                "UN_SUCURSAL          =>'", sucursal, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.PR_ACT_CAMPOS_NULOS_NU",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void registrarDetallesEvaluacion(
        String compania,
        BigInteger evaluacion,
        int claseEvaluacion,
        String cedulaEvaluado,
        String cedulaEvaluador,
        String sucursalEvaluado,
        String sucursalEvaluador,
        String escalafonEvaluador,
        String escalafonEvaluado,
        String tipoEvaluacion,
        String cargoEvaluador,
        String cargoEvaluado,
        String codigoEvaluador,
        String codigoEvaluado,
        String evaluadorComision,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_EVALUACION        =>",
                                evaluacion.toString(), ", ",
                                "UN_CLASE_EVALUACION  =>",
                                Integer.toString(claseEvaluacion), ", ",
                                "UN_CEDULA_EVALUADO   =>'", cedulaEvaluado,
                                "', ", "UN_CEDULA_EVALUADOR  =>'",
                                cedulaEvaluador, "', ",
                                "UN_SUCURSAL_EVALUADO =>'", sucursalEvaluado,
                                "', ", "UN_SUCURSAL_EVALUADOR =>'",
                                sucursalEvaluador, "', ",
                                "UN_ESCALAFON_EVALUADOR =>'",
                                escalafonEvaluador,
                                "', ", "UN_ESCALAFON_EVALUADO =>'",
                                escalafonEvaluado, "', ",
                                "UN_TIPO_EVALUACION   =>'", tipoEvaluacion,
                                "', ", "UN_CARGO_EVALUADOR   =>'",
                                cargoEvaluador, "', ",
                                "UN_CARGO_EVALUADO    =>'", cargoEvaluado,
                                "', ", "UN_CODIGO_EVALUADOR  =>'",
                                codigoEvaluador, "', ",
                                "UN_CODIGO_EVALUADO   =>'", codigoEvaluado,
                                "', ", "UN_EVALUADOR_COMISION =>'",
                                evaluadorComision, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.PR_DETALLE_EVALUACION",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void ActualizarDetallesProfesiones(
        String compania,
        String numeroDcto,
        String sucursal,
        BigDecimal idDeEmpleado,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUMERO_DCTO       =>'", numeroDcto, "', ",
                                "UN_SUCURSAL          =>'", sucursal, "', ",
                                "UN_ID_DE_EMPLEADO    =>",
                                idDeEmpleado.toString(), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.PR_ACTUALIZAR_DETA_PROFESIONES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarPuntaje(
        String compania,
        BigInteger evaluacion,
        int claseEvaluacion,
        String tipoEvaluacion,
        String criterioEvaluado,
        String criterioSeleccionado,
        String cedulaEvaluado,
        String cedulaEvaluador,
        String sucursalEvaluado,
        String sucursalEvaluador,
        boolean escompromiso,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_EVALUACION        =>",
                                evaluacion.toString(), ", ",
                                "UN_CLASE_EVALUACION  =>",
                                Integer.toString(claseEvaluacion), ", ",
                                "UN_TIPO_EVALUACION   =>'", tipoEvaluacion,
                                "', ", "UN_CRITERIO_EVALUADO =>'",
                                criterioEvaluado, "', ",
                                "UN_CRITERIO_SELECCIONADO =>'",
                                criterioSeleccionado, "', ",
                                "UN_CEDULA_EVALUADO   =>'", cedulaEvaluado,
                                "', ", "UN_CEDULA_EVALUADOR  =>'",
                                cedulaEvaluador, "', ",
                                "UN_SUCURSAL_EVALUADO =>'", sucursalEvaluado,
                                "', ", "UN_SUCURSAL_EVALUADOR =>'",
                                sucursalEvaluador, "',", "UN_ESCOMPROMISO =>",
                                escompromiso ? "-1" : "0", ", ",
                                " UN_USUARIO =>'",
                                usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.PR_ACTUALIZAR_PUNTAJE",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizaractividadesinscritos(
        String compania,
        String evento,
        String tipoEv,
        Date fechaEv,
        String numeroDcto,
        String sucursal,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_EVENTO            =>'", evento, "', ",
                                    "UN_TIPO_EV           =>'", tipoEv, "', ",
                                    "UN_FECHA_EV          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaEv),
                                    "','DD/MM/YYYY'), ",
                                    "UN_NUMERO_DCTO     =>'", numeroDcto,
                                    "', ", "UN_SUCURSAL          =>'", sucursal,
                                    "', ", "UN_USUARIO           =>'", usuario,
                                    "'"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_HOJAS_DE_VIDA.PR_ACT_INSCR_ACTIV",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void insertarActividades(
        String compania,
        int tipoTransaccion,
        long transaccion,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPO_TRANSACCION  =>",
                                Integer.toString(tipoTransaccion), ", ",
                                "UN_TRANSACCION       =>",
                                Long.toString(transaccion), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.PR_INSERTARACTIVIDADES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void documentosPresentados(
        String compania,
        String sucursal,
        String tercero,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_SUCURSAL          =>'", sucursal, "', ",
                                "UN_TERCERO           =>'", tercero, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.PR_DOCUMENTOSPRESENTADOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void experienciaLaboral(
        String compania,
        String tercero,
        String sucursal,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TERCERO           =>'", tercero, "', ",
                                "UN_SUCURSAL          =>'", sucursal, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.PR_EXPERIENCIALABORAL",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public int registrarPersonal(
        String compania,
        String tercero,
        String sucursal,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TERCERO           =>'", tercero, "', ",
                                "UN_SUCURSAL          =>'", sucursal, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.FC_INSERCPERSONAL",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public void actualizarEnvioCorreos(
        String compania,
        String nroConvocatoria,
        int opcion)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NRO_CONVOCATORIA  =>'", nroConvocatoria,
                                "', ",
                                "UN_OPCION            =>",
                                Integer.toString(opcion)

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.PR_ACTUALIZARCORREOSINSCRITOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void cargarConvocatoriaManual(
        String compania,
        String numeroManual,
        String version,
        String nroConvocatoria,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUMEROMANUAL      =>'", numeroManual, "', ",
                                "UN_VERSION           =>'", version, "', ",
                                "UN_NROCONVOCATORIA   =>'", nroConvocatoria,
                                "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.PR_CARGACONVOMANUAL",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void generarCompromisos(
        String compania,
        BigInteger evaluacion,
        int clase,
        int ano,
        String evaluado,
        String sucursalevaluado,
        int periodo,
        String tipo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_EVALUACION          =>'",
                                evaluacion.toString(), "', ",
                                "UN_CLASE             =>",
                                Integer.toString(clase), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_EVALUADO          =>'", evaluado, "', ",
                                "UN_SUCURSALEVALUADO  =>'", sucursalevaluado,
                                "', ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_TIPO           => '", tipo, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.PR_GENERARCOMPROMISOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void heredarEvidencias(
        String compania,
        BigInteger evaluacion,
        String cedulaEvaluado,
        String cedulaEvaluador,
        int clase,
        String tipo,
        int ano,
        int opcion,
        String usuario) throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_EVALUACION          =>",
                                evaluacion.toString(), ", ",
                                "UN_CEDULA_EVALUADO             =>'",
                                cedulaEvaluado, "', ",
                                "UN_CEDULA_EVALUADOR               =>'",
                                cedulaEvaluador, "', ",
                                "UN_CLASE          =>",
                                Integer.toString(clase), ", ",
                                "UN_TIPO  =>'", tipo,
                                "', ",
                                "UN_ANO           =>",
                                Integer.toString(ano), ", ",
                                "UN_OPCION           =>",
                                Integer.toString(opcion), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.PR_HEREDAREVIDENCIAS",
                        SysmanFunciones.concatenar(parametros));

    }

    @Override
    public void actualizarEnvioCorreosAutoservicio(
        String compania,
        long consecutivo,
        int clase,
        int opcion)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CONSECUTIVO       =>",
                                String.valueOf(consecutivo), ", ",
                                "UN_CLASE             =>",
                                Integer.toString(clase), ", ",
                                "UN_OPCION            =>",
                                String.valueOf(opcion)
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.PR_ACTCORREOSAUTOSERVICIO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String crearRutaAnexos(
        String compania,
        int modulo,
        String cedula,
        String codigoruta)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_MODULO            =>",
                                Integer.toString(modulo), ", ",
                                "UN_CEDULA            =>'", cedula, "', ",
                                "UN_CODIGORUTA        =>'", codigoruta, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.FC_CREARUTANEXOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }
    
    @Override
    public void actualizarExpLaboral(
        String compania,
        String numeroDcto,
        String codigoPersona,
        String sucursal,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUMERO_DCTO       =>'", numeroDcto, "', ",
                                "UN_CODIGO_PER       =>'", codigoPersona, "', ",
                                "UN_SUCURSAL          =>'", sucursal, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.PR_ACTUALIZAEXPLABORALPER",
                        SysmanFunciones.concatenar(parametros));
    }
    
    @Override
    public void actualizarFondosHV(
    		String compania,
    		long idDeEmpleado) 
    				throws SystemException {
    	try {
    		String[] parametros = {
    				"UN_COMPANIA       =>'", compania, "', ",
    				"UN_ID_DE_EMPLEADO =>", Long.toString(idDeEmpleado), ""
    		};
    		AccionesImp.ejecutarProcedimiento(
    				ConectorPool.ESQUEMA_SYSMAN,
    				"PCK_HOJAS_DE_VIDA.PR_FONDOS_HV_NOM",
    				SysmanFunciones.concatenar(parametros)
    				);
    	}
    	catch (Exception e) {
    		throw new SystemException(e);
    	}
    }

}
