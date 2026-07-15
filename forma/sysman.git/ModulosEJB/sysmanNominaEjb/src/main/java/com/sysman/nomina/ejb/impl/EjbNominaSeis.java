package com.sysman.nomina.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.nomina.ejb.EjbNominaSeisLocal;
import com.sysman.nomina.ejb.EjbNominaSeisRemote;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import co.com.sysman.acciones.Acciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class NominaSeis
 */
@Stateless
@LocalBean
public class EjbNominaSeis implements EjbNominaSeisRemote, EjbNominaSeisLocal
{
    /**
     * Default constructor.
     */
    public EjbNominaSeis()
    {
    }

    @Override
    public BigDecimal getSalarioBase(
        String compania,
        int idDeEmpleado)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ID_DE_EMPLEADO    =>",
                                Integer.toString(idDeEmpleado)
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM6.FC_CUALSALARIO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal getCupoDeuda(
        String compania,
        int empleado,
        Date fecha)
                    throws SystemException
    {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_EMPLEADO          =>",
                                    Integer.toString(empleado), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY')"
            };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM6.FC_CUPODEUDA_MESANTERIOR",
                            SysmanFunciones.concatenar(parametros),
                            Types.DECIMAL);
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal getCupoAsigando(
        String compania,
        BigDecimal cualsalario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CUALSALARIO       =>",
                                cualsalario.toString()
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM6.FC_CUPO_ASIGNADO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void prepararPeriodoFinan(
        String compania,
        int anio,
        int mes,
        int periodo,
        int anio2,
        int mes2,
        int periodo2,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_ANIO2             =>",
                                Integer.toString(anio2), ", ",
                                "UN_MES2              =>",
                                Integer.toString(mes2), ", ",
                                "UN_PERIODO2          =>",
                                Integer.toString(periodo2), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM6.PR_PREPARAR_PERIODO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void prepararFinanciableNuevo(
        String compania,
        int anio1,
        int mes1,
        int periodo1,
        int anio2,
        int mes2,
        int periodo2,
        String usuario,
        String tipoempleado,
        String igualODiferente)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO1             =>",
                                Integer.toString(anio1), ", ",
                                "UN_MES1              =>",
                                Integer.toString(mes1), ", ",
                                "UN_PERIODO1          =>",
                                Integer.toString(periodo1), ", ",
                                "UN_ANIO2             =>",
                                Integer.toString(anio2), ", ",
                                "UN_MES2              =>",
                                Integer.toString(mes2), ", ",
                                "UN_PERIODO2          =>",
                                Integer.toString(periodo2), ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_TIPOEMPLEADO      =>'", tipoempleado, "', ",
                                "UN_IGUAL_O_DIFERENTE =>'", igualODiferente, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM6.PR_PREPARARFINANCIABLES_NUEVO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public int borrarFinaciables(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM6.FC_BORRARFINANCIABLES",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public int borrarHistoricos(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        String empleado,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_EMPLEADO          =>'", empleado, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM6.FC_BORRARHISTORICOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public BigDecimal inconsistenciaSOI(
        String compania,
        int anio,
        int mes,
        int periodo,
        int proceso,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM6.FC_REVISIONINCONSISTENCIAS",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public boolean factorParafiscal(
        String compania,
        int concepto)
                    throws SystemException
    {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CONCEPTO          =>",
                                Integer.toString(concepto)
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM6.FC_FACTOR_PARAFISCAL",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public Date fechaCambioFondoRiesgo(
        String compania,
        int empleado)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_EMPLEADO          =>",
                                Integer.toString(empleado)
        };
        return (Date) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM6.FC_FECHACAMBIORIESGO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DATE);
    }

    @Override
    public String getCampoPersonal(
        String compania,
        int idempleado,
        String campo)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_IDEMPLEADO        =>",
                                Integer.toString(idempleado), ", ",
                                "UN_CAMPO             =>'", campo, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM6.FC_PERSONALCAMPO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String sucursalRiesgo(
        String compania,
        int empleado,
        boolean par)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_EMPLEADO          =>",
                                Integer.toString(empleado), ", ",
                                "UN_PAR             =>", par ? "-1" : "0"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM6.FC_SUCURSALRIESGO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getDatoCajaCompensacion(
        String compania,
        int empleado,
        int par)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_EMPLEADO          =>",
                                Integer.toString(empleado), ", ",
                                "UN_PAR               =>",
                                Integer.toString(par)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM6.FC_TRAERDATOSCAJACOMPENSACION",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String traerDatosFondoEmpleado(
        String compania,
        int empleado,
        String fondoactual,
        String tipofondo,
        int par)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_EMPLEADO          =>",
                                Integer.toString(empleado), ", ",
                                "UN_FONDOACTUAL       =>'", fondoactual, "', ",
                                "UN_TIPOFONDO         =>'", tipofondo, "', ",
                                "UN_PAR               =>",
                                Integer.toString(par)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM6.FC_TRAERDATOSENTIDADESEMPLEADO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public boolean cerrarPeriodo(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        String usuario)
                    throws SystemException
    {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM6.FC_CERRARPERIODO",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean cerrarNomina(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        int par,
        String usuario)
                    throws SystemException
    {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_PAR               =>",
                                Integer.toString(par), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM6.FC_CERRARNOMINA",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public String calcularPrenomina(
        String compania,
        int periodo,
        int proceso,
        int anio,
        int mes,
        String opcion,
        String inicial,
        String fin,
        String rutina,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_OPCION            =>'", opcion, "', ",
                                "UN_INICIAL           =>'", inicial, "', ",
                                "UN_FINAL             =>'", fin, "', ",
                                "UN_RUTINA            =>'", rutina, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM6.FC_CALCULARPRENOMINA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void pasarConfiguracionDian(String compania, int anoAct, int anoConf, String usuario) throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANOACTUAL              =>",
                                Integer.toString(anoAct), ", ",
                                "UN_ANOCONF               =>",
                                Integer.toString(anoConf), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM6.PR_PASARCONFDIAN",
                        SysmanFunciones.concatenar(parametros));

    }
    
	@Override
	public  String generarDiscofna(
			String compania, 
			int proceso, 
			int periodo, 
			int mes, 
			int anio,  
			String fondo,
			boolean mes13) 
					throws SystemException {
		try {
			String[] parametros ={
					  "UN_COMPANIA          =>'" , compania , "', "
					, "UN_PROCESO           =>" , Integer.toString(proceso) , ", "
					, "UN_PERIODO           =>" , Integer.toString(periodo) , ", "
					, "UN_MES               =>" , Integer.toString(mes) , ", "
					, "UN_ANIO              =>" , Integer.toString(anio) , ", "
					, "UN_BANCO             =>'" , fondo , "', "
					, "UN_MES13             =>" , (mes13 ? "-1" : "0")
			};
			return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM6.FC_DISCOFNA",
					SysmanFunciones.concatenar(parametros),
					Types.CLOB));
		}
		catch (IOException | SQLException e) {
			throw new SystemException(e);
		}
	}
	
	@Override
    public void revisaLey2277_2022_2(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        int empleado,
        boolean todos,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
				        		"UN_PROCESO          =>",
				                Integer.toString(proceso), ", ",
                                "UN_ANO              =>",
                                Integer.toString(ano), ", ",
                                "UN_MES              =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO          =>",
                                Integer.toString(periodo), ", ",
                                "UN_EMPLEADO         =>",
                                Integer.toString(empleado), ", ",
                                "UN_TODOS            =>",
                                (todos ? "-1" : "0"), ", ",
                                "UN_USUARIO          =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM6.PR_REVISAR_LEY2277_2022_2",
                        SysmanFunciones.concatenar(parametros));
    }	
	
	
	 @Override
	    public  String  subirCnHistoricos(
	String compania, 
	int proceso, 
	int anio, 
	int mes, 
	int periodo, 
	int idempleado, 
	int idconcepto, 
	double valor, 
	Date fechac,
	String obs,
	String usuario) 
	                      throws SystemException {
	         try {
	         String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
	                 , "UN_PROCESO           =>" ,Integer.toString(proceso) ,", "
	                 , "UN_ANIO              =>" ,Integer.toString(anio) ,", "
	                 , "UN_MES               =>" ,Integer.toString(mes) ,", "
	                 , "UN_PERIODO           =>" ,Integer.toString(periodo) ,", "
	                 , "UN_IDEMPLEADO        =>" ,Integer.toString(idempleado) ,", "
	                 , "UN_IDCONCEPTO        =>" ,Integer.toString(idconcepto) ,", "
	                 , "UN_VALOR             =>" ,Double.toString(valor) ,", "
	                 , "UN_FECHAC            =>TO_DATE('" , SysmanFunciones.convertirAFechaCadena(fechac) , "','DD/MM/YYYY'), "
	                 , "UN_OBS               =>'" , obs ,"', "
	                 , "UN_USUARIO           =>'" , usuario , "' "
	};
	         return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM6.FC_SUBIR_CN_HIST",
	SysmanFunciones.concatenar(parametros),
	                Types.VARCHAR);
	         }
	         catch (ParseException e) {
	             throw new SystemException(e);
	          }
	    }
	    
	    public String crearNovedadesPensionado(
	            String compania,
	            Date  fechaInicial,
	            Date  fechaFinal,
	            String numeroDocumento,
	            String idDeEmpleado,
	            String tipoNovedad
	            )
	                        throws SystemException
	        {
	     	try {
	            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
	                    				"UN_FECHAINICIAL     =>TO_DATE('", SysmanFunciones.convertirAFechaCadena(fechaInicial),  "','DD/MM/YYYY'), ",
	                    				"UN_FECHAFINAL     =>TO_DATE('", SysmanFunciones.convertirAFechaCadena(fechaFinal),  "','DD/MM/YYYY'), ",
	                                    "UN_NUMERODOC          =>'", numeroDocumento, "', ",
	                                    "UN_ID_DE_EMPLEADO       =>'", idDeEmpleado, "', ",
	                                    "UN_COD_NOVEDAD         =>'", tipoNovedad, "'"
	            };
	            return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
	                            "PCK_NOMINA_COM6.FC_CREARNOVEDADESPENSIONADOS",
	                            SysmanFunciones.concatenar(parametros),
	                            Types.VARCHAR);
	     	}
	        catch (ParseException e) {
	            throw new SystemException(e);
	        }
	        }
	    
	    public String generarPlanoPeriodico(
	            String compania,
	            Date  fechaInicial,
	            Date  fechaFinal
	            )
	                        throws SystemException
	        {
	    	try {
	            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
	                    "UN_FECHAINICIAL     =>TO_DATE('", SysmanFunciones.convertirAFechaCadena(fechaInicial),  "','DD/MM/YYYY'), ",
	                    "UN_FECHAFINAL     =>TO_DATE('", SysmanFunciones.convertirAFechaCadena(fechaFinal), "','DD/MM/YYYY')"
	               
	            };
	            return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
	                            "PCK_NOMINA_COM6.FC_GENERARPLANOPERIODICO",
	                            SysmanFunciones.concatenar(parametros),
	                            Types.VARCHAR);
	        }
	        catch (ParseException e) {
	            throw new SystemException(e);
	        }
	        }
	    
	    public String generarPlanosPensionados(
	            String compania,
	            Date  fechaInicial,
	            Date  fechaFinal
	            )
	                        throws SystemException
	        {
	    	try {
	            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
	                    "UN_FECHAINICIAL     =>TO_DATE('", SysmanFunciones.convertirAFechaCadena(fechaInicial),  "','DD/MM/YYYY'), ",
	                    "UN_FECHAFINAL     =>TO_DATE('", SysmanFunciones.convertirAFechaCadena(fechaFinal), "','DD/MM/YYYY')"
	               
	            };
	            return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
	                            "PCK_NOMINA_COM6.FC_GENERARPLANOSPENSIONADOS",
	                            SysmanFunciones.concatenar(parametros),
	                            Types.VARCHAR);
	        }
	        catch (ParseException e) {
	            throw new SystemException(e);
	        }
	        }
}