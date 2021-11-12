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
$output['message']='Food Waste Management';

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
			$password = md5($password);
			$userid = md5(time() . mt_rand(1,1000000));

			$query = "INSERT INTO `userinformationtable`(`username`,`email`,`phoneno`,`password`,`userid`,`usertoken`) VALUES ('$username','$email','$phoneno','$password','$userid','')";

			$res = mysqli_query($con,$query);

			if($res)
			{
				$output['message'] = 'registered successfully';
				$output['error']=false;
			}
			else{
				$output['message'] = $res;
				$output['error'] = true;
			}
		}
	}
	else if($request_type == "login")
	{
		$phoneno = $_POST['phoneno'];
		$password = $_POST['password'];
		$encrypted_pwd = md5($password);
		
		$query = "select * from `$table` where phoneno = '$phoneno' and password = '$encrypted_pwd'";
		$queryres = mysqli_query($con,$query);
		if($queryres)
		{
			if(mysqli_num_rows($queryres)){$output['error']=false;
					$row = mysqli_fetch_assoc($queryres);
					
					$list['name'] = $row['username'];
					$list['phoneno'] = $row['phoneno'];
				    $list['userid'] =	$row['userid'];
					$list['type']  = "member"; 
					$output['data']=$list;
			}
			else $output['error']=true;
		}
		else $output['error']=true;
	}
	else if($request_type == "addpickuprequest")
	{
		$table = "userrequesttable";
		$fooddesc = $_POST['food_description'];
		$phoneno  = $_POST['phoneno'];
		$iscurrentlocation = $_POST['iscurrentlocation'];
		$query = "";
		$pickupid = md5(time() . mt_rand(1,1000000));
		if($iscurrentlocation == "true")
		{
			// save latitude and longitude
			$longitude = $_POST['longitude'];
			$latitude = $_POST['latitude'];
			$itemdetails = $_POST['itemdetails'];
			$pickupstatus = "Pending";
			$query = "insert into `$table` (`phoneno`,`latitude`,`longitude`,`fooddescription`,`pickupid`,`itemdetails`,`pickupstatus`) values('$phoneno','$latitude','$longitude','$fooddesc','$pickupid','$itemdetails','$pickupstatus')";
		}
		$queryres = mysqli_query($con,$query);
		if($queryres)
		{	
					$list['phoneno'] = $phoneno;
					$list['itemdetails'] = $itemdetails;
				    $list['pickupid'] =	$pickupid;
					$list['fooddesc']  = $fooddesc; 
					if($iscurrentlocation == "true")
					{
						$list['longitude']  = $_POST['longitude'];
						$list['latitude']  = $_POST['latitude'];;
					}
					
			
			$output['error']=false;
			$query = "select DISTINCT `usertoken` from `admininfotable`";
			$queryres = mysqli_query($con,$query);
			$tokenlist= array();
			$i=0;
			if($queryres){
				if(mysqli_num_rows($queryres)>0){
					while($row = mysqli_fetch_assoc($queryres)){
						$tokenlist[$i]= $row['usertoken'];
						$i++;
					}				
					sendFCM($tokenlist,$list);
				}
			}
			else $output['error']=true;
		}
		else $output['error']=true;
	}
	else if($request_type == "getcancelled")
	{
		
		$ngoid = $_POST['ngoid'];
		
			$query = "select `pickupid` from `ngocancelpickuptable` where `ngoid` = '$ngoid' ";
			$tokenlist= array();
			$i=0;
			$queryres = mysqli_query($con,$query);
			
			if($queryres && isset($_POST["ngoid"])){
				if(mysqli_num_rows($queryres)>0){
					while($row = mysqli_fetch_assoc($queryres)){
						$tokenlist[$i]= $row['pickupid'];
						$i++;
					}
					$output['data'] = $tokenlist;
					$output['error']=false;
				}
				else {
					$output['error']=false;
					$output['data'] = $tokenlist;
					$output['message'] = 'No data';
		}
			}
		else {
			$output['error']=true;
			$output['message'] = 'Something went wrong';
		}
	
	}
}

echo json_encode($output);


function sendFCM($tokenlist,$list) {
  // FCM API Url
  $url = 'https://fcm.googleapis.com/fcm/send';

  // Put your Server Key here
  $apiKey = "AAAAu2noMZg:APA91bFWNX6tekCi0VGrR6VBCFZBJUVTL7UGHUmgs01IDxAVeVWZvUr_zT-Q9mTvxT4k3l1IXv6q6J65AuiL7CcBmyyKWfaztC1JEIjWKKw4QkrOcAOXMpVGqoW9Hn9q2siNHWK347K3";

  // Compile headers in one variable
  $headers = array (
    'Authorization:key=' .$apiKey,
    'Content-Type:application/json'
  );

  // Add notification content to a variable for easy reference
  $notifData = [
    'title' => "Food Waste",
    'body' => "You have a new pickup request"
  ];

  $dataPayload = ['to'=> 'NGO', 
  'data'=>$list
  ];

  // Create the api body
  $apiBody = [
    'notification' => $notifData,
    'data' => $dataPayload, //Optional
    //'registration_ids' = ID ARRAY
    'registration_ids' => $tokenlist
  ];

  // Initialize curl with the prepared headers and body
  $ch = curl_init();
  curl_setopt ($ch, CURLOPT_URL, $url);
  curl_setopt ($ch, CURLOPT_POST, true);
  curl_setopt ($ch, CURLOPT_HTTPHEADER, $headers);
  curl_setopt ($ch, CURLOPT_RETURNTRANSFER, true);
  curl_setopt ($ch, CURLOPT_POSTFIELDS, json_encode($apiBody));

  // Execute call and save result
  $result = curl_exec($ch);
  // Close curl after call
  curl_close($ch);

  return $result;
}
?>