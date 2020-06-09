<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Test Page</title>
</head>
<body>
<h1>Test JSP</h1>
<br/>
Test EL: ${2 + 3}
<br/>
<c:set var="testVar" value="It's working !"/>
Test taglibs: ${testVar} ${modelVar}
</body>
</html>