

<a href="#"><strong><i class="glyphicon glyphicon-briefcase"></i>
		Recepci�n de Items de Auditor�a</strong></a>
<hr>

<button id="pool" class="btn btn-success">Poll</button>

<div id="recepcion">
	<br>
	<h4>Listado de Informes de Auditor�a</h4>
	<div class="input-group">
		<input id="filtrado" type="text" class="form-control" placeholder="Filtrar tabla">
	</div>
	<br>
	<table class="table">
		<tr>
			<th>M�dulo</th>
			<th>Descripci�n</th>
			<th>Fecha</th>
			<th>Hora</th>
		</tr>
		<tbody id="tbody">

		</tbody>
	</table>
</div>


<script type="text/javascript">
	$(document).ready(function() {
		var ip = "localhost";
		var puerto = "8080";
		var flag = 0;

		$("#pool").click(function(){

			if(flag == 0){
				flag = 1;
				$("#pool").removeClass("btn-success");
				$("#pool").addClass("btn-warning");
			}
			else{
				$("#pool").removeClass("btn-warning");
				$("#pool").addClass("btn-success");
				flag = 0;
			}
			
			setInterval(function() {
				if(flag == 1){
				$.ajax({
					type : "GET",
					dataType : "json",
					url : "restLog/logEmail/all",
					success : function(data) {

						var cantidadFilas = $('#tbody tr').length;

						if(data.length > cantidadFilas){
								
							$("#tbody").empty();
							$.each(data, function(i, val) {	

								var fecha = formatFecha(val.fecha);
								var hora = formatHora(val.fecha);
																					
								$('#tbody').append("<tr><td>"+val.modulo+"</td><td>"+val.descripcion+"</td><td>"
										+fecha+"</td><td>"+hora+"</td></tr>");
							});
						}
					}
				})};
		}, 2000);
	});

		function formatFecha(date) {
		    var d = new Date(date),
		        month = '' + (d.getMonth() + 1),
		        day = '' + d.getDate(),
		        year = d.getFullYear();

		    if (month.length < 2) month = '0' + month;
		    if (day.length < 2) day = '0' + day;

		    return [day, month, year].join('/');
		}

		function formatHora(date) {
		    var d = new Date(date),
		    hour = "" + d.getHours(); if (hour.length == 1) { hour = "0" + hour; }
		    minute = "" + d.getMinutes(); if (minute.length == 1) { minute = "0" + minute; }
		    second = "" + d.getSeconds(); if (second.length == 1) { second = "0" + second; }

		    return [hour, minute, second].join(':');
		}

		$("#contar").click(function() {

			$.ajax({
				type : "POST",
				data : cantidad,
				//dataType : "json",
				url : "PoolDatabase",
				success : function(data) {

				}
			});
		});

		$("#filtrado").keyup(function () {
		    //split the current value of searchInput
		    var data = this.value.toUpperCase().split(" ");
		    //create a jquery object of the rows
		    var jo = $("#tbody").find("tr");
		    if (this.value == "") {
		        jo.show();
		        return;
		    }
		    //hide all the rows
		    jo.hide();

		    //Recusively filter the jquery object to get results.
		    jo.filter(function (i, v) {
		        var $t = $(this);
		        for (var d = 0; d < data.length; ++d) {
		            if ($t.text().toUpperCase().indexOf(data[d]) > -1) {
		                return true;
		            }
		        }
		        return false;
		    })
		    //show the rows that match.
		    .show();
		});

		$("#ir_conf").click(function(){
			
			$('#myModal').on('hidden.bs.modal', function () {
				$("#contenido").load("configuracion"); 
			});	
		});
	});
</script>
