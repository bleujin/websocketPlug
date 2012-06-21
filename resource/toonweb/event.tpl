<!DOCTYPE html>
<html>
<head>
    <title>EventSource Demo</title>
    <script type="text/javascript">
        function logText(msg) {
            var textArea = document.getElementById('data');
            textArea.value = textArea.value + msg + '\n';
            textArea.scrollTop = textArea.scrollHeight; // scroll into view
        }

        var es = new EventSource('/event/$userId$');
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
<textarea id="data" rows="20" cols="100%"></textarea>



</body>
</html>