//variable para definir el ángulo de rotación de la imagen
var angulo = 0;
//variable para definir la resolucion que se envìa al escaner
var resolucionEscaner = 600;
var ajustePantallaImagen = false;
var rutaImg = "/sysmanWeb/images/escaner/prueba.jpg";
var rutaPdf = "/sysmanWeb/images/escaner/test.pdf";
var rutaSrv = "/opt/jboss/wildfly11/welcome-content/util/";
var directory="";
var archivo="";
var nombreImagenSrv = "imagenGuardada.jpg";
var DWObject;
var base64Imagen = "";
const endpoint = "http://190.90.143.59:8040/";

/**
 * modal
 */
var modal = new tingle.modal({
    footer: false,
    stickyFooter: false,
    closeMethods: ['overlay', 'button', 'escape'],
    closeLabel: "Cerrar",
   
    onOpen: function () {
        console.log('modal open');
    },
    onClose: function () {
        console.log('modal closed');
    },
    beforeClose: function () {
        // here's goes some logic
        // e.g. save content before closing the modal
        return true; // close the modal
        return false; // nothing happens
    }
});


if (rutaImg.lastIndexOf("/") && rutaImg.lastIndexOf("/")>=0){
    directory=rutaImg.substring(0,rutaImg.lastIndexOf("/"))
}
if (rutaImg.lastIndexOf("/")  ){
  archivo=rutaImg.substring(directory.length+1);
}
else{
   archivo=rutaImg;
}
document.getElementById("Destino").value=directory;
document.getElementById("fichero").value=archivo;
if(document.getElementById("Destino").value){
   document.getElementById("Destino").disabled=true;
}
if(document.getElementById("fichero").value){
   document.getElementById("fichero").disabled=true;
}


console.log(directory+";"+archivo);


/**
 * Estos elementos me permiten definir el movimiento
 * de la imagen durante un evento de click y arrastrar
 * La imagen permanece con posiciòn absoluta relativa al
 * contenedor
 */
var offset = [0, 0];
var divOverlay = document.getElementById("imagen");
var isDown = false;
const posicionInicialImagen = divOverlay.getBoundingClientRect();



//Aqui empieza la magia
divOverlay.addEventListener('mousedown', function (e) {
    isDown = true;
    offset = [
        divOverlay.offsetLeft - e.clientX,
        divOverlay.offsetTop - e.clientY
    ];
}, true);
divOverlay.addEventListener('mouseup', function () {
    isDown = false;
}, true);

divOverlay.addEventListener('mousemove', function (e) {
    event.preventDefault();
    if (isDown) {
        divOverlay.style.left = (e.clientX + offset[0]) + 'px';
        divOverlay.style.top = (e.clientY + offset[1]) + 'px';
    }
}, true);

/**
 * Evento para restablecer la posicion de
 *  la imagen cuando se hace doble click
 */
divOverlay.addEventListener('dblclick', function (e) {
    event.preventDefault();
    console.log(e);
    divOverlay.style = `position: absolute; left: ${posicionInicialImagen.left}; top: ${posicionInicialImagen.top}; `;
});


//Funcion para ejecutar el select del filtro del color
function gestionSelectColor() {

    var select = document.getElementById("seleccionColor");
    let grises = document.getElementById("grayScale");

    if (select.value == 'BN' || select.value == 'EG') {
        grises.value = "100";
        gestionEscalaGrises();
    } else {
        grises.value = "0";
        gestionEscalaGrises();
    }
};

/**
 * Funcion para aumentar el tamaño de la imagen
 * relativamente al tamaño del contenedor
 * 
 */
function agrandarImagen() {
    let imagen = document.getElementById("imagen");
    let box = document.getElementById("imagen");
    const anchoAum = 13.714285714285714285714285714286;
    const altoAum = 10;
    let anchoActual = imagen.width;
    let altoActual = imagen.height;
    console.log(anchoActual, altoActual);
    anchoActual += anchoAum;
    altoActual += altoAum;
    console.log(anchoActual, altoActual);
    imagen.style.width = anchoActual + "px";
    imagen.style.height = altoActual + "px";
}

/**
 * Funcion para disminuir el tamaño de la 
 * imagen relativamente al contenedor
 */
function encogerImagen() {
    let imagen = document.getElementById("imagen");
    let box = document.getElementById("imagen");
    const anchoAum = ajustePantallaImagen ? 13.714285714285714285714285714286 : 10;
    const altoAum = 10;
    let anchoActual = imagen.width;
    let altoActual = imagen.height;
    console.log(anchoActual, altoActual);
    anchoActual -= anchoAum;
    altoActual -= altoAum;
    console.log(anchoActual, altoActual);
    imagen.style.width = anchoActual + "px";
    imagen.style.height = altoActual + "px";
}

/**
 * Funcion que evalua y ejecuta un filtro
 * a la imagen para escala de grises
 */
function gestionEscalaGrises() {
    let valorGris = document.getElementById("grayScale");
    let imagen = document.getElementById("imagen");
    imagen.style.filter = "";
    imagen.style.filter = "grayscale(" + valorGris.value + "%)";
}

/**
 * Funcion que rota 90 grados la imagen
 * y la reestablece cuando se superan los 270 grados
 * @param {*} id 
 */
function rotar(id) {
    let x = document.getElementById(id);
    if (angulo >= 270) {
        angulo = 0;
        x.className = "";
    } else {
        angulo = angulo + 90;
    }
    x.classList.add("rotar" + angulo);
}

/**
 * Ajusta la imagen al contenedor
 * que se envia por parametro
 * @param {*} idContenedor 
 */
function ajustarAPantalla(idContenedor) {

    console.log(idContenedor);
    ajustePantallaImagen = true;
    let box = document.getElementById(idContenedor);
    box.style.width = "100%";
    box.style.height = "100%";


}
/**
 * Escala la imagen al contenedor
 * inicial, que se envia por parametro
 * @param {*} idContenedor 
 */
function escalarImagen(idContenedor) {
    ajustePantallaImagen = false;
    let box = document.getElementById(idContenedor);
    box.style.width = "300px";
    box.style.height = "300px";
}
/**
 * Realiza el cambio de resolucion de la
 * imagen recibiendo la resolucion por paràmetro
 */
function cambiarResImg(res) {
    let img = document.getElementById("imagen");
    image.style["image-resolution"] = res + "dpi";
}

/**
 * Gestiona el cambio de resolucion y lo guarda en 
 * una variable
 */
function gestionResolucion() {
    let select = document.getElementById("seleccionRes");
    var resolucionEscaner = select.value;

}

/**
 * Gestiona el cambio del brillo de la imagen
 * con el objeto que se define
 * en el html
 */
function cambiarBrillo() {
    let valorBrillo = document.getElementById("brillo");
    let imagen = document.getElementById("imagen");
    imagen.style.filter = "";
    imagen.style.filter = "brightness(" + valorBrillo.value + "%)";
}

/**
 * Gestiona el cambio del contraste de la imagen
 * con el objeto que se define
 * en el html
 */
function cambiarContraste() {
    let valorContraste = document.getElementById("contraste");
    let imagen = document.getElementById("imagen");
    imagen.style.filter = "";
    imagen.style.filter = "contrast(" + valorContraste.value + "%)";
}


function consumirOCR() {
    console.log("inicio funcion ocr");
    var data = JSON.stringify({
        "ruta": rutaSrv,
        "nombreArchivo": nombreImagenSrv,
        "idioma": "spa"
    });

    console.log(data);

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = false;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            console.log("termino consumo ocr");
            console.log(this.responseText);
            let res = JSON.parse(this.responseText).cuerpo;
            let ocr = res ? res.ocr: "";
            abrirModal(ocr);


        }
    });

    xhr.open("POST", endpoint+"sysman-erp-servicio-base/servicio/servicioGenerico/sisOcr");
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.setRequestHeader("Accept", "*/*");
    xhr.setRequestHeader("cache-control", "no-cache");

    xhr.send(data);



    /* 
        let response = XMLHttpRequest.response;
        console.log(response); */
}


function generarQr() {
    let data = JSON.stringify({
        "texto": "192.168.1.249",
        "rutaImagen": rutaSrv
    });

    let xhr = new XMLHttpRequest();
    xhr.withCredentials = false;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            console.log("termino consumo generar qr");
            console.log(this.responseText);
        }
    });

    xhr.open("POST", endpoint+"sysman-erp-servicio-base/servicio/servicioGenerico/qr");
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.setRequestHeader("Accept", "*/*");
    xhr.setRequestHeader("cache-control", "no-cache");

    xhr.send(data);
}


function leerCodigoBarras() {
    console.log("inicio a leer codigo barras de imagen :", nombreImagenSrv);
    var data = JSON.stringify({
        "rutaImagen": rutaSrv + nombreImagenSrv
    });

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = false;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            
            console.log(this.responseText);
            let cuerpo = JSON.parse(this.responseText).cuerpo;
            if(cuerpo){
               abrirModal(cuerpo.texto);
            }else{
                alert("No se encontró un código de barras o qr en la imágen");
            }
            console.log(JSON.parse(this.responseText).cuerpo);
        }else {
            console.log(this.response);
        }
       
    });

    xhr.open("POST", endpoint+"sysman-erp-servicio-base/servicio/servicioGenerico/lectorBarras");
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.setRequestHeader("Accept", "*/*");
    xhr.setRequestHeader("cache-control", "no-cache");

    xhr.send(data);
}


function leerContenidoArchivo() {
    var data = JSON.stringify({
        "ruta": rutaSrv + "ESCANER.txt"
    });

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = false;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            console.log("termino leer contenido txt");
            console.log(this.responseText);
        }
    });

    xhr.open("POST", endpoint+"sysman-erp-servicio-base/servicio/servicioGenerico/leer");
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.setRequestHeader("Accept", "*/*");
    xhr.setRequestHeader("cache-control", "no-cache");

    xhr.send(data);
}

function consumoGuardar() {
    console.log("inicio consumo guardar");
    let data = JSON.stringify({
        "nombre": nombreImagenSrv,
        "base64": base64Imagen,
        "ruta": rutaSrv
    });
    console.log(data);

    let xhr = new XMLHttpRequest();
    xhr.withCredentials = false;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            console.log(this.status);
            alert("Terminó de guardar imagen");
            console.log("termino consumo guardar ok");
        }
        else {
            console.log("termino consumo guardar: ", this.readyState);
            console.log(JSON.parse(this.responseText));
        }
    });

    xhr.open("POST", endpoint+"sysman-erp-servicio-base/servicio/servicioGenerico/guardar");
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.setRequestHeader("Accept", "*/*");
    xhr.setRequestHeader("cache-control", "no-cache");

    xhr.send(data);
}


/**
 * Escaner
 */

function Dynamsoft_OnReady() {
    DWObject = Dynamsoft.WebTwainEnv.GetWebTwain('dwtcontrolContainer');
    document.getElementById("dwtcontrolContainer").classList.add("invisible");
}
//Callback functions for async APIs
function OnSuccess() {
    console.log('successful');
    resetDw();
}
function OnFailure(errorCode, errorString) {
    alert(errorString);
}
function btn_SaveOnClick() {
    console.log("funcion guardar imagen");
    console.log(DWObject);
    if (DWObject.HowManyImagesInBuffer == 0) {
        DWObject.LoadImage(rutaImg, function () {
            guardarImagenSrv();
        });
    } else if (DWObject.HowManyImagesInBuffer == 1) {
        DWObject.IfShowFileDialog = false;
        DWObject.SaveAsJPEG(rutaImg, 0, OnSuccess, OnFailure);
    } else {
        alert(`Se han escaneado varias imagenes, y se procede a guardar 
                en la ruta ${rutaPdf}`);
        DWObject.SaveAllAsPDF(rutaPdf, function () {
            console.log("exito guardando pdf");
            resetDw();
        }, function () {
            console.log("error guardando pdf");
        });
    }


}

function recargarImagen() {
    console.log("recarga imagen");
    document.getElementById('imagen').innerHtml = location.reload();
}


function AcquireImage() {

    document.getElementById("dwtcontrolContainer").classList.add("invisible");
    if (DWObject) {
        DWObject.SelectSource(function () {
            DWObject.OpenSource();
            DWObject.AcquireImage(function () {
                guardarImagenSrv();
                btn_SaveOnClick();
            });
        },
            function () { console.log("SelectSource failed!"); });
    }

}

function resetDw() {
    DWObject.HowManyImagesInBuffer = 0;
    recargarImagen();

}




/**
 * Genera el base 64 de la imagen
 * y lo guarda en la variable asignada
 */
function crearBase64() {
    DWObject.GetSelectedImagesSize(EnumDWT_ImageType.IT_JPG);
    base64Imagen = DWObject.SaveSelectedImagesToBase64Binary();
    console.log("base 64: ", base64Imagen);
    consumoGuardar();
}

function guardarImagenSrv() {
    crearBase64();

}


function abrirModal(str) {
    // set content
    modal.setContent(`<h4>Resultado </h4><br/><p>${str}</p>`);
    modal.open();
}




