<?php
// db credentials
$dbname = "HungerCure";
$dbuser = "root";
$dbpass = "kollol36";

$con = mysqli_connect("localhost",$dbuser,$dbpass,$dbname);
$request_type = $_REQUEST['request_type'];
$output = Array();
$output['error'] = true;
$output['message']='dummy';

if($con)
{
	$table = "userinformationtable";
	
	if($request_type == 'registration')
	{
		$username = $_REQUEST['username'];
		$email = $_REQUEST['email'];
		$phoneno = $_REQUEST['phoneno'];
		$password = $_REQUEST['password'];

		$query = "insert into `$table` values('$username','$email','$phoneno','$password')";

		$res = mysqli_query($con,$query);

		if($res)
		{
			$output['message'] = 'registered successfully';
			$output['error']=false;
		}
		else{
			$output['message'] = 'you might be already registered . please consider login';
			$output['error'] = true;
		}
	}
	else if($request_type == "login")
	{
		$phoneno = $_REQUEST['phoneno'];
		$password = $_REQUEST['password'];
		
		$query = "select * from `$table` where phoneno = '$phoneno' and password = '$password'";
		$queryres = mysqli_query($con,$query);
		if($queryres)
		{
			if(mysqli_num_rows($queryres))$output['error']=false;
			else $output['error']=true;
		}
		else $output['error']=true;
	}
	else if($request_type == "getuserinfo")
	{
		$table = "userrequesttable";
		$fooddesc = $_REQUEST['food_description'];
		$phoneno  = $_REQUEST['phoneno'];
		$iscurrentlocation = $_REQUEST['iscurrentlocation'];
		$query = "";
		if($iscurrentlocation == "true")
		{
			// save latitude and longitude
			$longitude = $_REQUEST['longitude'];
			$latitude = $_REQUEST['latitude'];
			$query = "insert into `$table` (`phoneno`,`latitude`,`longitude`,`fooddescription`) values('$phoneno','$latitude','$longitude','$fooddesc')";
		}
		else
		{
			// save location text
			$locationtext = $_REQUEST['locationtext'];
			$query = "insert into `$table` (`phoneno`,`locationtext`,`fooddescription`) values('$phoneno','$locationtext','$fooddesc')";
		}

		$queryres = mysqli_query($con,$query);
		if($queryres)
		{
			$output['error']=false;
		}
		else $output['error']=true;
	}
}
else
{
	$output['message'] = 'cannot connect to database';
	$output['error'] = true;
}

echo json_encode($output);

?>