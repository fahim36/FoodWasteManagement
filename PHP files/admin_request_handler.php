<?php
// db credentials
$dbname = "hungercure";
$dbuser = "saiful";
$dbpass = "kollol36";

$con = mysqli_connect("localhost",$dbuser,$dbpass,$dbname);
$output = Array();
$output['error'] = true;
$output['message']='dummy';

if($con)
{
   if($_REQUEST['request_type'] == 'login')
   {
   	    $table = 'admininfotable';
   	    $username = $_REQUEST['username'];
		$password = $_REQUEST['password'];
		$password = md5($password);
		
		$query = "select * from `$table` where username = '$username' and password = '$password'";
		$queryres = mysqli_query($con,$query);
		if($queryres)
		{
			if(mysqli_num_rows($queryres)){
					$output['error']=false;
					$row = mysqli_fetch_assoc($queryres);
					
					$list['name'] = $row['username'];
					$list['phoneno'] = $row['phoneno'];
				    $list['userid'] =	$row['userid'];
					$list['type']  = "ngo";
					$list['token'] = $row['usertoken'];
					$output['data']=$list;
			}
			else $output['error']=true;
		}
		else $output['error']=true;
   }
	else if($_REQUEST['request_type'] == 'registration')
	{
		if (isset($_POST["username"]) && isset($_POST["email"]) && isset($_POST["phoneno"]) && isset($_POST["password"]))
		{
			$table = 'admininfotable';
			
			$username = $_POST['username'];
			$email = $_POST['email'];
			$phoneno = $_POST['phoneno'];
			$password = $_POST['password'];
			$password = md5($password);
	
			$userid = md5(time() . mt_rand(1,1000000));

			$query = "insert into $table (username,email,phoneno,password,userid) values('$username','$email','$phoneno','$password','$userid')";

			$res = mysqli_query($con,$query);

			if($res)
			{
				$output['message'] = 'registered successfully';
				$output['error']=false;
			}
			else{
				$output['message'] = 'You might be already registered . Please consider login';
				$output['error'] = true;
			}
		}
	}else if($_REQUEST['request_type'] == 'update_token')
	{
		if (isset($_POST["token"]) && isset($_POST["userid"]))
		{   
			$table = 'admininfotable';
			$token = $_POST['token'];
			$userid = $_POST['userid'];
			
			$query = "update $table set usertoken = '$token' where userid='$userid'";

			$res = mysqli_query($con,$query);

			if($res)
			{
				$output['message'] = 'User token updated successfully';
				$output['error']=false;
			}
			else{
				$output['message'] = 'Something went wrong';
				$output['error'] = true;
			}
		}
	}else if($_REQUEST['request_type'] == 'cancelpickuprequest')
	{		
		if (isset($_POST["ngoid"]) && isset($_POST["pickupid"])){
			
		$table = "ngocancelpickuptable";
		$ngoid = $_POST['ngoid'];
		$pickupid = $_POST['pickupid'];
		
		$query = "insert into `$table` (`ngoid`,`pickupid`) values('$ngoid','$pickupid')";
		
		$queryres = mysqli_query($con,$query);
			if($queryres)
			{	
				$output['message'] = 'Pickup request cancelled successfully';
				$output['error']=false;
			}
			else {
			$output['error']=true;
			$output['message'] = 'Something went wrong';
			}
		}
		else {
		$output['error']=true;
		$output['message'] = 'Something went wrong';
		}	
	}
	else if($_REQUEST['request_type'] == 'getlist')
   {
   		$table = 'userrequesttable';
   		$query = "select a.username, b.* from userinformationtable a, userrequesttable b where a.phoneno = b.phoneno";

   		$queryres = mysqli_query($con,$query);
		
   		if($queryres && isset($_POST["ngoid"]))
   		{		
				$table = "ngocancelpickuptable";
				$ngoid = $_POST['ngoid'];
				$query = "select  `pickupid` from `ngocancelpickuptable` where `ngoid` = '$ngoid'";
		if($queryres)
			{
   			$list = array(array());
			$i = 0;
				while($row = mysqli_fetch_assoc($queryres))
				{
							$list[$i]['name'] = $row['username'];
							$list[$i]['phoneno'] = $row['phoneno'];
							$list[$i]['pickupid'] = $row['pickupid'];
							$list[$i]['fooddesc'] =	$row['fooddescription'];
							$list[$i]['itemdetails'] = $row['itemdetails'];
							$list[$i]['longitude']=$row['longitude'];
							$list[$i]['latitude']=$row['latitude'];
							$list[$i]['timestamp']=$row['timestamp'];
							$list[$i]['pickupstatus']=$row['pickupstatus'];
							$i++;
				}
				$output['list'] = $list;
				$output['error'] = false;
				$output['message'] = "List loaded successfully";
				}
			else 
			{
   			$output['error']=true;
   			$output['message'] = "Cannot execute query";
			}
		}
		else 
   		{
   			$output['error']=true;
   			$output['message'] = "Cannot execute query";
   		}	
   }
   else if($_REQUEST['request_type'] == "removerows")
   {
   	  $list = json_decode($_REQUEST['removeditemslist']);
   	 
   	  foreach ($list as $k)
   	  {
   	  	$list1=json_decode($k,true);
   	  	$phoneno = $list1['phoneno'];
   	  	$fooddesc = $list1['fooddesc'];
		$pickupid = $list1['pickupid'];
   	  	$query="delete from `userrequesttable` where pickupid='$pickupid' ";
   	  	$queryres = mysqli_query($con,$query);
   	  	if($queryres)
   	  	{
   	  		$output['error'] = false;
			$output['message'] = "Request Deleted Successfully";
   	  	}
   	  	else 
   	  		{
   	  			$output['error'] = true;
   	  			break;
   	  		}
   	  }
   	  
   	  //$output["message"]="hello".$listi['phoneno'];
   }
    else if($_REQUEST['request_type'] == "updatestatus")
   {
   	 if (isset($_POST["pickupid"]) && isset($_POST["pickupstatus"])){
			
		$table = "userrequesttable";
		
		$pickupid = $_POST['pickupid'];
		$pickupstatus = $_POST['pickupstatus'];
		
		
		$query ="update $table set pickupstatus = '$pickupstatus' where pickupid='$pickupid'";
		
		$queryres = mysqli_query($con,$query);
			if($queryres)
			{	
				$output['message'] = 'Pickup status updated';
				$output['error']=false;
			}
			else {
			$output['error']=true;
			$output['message'] = 'Something went wrong';
			}
		}
		else {
		$output['error']=true;
		$output['message'] = 'Something went wrong';
		}	
   	  
   }

}
else
{
	$output['error']=true;
	$output['message']='cannot connect to database';
}

echo json_encode($output);

?>