package com.sysman.almacen;

import com.sysman.almacen.enums.FichaTecnicaInmuebleEnum;
import com.sysman.almacen.enums.FichaTecnicaInmuebleUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Formulario;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author OTORRES
 * @version 1, 25/02/2016
 * 
 * @author eamaya
 * @version 2, 27/04/2017 Proceso de Refactoring y Correcciones
 * SonarLint
 * 
 * @author asana
 * @version 3, 12/06/2017 se implementa enum y se modifica conexi�n
 * 
 */
@ManagedBean
@ViewScoped

public class FichaTecnicaInmueble extends BeanBaseModal {

    private final String compania = SessionUtil.getCompania();
    private final String modulo = SessionUtil.getModulo();

    private final String seriePlaca;
    private boolean[] permisosAux;
    private String tipoFicha;
    private String elementoInicial;
    private String elementoFinal;
    private String ordenar;
    private String ordenarDato;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaINICIAL;
    private RegistroDataModelImpl listaFINAL;

    /**
     * Creates a new instance of FichaTecnicaInmueble
     */
    public FichaTecnicaInmueble() {
        numFormulario = GeneralCodigoFormaEnum.FICHA_TECNICA_INMUEBLE
                        .getCodigo();

        seriePlaca = "SERIE_PLACA";
    }

    @PostConstruct
    public void inicializar() {
        if (permisosAux == null) {
            Formulario form = SessionUtil.cargarFormulario(
                            numFormulario + "," + SessionUtil.getModulo());
            if (form == null) {
                SessionUtil.redireccionarMenuPermisos();
                return;
            }
            permisosAux = form.getPermisos();
            if (permisosAux == null || !permisosAux[3]) {
                SessionUtil.redireccionarMenuPermisos();
                return;
            }
        }
        abrirFormulario();
    }

    public void cargarListaINICIAL() {

        ordenarDato = seriePlaca;
        if (Integer.parseInt(tipoFicha) == 1) {
            if ("1".equals(ordenar)) {
                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FichaTecnicaInmuebleUrlEnum.URL2929
                                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                listaINICIAL = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true,
                                "ID");

            }
            else if ("3".equals(ordenar)) {
                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FichaTecnicaInmuebleUrlEnum.URL3030
                                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                listaINICIAL = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true,
                                "ID");

            }
            else {

                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FichaTecnicaInmuebleUrlEnum.URL3131
                                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                listaINICIAL = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true,
                                "ID");

            }
        }
        else if (Integer.parseInt(tipoFicha) == 2) {
            if ("1".equals(ordenar)) {
                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FichaTecnicaInmuebleUrlEnum.URL3232
                                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                listaINICIAL = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true,
                                "ID");

            }
            else if ("3".equals(ordenar)) {

                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FichaTecnicaInmuebleUrlEnum.URL3333
                                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                listaINICIAL = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true,
                                "ID");

            }
            else {
                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FichaTecnicaInmuebleUrlEnum.URL3434
                                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                listaINICIAL = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true,
                                "ID");
            }
        }

    }

    public void cargarListaFINAL() {
        ordenarDato = seriePlaca;
        if (Integer.parseInt(tipoFicha) == 1) {
            if (Integer.parseInt(ordenar) == 1) {

                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FichaTecnicaInmuebleUrlEnum.URL3535
                                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                param.put(FichaTecnicaInmuebleEnum.PARAM0.getValue(),
                                elementoInicial);

                listaFINAL = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true,
                                "ID");

            }
            else if (Integer.parseInt(ordenar) == 3) {
                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FichaTecnicaInmuebleUrlEnum.URL3636
                                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                param.put(FichaTecnicaInmuebleEnum.PARAM0.getValue(),
                                elementoInicial);

                listaFINAL = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true,
                                "ID");
            }
            else {

                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FichaTecnicaInmuebleUrlEnum.URL3737
                                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                param.put(FichaTecnicaInmuebleEnum.PARAM0.getValue(),
                                elementoInicial);

                listaFINAL = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true,
                                "ID");
            }
        }
        else if (Integer.parseInt(tipoFicha) == 2) {
            if (Integer.parseInt(ordenar) == 1) {
                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FichaTecnicaInmuebleUrlEnum.URL3838
                                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                param.put(FichaTecnicaInmuebleEnum.PARAM0.getValue(),
                                elementoInicial);

                listaFINAL = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true,
                                "ID");

            }
            else if (Integer.parseInt(ordenar) == 3) {
                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FichaTecnicaInmuebleUrlEnum.URL3939
                                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                param.put(FichaTecnicaInmuebleEnum.PARAM0.getValue(),
                                elementoInicial);

                listaFINAL = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true,
                                "ID");

            }
            else {
                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FichaTecnicaInmuebleUrlEnum.URL4040
                                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                param.put(FichaTecnicaInmuebleEnum.PARAM0.getValue(),
                                elementoInicial);

                listaFINAL = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true,
                                "ID");
            }
        }
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        String informe = " ";
        String condicion = " ";
        String condicionSub = " ";
        Map<String, Object> parametros = new HashMap<>();
        HashMap<String, Object> reemplazar = new HashMap<>();
        ordenarDato = seriePlaca;

        if (Integer.parseInt(tipoFicha) == 1) {
            if (Integer.parseInt(ordenar) == 1) {
                ordenarDato = GeneralParameterEnum.NOMBRE.getName();
            }
            else if (Integer.parseInt(ordenar) == 3) {
                ordenarDato = "ID_PREDIO";
            }
            condicion = " AND  PREDIOS." + ordenarDato + " BETWEEN '"
                + elementoInicial + "' AND   '" + elementoFinal + "' "
                + " ORDER BY PREDIOS." + ordenarDato;
            condicionSub = " WHERE ADICIONES.COMPANIA = '" + compania + "' "
                + " AND ADICIONES.ID_PREDIO = $P{PR_ID_PREDIO}";
            informe = "000543FICHATECNICAPREDIOS";

        }
        else if (Integer.parseInt(tipoFicha) == 2) {
            if (Integer.parseInt(ordenar) == 1) {
                ordenarDato = GeneralParameterEnum.NOMBRE.getName();
            }
            else if (Integer.parseInt(ordenar) == 3) {
                ordenarDato = "ID_VIA";
            }
            condicion = " AND  VIAS." + ordenarDato + " BETWEEN '"
                + elementoInicial + "' AND   '" + elementoFinal + "' "
                + " ORDER BY VIAS." + ordenarDato;

            informe = "000542FICHATECNICAVIAS";
        }
        reemplazar.put("compania", compania);
        reemplazar.put("condicion", condicion);
        reemplazar.put("condicionSub", condicionSub);
        parametros.put("PR_NOMBRECOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNombre());

        Reporteador.resuelveConsulta(informe, Integer.parseInt(modulo),
                        reemplazar, parametros);
        try {
            archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarTipoFicha() {
        // <CODIGO_DESARROLLADO>
        elementoInicial = null;
        elementoFinal = null;
        cargarListaINICIAL();
        ordenar = null;
        cambiarcmbOrdenadoPor();
        listaFINAL = null;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcmbOrdenadoPor() {
        // <CODIGO_DESARROLLADO>
        elementoInicial = null;
        elementoFinal = null;
        cargarListaINICIAL();
        listaFINAL = null;
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaINICIAL(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if ("1".equals(ordenar)) {
            elementoInicial = SysmanFunciones.nvl(registroAux.getCampos()
                            .get(GeneralParameterEnum.NOMBRE.getName()), "")
                            .toString();
        }
        else if ("2".equals(ordenar)) {
            elementoInicial = SysmanFunciones
                            .nvl(registroAux.getCampos().get(seriePlaca), "")
                            .toString();
        }
        else if ("3".equals(ordenar)) {
            elementoInicial = SysmanFunciones
                            .nvl(registroAux.getCampos().get("ID"), "")
                            .toString();
        }
        elementoFinal = null;
        cargarListaFINAL();
    }

    public void seleccionarFilaFINAL(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (Integer.parseInt(ordenar) == 1) {
            elementoFinal = SysmanFunciones.nvl(registroAux.getCampos()
                            .get(GeneralParameterEnum.NOMBRE.getName()), "")
                            .toString();
        }
        else if (Integer.parseInt(ordenar) == 2) {
            elementoFinal = SysmanFunciones
                            .nvl(registroAux.getCampos().get(seriePlaca), "")
                            .toString();
        }
        else if (Integer.parseInt(ordenar) == 3) {
            elementoFinal = SysmanFunciones
                            .nvl(registroAux.getCampos().get("ID"), "")
                            .toString();
        }
    }

    public String getTipoFicha() {
        return tipoFicha;
    }

    public void setTipoFicha(String tipoFicha) {
        this.tipoFicha = tipoFicha;
    }

    public String getElementoInicial() {
        return elementoInicial;
    }

    public void setElementoInicial(String elementoInicial) {
        this.elementoInicial = elementoInicial;
    }

    public String getElementoFinal() {
        return elementoFinal;
    }

    public void setElementoFinal(String elementoFinal) {
        this.elementoFinal = elementoFinal;
    }

    public String getOrdenar() {
        return ordenar;
    }

    public void setOrdenar(String ordenar) {
        this.ordenar = ordenar;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaINICIAL() {
        return listaINICIAL;
    }

    public void setListaINICIAL(RegistroDataModelImpl listaINICIAL) {
        this.listaINICIAL = listaINICIAL;
    }

    public RegistroDataModelImpl getListaFINAL() {
        return listaFINAL;
    }

    public void setListaFINAL(RegistroDataModelImpl listaFINAL) {
        this.listaFINAL = listaFINAL;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR530-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 15, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    public boolean[] getPermisosAux() {
        return permisosAux;
    }

    public void setPermisosAux(boolean[] permisosAux) {
        this.permisosAux = permisosAux;
    }

}
