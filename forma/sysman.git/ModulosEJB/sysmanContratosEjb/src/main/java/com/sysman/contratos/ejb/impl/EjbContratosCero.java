package com.sysman.contratos.ejb.impl;

import com.sysman.contratos.ejb.EjbContratosCeroLocal;
import com.sysman.contratos.ejb.EjbContratosCeroRemote;
import com.sysman.contratos.ejb.EjbContratosGeneralRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ContratosCero
 */
@Stateless
@LocalBean
public class EjbContratosCero
                implements EjbContratosCeroRemote, EjbContratosCeroLocal {
	
	@EJB 
	private EjbContratosGeneralRemote ejbContratosGeneralRemote;
    /**
     * Default constructor.
     */
    public EjbContratosCero() {
    }

    @Override
    public String getPolizaNumero(
        String claseorden,
        long ordendecompra,
        String compania)
                    throws SystemException {
        String[] parametros = { "UN_CLASEORDEN        =>'", claseorden, "', ",
                                "UN_ORDENDECOMPRA     =>",
                                Long.toString(ordendecompra), ", ",
                                "UN_COMPANIA          =>'", compania, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS.FC_NOPOLIZA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getPolizaFecha(
        String claseorden,
        long ordendecompra,
        int nufecha,
        String compania)
                    throws SystemException {
        String[] parametros = { "UN_CLASEORDEN        =>'", claseorden, "', ",
                                "UN_ORDENDECOMPRA     =>",
                                Long.toString(ordendecompra), ", ",
                                "UN_NUFECHA           =>",
                                Integer.toString(nufecha), ", ",
                                "UN_COMPANIA          =>'", compania, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS.FC_FECHAPOLIZA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getPolizaEstado(
        String claseorden,
        long ordendecompra,
        String compania)
                    throws SystemException {
        String[] parametros = { "UN_CLASEORDEN        =>'", claseorden, "', ",
                                "UN_ORDENDECOMPRA     =>",
                                Long.toString(ordendecompra), ", ",
                                "UN_COMPANIA          =>'", compania, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS.FC_ESTADOPOLIZA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getPivotDependencias(
        String compania,
        int anioinicial,
        int aniofinal,
        String dependencia)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIOINICIAL       =>",
                                Integer.toString(anioinicial), ", ",
                                "UN_ANIOFINAL         =>",
                                Integer.toString(aniofinal), ", ",
                                "UN_DEPENDENCIA       =>'", dependencia, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS.FC_PREPPIVOTGRAFDEPENDENCIAS",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getPlanoComercio(
        String compania,
        String cargo,
        int semestre,
        int anio,
        String camComercio,
        int conProponentes,
        int secXReg,
        String nit,
        String nombre,
        String ciudad,
        String direccion,
        String funcionario,
        Date fecha,
        int modulo,
        String usuario)
                    throws SystemException {
        try {
        	String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                    "UN_CARGO             =>'", cargo, "', ",
                    "UN_SEMESTRE          =>",
                    Integer.toString(semestre), ", ",
                    "UN_ANIO              =>",
                    Integer.toString(anio), ", ",
                    "UN_CAMCOMERCIO       =>'", camComercio,
                    "', ", "UN_CONPROPONENTES    =>",
                    Integer.toString(conProponentes), ", ",
                    "UN_SECXREG           =>",
                    Integer.toString(secXReg), ", ",
                    "UN_NIT               =>'", nit, "', ",
                    "UN_NOMBRE            =>'", nombre, "', ",
                    "UN_CIUDAD            =>'", ciudad, "', ",
                    "UN_DIRECCION         =>'", direccion,
                    "', ", "UN_FUNCIONARIO       =>'",
                    funcionario, "', ",
                    "UN_FECHA             =>TO_DATE('",
                    SysmanFunciones.convertirAFechaCadena(
                                    fecha),
                    "','DD/MM/YYYY'), ",
                    "UN_MODULO           =>",
                    Integer.toString(modulo), ", ",
                    "UN_USUARIO          =>'", usuario, "'"
        		};
        	return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                    ConectorPool.ESQUEMA_SYSMAN,
                    "PCK_CONTRATOS.FC_ARCHIVOPLANOCAMARACOMERCIO",
                    SysmanFunciones.concatenar(parametros),
                    Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String getNovedadDatoContratos(
        String claseorden,
        long ordendecompra,
        int opcion,
        String tiponovedad,
        String compania)
                    throws SystemException {
        String[] parametros = { "UN_CLASEORDEN        =>'", claseorden, "', ",
                                "UN_ORDENDECOMPRA     =>",
                                Long.toString(ordendecompra), ", ",
                                "UN_OPCION            =>",
                                Integer.toString(opcion), ", ",
                                "UN_TIPONOVEDAD       =>'", tiponovedad, "', ",
                                "UN_COMPANIA          =>'", compania, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS.FC_NONOVEDAD",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String actualizaFuentesOrdenCompra(
        String compania,
        int anio,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS.FC_MANT_ACTFTERECURSO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String actualizarPagosOrdenCompra(
        String compania,
        Date fechacorte,
        String tipocontratoini,
        String tipocontratofin,
        int modulo,
        Date fechapar,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHACORTE        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechacorte),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TIPOCONTRATOINI   =>'", tipocontratoini,
                                    "', ", "UN_TIPOCONTRATOFIN   =>'",
                                    tipocontratofin, "', ",
                                    "UN_MODULO            =>",
                                    Integer.toString(modulo), ", ",
                                    "UN_FECHAPAR          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechapar),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTRATOS.FC_MANT_ACTPAGOS",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void actualizarPlanDeCompras(
        String compania,
        String claseorden,
        long numeroini,
        long numerofin,
        int modulo,
        int ano,
        String usuario)
                    throws SystemException {
    	
    	ejbContratosGeneralRemote.actualizarPlanDeCompras(
    			compania,
    			claseorden, 
    			numeroini,
    			numerofin,
    			modulo, 
    			ano, 
    			usuario);
    	
 
    }

    @Override
    public void actualizarImpresoPlanDeComprasOrdenDeCompra(
        String compania,
        String claseorden,
        long numeroini,
        long numerofin,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseorden, "', ",
                                "UN_NUMEROINI         =>",
                                Long.toString(numeroini), ", ",
                                "UN_NUMEROFIN         =>",
                                Long.toString(numerofin), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS.PR_INDAFECTAPLANCOMPRASLOTES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void afectarItems(
        String compania,
        long numeroAfectado,
        String tipoAfectado,
        long numeroAfectar,
        String tipoAfectar,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUMEROAFECTADO    =>",
                                Long.toString(numeroAfectado), ", ",
                                "UN_TIPOAFECTADO      =>'", tipoAfectado, "', ",
                                "UN_NUMEROAFECTAR     =>",
                                Long.toString(numeroAfectar), ", ",
                                "UN_TIPOAFECTAR       =>'", tipoAfectar, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS.PR_AFECTARITEMS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void importarPagos(
        String compania,
        String claseOrden,
        long ordenDeCompra,
        String tipoAfectado,
        long numeroAfectado,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseOrden, "', ",
                                "UN_ORDENDECOMPRA     =>",
                                Long.toString(ordenDeCompra), ", ",
                                "UN_TIPOAFECTADO      =>'", tipoAfectado, "', ",
                                "UN_NUMEROAFECTADO    =>",
                                Long.toString(numeroAfectado), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS.PR_IMPORTARPAGOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void afectarPagos(
        String compania,
        String claseOrden,
        long ordenDeCompra,
        String tipoAfectado,
        long numeroAfectado,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseOrden, "', ",
                                "UN_ORDENDECOMPRA     =>",
                                Long.toString(ordenDeCompra), ", ",
                                "UN_TIPOAFECTADO      =>'", tipoAfectado, "', ",
                                "UN_NUMEROAFECTADO    =>",
                                Long.toString(numeroAfectado), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS.PR_AFECTARPAGOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public boolean afectarPolizas(
        String compania,
        long numeroAfectado,
        String tipoAfectado,
        long numeroAfectar,
        String tipoAfectar,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUMEROAFECTADO    =>",
                                Long.toString(numeroAfectado), ", ",
                                "UN_TIPOAFECTADO      =>'", tipoAfectado, "', ",
                                "UN_NUMEROAFECTAR     =>",
                                Long.toString(numeroAfectar), ", ",
                                "UN_TIPOAFECTAR       =>'", tipoAfectar, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS.FC_AFECTARPOLIZAS",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void cambiarConsecutivoContrato(
        String compania,
        String claseContrato,
        int anioVigencia,
        long anteriorConsec,
        long nuevoConsec,
        String usuario)
                    throws SystemException {
    	
    	ejbContratosGeneralRemote.cambiarConsecutivoContrato(
    			compania, 
    			claseContrato, 
    			anioVigencia, 
    			anteriorConsec, 
    			nuevoConsec, 
    			usuario);
    	
         }

}