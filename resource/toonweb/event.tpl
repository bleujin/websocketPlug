<!DOCTYPE html>
<html>
<head>
    <title>EventSource Demo</title>
    <script type="text/javascript">
        function logText(str) {
            var log = document.getElementById("outputDiv");
	    	var escaped = str.replace(/&/, "&amp;").replace(/</, "&lt;").replace(/>/, "&gt;").replace(/"/, "&quot;"); // "
    		log.innerHTML = "<div align='left' style=\"width:500; padding:7; background-color:efefef \">" + escaped + "</div><br />" + log.innerHTML;
        }

        var es = new EventSource('/async/event/$topicId$');
        es.onopen = function() {
            logText('OPEN');
        };
        es.onmessage = function(event) {
            logText(event.data);
        };
        es.onerror = function() {
            logText('ERROR');
        };
    </script>
</head>
<body>

<div id="outputDiv" style="overflow:scroll; width:580; height:380"></div>

</body>
</html>