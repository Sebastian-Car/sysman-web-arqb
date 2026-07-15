package com.sysman.nomina.ejb.impl;
import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.nomina.ejb.EjbNominaDiezLocal;
import com.sysman.nomina.ejb.EjbNominaDiezRemote;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;

import java.util.Date;
import java.text.ParseException;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class NominaDiez
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbNominaDiez implements EjbNominaDiezRemote, EjbNominaDiezLocal {
	/**
	 * Default constructor.
	 */
	public EjbNominaDiez() {
	}

	@Override
	public  String  discoBancoAgrario(
			String compania, 
			int proceso, 
			int anio, 
			int mes, 
			int periodo, 
			String banco, 
			String fechareporte, 
			boolean todoslosbancos, 
			String observacion, 
			String lote, 
			int informe, 
			String tcuentabanorigen, 
			String cuentabanorigen) 
					throws SystemException {
		try {
			String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
					, "UN_PROCESO           =>" , Integer.toString(proceso) , ", "
					, "UN_ANIO              =>" , Integer.toString(anio) , ", "
					, "UN_MES               =>" , Integer.toString(mes) , ", "
					, "UN_PERIODO           =>" , Integer.toString(periodo) , ", "
					, "UN_BANCO             =>'" , banco , "', "
					, "UN_FECHAREPORTE      =>'" ,fechareporte , "', "
					, "UN_TODOSLOSBANCOS    =>" , (todoslosbancos?"-1":"0")  , ", "
					, "UN_OBSERVACION       =>'" , observacion , "', "
					, "UN_LOTE              =>'" , lote , "', "
					, "UN_INFORME           =>" , Integer.toString(informe) , ", "
					, "UN_TCUENTABANORIGEN  =>'" , tcuentabanorigen , "', "
					, "UN_CUENTABANORIGEN   =>'" , cuentabanorigen , "'"
			};
			return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM10.FC_DISCOBANCOAGRARIO",
					SysmanFunciones.concatenar(parametros),
					Types.CLOB));
		}
		catch ( IOException | SQLException e) {
			throw new SystemException(e);
		}
	}
	
	@Override
	public  void  actPersonalHist(
			String compania, 
			int procesoin, 
			int ano, 
			int mes, 
			int periodoin, 
			String usuario) 
					throws SystemException {
		String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
				, "UN_PROCESO         =>" , Integer.toString(procesoin) , ", "
				, "UN_ANO               =>" , Integer.toString(ano) , ", "
				, "UN_MES               =>" , Integer.toString(mes) , ", "
				, "UN_PERIODO         =>" , Integer.toString(periodoin) , ", "
				, "UN_USUARIO           =>'" , usuario , "'"
		};
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM10.PR_ACT_PERSONAL_HIST",
				SysmanFunciones.concatenar(parametros));
	}
	
	
	@Override
	public  void  actEnvioCorreoDian(
			String compania, 
			String empleado, 
			String ano) 
					throws SystemException {
		String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
				,"UN_EMPLEADO          =>'" , empleado , "', "
				, "UN_ANO               =>" , ano , " "
		};
		
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM10.PR_ACT_ENVIO_CORREO_DIAN",
				SysmanFunciones.concatenar(parametros));
	}
	
		@Override
	    public  void   duplicarNovedadesVac(
	String compania,
	String proceso,
	String concepto,
	String empleado,
	String nomEmpleado,
	String escalafon,
	String ano,
	String mes,
	String periodo,
	String usuario)
	                      throws SystemException {
	         String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
	                 , "UN_PROCESO           =>'" , proceso , "', "
	                 , "UN_CONCEPTO          =>'" , concepto , "', "
	                 , "UN_EMPLEADO          =>'" , empleado , "', "
	                 , "UN_NOMEMPLEADO       =>'" , nomEmpleado , "', "
	                 , "UN_ESCALAFON         =>'" , escalafon , "', "
	                 , "UN_ANO               =>'" , ano , "', "
	                 , "UN_MES               =>'" , mes , "', "
	                 , "UN_PERIODO           =>'" , periodo , "', "
	                 , "UN_USUARIO           =>'" , usuario , "'"
	};
	         AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM10.PR_DUPLICAR_NOVEDADES_VAC",
	SysmanFunciones.concatenar(parametros));
	    }
		
		@Override
        public int  copiarDistribucion(
    String compania, 
    int proceso, 
    int anoOrigen, 
    int anoDestino, 
    int mesOrigen, 
    int mesDestino, 
    int periodoOrigen, 
    int periodoDestino) 
                          throws SystemException {
             String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
                     , "UN_PROCESO           =>" , Integer.toString(proceso) , ", "
                     , "UN_ANO_ORIGEN        =>" , Integer.toString(anoOrigen) , ", "
                     , "UN_ANO_DESTINO       =>" , Integer.toString(anoDestino) , ", "
                     , "UN_MES_ORIGEN        =>" , Integer.toString(mesOrigen) , ", "
                     , "UN_MES_DESTINO       =>" , Integer.toString(mesDestino) , ", "
                     , "UN_PERIODO_ORIGEN    =>" , Integer.toString(periodoOrigen) , ", "
                     , "UN_PERIODO_DESTINO   =>" , Integer.toString(periodoDestino) , ""
    };
            return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM10.FC_COPIAR_DISTRIBUCION",
    SysmanFunciones.concatenar(parametros), Types.INTEGER);
        }

        @Override
        public  String  ajustarDecimalesDist(
                String compania, 
                String mes, 
                String periodo, 
                String anio, 
                String proceso, 
                String user) 
                        throws SystemException {
            String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
                    , "UN_MES               =>'" , mes , "', "
                    , "UN_PERIODO           =>'" , periodo , "', "
                    , "UN_ANIO              =>'" , anio , "', "
                    , "UN_PROCESO           =>'" , proceso , "', "
                    , "UN_USER              =>'" , user , "'"
            };
            return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM10.FC_AJUST_DECIMALES_DIST",
                    SysmanFunciones.concatenar(parametros),
                    Types.VARCHAR);
        }
        
        
        @Override
        public  void  distribuirDatos(
                String compania, 
                String proceso, 
                String anio, 
                String mes, 
                String periodo, 
                String usuario) 
                        throws SystemException {
            String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
                    , "UN_PROCESO           =>'" , proceso , "', "
                    , "UN_ANIO              =>'" , anio , "', "
                    , "UN_MES               =>'" , mes , "', "
                    , "UN_PERIODO           =>'" , periodo , "', "
                    , "UN_USUARIO           =>'" , usuario , "'"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM10.PR_DISTRIBUIR_DATOS",
                    SysmanFunciones.concatenar(parametros));
        }
        
        
        @Override
        public  void  calcularDistAux(
                String compania, 
                String mes, 
                String periodo, 
                String anio, 
                String proceso, 
                String codempleado, 
                String user) 
                        throws SystemException {
            String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
                    , "UN_MES               =>'" , mes , "', "
                    , "UN_PERIODO           =>'" , periodo , "', "
                    , "UN_ANIO              =>'" , anio , "', "
                    , "UN_PROCESO           =>'" , proceso , "', "
                    , "UN_CODEMPLEADO       =>'" , codempleado , "', "
                    , "UN_USER              =>'" , user , "'"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM10.PR_CALCULAR_DIST_AUX",
                    SysmanFunciones.concatenar(parametros));
        }
        
        @Override
        public  void  generarDistMensual(
                String compania, 
                String proceso, 
                String anio, 
                String mes, 
                String periodo, 
                String usuario) 
                        throws SystemException {
            String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
                    , "UN_PROCESO           =>'" , proceso , "', "
                    , "UN_ANIO              =>'" , anio , "', "
                    , "UN_MES               =>'" , mes , "', "
                    , "UN_PERIODO           =>'" , periodo , "', "
                    , "UN_USUARIO           =>'" , usuario , "'"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM10.PR_GENERAR_DIST_MENSUAL",
                    SysmanFunciones.concatenar(parametros));
        }
	
}