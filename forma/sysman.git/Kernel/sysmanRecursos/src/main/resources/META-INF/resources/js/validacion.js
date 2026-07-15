/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var aux;
var ind;
function validarVacios(campos) {
    var val;
    var rta = true;
    var topO;
    var leftO
    var total;
    var widthO;
    var indGrande = false;
    for (var i = 0; i < campos.length; i++) {
        try {
            if (PF(campos[i]) === undefined || PF(campos[i]) === null) {
                continue;
            }
            if (campos[i].substring(0, 2) === 'CP') {
                if (PF(campos[i]).inputExternal != undefined && PF(campos[i]).inputExternal != null)
                    val = PF(campos[i]).inputExternal[0].value;
                else if (PF(campos[i]).input != undefined)
                    val = PF(campos[i]).input[0].value;
                else
                    val = PF(campos[i]).jq[0].value
            } else {
                try {
                    val = PF(campos[i]).input[0].value;
                } catch (e) {
                    val = PF(campos[i]).jq[0].value;
                    indGrande = true;
                }
            }
            if (val === undefined || val === null || val.length === 0 || (/^\s+$/.test(val) && !indGrande)) {
                topO = PF(campos[i]).jq.css('top').replace('px', '');
                leftO = PF(campos[i]).jq.css('left').replace('px', '');
                widthO = PF(campos[i]).jq.css('width').replace('px', '');
                total = parseInt(leftO) + parseInt(widthO);
                $('#spe' + campos[i]).remove();

                if (indGrande) {
                    total = total + 20;
                    PF('BT' + campos[i]).jq.addClass('errorRq');
                    indGrande = false;
                }
                PF(campos[i]).jq.after('<span id="spe' + campos[i] + '" class="error" style="top: ' + topO + 'px; left: ' + total + 'px;">Campo requerido</span>');


                PF(campos[i]).jq.addClass('errorRq');
                rta = false;
            } else {
                $('#spe' + campos[i]).remove();
                PF(campos[i]).jq.removeClass('errorRq');
                ;
                $('#spe' + campos[i]).remove();
                if (indGrande) {
                    PF('BT' + campos[i]).jq.removeClass('errorRq');
                    indGrande = false;
                }
            }
        } catch (e) {

        }
    }
    return rta;
}


function validarVaciosModal(campos) {
    var val;
    var rta = true;
    var ind = false;
    var grande = false;
    for (var i = 0; i < campos.length; i++) {
        try {
            ind = false;
            grande = false;
            if (campos[i].substring(0, 2) === 'CP') {
                if (PF(campos[i]).inputExternal != undefined && PF(campos[i]).inputExternal != null) {
                    val = PF(campos[i]).inputExternal[0].value;
                    ind = true;
                } else if (PF(campos[i]).input != undefined) {
                    val = PF(campos[i]).input[0].value;
                } else {
                    val = PF(campos[i]).jq[0].value
                }
            } else {
                if (PF(campos[i]).jq[0].value != undefined && PF(campos[i]).jq[0].value != null) {
                    val = PF(campos[i]).jq[0].value
                    grande = true;
                } else {
                    val = PF(campos[i]).input[0].value;
                }
            }

            if (val === undefined || val === null || val.length === 0 || (/^\s+$/.test(val) && !indGrande)) {
                if (!grande) {
                    $('#spe' + campos[i]).remove();
                    PF(campos[i]).jq.after('<span id="spe' + campos[i] + '"  class="obligatorioContinuo" >*</span>');
                    PF(campos[i]).jq.addClass('errorRq');
                    if (ind) {
                        PF(campos[i]).jq.addClass('errorRq');
                    }
                } else {
                    $('#spe' + campos[i]).remove();
                    PF("BT" + campos[i]).jq.after('<span id="spe' + campos[i] + '"  class="obligatorioContinuo" >*</span>');
                    PF(campos[i]).jq.addClass('errorRq');
                    PF("BT" + campos[i]).jq.addClass('errorRq');
                }
                rta = false;
            } else {
                PF(campos[i]).jq.removeClass('errorRq');
                $('#spe' + campos[i]).remove();
                if (ind) {
                    PF(campos[i]).jq.removeClass('errorRq');
                }
            }
        } catch (e) {

        }
    }
    return rta;
}

function restaurarCampos(campos) {
    for (var i = 0; i < campos.length; i++) {
        try {
            PF(campos[i]).jq.removeClass('errorRq');
            $('#spe' + campos[i]).remove();
            if (campos[i].substring(0, 2) == 'CB' && PF(campos[i]).jq.is('input')) {
                PF('BT' + campos[i]).jq.removeClass('errorRq');
            }
        } catch (e) {

        }
    }
}


function validarVaciosViewModal(campos) {
    var val;
    var rta = true;
    var topO;
    var leftO
    var total;
    var widthO;
    var ind = false;
    var grande = false;
    for (var i = 0; i < campos.length; i++) {
        try {
            ind = false;
            grande = false;
            if (campos[i].substring(0, 2) === 'CP') {
                if (PF(campos[i]).inputExternal != undefined && PF(campos[i]).inputExternal != null) {
                    val = PF(campos[i]).inputExternal[0].value;
                    ind = true;
                } else if (PF(campos[i]).input != undefined) {
                    val = PF(campos[i]).input[0].value;
                } else {
                    val = PF(campos[i]).jq[0].value
                }
            } else {
                if (PF(campos[i]).jq[0].value != undefined && PF(campos[i]).jq[0].value != null) {
                    val = PF(campos[i]).jq[0].value
                    grande = true;
                } else {
                    val = PF(campos[i]).input[0].value;
                }
            }
            if (val === undefined || val === null || val.length === 0 || (/^\s+$/.test(val) && !indGrande)) {
                topO = PF(campos[i]).jq.css('top').replace('px', '');
                leftO = PF(campos[i]).jq.css('left').replace('px', '');
                widthO = PF(campos[i]).jq.css('width').replace('px', '');
                total = parseInt(leftO) + parseInt(widthO) + 4;
                if (!grande) {
                    $('#spe' + campos[i]).remove();
                    PF(campos[i]).jq.after('<span id="spe' + campos[i] + '" style="top: ' + topO + 'px; left: ' + total + 'px; color:red; position:absolute;" )>*</span>');
                    PF(campos[i]).jq.addClass('errorRq');
                } else {
                    total += 20;
                    $('#spe' + campos[i]).remove();
                    PF("BT" + campos[i]).jq.after('<span id="spe' + campos[i] + '" style="top: ' + topO + 'px; left: ' + total + 'px; color:red; position:absolute;" )>*</span>');
                    PF(campos[i]).jq.addClass('errorRq');
                    PF("BT" + campos[i]).jq.addClass('errorRq');
                }
                rta = false;

            } else {
                $('#spe' + campos[i]).remove();
                PF(campos[i]).jq.removeClass('errorRq');
                $('#spe' + campos[i]).remove();
                if (grande) {
                    PF('BT' + campos[i]).jq.removeClass('errorRq');

                }
            }
        } catch (e) {

        }
    }
    return rta;
}

function validarFechasPrestamo() {
    var fechaIni = document.getElementById('FR83_nuevo:CP2310_input').getAttribute('value');
    var fechaFin = document.getElementById('FR83_nuevo:CP2311_input').getAttribute('value');
    alert(fechaIni);
    alert(fechaFin);
}


function vaciarSubforms(campos) {
    for (var i = 0; i < campos.length; i++) {
        if (PF('table' + campos[i]) != undefined && PF('table' + campos[i]) != null) {
            PF('table' + campos[i]).clearFilters();
            PF('table' + campos[i]).filter();
        }
    }
}

function mostrarEspera() {
    PF('blockNuevo').show();
}

function ocultarEspera() {
    PF('blockNuevo').hide();
}

function mostrarEsperaCont() {
    PF('blockCarga').show();
}

function ocultarEsperaCont() {
    PF('blockCarga').hide();
}


function mostrarMensaje(mensaje, tipo) {
    PF('alert').renderMessage({'summary': document.getElementById('empresaParametrizada').outerText+' Software',
        'detail': mensaje,
        'severity': tipo});
}


function mostrarMensajeError(mensaje) {
    mostrarMensaje(mensaje, 'error');
}

function mostrarMensajeInformacion(mensaje) {
    mostrarMensaje(mensaje, 'info');
}


function mostrarMensajeAdvertencia(mensaje) {
    mostrarMensaje(mensaje, 'warn');
}

function mostrarMensajeFatal(mensaje) {
    mostrarMensaje(mensaje, 'fatal');
}

function validarCargaGrandes() {
    asignarEventoContGrande();
    for (i in PrimeFaces.widgets) {
        try {
            if (i.indexOf('TS') == 0) {
                for (var j = 0; j < PF(i).navContainerItems.length; j++) {
                    PF(i).select(j);
                }
                PF(i).select(0);
            }
        } catch (e) {
        }
    }
    for (i in PrimeFaces.widgets) {
        try {
            if (i.indexOf('TBCB') == 0 || i.indexOf('TBSCB') == 0 || (i.indexOf('LM') == 0 && i.indexOf('s') != i.length - 1)) {
                PF(i).nuevo = true;
                PF(i).getPaginator().setPage(0);
            }
            if (i.indexOf('LM') == 0 && i.indexOf('s') == i.length - 1) {
                PF(i).filter();

            }
        } catch (e) {
        }
    }

}


function validarEmail(campo) {
    var rta = true;
    var emailReg = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
    var email = PF(campo).jq[0].value;
    if (email !== "") {
        if (!emailReg.test(email)) {
            PF(campo).jq[0].value = "";
            mostrarMensajeError("Por favor ingrese una dirección de correo valida");
            PF(campo).jq.addClass('errorRq');
            rta = false;
        } else {
            PF(campo).jq.removeClass('errorRq');
            rta = true;
        }
    }
    return rta;
}

function cerrarModalDefault() {
	var documento = $(window.parent.parent.document);
	$(documento).find('a.ui-dialog-titlebar-icon.ui-dialog-titlebar-close.ui-corner-all')[0].click();
	return false;	
}

function cerrarModalContinuoDefault() {
	window.parent.$('a.ui-dialog-titlebar-icon.ui-dialog-titlebar-close.ui-corner-all').click();
	return false;	
}


function cerrarModalContinuoModulo() {
	window.parent.parent.$('a.ui-dialog-titlebar-icon.ui-dialog-titlebar-close.ui-corner-all').click();
	return false;	
}

function validarNumericosModal(campos) {
    var val;
    var rta = true;
    var ind = false;
    var grande = false;
    console.log("Iniciando validación de formato numérico opcional");
    
    for (var i = 0; i < campos.length; i++) {
        try {
            ind = false;
            grande = false;
            
            if (campos[i].substring(0, 2) === 'CP') {
                if (PF(campos[i]).inputExternal != undefined && PF(campos[i]).inputExternal != null) {
                    val = PF(campos[i]).inputExternal[0].value;
                    ind = true;
                } else if (PF(campos[i]).input != undefined) {
                    val = PF(campos[i]).input[0].value;
                } else {
                    val = PF(campos[i]).jq[0].value;
                }
            } else {
                if (PF(campos[i]).jq[0].value != undefined && PF(campos[i]).jq[0].value != null) {
                    val = PF(campos[i]).jq[0].value;
                    grande = true;
                } else {
                    val = PF(campos[i]).input[0].value;
                }
            }


            var valorTrim = (val !== undefined && val !== null) ? val.toString().trim() : "";
            
            if (valorTrim === "") {
                PF(campos[i]).jq.removeClass('errorRq');
                $('#spe' + campos[i]).remove();
                if (grande) PF("BT" + campos[i]).jq.removeClass('errorRq');
                continue; 
            }

            var esNumerico = /^\d+$/.test(valorTrim);

            if (!esNumerico) {
            	
                if (!grande) {
                    $('#spe' + campos[i]).remove();
                    PF(campos[i]).jq.after('<span id="spe' + campos[i] + '" class="obligatorioContinuo">*</span>');
                    PF(campos[i]).jq.addClass('errorRq');
                } else {
                    $('#spe' + campos[i]).remove();
                    PF("BT" + campos[i]).jq.after('<span id="spe' + campos[i] + '" class="obligatorioContinuo">*</span>');
                    PF(campos[i]).jq.addClass('errorRq');
                    PF("BT" + campos[i]).jq.addClass('errorRq');
                }
                rta = false;
            } else {
            	
                PF(campos[i]).jq.removeClass('errorRq');
                $('#spe' + campos[i]).remove();
                if (grande) PF("BT" + campos[i]).jq.removeClass('errorRq');
            }

        } catch (e) {
            console.error("Error validando campo: " + campos[i], e);
        }
    }
    return rta;
}