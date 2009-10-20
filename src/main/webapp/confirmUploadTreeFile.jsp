<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Tree file uploaded!</title>
</head>
<body>
File <%= (String)request.getAttribute("uploded_filename") %> uploaded successfully at <%= (String)request.getAttribute("uploaded_date") %> !
</body>
</html>