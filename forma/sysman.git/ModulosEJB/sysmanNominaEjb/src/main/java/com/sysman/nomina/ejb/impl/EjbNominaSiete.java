package com.sysman.nomina.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.nomina.ejb.EjbNominaSieteLocal;
import com.sysman.nomina.ejb.EjbNominaSieteRemote;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class nominaCero
 */
@Stateless
@LocalBean
public class EjbNominaSiete
                implements EjbNominaSieteRemote, EjbNominaSieteLocal {
    /**
     * Default constructor.
     */
    public EjbNominaSiete() {
    }

    @Override
    public String liquidarNomina(
        String compania,
        int proceso,
        String inicial,
        String fin,
        String rutina,
        int ano,
        int mes,
        int periodo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_INICIAL           =>'", inicial, "', ",
                                "UN_FINAL             =>'", fin, "', ",
                                "UN_RUTINA            =>'", rutina, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM7.FC_LIQUIDAR",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void eliminarQuinquenio(
        String compania,
        int procesoNomina,
        Date fechaPagoQuin,
        int periodoNomina,
        int idEmpleado,
        String opcion,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_PROCESONOMINA     =>'",
                                    Integer.toString(procesoNomina),
                                    "', ",
                                    "UN_FECHAPAGOQUIN     =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaPagoQuin),
                                    "','DD/MM/YYYY'), ",
                                    "UN_PERIODONOMINA     =>'",
                                    Integer.toString(periodoNomina),
                                    "', ",
                                    "UN_IDEMPLEADO        =>",
                                    Integer.toString(idEmpleado), ", ",
                                    "UN_OPCION            =>'", opcion, "', ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM7.PR_ELIMINARQUINQUENIO",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void revisarConceptos(
        String compania,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM7.PR_REVISAR_CONCEPTOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public boolean actParametroCertDian(
        String compania,
        int modulo,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_MODULO            =>",
                                Integer.toString(modulo), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM7.FC_ACTPARAMETROCERTDIAN",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public String getPrepararPivotTodosDescuentos(
        String compania,
        int ano,
        int mes,
        int proceso,
        String periodo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_PERIODO           =>'", periodo, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM7.FC_PREPARARPIVOT_TODOSDESCUENT",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void generarErroresCuentasContables(
        String compania,
        int anio,
        int mes,
        String periodo,
        String proceso,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA => '", compania, "',",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>'", periodo, "', ",
                                "UN_PROCESO           =>'", proceso, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM7.PR_REVCUEN_PLANCONTJD04",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String retiroMasivo(
        String compania,
        String tipo,
        Date fechaRetiro,
        Date fechaTermina,
        long estado,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA => '", compania, "',",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_FECHARETIRO       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaRetiro),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHATERMINA      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaTermina),
                                    "','DD/MM/YYYY'), ",
                                    "UN_ESTADO            =>",
                                    Long.toString(estado), ", ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM7.FC_RETIROMASIVO",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void actualiarConcepUgpp(
        String compania,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM7.PR_ACTUALIZAR_CONCEP_NOM",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void migrarACesantias(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        int conceptoCesantias,
        int conceptoInteres,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_CONCEPTOCESANTIAS =>",
                                Integer.toString(conceptoCesantias), ", ",
                                "UN_CONCEPTOINTERES   =>",
                                Integer.toString(conceptoInteres), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM7.PR_MIGRAR_A_CESANTIAS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void migrarAHistoricos(
        String compania,
        int proceso,
        int anioInicial,
        int mesInicial,
        int periodoInicial,
        int anioFinal,
        int mesFinal,
        int periodoFinal,
        int conceptoCesantias,
        int conceptoInteres,
        int codigoCesantias,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANIOINICIAL       =>",
                                Integer.toString(anioInicial), ", ",
                                "UN_MESINICIAL        =>",
                                Integer.toString(mesInicial), ", ",
                                "UN_PERIODOINICIAL    =>",
                                Integer.toString(periodoInicial), ", ",
                                "UN_ANIOFINAL         =>",
                                Integer.toString(anioFinal), ", ",
                                "UN_MESFINAL          =>",
                                Integer.toString(mesFinal), ", ",
                                "UN_PERIODOFINAL      =>",
                                Integer.toString(periodoFinal), ", ",
                                "UN_CONCEPTOCESANTIAS =>",
                                Integer.toString(conceptoCesantias), ", ",
                                "UN_CONCEPTOINTERES   =>",
                                Integer.toString(conceptoInteres), ", ",
                                "UN_CODIGOCESANTIAS   =>",
                                Integer.toString(codigoCesantias), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM7.PR_MIGRAR_A_HISTORICOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void programarVacaciones(
        String compania,
        int anio,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM7.PR_PROGRAMAR_VACACIONES",
                        SysmanFunciones.concatenar(parametros));
    }
    
    public void almacenarCesantiasAuto(
    		String compania, 
    		int proceso, 
    		String idEmpleado, 
    		int ano, 
    		int mes, 
    		int periodo, 
    		String usuario)    	throws SystemException {
            String[] parametros = { "UN_COMPANIA => '", compania, "',",
                                    "UN_ANO              =>",
                                    Integer.toString(ano), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), ", ",
                                    "UN_PERIODO           =>'", Integer.toString(periodo), "', ",
                                    "UN_PROCESO           =>'", Integer.toString(proceso), "', ",
                                    "UN_USUARIO           =>'", usuario, "', ",
                                    "UN_EMPLEADO          =>", idEmpleado, ""
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM7.PR_ALMACENAR_CESANTIAS_AUTO",
                            SysmanFunciones.concatenar(parametros));
    }
    
    @Override
    public String getArmarConsultatDevengosDescuentos(
        String compania,
        String ano,
        String mes,
        String proceso,
        String periodo,
        String consulta)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",ano, ", ",
                                "UN_MES               =>", mes, ", ",
                                "UN_PROCESO           =>",proceso, ", ",
                                "UN_PERIODO           =>'", periodo, "',",
                                "UN_CONSULTA           =>'", consulta, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM7.FC_GETDEVENGOSDESCUENTOSPLANO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }
    
}