var mymap = L.map("mapa").setView([2.439944, -76.605949], 12);
var markers={};
var seletedMarkers={};

navigator.geolocation.getCurrentPosition( success, error, {
  enableHighAccuracy: true,
  timeout: 6000,
  maximumAge: 0
});

function success(position) {
  var coordenadas = position.coords;
  console.log('Tu posición actual es:');
  console.log('Latitud : ' + coordenadas.latitude);
  console.log('Longitud: ' + coordenadas.longitude);
  console.log('Más o menos ' + coordenadas.accuracy + ' metros.');
};

function error(error) {
  console.warn('Error al consultar posición, detalles: ' + error.code + ': ' + error.message);
};

L.tileLayer(
    "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
).addTo(mymap);


function loadMaker(data){
   var marker=new L.Marker([data.CX, data.CY]);
    mymap.addLayer(marker);
    var myPopup = L.popup({className: 'custom-popup'}).setContent(
        "<table style='border-collapse: collapse; width: 100%; font-size: 12px;border: 1px solid #CCC; font-family: Arial, Helvetica, sans-serif;' border='1'>"+
        "<thead style='background-color: #104E8B; color: #FFF;font-weight: bold;text-align: center;'><tr><th colspan='5'><strong>Registro Cultural</strong></th></tr></thead>"+
        "<tbody>"+
        "<tr><td><strong>Identificador</strong></td><td colspan='4' style='text-align: left;'>"+(data.ID?data.ID:'-')+"</td></tr>"+
        "<tr><td><strong>Bandera</strong></td><td colspan='4' style='text-align: left;'><img src='sic/r/files/static/v48/"+(data.BANDERA?data.BANDERA:'-')+" width=30 height=30 /> </td></tr>"+
        "<tr><td><strong>Municipio</strong></td><td colspan='4' style='text-align: left;'>"+(data.NOM_MUNICIPIO?data.NOM_MUNICIPIO:'-')+"</td></tr>"+
        "<tr><td><strong>Registro</strong></td><td colspan='4' style='text-align: left;'>"+(data.REGISTRO?data.REGISTRO:'-')+"</td></tr>"
    );  
    marker.bindPopup(myPopup);
    markers[data.DANE_SEDE]=marker;
}


function clearSeletedMarkerOnMap(){
   seletedMarkers={}
   mymap.setView([2.439944, -76.605949], 12);
   loadMap([]);
}


function clearMap(){
   for(const key in markers) {
       mymap.removeLayer(markers[key]);
   }  
   markers={};
}

//al hacer click en la grilla abrir el popup y centrar el mapa
function showMarker(marker){
    mymap.panTo(new L.LatLng(marker.lat,marker.long));
    //mymap.setView([marker.lat, marker.long], 10);
    mymap.flyTo([marker.lat, marker.long], 13);
    markers[marker.dane].openPopup();
}

//al ir agregando datos en la grilla se agreagan y remueven los markers
function loadMap(mapMakers){
    clearMap();
    var latlong=[];
    for(i=0;i<mapMakers.length;i++){
      loadMaker(mapMakers[i]);
      //if(i==mapMakers.length-1){
          //centrar mapa en la primera fila
          //showMarker(mapMakers[i]);
      //}
       var coordenada=[];
       coordenada.push(mapMakers[i].CX);
       coordenada.push(mapMakers[i].CY);
       latlong.push(coordenada);
    } 
    if(mapMakers.length>0){
        mymap.fitBounds(latlong);
        //mymap.setZoom(mymap.getZoom()-1);
    }
}

function loadSeletedMarkersOnMap(){
	var markers=[];
	for(const key in seletedMarkers) {
        var marker={};
        marker.CX=seletedMarkers[key].CX.replace(',', '.');
        marker.CY=seletedMarkers[key].CY.replace(',', '.');
        marker.ID=seletedMarkers[key].ID;
        marker.NOM_MUNICIPIO=seletedMarkers[key].NOM_MUNICIPIO;
        marker.REGISTRO=seletedMarkers[key].REGISTRO;
        marker.BANDERA=seletedMarkers[key].BANDERA;
        markers.push(marker);
	}  
	loadMap(markers);
}





