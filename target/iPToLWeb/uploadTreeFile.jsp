<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload tree file</title>
</head>
<body>
<br/>
<br/>
<br/>
<br/>

<B>Upload tree in NEXUS format:</B>
<form name='upload_tree_form' action='/iPToLWeb/uploadfile' enctype="multipart/form-data" method='post'>
	<input type="file" name='tree_file' size='50'></input>
	<input type='submit' value='Upload'></input> 
</form>
</body>
</html>