<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><title>GVT report</title>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
 <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
   <script src="https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js?lang=css&skin=desert"> </script>
 <style>
.modal.modal-wide .modal-dialog {
  width: 90%;
}
.modal-wide .modal-body {
  overflow-y: auto;
}
p {word-wrap: break-word; }
</style>
<script>
\$(".modal-wide").on("show.bs.modal", function() {
  var height = \$(window).height() - 200;
  \$(this).find(".modal-body").css("max-height", height);
});
</script>
<style>
div.tooltip-inner {
    max-width: 1500px;
}
.input-color {
    position: relative;
}
.input-color input {
    padding-left: 40px;
}
.input-color .color-box {
    width: 30px;
    height: 10px;
    display: inline-block;
    background-color: #ccc;
    position: absolute;
    left: 5px;
    top: 5px;
}
</style>
<script>
\$(document).ready(function () {
  \$("a").tooltip({
    'selector': '',
    'placement': 'bottom',
    'container':'body'
  });
});
</script>
 </head>
 <body>

<div class="container">
	<div class="row">
		<div class="col-xs-11" style="float:right;right:0px">
		
			$IDNumber,
			Heading, 
			Image,
			ViolationsDesc,
			sourceFile,
			sourceLink,
			ImageDS,
			ImageUI:
			{id, hd, img, vsd, sf, src, imgDS, imgUI| 
			<div class="panel panel-primary">
				<div class="panel-heading"><b>$hd$</b></div>
				<div class="panel-body">
				
								<p align="right">
										<a data-toggle="modal" href="#$id$" class="btn btn-primary">Linked XML element</a>
										<div id="$id$" class="modal modal-wide fade">
										  <div class="modal-dialog">
											<div class="modal-content">
											  <div class="modal-header">
												<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
												<h4 class="modal-title"> $sf$</h4>
											  </div>
											  <div class="modal-body">
												<pre class="prettyprint"> <xmp> $src$ </xmp></pre>
											  </div>
											  <div class="modal-footer">
												<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
											  </div>
											</div><!-- /.modal-content -->
										  </div><!-- /.modal-dialog -->
										</div><!-- /.modal -->
								</p>
				<p>
				<img src="$img$" alt="Mountain View" width="25%" ><br><br>
				
				$imgDS$
				$imgUI$
				<b> <br>$vsd$</b>
				</p>
				</div>
			</div>
			}
			$
			
			<!-- /.warning -->
			$WIDNumber,
			WHeading, 
			WImage,
			WViolationsDesc,
			WsourceFile,
			WsourceLink,
			WImageDS,
			WImageUI:
			{id, hd, img, vsd, sf, src, wimgDS, wimgUI| 
			<div class="panel panel-success">
				<div class="panel-heading"><b>Warning: $hd$</b></div>
				<div class="panel-body">
				
								<p align="right">
										<a data-toggle="modal" href="#$id$" class="btn btn-primary">Linked XML element</a>
										<div id="$id$" class="modal modal-wide fade">
										  <div class="modal-dialog">
											<div class="modal-content">
											  <div class="modal-header">
												<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
												<h4 class="modal-title"> $sf$</h4>
											  </div>
											  <div class="modal-body">
												<pre class="prettyprint"> <xmp> $src$ </xmp></pre>
											  </div>
											  <div class="modal-footer">
												<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
											  </div>
											</div><!-- /.modal-content -->
										  </div><!-- /.modal-dialog -->
										</div><!-- /.modal -->
								</p>
				<p>
				<img src="$img$" alt="Mountain View" width="25%" ><br><br>
				<b>Design cropped image:</b>
				$wimgDS$
				<b>implementation cropped image:</b>
				$wimgUI$
				<b> <br>$vsd$</b>
				</p>
				</div>
			</div>
			}
			$
			
			
			<div class="panel panel-success" style="word-wrap: break-word;">
				<div class="panel-heading"><b>Design input warnings</b></div>
				<div class="panel-body">
				<p>
				<b> $Warnings$</b>
				</p>
				</div>
			</div>
			
			
			<div class="panel panel-info" style="word-wrap: break-word;">
				<div class="panel-heading"><b>PID comparison</b></div>
				<div class="panel-body">
				<p>
				<img src="PIDOutput.png" alt="Mountain View" width="25%" ><br><br>
				</p>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
\$(document).ready(function(){
    \$('[data-toggle="tooltip"]').tooltip();   
});
</script>
</body>
</html>