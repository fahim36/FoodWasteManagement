<?php
// db credentials
$dbname = "hungercure";
$dbuser = "saiful";
$dbpass = "kollol36";

$con = new mysqli("localhost",$dbuser,$dbpass,$dbname);
if (isset($_POST["request_type"]))
{
	$request_type = $_POST['request_type'];
}else{
	$request_type= "";
}
$output = Array();
$output['error'] = true;
$output['message']='dummy';

if($con)
{
	$table = "userinformationtable";
	
	if($request_type == 'registration')
	{
		if (isset($_POST["username"]) && isset($_POST["email"]) && isset($_POST["phoneno"]) && isset($_POST["password"]))
		{
			$username = $_POST['username'];
			$email = $_POST['email'];
			$phoneno = $_POST['phoneno'];
			$password = $_POST['password'];

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
	}
	else if($request_type == "login")
	{
		$phoneno = $_POST['phoneno'];
		$password = $_POST['password'];
		
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
		$fooddesc = $_POST['food_description'];
		$phoneno  = $_POST['phoneno'];
		$iscurrentlocation = $_POST['iscurrentlocation'];
		$query = "";
		if($iscurrentlocation == "true")
		{
			// save latitude and longitude
			$longitude = $_POST['longitude'];
			$latitude = $_POST['latitude'];
			$query = "insert into `$table` (`phoneno`,`latitude`,`longitude`,`fooddescription`) values('$phoneno','$latitude','$longitude','$fooddesc')";
		}
		else
		{
			// save location text
			$locationtext = $_POST['locationtext'];
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