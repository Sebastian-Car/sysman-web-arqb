package com.sysman.ejb.report.impl;


import com.sysman.ejb.report.ParameterProviderLocal;
import com.sysman.ejb.report.ParameterProviderRemote;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.logica.Compania;
import com.sysman.logica.Usuario;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ParameterProvider
 */
@Stateless
@LocalBean
public class ParameterProvider implements ParameterProviderRemote, ParameterProviderLocal {

    /**
     * Objeto implementacion de EjbSysmnUtil utilizado en la obtencion de parametros y demas funcionalidades generales
     */
	private String headerEspecial;
	
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Default constructor.
     */
    public ParameterProvider() {
        //
    }

    @Override
    public Map<String, Object> getParametrosModulo(int modulo, Usuario user,
                    Compania compania, Map<String, Object> sessionVars, String reporte)
                    throws SysmanException {
        Map<String, Object> parametros = new HashMap<>();
        cargarMapGeneral(user, compania, parametros);

        if (modulo == SysmanConstantes.MODULO_NOMINA) {
            cargarMapNomina(compania, sessionVars, parametros, reporte);
        }

        return parametros;
    }

    @Override
    public Map<String, Object> getReemplazosModulo(int modulo, Usuario user,
                    Compania compania, Map<String, Object> sessionVars, String reporte,
                    String menu)
                    throws SysmanException {
        Map<String, Object> reemplazos = new HashMap<>();

        switch (modulo) {
        case SysmanConstantes.MODULO_NOMINA:
            cargarReemplazosNomina(sessionVars, reemplazos);
            break;
        case SysmanConstantes.MODULO_ALMACEN:
            cargarReemplazosAlmacen(compania, reemplazos, menu);
            break;
        case SysmanConstantes.MODULO_BANCOPROY:
            cargarReemplazosBancoProy(compania, reemplazos);
            break;
        default:
            break;
        }

        return reemplazos;
    }

    private void cargarMapGeneral(Usuario user,
                    Compania compania, Map<String, Object> parametros) {

        parametros.put("PR_COMPANIA", compania.getCodigo());
        parametros.put("PR_NOMBRECOMPANIA", compania.getNombre());
        parametros.put("PR_NITCOMPANIA", compania.getNit());
        parametros.put("PR_SIGLACOMPANIA", compania.getSigla());
        parametros.put("PR_GETUSER", user);
    }

    private void cargarMapNomina(Compania compania,
                    Map<String, Object> sessionVars,
                    Map<String, Object> parametros, String reporte) throws SysmanException {
        String procesoNomina = (String) sessionVars.get("procesoNomina");
        String anioNomina = (String) sessionVars.get("anioNomina");
        String mesNomina = (String) sessionVars.get("mesNomina");
        String periodoNomina = (String) sessionVars.get("periodoNomina");
        String nombreMes = (String) sessionVars.get("nombreMesNomina");
        String nombrePeriodo = (String) sessionVars.get("nombrePeriodoNomina");
        String nombreProceso = (String) sessionVars.get("nombreProcesoNomina");
        boolean activo = (boolean) sessionVars.get("periodoActivo");

        parametros.put("PR_PROCESO", procesoNomina);
        parametros.put("PR_NOMBREPROCESO", nombreProceso);
        parametros.put("PR_ANO", anioNomina);
        parametros.put("PR_MES", mesNomina);
        parametros.put("PR_NOMBREMES", nombreMes);
        parametros.put("PR_PERIODO", periodoNomina);
        parametros.put("PR_NOMBREPERIODO", nombrePeriodo);
        parametros.put("PR_PERIODOACTIVO", activo);

        // Parametros personalizados por Informe
        if ("000094PlanillaTiemposTrabajo".equals(reporte)
                        || "000093PlanillaResumenAutoSalud".equals(reporte)) {
            String encabezado = "Periodo " + periodoNomina + " de "
                            + nombreMes + " de " + anioNomina + " ";
            parametros.put("PR_ENCABEZADO", encabezado);
        }

        try {
            String nombreJefeRH = ejbSysmanUtil.consultarParametro(
                            compania.getCodigo(),
                            "NOMBRE DEL JEFE DE RECURSOS HUMANOS",
                            String.valueOf(SysmanConstantes.MODULO_NOMINA),
                            new Date(), true);

            String cargoJefeRH = ejbSysmanUtil.consultarParametro(
                            compania.getCodigo(),
                            "CARGO DEL JEFE DE RECURSOS HUMANOS",
                            String.valueOf(SysmanConstantes.MODULO_NOMINA),
                            new Date(), true);
            String nombreGerente = ejbSysmanUtil.consultarParametro(
                            compania.getCodigo(),
                            "NOMBRE DEL GERENTE",
                            String.valueOf(SysmanConstantes.MODULO_NOMINA),
                            new Date(), true);
            String cargoGerente = ejbSysmanUtil.consultarParametro(
                            compania.getCodigo(),
                            "CARGO DEL GERENTE",
                            String.valueOf(SysmanConstantes.MODULO_NOMINA),
                            new Date(), true);
            String nombreCargoTesoreroPagador = ejbSysmanUtil
                            .consultarParametro(
                                            compania.getCodigo(),
                                            "NOMBRE DEL CARGO TESORERO PAGADOR",
                                            String.valueOf(SysmanConstantes.MODULO_NOMINA),
                                            new Date(), true);
            String cargoTesoreroPagador = ejbSysmanUtil.consultarParametro(
                            compania.getCodigo(),
                            "CARGO DEL TESORERO PAGADOR",
                            String.valueOf(SysmanConstantes.MODULO_NOMINA),
                            new Date(), true);
            String nombreQuienAutoriza = ejbSysmanUtil.consultarParametro(
                            compania.getCodigo(),
                            "NOMBRE DE QUIEN AUTORIZA NOMINA",
                            String.valueOf(SysmanConstantes.MODULO_NOMINA),
                            new Date(), true);
            String cargoQuienAutoriza = ejbSysmanUtil.consultarParametro(
                            compania.getCodigo(),
                            "CARGO DE QUIEN AUTORIZA NOMINA",
                            String.valueOf(SysmanConstantes.MODULO_NOMINA),
                            new Date(), true);
            String jefeRH = ejbSysmanUtil.consultarParametro(
                    compania.getCodigo(),
                    "NOMBRE JEFE DESARROLLO HUMANO",
                    String.valueOf(SysmanConstantes.MODULO_NOMINA),
                    new Date(), true);
            String jefeNomina = ejbSysmanUtil.consultarParametro(
                    compania.getCodigo(),
                    "NOMBRE JEFE NOMINA",
                    String.valueOf(SysmanConstantes.MODULO_NOMINA),
                    new Date(), true);

            headerEspecial = ejbSysmanUtil.consultarParametro(
            		compania.getCodigo(),
                    "FORMATOS ESPECIALES BUCARAMANGA",
                    String.valueOf(SysmanConstantes.MODULO_NOMINA),
                    new Date(), true);
            
            String cargoResponsableNomina = ejbSysmanUtil.consultarParametro(compania.getCodigo(),
                            "CARGO RESPONSABLE DE NOMINA", 
                            String.valueOf(SysmanConstantes.MODULO_NOMINA),
                            new Date(), true);
            String cargoJefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania.getCodigo(),
                            "CARGO JEFE DESARROLLO HUMANO", 
                            String.valueOf(SysmanConstantes.MODULO_NOMINA),
                            new Date(), true);
            String liquidaNomina = ejbSysmanUtil.consultarParametro(
                    compania.getCodigo(),
                    "NOMBRE DE QUIEN LIQUIDA LA NOMINA",
                    String.valueOf(SysmanConstantes.MODULO_NOMINA),
                    new Date(), true);
            String cargoliquidaNomina = ejbSysmanUtil.consultarParametro(
                    compania.getCodigo(),
                    "CARGO DE QUIEN LIQUIDA LA NOMINA",
                    String.valueOf(SysmanConstantes.MODULO_NOMINA),
                    new Date(), true);
            
            parametros.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", jefeRH);
            parametros.put("PR_NOMBRE_JEFE_NOMINA", jefeNomina);
            parametros.put("PR_CARGO_RESPONSABLE_DE_NOMINA", cargoResponsableNomina);
            parametros.put("PR_CARGO_JEFE_DESARROLLO_HUMANO", cargoJefeDesarrolloHumano);
            parametros.put("PR_NOMBRE_LIQUIDA_NOMINA", liquidaNomina);
            parametros.put("PR_CARGO_LIQUIDA_NOMINA", cargoliquidaNomina);
            
            
                     
            String sticker = compania.getRutaSticker();
            
            if ("001708PlanillaautoPensionVolAFC".equals(reporte)) {
                String encabezado = nombreMes + " de " + anioNomina + " ";
                parametros.put("PR_ENCABEZADO", encabezado);
            }
            if ("000054PlanillaFirmas".equals(reporte)
                            || "000090PlanillaAutoPensionVol".equals(reporte)) {

                String encabezado = "Periodo " + periodoNomina + " de "
                                + nombreMes + " de " + anioNomina + " ";
                parametros.put("PR_ENCABEZADO", encabezado);

            }
            parametros.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS", nombreJefeRH);
            parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", cargoJefeRH);
            parametros.put("PR_NOMBRE_DEL_GERENTE", nombreGerente);
            parametros.put("PR_CARGO_DEL_GERENTE", cargoGerente);
            parametros.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR",
                            nombreCargoTesoreroPagador);
            parametros.put("PR_CARGO_DEL_TESORERO_PAGADOR",
                            cargoTesoreroPagador);
            parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA",
                            nombreQuienAutoriza);
            parametros.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA",
                            cargoQuienAutoriza);
            parametros.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", 
    		        jefeRH);
            parametros.put("PR_NOMBRE_JEFE_NOMINA", 
    		        jefeNomina);
            
            parametros.put("PR_HEADER_ESPECIAL", headerEspecial.equals("SI")?true:false);
            parametros.put("PR_IMAGEN_ESPECIAL", sticker);
        }
        catch (SystemException e) {
            throw new SysmanException(e, e.getMessage());
        }
    }

    public String getHeaderEspecial() {
		return headerEspecial;
	}

	public void setHeaderEspecial(String headerEspecial) {
		this.headerEspecial = headerEspecial;
	}

	/**
     * Metodo utilizado para cargar las variables de sesion como variables de reemplazo.
     *
     * @param sessionVars
     * -> Variable de sesion.
     * @param reemplazos
     * -> Referencia que apunta a la coleccion de variables de reemplazo.
     */
    public void cargarReemplazosNomina(Map<String, Object> sessionVars,
                    Map<String, Object> reemplazos) {
        reemplazos.put("procesoNomina", sessionVars.get("procesoNomina"));
        reemplazos.put("mesNomina", sessionVars.get("mesNomina"));
        reemplazos.put("anioNomina", sessionVars.get("anioNomina"));

        String menuActual = SysmanFunciones
                        .nvl(sessionVars.get("menuActual"), "").toString();

        switch (menuActual) {
        case "6020603":
        case "603040203":
        case "603040204":
        case "603040302":
        case "603040305":
        case "603030302":
        case "6030519":
        case "603030303":
        case "6030520":
        case "6030521": 
        case "603030701":
        case "603030419":
            reemplazos.put("periodoNomina", sessionVars.get("periodoNomina"));
            break;
        case "6030221":
            reemplazos.put("periodoNomina", sessionVars.get("periodoNomina"));
            break;
        case "603040402":
            reemplazos.put("periodoNomina", sessionVars.get("periodoNomina"));
            break;
        case "603030413":
            reemplazos.put("periodoNomina", sessionVars.get("periodoNomina"));
            break;
        case "603030414":
            reemplazos.put("periodoNomina", sessionVars.get("periodoNomina"));
            break;
        case "603030415":
            reemplazos.put("periodoNomina", sessionVars.get("periodoNomina"));
            break;
        case "603040206":
            reemplazos.put("periodoNomina", sessionVars.get("periodoNomina"));
            break;
        case "603040307":
        	reemplazos.put("periodoNomina", sessionVars.get("periodoNomina"));
        break;
        case "603040405":
        	reemplazos.put("periodoNomina", sessionVars.get("periodoNomina"));
        break;
        case "603040308":
        	reemplazos.put("periodoNomina", sessionVars.get("periodoNomina"));
        break;
        case "603040207":
        	reemplazos.put("periodoNomina", sessionVars.get("periodoNomina"));
        break;
        case "603040406":
        	reemplazos.put("periodoNomina", sessionVars.get("periodoNomina"));
        break;
        case "603041101":
        	reemplazos.put("periodoNomina", sessionVars.get("periodoNomina"));
        break;
        case "603030115":
        	reemplazos.put("periodoNomina", sessionVars.get("periodoNomina"));
        break;
        case "603040504":
        	reemplazos.put("periodoNomina", sessionVars.get("periodoNomina"));
        break;
        case "603040208":
        	reemplazos.put("periodoNomina", sessionVars.get("periodoNomina"));
        break;
        case "603040407":
        	reemplazos.put("periodoNomina", sessionVars.get("periodoNomina"));
        break;
        case "603030118":
        	reemplazos.put("periodoNomina", sessionVars.get("periodoNomina"));
        break;
        default:
            reemplazos.put("periodoNomina", "V_ACUMULADOS.PERIODO");
            break;
        }
    }

    public void cargarReemplazosAlmacen(Compania compania,
                    Map<String, Object> reemplazos, String menu) {
        if ("10070301".equals(menu)) {
            reemplazos.put("condicion", " ");
            reemplazos.put("condicionSub", " WHERE ADICIONES.COMPANIA = '"
                            + compania.getCodigo()
                            + "' AND ADICIONES.ID_PREDIO = $P{PR_ID_PREDIO} ");
        }

    }

    public void cargarReemplazosBancoProy(Compania compania,
                    Map<String, Object> reemplazos) throws SysmanException {

        String vigenciaGubernamental;
        try {
            vigenciaGubernamental = ejbSysmanUtil.consultarParametro(
                            compania.getCodigo(),
                            "VIGENCIA GUBERNAMENTAL ACTUAL",
                            String.valueOf(SysmanConstantes.MODULO_BANCOPROY),
                            new Date(), true);
        }
        catch (SystemException e) {
            throw new SysmanException(e, e.getMessage());
        }

        reemplazos.put("vigenciaGubernamental", vigenciaGubernamental);

    }
}
