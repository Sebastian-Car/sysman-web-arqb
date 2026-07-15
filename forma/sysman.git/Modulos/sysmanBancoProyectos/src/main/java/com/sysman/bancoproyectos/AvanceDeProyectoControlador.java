package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.AvanceDeProyectoControladorEnum;
import com.sysman.bancoproyectos.enums.AvanceDeProyectoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jacelas
 * @version 1, 27/08/2015
 *
 * @author jguerrero
 * @version 2, 20/09/2017 Se realiza refactoring a la clase.
 * 
 * @author asana version 3, se realizan modificaciones en lista final
 * para mostrar desde e registro seleccionado en lista inicial.
 */

@ManagedBean
@ViewScoped
public class AvanceDeProyectoControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private final String proyectoInicialCons;
    private final String proyectoFinCons;
    private String opcion;
    private Object proyectoInicial;
    private Object proyectoFinal;
    private String tipoNovedad;
    private Object codigoNovedad;
    private String infProyPersDuit;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaProyectoinicial;
    private RegistroDataModelImpl listaProyectofinal;
    private RegistroDataModelImpl listaTIPONOVEDAD;

    private static final String CODIGO_BP = "CODIGO";
    private static final String TIPOT = "TIPOT";
    private static final String REPORTE196 = "000196RptAvanceProyecto";
    private static final String REPORTE205 = "000205RptAvanceProyectoComAct";
    private static final String REPORTE206 = "002326SEG_PLAN_DESARROLLO";
    private static final String REPORTE206_DUIT = "002716SEG_PLAN_DESA_DUIT";
    private String reporte206esp = "";

    @EJB
    private EjbSysmanUtilRemote ejSysmanUtil;

    /**
     * gh Creates a new instance of AvanceDeProyectoControlador
     */
    public AvanceDeProyectoControlador()
    {
        super();
        numFormulario = GeneralCodigoFormaEnum.AVANCE_DE_PROYECTO_CONTROLADOR.getCodigo();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        opcion = "1";
        proyectoInicialCons = "proyectoInicial";
        proyectoFinCons = "proyectoFin";
        proyectoInicial = "0";
        proyectoFinal = "99999999";

        try
        {
            validarPermisos();
        }
        catch (Exception ex)
        {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(AvanceDeProyectoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void init()
    {
        cargarListaProyectoinicial();
        cargarListaProyectofinal();
        cargarListaTIPONOVEDAD();
        abrirFormulario();
    }

    public void cargarListaProyectoinicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AvanceDeProyectoControladorUrlEnum.URL0004
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaProyectoinicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CODIGO_BP);
    }

    public void cargarListaProyectofinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AvanceDeProyectoControladorUrlEnum.URL0001
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(AvanceDeProyectoControladorEnum.PARAM1.getValue(), proyectoInicial);

        listaProyectofinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CODIGO_BP);
    }

    public void cargarListaTIPONOVEDAD()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AvanceDeProyectoControladorUrlEnum.URL0002
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTIPONOVEDAD = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        TIPOT);
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>

        if (proyectoInicial == null)
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB809"));
            return;
        }
        if (proyectoFinal == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB810"));
            return;
        }
        if (!"1".equals(opcion) && (tipoNovedad == null))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2267"));
            return;
        }
        generarExcel();

        // </CODIGO_DESARROLLADO>
    }

    public void generarExcel()
    {
        archivoDescarga = null;
        String strSql;
        String nombreReporte;
        StringBuilder builder = new StringBuilder();
        try
        {

            switch (opcion)
            {
            case "1":
                nombreReporte = REPORTE196;
                strSql = resolverConsulta(nombreReporte, 0);
                genInforme(ReportesBean.FORMATOS.EXCEL97, nombreReporte,
                                strSql);
                break;
            case "2":
                generarInformeDetallePorComponente(FORMATOS.EXCEL97);
                break;
            case "3":
                nombreReporte = REPORTE205;
                strSql = resolverConsulta(nombreReporte, 1);
                genInforme(ReportesBean.FORMATOS.EXCEL97, nombreReporte,
                                strSql);
                break;
            case "4":
            	if(infProyPersDuit.equals("SI") && codigoNovedad.equals("SCD")) {
            		nombreReporte = REPORTE206_DUIT;
            	} else {
            		nombreReporte = REPORTE206.equalsIgnoreCase(reporte206esp) ? REPORTE206 : reporte206esp;
            	}
                strSql = resolverConsulta(nombreReporte, 2);
                genInformeExcel(ReportesBean.FORMATOS.EXCEL97, nombreReporte,
                                strSql);
                break;
            default:
                break;
            }

        }
        catch (JRException | IOException | ParseException ex)
        {
            builder.append(idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)).append(ex.getMessage());
            JsfUtil.agregarMensajeError(builder.toString());
            Logger.getLogger(AvanceDeProyectoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public Map<String, Object> asignarParametros(Map<String, Object> parametros,
        int opcion)
    {
        parametros.put(proyectoInicialCons, proyectoInicial);
        parametros.put(proyectoFinCons, proyectoFinal);
        if (opcion == 1)
        {
            parametros.put("tipoNovedad", tipoNovedad);
        }
        if (opcion == 2)
        {
            parametros.put("novedad", codigoNovedad);
        }

        return parametros;
    }

    public void seleccionarFilaProyectoinicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        proyectoInicial = registroAux.getCampos().get(CODIGO_BP);

        cargarListaProyectofinal();
    }

    public void seleccionarFilaProyectofinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        proyectoFinal = registroAux.getCampos().get(CODIGO_BP);
    }

    public void seleccionarFilaTIPONOVEDAD(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        //tipoNovedad = (String) registroAux.getCampos().get("NOMBRE");
        tipoNovedad = registroAux.getCampos().get("NOMBRE").toString();
        codigoNovedad = registroAux.getCampos().get(TIPOT);
    }

    public void oprimirpdf()
    {
        // <CODIGO_DESARROLLADO>

        if (proyectoInicial == null)
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB809"));
            return;
        }
        if (proyectoFinal == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB810"));
            return;
        }
        if (!"1".equals(opcion) && (tipoNovedad == null))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2267"));
            return;
        }
        generarPdf();

        // </CODIGO_DESARROLLADO>
    }

    public String resolverConsulta(String nombreReporte, int valor)
    {
        String strSql;
        Map<String, Object> parametros = new HashMap<>();
        strSql = Reporteador.resuelveConsulta(nombreReporte,
                        Integer.parseInt(modulo),
                        asignarParametros(parametros, valor));
        return strSql;
    }

    public void generarPdf()
    {
        archivoDescarga = null;
        String strSql;
        String nombreReporte;
        StringBuilder builder = new StringBuilder();
        try
        {
            switch (opcion)
            {
            case "1":
                nombreReporte = REPORTE196;
                strSql = resolverConsulta(nombreReporte, 0);
                genInforme(ReportesBean.FORMATOS.PDF, nombreReporte, strSql);
                break;
            case "2":
                generarInformeDetallePorComponente(FORMATOS.PDF);
                break;
            case "3":
                nombreReporte = REPORTE205;
                strSql = resolverConsulta(nombreReporte, 1);
                genInforme(ReportesBean.FORMATOS.PDF, nombreReporte, strSql);
                break;
            case "4":
            	nombreReporte = REPORTE206.equalsIgnoreCase(reporte206esp) ? REPORTE206 : reporte206esp;
                strSql = resolverConsulta(nombreReporte, 2);
                genInforme(ReportesBean.FORMATOS.PDF, nombreReporte, strSql);
                break;
            default:
                break;
            }
        }
        catch (JRException | IOException | ParseException ex)
        {
            builder.append(idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)).append(ex.getMessage());
            JsfUtil.agregarMensajeError(builder.toString());
            Logger.getLogger(AvanceDeProyectoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void generarInformeDetallePorComponente(FORMATOS formato)
    {
        int contador = 1;
        StringBuilder niveles = new StringBuilder("");
        StringBuilder join = new StringBuilder("");
        String strSqlSub = "";
        String dependencia = "";
        String vigenciaCompania = "";
        String strSql = "";

        String nombreSubReporte = "000201RptAvanceProyectoRubros";
        String nombreReporte = "000200RptAvanceProyectoDeta";
        HashMap<String, Object> reemplazar = new HashMap<>();
        HashMap<String, Object> parametros = new HashMap<>();
        StringBuilder builder = new StringBuilder();

        try
        {
            dependencia = ejSysmanUtil.consultarParametro(compania,
                            "DEPENDENCIA DE BANCO DE PROYECTOS", SessionUtil.getModulo(), new Date(), true);

            vigenciaCompania = ejSysmanUtil.consultarParametro(compania,
                            "VIGENCIA GUBERNAMENTAL ACTUAL", SessionUtil.getModulo(), new Date(), true);

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(AvanceDeProyectoControladorEnum.VIGENCIA.name(), vigenciaCompania);

            List<Registro> listaNiveles = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                                            AvanceDeProyectoControladorUrlEnum.URL0003.getValue())
                                            .getUrl(),
                            param));

            for (Registro reg : listaNiveles)
            {
                niveles.append("PLAN" + contador + ".DESCRIPCION NIVEL"
                    + contador + ", \n");
                join.append("LEFT JOIN BP_PLAN_INDICATIVO PLAN" + contador
                    + " ON \n"
                    + "BP_D_NOVEDADPROYECTO.COMPANIA = PLAN" + contador
                    + ".COMPANIA\n"
                    + "    AND BP_PLAN_INDICATIVO.VIGENCIA_INICIAL = PLAN"
                    + contador + ".VIGENCIA_INICIAL \n"
                    + "AND SUBSTR(BP_PLAN_INDICATIVO.ID, 1, LENGTH(PLAN"
                    + contador + ".ID))=PLAN" + contador
                    + ".ID \n" + " AND PLAN" + contador + ".ID ='" 
                    + reg.getCampos().get("DIGITOS") +"'"+ "\n ");
                contador++;
            }
            while (contador <= 7)
            {
                niveles.append("'' NIVEL" + contador + ", \n");
                contador++;
            }
            reemplazar.put(proyectoInicialCons, proyectoInicial);
            reemplazar.put(proyectoFinCons, proyectoFinal);
            reemplazar.put("niveles", niveles.toString());
            reemplazar.put("join", join.toString());
            reemplazar.put("novedad", codigoNovedad);
            strSqlSub = Reporteador.resuelveConsulta(nombreSubReporte,
                            Integer.parseInt(modulo), new HashMap<String, Object>());
            strSql = Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(modulo), reemplazar);

            parametros.put("PR_DEPENDENCIA", dependencia);
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_PROYECTOFINAL", proyectoFinal);
            parametros.put("PR_PROYECTOINICIAL", proyectoInicial);
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_TIPONOVEDAD", tipoNovedad);
            parametros.put("PR_CIUDADCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getCiudad());
            parametros.put("PR_STRSQL_AVANCE", strSqlSub);
            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (FileNotFoundException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        catch (SysmanException | JRException | SystemException
                        | IOException ex)
        {
            builder.append(idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)).append(ex.getMessage());
            JsfUtil.agregarMensajeError(builder.toString());

            Logger.getLogger(AvanceDeProyectoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    private void genInforme(ReportesBean.FORMATOS formato, String nombreReporte,
        String psql)
                        throws JRException, IOException, ParseException
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        HashMap<String, Object> parametros = new HashMap<>();
        String dependencia = "";
        String strSql = psql;
        StringBuilder builder = new StringBuilder();
        try
        {

            dependencia = ejSysmanUtil.consultarParametro(compania,
                            "DEPENDENCIA DE BANCO DE PROYECTOS", SessionUtil.getModulo(), new Date(), true);

            if (nombreReporte != REPORTE196)
            {
                reemplazar.put(proyectoInicialCons, proyectoInicial);
                reemplazar.put(proyectoFinCons, proyectoFinal);
                reemplazar.put("novedad", codigoNovedad);
                reemplazar.put("compania", compania);
                reemplazar.put("proyectoInicial", proyectoInicial);
                reemplazar.put("proyectoFin", proyectoFinal);

                strSql = Reporteador.resuelveConsulta(nombreReporte,
                                Integer.parseInt(modulo), reemplazar);
            }

            parametros.put("PR_DIRECCION",
                            SessionUtil.getCompaniaIngreso().getDireccion());
            parametros.put("PR_DEPENDENCIA", dependencia);
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_PROYECTOINICIAL", proyectoInicial);
            parametros.put("PR_PROYECTOFINAL", proyectoFinal);

            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_TIPONOVEDAD", tipoNovedad);
            parametros.put("PR_CIUDADCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getCiudad());

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        catch (SystemException | SysmanException ex)
        {
            if (ex.getMessage() != null)
            {
                builder.append(idioma
                                .getString(Constantes.MSM_TRANS_INTERRUMPIDA)).append(ex.getMessage());
                JsfUtil.agregarMensajeError(builder.toString());
            }
            Logger.getLogger(AvanceDeProyectoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
        // </CODIGO_DESARROLLADO>

    }
    
    private void genInformeExcel(ReportesBean.FORMATOS formato, String nombreReporte,
            String psql)
                            throws JRException, IOException, ParseException
        {
            // <CODIGO_DESARROLLADO>
            archivoDescarga = null;
            HashMap<String, Object> reemplazar = new HashMap<>();
            HashMap<String, Object> parametros = new HashMap<>();
            String dependencia = "";
            String strSql = psql;
            StringBuilder builder = new StringBuilder();
            try
            {

                dependencia = ejSysmanUtil.consultarParametro(compania,
                                "DEPENDENCIA DE BANCO DE PROYECTOS", SessionUtil.getModulo(), new Date(), true);

                if (nombreReporte != REPORTE196)
                {
                    reemplazar.put(proyectoInicialCons, proyectoInicial);
                    reemplazar.put(proyectoFinCons, proyectoFinal);
                    reemplazar.put("novedad", codigoNovedad);
                    reemplazar.put("compania", compania);
                    reemplazar.put("proyectoInicial", proyectoInicial);
                    reemplazar.put("proyectoFin", proyectoFinal);

                    strSql = Reporteador.resuelveConsulta(nombreReporte,
                                    Integer.parseInt(modulo), reemplazar);
                }

                parametros.put("PR_DIRECCION",
                                SessionUtil.getCompaniaIngreso().getDireccion());
                parametros.put("PR_DEPENDENCIA", dependencia);
                parametros.put("PR_NITCOMPANIA",
                                SessionUtil.getCompaniaIngreso().getNit());
                parametros.put("PR_PROYECTOINICIAL", proyectoInicial);
                parametros.put("PR_PROYECTOFINAL", proyectoFinal);

                parametros.put("PR_STRSQL", strSql);
                parametros.put("PR_TIPONOVEDAD", tipoNovedad);
                parametros.put("PR_CIUDADCOMPANIA",
                                SessionUtil.getCompaniaIngreso().getCiudad());

               /* archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                                parametros, ConectorPool.ESQUEMA_SYSMAN, formato);*/
                
                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                        ConectorPool.ESQUEMA_SYSMAN,
                        FORMATOS.EXCEL, nombreReporte);
            }
            catch (FileNotFoundException  ex)
            {
                logger.error(ex.getMessage(), ex);
                JsfUtil.agregarMensajeError(ex.getMessage());
            }
            catch (SystemException | SysmanException ex)
            {
                if (ex.getMessage() != null)
                {
                    builder.append(idioma
                                    .getString(Constantes.MSM_TRANS_INTERRUMPIDA)).append(ex.getMessage());
                    JsfUtil.agregarMensajeError(builder.toString());
                }
                Logger.getLogger(AvanceDeProyectoControlador.class.getName())
                                .log(Level.SEVERE, null, ex);

            }
            // </CODIGO_DESARROLLADO>
            catch (SQLException e) {
            	Logger.getLogger(AvanceDeProyectoControlador.class.getName())
                .log(Level.SEVERE, null, e);
			} catch (DRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }

    
    public void cambiaropciones()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getOpcion()
    {
        return opcion;
    }

    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
    }

    public Object getProyectoInicial()
    {
        return proyectoInicial;
    }

    public void setProyectoInicial(Object proyectoInicial)
    {
        this.proyectoInicial = proyectoInicial;
    }

    public Object getProyectoFinal()
    {
        return proyectoFinal;
    }

    public void setProyectoFinal(Object proyectoFinal)
    {
        this.proyectoFinal = proyectoFinal;
    }

    public String getTipoNovedad()
    {
        return tipoNovedad;
    }

    public void setTipoNovedad(String tipoNovedad)
    {
        this.tipoNovedad = tipoNovedad;
    }

    public RegistroDataModelImpl getListaProyectoinicial()
    {
        return listaProyectoinicial;
    }

    public void setListaProyectoinicial(
        RegistroDataModelImpl listaProyectoinicial)
    {
        this.listaProyectoinicial = listaProyectoinicial;
    }

    public RegistroDataModelImpl getListaProyectofinal()
    {
        return listaProyectofinal;
    }

    public void setListaProyectofinal(RegistroDataModelImpl listaProyectofinal)
    {
        this.listaProyectofinal = listaProyectofinal;
    }

    public RegistroDataModelImpl getListaTIPONOVEDAD()
    {
        return listaTIPONOVEDAD;
    }

    public void setListaTIPONOVEDAD(RegistroDataModelImpl listaTIPONOVEDAD)
    {
        this.listaTIPONOVEDAD = listaTIPONOVEDAD;
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
    	try {
			infProyPersDuit = SysmanFunciones.nvl(ejSysmanUtil.consultarParametro(compania,
							  "INFORME AVANCE DE PROYECTO PERSONALIZADO",SessionUtil.getModulo(),
							  new Date(),true),"NO").toString();
			
			reporte206esp = SysmanFunciones.nvl(ejSysmanUtil.consultarParametro(compania,
					  "FORMATO DE REPORTE DE SEGUIMIENTO DE AVANCE DE PROYECTO PERSONALIZADO",SessionUtil.getModulo(),
					  new Date(),true),"002326SEG_PLAN_DESARROLLO").toString();

		} catch (SystemException e) {
			e.printStackTrace();
		}
        // </CODIGO_DESARROLLADO>
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

}