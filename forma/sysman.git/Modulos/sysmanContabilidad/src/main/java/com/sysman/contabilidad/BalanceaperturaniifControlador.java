package com.sysman.contabilidad;

import static com.sysman.util.SysmanFunciones.nvl;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BalanceaperturaniifControladorEnum;
import com.sysman.contabilidad.enums.BalanceaperturaniifControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author lcortes
 * @version 1, 28/04/2016
 * @author spina
 * @version 2, 06/04/2014 - se refactoriza servicios dss y depuracion sonar
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class BalanceaperturaniifControlador extends BeanBaseModal
{
    private final String compania;
    /**
     * Constante que almacena el codigo del modulo por la cual se ingresa en la aplicacion
     */
    private final String modulo;
    private String codigoInicial;
    private String codigoFinal;
    private String anio;
    private String mes;
    private String digitos;
    private String nombreCodInicial;
    private String nombreCodFinal;
    private boolean seleccionado;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAnoTrabajo;

    /** Contiene los items del ciclo Mes. */
    private List<Registro> listaMesTrabajo;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    @EJB
    private EjbSysmanUtilRemote ejbContabilidadCero;

    /**
     * Creates a new instance of BalanceaperturaniifControlador
     */
    public BalanceaperturaniifControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.BALANCEAPERTURANIIF_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(BalanceaperturaniifControlador.class.getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaAnoTrabajo();
        cargarListaMesTrabajo();

        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        anio = String.valueOf(SysmanFunciones.ano(new Date()));

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Carga la lista del ciclo Ano.
     *
     */
    public void cargarListaAnoTrabajo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAnoTrabajo = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            BalanceaperturaniifControladorUrlEnum.URL3309
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista de items del combo Mes.
     */
    public void cargarListaMesTrabajo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try
        {
            listaMesTrabajo = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            BalanceaperturaniifControladorUrlEnum.URL3815
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(BalanceaperturaniifControladorUrlEnum.URL4326.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCodigoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(BalanceaperturaniifControladorUrlEnum.URL5411.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(BalanceaperturaniifControladorEnum.CODIGOINICIAL.getValue(), codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void oprimirXBRL()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generarArchivo("application/xml", ".xbrl");

    }

    public void oprimirImprimir()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generarArchivo("application/vnd.ms-excel", ".csv");
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnoTrabajo()
    {
        /* Muestre los meses del anio. */
        cargarListaMesTrabajo();
        cargarListaCodigoInicial();

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarEspecial()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(BalanceaperturaniifControladorEnum.CODIGO.getValue()), "").toString();
        nombreCodInicial = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), "").toString();
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(BalanceaperturaniifControladorEnum.CODIGO.getValue()), "").toString();
        nombreCodFinal = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), "").toString();
    }

    public void generarArchivo(String content, String extension)
    {
        try
        {
            String codigo;
            String orderBy;
            String codigoNiif;
            if (seleccionado)
            {
                codigo = BalanceaperturaniifControladorEnum.CODIGO.getValue();
                orderBy = "";
                codigoNiif = ", CODIGO NIIF";
            }
            else
            {
                codigo = "ID_NIIF";
                orderBy = " ORDER BY CASE WHEN ID_NIIF IS NULL THEN " + "                   CODIGO "
                    + "              ELSE ID_NIIF END ";
                codigoNiif = "";
            }
            HashMap<String, Object> reemplazar = new HashMap<>();
            int mesTrabajo = Integer.parseInt(mes) - 1;
            String nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
            String nombreArchivo = "";

            // Reemplazos consulta
            reemplazar.put("codigo", codigo);
            reemplazar.put("orderBy", orderBy);
            reemplazar.put("anio", anio);
            reemplazar.put("mes", mes);
            reemplazar.put("mesTrabajo", mesTrabajo);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("digitos", digitos);
            reemplazar.put("parAenValor", SysmanFunciones.nvl(ejbContabilidadCero.consultarParametro(compania,
                            "TIPO DE COMPROBANTE AJUSTES POR ERRORES", modulo, new Date(), true), " "));
            reemplazar
                            .put("parAcnValor",
                                            SysmanFunciones.nvl(
                                                            ejbContabilidadCero.consultarParametro(compania,
                                                                            "TIPO DE COMPROBANTE AJUSTES POR CONVERGENCIA", modulo,
                                                                            new Date(), true),
                                                            " "));
            reemplazar
                            .put("parReValor",
                                            SysmanFunciones.nvl(
                                                            ejbContabilidadCero.consultarParametro(compania,
                                                                            "TIPO DE COMPROBANTE RECLASIFICACIONES NIIF", modulo,
                                                                            new Date(), true),
                                                            " "));

            String sql = Reporteador.resuelveConsulta("000746SituacionFinanciera",
                            Integer.parseInt(SessionUtil.getModulo()), reemplazar);
            List<Registro> regAux = service.getListado(ConectorPool.ESQUEMA_SYSMAN, sql);
            StringBuilder textoPlano = new StringBuilder();
            ByteArrayInputStream archivo = null;
            if (".csv".equals(extension))
            {
                nombreArchivo = idioma.getString("TB_TB1760") + extension;
                String[] titulos = { idioma.getString("TB_TB1761").replace("s$nombreCompania$s", nombreCompania)
                                .replace("s$fecha$s",
                                                SysmanFunciones.convertirAFechaCadena(new Date(), "MMMMM 'de' YYYY").toUpperCase())
                                .replace("s$codigoNiif$s", codigoNiif) };

                textoPlano = insertarComas(textoPlano, titulos);
                textoPlano = new StringBuilder(textoPlano.toString().substring(0, textoPlano.toString().length() - 1) + "\n");

                for (Registro registro : regAux)
                {
                    textoPlano = agregarCampos(textoPlano, registro);
                }
                archivo = JsfUtil.serializarPlano(textoPlano.toString());
            }
            else if (".xbrl".equals(extension))
            {
                nombreArchivo = "AperturaNIIF" + extension;
                String[] titulos = {
                                     "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <!-- Copyright 2014 SYSMAN,Paipa,Boyaca-Colombia. --> "
                                         + "\n"
                                         + "<xbrli:xbrl xmlns:xbrli=\"http://www.xbrl.org/2003/instance\" xmlns:link=\"http://www.xbrl.org/2003/linkbase\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:iso4217=\"http://www.xbrl.org/2003/iso4217\" xmlns:dt=\"http://xbrl.c-ebs.org/dt\" xmlns:xbrldi=\"http://xbrl.org/2005/xbrldi\" xmlns:d-hh=\"http://www.c-ebs.org/eu/fr/esrs/corep/2006-07-01/d-hh-2006-07-01\" xmlns:d-ty=\"http://www.c-ebs.org/eu/fr/esrs/corep/2006-07-01/d-ty-2006-07-01\" xmlns:ref=\"http://www.xbrl.org/2004/ref\" xmlns:xbrldt=\"http://xbrl.org/2005/xbrldt\" xmlns:ref-corep=\"http://www.c-ebs.org/eu/fr/esrs/corep/2005-09-30/ref-corep-2005-09-30\"> "
                                         + "\n"
                                         + "<xbrli:unit id=\"COP\"> <xbrli:measure>iso4217:COP</xbrli:measure> </xbrli:unit> <xbrli:unit id=\"Pure\"> <xbrli:measure>xbrli:pure</xbrli:measure> </xbrli:unit>"
                                         + "\n" };
                textoPlano = insertarComas(textoPlano, titulos);
                textoPlano = new StringBuilder(textoPlano.toString().substring(0, textoPlano.toString().length() - 1) + "\n");

                for (Registro registro : regAux)
                {
                    textoPlano
                                    .append("\t <Nivel> " + nvl(registro.getCampos().get("NIVEL"), "ND") + " </Nivel>" + "\n");
                    textoPlano
                                    .append("\t\t " + "<Codigo> "
                                        + nvl(registro.getCampos()
                                                        .get(BalanceaperturaniifControladorEnum.CODIGO.getValue()), "ND")
                                        + " </Codigo>" + "\n");
                    textoPlano.append("\t\t\t " + "<Cuenta> " + nvl(registro.getCampos().get("CUENTA"), "ND")
                        + " </Cuenta>" + "\n");
                    textoPlano.append(BalanceaperturaniifControladorEnum.TABULADORES.getValue() + "<SaldosPcga> "
                        + nvl(registro.getCampos().get("SALDOS_PCGA"), "ND").toString().replace(",", "")
                        + " </SaldosPcga>" + "\n");
                    textoPlano.append(
                                    BalanceaperturaniifControladorEnum.TABULADORES.getValue() + "<ReclasificacionesDebito> "
                                        + nvl(registro.getCampos().get("RECLASIFICACIONES_DEBITO"), "ND").toString()
                                                        .replace(",", "")
                                        + " </ReclasificacionesDebito>" + "\n");
                    textoPlano.append(
                                    BalanceaperturaniifControladorEnum.TABULADORES.getValue() + " <ReclasificacionesCredito> "
                                        + nvl(registro.getCampos().get("RECLASIFICACIONES_CREDITO"), "ND").toString()
                                                        .replace(",", "")
                                        + " </ReclasificacionesCredito>" + "\n");
                    textoPlano.append(
                                    BalanceaperturaniifControladorEnum.TABULADORES.getValue() + " <AjustesPorErroresDebito> "
                                        + nvl(registro.getCampos().get("AJUSTESPORERRORES_DEBITO"), "ND").toString()
                                                        .replace(",", "")
                                        + " </AjustesPorErroresDebito>" + "\n");
                    textoPlano.append(
                                    BalanceaperturaniifControladorEnum.TABULADORES.getValue() + "<AjustesPorErroresCredito> "
                                        + nvl(registro.getCampos().get("AJUSTESPORERRORES_CREDITO"), "ND").toString()
                                                        .replace(",", "")
                                        + " </AjustesPorErroresCredito>" + "\n");
                    textoPlano.append(
                                    BalanceaperturaniifControladorEnum.TABULADORES.getValue() + "<AjustePorConvergenciaDebito> "
                                        + nvl(registro.getCampos().get("AJUSTESPORCONVERGENCIA_DEBITO"), "ND").toString()
                                                        .replace(",", "")
                                        + " </AjustePorConvergenciaDebito> " + "\n");
                    textoPlano.append(BalanceaperturaniifControladorEnum.TABULADORES.getValue()
                        + " <AjustesPorConvergenciaCredito> "
                        + nvl(registro.getCampos().get("AJUSTESPORCONVERGENCIA_CREDITO"), "ND").toString()
                                        .replace(",", "")
                        + " </AjustesPorConvergenciaCredito>" + "\n");

                }
                textoPlano.append("</xbrli:xbrl>");
                archivo = JsfUtil.serializarPlano(textoPlano.toString(), StandardCharsets.UTF_8.name());
            }

            if ((regAux == null) || regAux.isEmpty())
            {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB747"));
            }
            else
            {
                archivoDescarga = JsfUtil.getArchivoDescarga(archivo, nombreArchivo, content, StandardCharsets.UTF_8.name());
            }
        }
        catch (JRException | IOException | SystemException e)
        {
            Logger.getLogger(RelacionchequesControlador.class.getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        catch (ParseException e)
        {
            Logger.getLogger(BalanceaperturaesfaControlador.class.getName()).log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    private StringBuilder insertarComas(StringBuilder textoPlano, String[] titulos)
    {
        for (String celda : titulos)
        {
            textoPlano.append(celda + ",");
        }
        return textoPlano;
    }

    private StringBuilder agregarCampos(StringBuilder textoPlano, Registro registro)
    {
        textoPlano.append(nvl(registro.getCampos().get("NIVEL"), "") + ",");
        textoPlano
                        .append(nvl(registro.getCampos().get(BalanceaperturaniifControladorEnum.CODIGO.getValue()), "") + ",");
        textoPlano.append(nvl(registro.getCampos().get("CUENTA"), "") + ",");
        textoPlano.append(nvl(registro.getCampos().get("SALDOS_PCGA"), 0).toString().replace(",", "") + ",");
        textoPlano
                        .append(nvl(registro.getCampos().get("RECLASIFICACIONES_DEBITO"), 0).toString().replace(",", "") + ",");
        textoPlano.append(
                        nvl(registro.getCampos().get("RECLASIFICACIONES_CREDITO"), 0).toString().replace(",", "") + ",");
        textoPlano
                        .append(nvl(registro.getCampos().get("AJUSTESPORERRORES_DEBITO"), 0).toString().replace(",", "") + ",");
        textoPlano.append(
                        nvl(registro.getCampos().get("AJUSTESPORERRORES_CREDITO"), 0).toString().replace(",", "") + ",");
        textoPlano.append(
                        nvl(registro.getCampos().get("AJUSTESPORCONVERGENCIA_DEBITO"), 0).toString().replace(",", "") + ",");
        textoPlano.append(
                        nvl(registro.getCampos().get("AJUSTESPORCONVERGENCIA_CREDITO"), 0).toString().replace(",", "") + ",");
        if (seleccionado)
        {
            textoPlano.append(nvl(registro.getCampos().get("SALDOS_NIIF"), 0).toString().replace(",", "") + ",");
            textoPlano.append(nvl(registro.getCampos().get("CODIGO_NIIF"), "") + "\n");
        }
        else
        {
            textoPlano.append(nvl(registro.getCampos().get("SALDOS_NIIF"), 0).toString().replace(",", "") + "\n");
        }
        return textoPlano;
    }

    public String getCodigoInicial()
    {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial)
    {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal()
    {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal)
    {
        this.codigoFinal = codigoFinal;
    }

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getMes()
    {
        return mes;
    }

    public void setMes(String mes)
    {
        this.mes = mes;
    }

    public String getDigitos()
    {
        return digitos;
    }

    public void setDigitos(String digitos)
    {
        this.digitos = digitos;
    }

    public String getNombreCodInicial()
    {
        return nombreCodInicial;
    }

    public void setNombreCodInicial(String nombreCodInicial)
    {
        this.nombreCodInicial = nombreCodInicial;
    }

    public String getNombreCodFinal()
    {
        return nombreCodFinal;
    }

    public void setNombreCodFinal(String nombreCodFinal)
    {
        this.nombreCodFinal = nombreCodFinal;
    }

    public boolean isSeleccionado()
    {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado)
    {
        this.seleccionado = seleccionado;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public List<Registro> getListaAnoTrabajo()
    {
        return listaAnoTrabajo;
    }

    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo)
    {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    /**
     * Retorna la lista listaMesTrabajo
     *
     * @return listaMesTrabajo
     */
    public List<Registro> getListaMesTrabajo()
    {
        return listaMesTrabajo;
    }

    /**
     * Asigna la lista listaMesTrabajo
     *
     * @param listaMesTrabajo
     * Variable a asignar en listaMesTrabajo
     */
    public void setListaMesTrabajo(List<Registro> listaMesTrabajo)
    {
        this.listaMesTrabajo = listaMesTrabajo;
    }

    public RegistroDataModelImpl getListaCodigoInicial()
    {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(RegistroDataModelImpl listaCodigoInicial)
    {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigoFinal()
    {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal)
    {
        this.listaCodigoFinal = listaCodigoFinal;
    }
}
