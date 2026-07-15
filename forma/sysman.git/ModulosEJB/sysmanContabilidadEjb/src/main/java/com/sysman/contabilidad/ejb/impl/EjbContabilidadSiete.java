package com.sysman.contabilidad.ejb.impl;

import com.sysman.contabilidad.ejb.EjbContabilidadSieteLocal;
import com.sysman.contabilidad.ejb.EjbContabilidadSieteRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import co.com.sysman.acciones.Acciones;

/**
 * Session Bean implementation class ContabilidadSiete
 */
@Stateless
@LocalBean

public class EjbContabilidadSiete implements EjbContabilidadSieteRemote,
                EjbContabilidadSieteLocal {
    /**
     * Default constructor.
     */
    public EjbContabilidadSiete() {
    }

    @Override
    public String generarBancosPlanos(
        String compania,
        int ano,
        BigInteger egresoInicial,
        BigInteger egresoFinal,
        Date fechapago,
        long cuentaCliente,
        long cuentaPrincipal,
        String numerobanco,
        String tipoegreso,
        String cuentainicial,
        String cuentafinal,
        int identificador,
        boolean digitoVerificacion,
        String tipoCuentaCliente,
        String claseTransaccion)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_EGRESOINICIAL     =>",
                                    egresoInicial.toString(), ", ",
                                    "UN_EGRESOFINAL       =>",
                                    egresoFinal.toString(), ", ",
                                    "UN_FECHAPAGO         =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechapago),
                                    "','DD/MM/YYYY'), ",
                                    "UN_CUENTACLIENTE     =>",
                                    Long.toString(cuentaCliente), ", ",
                                    "UN_CUENTAPRINCIPAL   =>",
                                    Long.toString(cuentaPrincipal), ", ",
                                    "UN_NUMEROBANCO       =>'", numerobanco,
                                    "', ", "UN_TIPOEGRESO        =>'",
                                    tipoegreso, "', ",
                                    "UN_CUENTAINICIAL     =>'", cuentainicial,
                                    "', ", "UN_CUENTAFINAL       =>'",
                                    cuentafinal, "', ",
                                    "UN_IDENTIFICADOR     =>",
                                    Integer.toString(identificador), ", ",
                                    "UN_DIGITOVERIFICACION =>",
                                    digitoVerificacion ? "-1" : "0", ", ",
                                    "UN_TIPO_CNTA_CLIENTE => '", tipoCuentaCliente,"', ",
                                    "UN_CLASE_TRANSACCION =>'",  claseTransaccion,"'"
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIDAD7.FC_GENERARARCPLANOS_BANCOS",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException | ParseException e) {

            throw new SystemException(e);
        }
    }

    @Override
    public void depurarPagoRetenciones(
        String compania,
        int anio,
        int mes)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ""
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD7.PR_DEPURARPAGORETENCIONES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public BigDecimal actualizarEstadoConsol(
        String compania,
        int ano,
        int mesinicial,
        int mesfinal,
        String usuario,
        int modulo,
        String estado)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA           =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MESINICIAL        =>",
                                Integer.toString(mesinicial), ", ",
                                "UN_MESFINAL          =>",
                                Integer.toString(mesfinal), ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_MODULO            =>",
                                Integer.toString(modulo), ", ",
                                "UN_ESTADO            =>'", estado, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD7.FC_UPD_ESTADOCONSOLID",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void configurarCuentasDesc(
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
                        "PCK_CONTABILIDAD7.PR_TRANSLADAR_ANIO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String diferenciasPorMes(
        String compania,
        int ano)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ""
        };
        try {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIDAD7.FC_DIFERENCIAS_POR_MES",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void actualizarFechaPago(
        String compania,
        int ano,
        int mes,
        Date fecha,
        String usuario,
        boolean seleccion)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIO           =>'", usuario, "', ",
                                    "UN_SELECCION         =>",
                                    seleccion ? "-1" : "0", ""
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD7.PR_ACTUALIZAR_FECHA_PAGO",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarArchivoVerde(String compania, int ano, int mesInicial,
        int mesFinal) throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MESINICIAL               =>",
                                Integer.toString(mesInicial), ", ",
                                "UN_MESFINAL               =>",
                                Integer.toString(mesFinal), " "
        };
        try {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIDAD7.FC_GENERAR_ARCHIVOVERDE",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void validarCuentasEquivalentes(
        String compania,
        int anio,
        String tipoCpte,
        long comprobante)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_TIPO_CPTE         =>'", tipoCpte, "', ",
                                "UN_COMPROBANTE       =>",
                                Long.toString(comprobante)
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD7.PR_CUENTASEQUIVALENTES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String validarCuentasEquivalentesEgr(
        String compania,
        int ano,
        String listaNumeroAfectar)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_LISTANUMEROAFECTAR =>'", listaNumeroAfectar,
                                "'"
        };
        try {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIDAD7.FC_CUENTASBANCEGR",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String carteraCuenta(
        String compania,
        int modulo,
        String cuenta)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_MODULO            =>",
                                Integer.toString(modulo), ", ",
                                "UN_CUENTA            =>'", cuenta, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD7.FC_CARTERA_CUENTA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void cargarPlanContable(
        String compania,
        String cadenaplan,
        int opcion,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CADENAPLAN        =>", cadenaplan, ", ",
                                "UN_OPCION            =>",
                                Integer.toString(opcion), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD7.PR_CARGAR_PLAN_CONTABLE",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void causarHeredandoConciliacion(
        String compania,
        int anio,
        BigInteger numero,
        String tipomovimiento,
        BigInteger numeroacopiar,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_NUMERO            =>", numero.toString(),
                                ", ", "UN_TIPOMOVIMIENTO    =>'",
                                tipomovimiento, "', ",
                                "UN_NUMEROACOPIAR     =>",
                                numeroacopiar.toString(), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD7.PR_CAUSAR_HEREDANDO_CONCI",
                        SysmanFunciones.concatenar(parametros));
    }

    public String getPrepararPivotInformeAuxGC(
        String compania,
        String anio,
        String auxiliarI,
        String auxiliarF,
        String cuentaI,
        String cuentaF,
        Date fechaI,
        Date fechaF)
                    throws SystemException, ParseException, IOException,
                    SQLException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO              =>",
                                anio, ", ",
                                "UN_AUXILIAR_I            =>'", auxiliarI,
                                "', ",
                                "UN_AUXILIAR_F            =>'", auxiliarF,
                                "', ",
                                "UN_CUENTA_I            =>'", cuentaI, "', ",
                                "UN_CUENTA_F            =>'", cuentaF, "', ",
                                "UN_FECHA_I         =>TO_DATE('",
                                SysmanFunciones.convertirAFechaCadena(
                                                fechaI),
                                "','DD/MM/YYYY'), ",
                                "UN_FECHA_F         =>TO_DATE('",
                                SysmanFunciones.convertirAFechaCadena(fechaF),
                                "','DD/MM/YYYY') "
        };
        return Acciones.clobToStringSalto(
                        (Clob) AccionesImp.ejecutarFuncion(
                                        ConectorPool.ESQUEMA_SYSMAN,
                                        "PCK_CONTABILIDAD7.FC_PREPARARPIVOT_INFORMEAUXGC",
                                        SysmanFunciones.concatenar(
                                                        parametros),
                                        Types.CLOB));
    }

    public String getPrepararPivotInformeAuxCCT(
        String compania,
        String anio,
        String cuentaI,
        String cuentaF,
        String terceroI,
        String terceroF,
        String centroI,
        String centroF,
        String auxiliarI,
        String auxiliarF,
        String tipoI,
        String tipoF,
        String mesI,
        String mesF)
                    throws SystemException, IOException, SQLException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO              =>",
                                anio, ", ",
                                "UN_CUENTA_I            =>'", cuentaI, "', ",
                                "UN_CUENTA_F            =>'", cuentaF, "', ",
                                "UN_TERCERO_I            =>'", terceroI, "', ",
                                "UN_TERCERO_F            =>'", terceroF, "', ",
                                "UN_CENTRO_I            =>'", centroI, "', ",
                                "UN_CENTRO_F            =>'", centroF, "', ",
                                "UN_AUXILIAR_I            =>'", auxiliarI,
                                "', ",
                                "UN_AUXILIAR_F            =>'", auxiliarF, "', ",
                                "UN_COMPROBANTE_I            =>'", tipoI, "', ",
                                "UN_COMPROBANTE_F            =>'", tipoF, "', ",
                                "UN_MES_I            =>'", mesI, "', ",
                                "UN_MES_F            =>'", mesF, "'"
        };
        return Acciones.clobToStringSalto(
                        (Clob) AccionesImp.ejecutarFuncion(
                                        ConectorPool.ESQUEMA_SYSMAN,
                                        "PCK_CONTABILIDAD7.FC_PREPARARPIVOT_INFORMEAUXCCT",
                                        SysmanFunciones.concatenar(
                                                        parametros),
                                        Types.CLOB));
    }

    @Override
    public void actualizarAbono(
        String compania,
        int ano,
        String tipoCpte,
        BigInteger comprobante,
        int consecutivo,
        BigDecimal abono,
        Date fechaabono,
        String usuario)
                    throws SystemException, ParseException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_TIPO_CPTE         =>'", tipoCpte, "', ",
                                "UN_COMPROBANTE       =>",
                                comprobante.toString(), ", ",
                                "UN_CONSECUTIVO       =>",
                                Integer.toString(consecutivo), ", ",
                                "UN_ABONO             =>", abono.toString(),
                                ", ", "UN_FECHAABONO         =>TO_DATE('",
                                SysmanFunciones.convertirAFechaCadena(
                                                fechaabono),
                                "','DD/MM/YYYY') ",
                                ", ", "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD7.PR_CAMBIOABONO",
                        SysmanFunciones.concatenar(parametros));
    }
    
                
	/**
	 * Se reemplaza el nombre del metodo original FC_ACTUALIZAR_TOTAL_COMP_FISCAL por 
	 * FC_ACT_TOTAL_COMP_FISCAL para evitar que soprepase los 30 caractereres
	 */
    @Override
    public String actualizarValoresTotalesComp(String compania, String ano, String comprobante)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          		=>'", compania, "', ",
                                 "UN_ANO                    =>'", ano, "',",
                                 "UN_COMPROBANTE            =>'", comprobante, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD7.FC_ACT_TOTAL_COMP_FISCAL",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }
    
    @Override
    public String generarPlanoCnt(
        String compania,
        int ano,
        String tipoCpte, 
        String comprobante)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				            		"UN_ANO               =>",
				                    Integer.toString(ano), ", ",
				                    "UN_TIPO_CPTE         =>'", tipoCpte, "', ",
                                    "UN_COMPROBANTE       =>'", comprobante,
                                    "'"
            };

            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIDAD7.FC_GENERAR_PLANO_CNT",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }
    
    @Override
    public void copiarConfigConceptos(
            String compania,
            int anoDestino,
            int anoOrigen) throws SystemException {

        try {
            String[] parametros = {
                "UN_COMPANIA          => '", compania, "', ",
                "UN_ANO_DESTINO       => ", Integer.toString(anoDestino), ", ",
                "UN_ANO_ORIGEN        => ", Integer.toString(anoOrigen)
            };

            AccionesImp.ejecutarProcedimiento(
                    ConectorPool.ESQUEMA_SYSMAN,
                    "PCK_CONTABILIDAD7.PR_COPIAR_CONFIG_CONCEPTOS",
                    SysmanFunciones.concatenar(parametros)
            );

        } catch (Exception e) {
            throw new SystemException(e);
        }
    }
    
    @Override
    public void copiarConfigPlanContable(
            String compania,
            int anoDestino,
            int anoOrigen) throws SystemException {

        try {
            String[] parametros = {
                "UN_COMPANIA          => '", compania, "', ",
                "UN_ANO_DESTINO       => ", Integer.toString(anoDestino), ", ",
                "UN_ANO_ORIGEN        => ", Integer.toString(anoOrigen)
            };

            AccionesImp.ejecutarProcedimiento(
                    ConectorPool.ESQUEMA_SYSMAN,
                    "PCK_CONTABILIDAD7.PR_COPIAR_PLAN_CUENTAS",
                    SysmanFunciones.concatenar(parametros)
            );

        } catch (Exception e) {
            throw new SystemException(e);
        }
    }
    
    @Override
	public String cargarConciliar(
			String tabla,
			String cadena,
			String usuario,
			String compania,
			int ano)
					throws SystemException {
		try {
		
			String[] parametro = {
					"UN_TABLA       =>'", tabla, "', ",
					"UN_CADENA      =>", cadena, ", ",
					"UN_USUARIO     =>'", usuario, "', ", 
					"UN_COMPANIA    =>'", compania, "', ",
					"UN_ANO    		=>", Integer.toString(ano), " "
			};

			return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(
					ConectorPool.ESQUEMA_SYSMAN,
					"PCK_CONTABILIDAD7.FC_CARGAR_CONCILIAR",
					SysmanFunciones.concatenar(parametro),
					Types.CLOB));
		}
		catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}
    
    
}