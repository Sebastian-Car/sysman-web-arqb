parser = new DOMParser();
var xmlDoc;
var menus;
var rtaXML;
var ruta;


function setRuta(rut) {
    ruta = rut;
}
function setXml(menusJava) {
    xmlDoc = parser.parseFromString(menusJava, 'text/xml');
    rtaXML = new objXML(xmlDoc);
}



function pintarNavegacion(padre, ind) {
    var menuPadre = rtaXML.getById(padre);
    //si entra menu atras debe pintar el boton atras

    //Editado por cmanrique
    //Se agrega la opcion de pintar los menus anteriores con los respectivos eventos
    if (padre == '0') {
        document.getElementById('panelAccion').innerHTML = '<div id="men' + padre + '"class="eight columns" style="background:#E6EFF9; width:auto; float:left; cursor: default;"> <img src="images/menus/48/' + rtaXML.getAtribVal(menuPadre, 'I') + '" >	<span > &lt;&lt; ' + rtaXML.getAtribVal(menuPadre, 'N') + '</span></div>';
        return '';
    } else {
        var menuAbuelo = rtaXML.getById(rtaXML.getAtribVal(menuPadre, "class"));
        var aux = document.getElementById('panelAccion').innerHTML;
        var id = rtaXML.getAtribVal(menuPadre, "class");
        var i = aux.indexOf('<div id="men' + id + '"');
        if (i > -1) {
            aux = aux.substring(0, i);
        } else {
            i = aux.indexOf('id=\"men' + id + '\"');
            aux = aux.substring(0, i);
            aux = aux.substring(0, aux.lastIndexOf('<div'));
        }
        var evento = "PF('men').jq[0].value='" + id + "';";
        aux = pintarNavegacion(id, false) + '<div id="men' + id + '"class="eight columns" onclick="' + evento + 'pintarMenus(' + id + ');" style="width:auto; float:left;"> <img src="images/menus/48/' + rtaXML.getAtribVal(menuAbuelo, 'I') + '" >	<span > &lt;&lt; ' + rtaXML.getAtribVal(menuAbuelo, 'N') + '</span></div>';
        if (ind) {
            document.getElementById('panelAccion').innerHTML = aux + '<div id="men' + padre + '"class="eight columns" style="background:#E6EFF9; width:auto; float:left; cursor: default; "> <img src="images/menus/48/' + rtaXML.getAtribVal(menuPadre, 'I') + '" >	<span > &lt;&lt; ' + rtaXML.getAtribVal(menuPadre, 'N') + '</span></div>';
        }
        return aux;
    }

}

function pintarMenus(padre) {

    $('#panelMenu')[0].innerHTML = '';
    $('#panelMenu')[0].style.left = "-1000px";
    pintarNavegacion(padre, true);

    
    rcMenuBarra([ {name:'menu', value:padre} ]); 
    
    menus = rtaXML.getClass(padre);
    var evento;
    var data = "PrimeFaces.ab({s:'opsMenu:menu-10',e:'dialogReturn',p:'opsMenu:menu-10'},ext);;";
    for (var i = 0; i < menus.length; i++) {
        //Insertar Menus en el content
        contenido = document.createElement("aside");
        clickMenu = null;
        if (menus[i].nodeName == 'P' && rtaXML.getAtribVal(menus[i], "P") != '' && (rtaXML.getAtribVal(menus[i], "CO") != 'RP' && rtaXML.getAtribVal(menus[i], "CO") != 'QR')) {
            evento = "PF('com').jq[0].value='" + rtaXML.getAtribVal(menus[i], "CO") + "'; PF('mod').jq[0].value='" + rtaXML.getAtribVal(menus[i], "A") + "'; PF('form').jq[0].value='" + rtaXML.getAtribVal(menus[i], "P") + "'; PF('menAct').jq[0].value='" + rtaXML.getAtribVal(menus[i], "id") + "'; PrimeFaces.ab({s:'opsMenu:menu-10',u:'opsMenu:alert'}); auditar(" + rtaXML.getAtribVal(menus[i], "id") + ",'"+ rtaXML.getAtribVal(menus[i], "N") + "'); return false;";
            contenido.innerHTML = '<a href="#"  data-dialogreturn="' + data + '" id="menu' + rtaXML.getAtribVal(menus[i], "id") + '" onclick="' + evento + '" title="' + rtaXML.getAtribVal(menus[i], "N") + '"><img src="images/menus/48/' + rtaXML.getAtribVal(menus[i], "I") + '" alt=""/><div><span>' + rtaXML.getAtribVal(menus[i], "N") + '</span></div></a>';
        } else if (menus[i].nodeName == 'M' && rtaXML.getAtribVal(menus[i], "P") != '') {
            evento = "PF('com').jq[0].value='" + rtaXML.getAtribVal(menus[i], "CO") + "'; PF('mod').jq[0].value='" + rtaXML.getAtribVal(menus[i], "A") + "'; PF('form').jq[0].value='" + rtaXML.getAtribVal(menus[i], "P") + "'; PF('menAct').jq[0].value='" + rtaXML.getAtribVal(menus[i], "id") + "'; PrimeFaces.ab({s:'opsMenu:menu-10',u:'opsMenu:alert'}); auditar(" + rtaXML.getAtribVal(menus[i], "id") + ",'"+ rtaXML.getAtribVal(menus[i], "N") + "'); return false;";
            contenido.innerHTML = '<a href="#"  data-dialogreturn="' + data + '" id="menu' + rtaXML.getAtribVal(menus[i], "id") + '" onclick="' + evento + '"title="' + rtaXML.getAtribVal(menus[i], "N") + '"><img src="images/menus/48/' + rtaXML.getAtribVal(menus[i], "I") + '" alt=""/><div><span>' + rtaXML.getAtribVal(menus[i], "N") + '</span></div></a>';
        } else if (menus[i].nodeName == 'P' && rtaXML.getAtribVal(menus[i], "P") != '' && (rtaXML.getAtribVal(menus[i], "CO") == 'RP' || rtaXML.getAtribVal(menus[i], "CO") == 'QR')) {
            evento = "PF('fil').jq[0].value='" + rtaXML.getAtribVal(menus[i], "F") + "'; PF('com').jq[0].value='" + rtaXML.getAtribVal(menus[i], "CO") + "'; PF('mod').jq[0].value='" + rtaXML.getAtribVal(menus[i], "A") + "'; PF('form').jq[0].value='" + rtaXML.getAtribVal(menus[i], "P") + "'; PF('menAct').jq[0].value='" + rtaXML.getAtribVal(menus[i], "id") + "';PrimeFaces.addSubmitParam('opsMenu',{'opsMenu:menu-10':'opsMenu:menu-10'}).submit('opsMenu');  auditar(" + rtaXML.getAtribVal(menus[i], "id") + ",'"+ rtaXML.getAtribVal(menus[i], "N") + "'); return false;";
            contenido.innerHTML = '<a href="#"  data-dialogreturn="' + data + '" id="menu' + rtaXML.getAtribVal(menus[i], "id") + '" onclick="' + evento + '" title="' + rtaXML.getAtribVal(menus[i], "N") + '"><img src="images/menus/48/' + rtaXML.getAtribVal(menus[i], "I") + '" alt=""/><div><span>' + rtaXML.getAtribVal(menus[i], "N") + '</span></div></a>';
        } else {
            evento = "PF('mod').jq[0].value='" + rtaXML.getAtribVal(menus[i], "A") + "'; PF('men').jq[0].value='" + rtaXML.getAtribVal(menus[i], "id") + "'; auditar(" + rtaXML.getAtribVal(menus[i], "id") + ",'"+ rtaXML.getAtribVal(menus[i], "N") + "'); ";
            contenido.innerHTML = '<a href="#"  id="menu' + rtaXML.getAtribVal(menus[i], "id") + '" onclick="pintarMenus(' + rtaXML.getAtribVal(menus[i], "id") + ');' + evento + '" title="' + rtaXML.getAtribVal(menus[i], "N") + '"><img src="images/menus/48/' + rtaXML.getAtribVal(menus[i], "I") + '" alt=""/><div><span>' + rtaXML.getAtribVal(menus[i], "N") + '</span></div></a>';
        }
        document.getElementById('panelMenu').appendChild(contenido);

    }
    // 09/04/2018 - Por solicitud de Henry Puerto solo se pintan los menus de primer nivel, en cuadros. 
//    if (padre==0) {
    	$('#panelMenu').animate({"left": "-4px"}, "slow");
    	$('.imgCompania').css("opacity","0");
//	}
//    else{
//    	$('.imgCompania').animate({"opacity": "1"}, "slow");
//    }
    

}


function pintarMenusFormulario(aux, padre) {


    var menuPadre = rtaXML.getById(padre);
    //si entra menu atras debe pintar el boton atras

    //Editado por cmanrique
    //Se agrega la opcion de pintar los menus anteriores con los respectivos eventos
    if (padre == '0')
        document.getElementById('panelAccion').innerHTML = aux;
    else {
        var menuAbuelo = rtaXML.getById(rtaXML.getAtribVal(menuPadre, "class"));
        //aux = document.getElementById('panelAccion').innerHTML+aux;
        var id = rtaXML.getAtribVal(menuPadre, "class");
        var i = aux.indexOf('<div id="men' + id + '"');
        if (aux == "") {
            aux = '<div id="men' + padre + '"class="eight columns" style="background:#E6EFF9; width:auto; float:left; cursor: default; "> <img src="' + ruta + '/images/menus/48/' + rtaXML.getAtribVal(menuPadre, 'I') + '" >	<span > &lt;&lt; ' + rtaXML.getAtribVal(menuPadre, 'N') + '</span></div>';
        }

        aux = '<div id="men' + id + '"class="eight columns" onclick="pintarMenus(' + id + ')" style="width:auto; float:left;"> <img src="' + ruta + '/images/menus/48/' + rtaXML.getAtribVal(menuAbuelo, 'I') + '" >	<span > &lt;&lt; ' + rtaXML.getAtribVal(menuAbuelo, 'N') + '</span></div>' + aux;
        pintarMenusFormulario(aux, id);
    }

}

function auditar(menu, ruta){
	menuAditoria([{name:'menu', value:menu},
				  {name:'ruta', value:ruta}]);
}

function verMenu(modulo, frm, menu, com = 'FR', fil = null){
	  PF('com').jq[0].value = com; 
	  PF('mod').jq[0].value = modulo; 
	  PF('form').jq[0].value = frm; 
	  PF('menAct').jq[0].value = menu;
	  
	  PF('fil').jq[0].value = fil; 

	  if (fil!=null) {
		  console.log("fil");
		  PrimeFaces.addSubmitParam('opsMenu',{'opsMenu:menu-10':'opsMenu:menu-10'}).submit('opsMenu');
	  }
	  else {
		  console.log("FR");
		  PrimeFaces.ab({s:'opsMenu:menu-10',u:'opsMenu:alert'});  
	  }
	  
	  return false;
	}

	
