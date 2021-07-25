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
   if($request_type == 'login')
   {
   	   $table = 'admininfotable';
   	   $username = $_REQUEST['username'];
		$password = $_REQUEST['password'];
		
		$query = "select * from `$table` where username = '$username' and password = '$password'";
		$queryres = mysqli_query($con,$query);
		if($queryres)
		{
			if(mysqli_num_rows($queryres))$output['error']=false;
			else $output['error']=true;
		}
		else $output['error']=true;
   }

   else if($request_type == 'getlist')
   {
   		$table = 'userrequesttable';
   		$query = "select a.username, b.* from userinformationtable a, userrequesttable b where a.phoneno = b.phoneno";

   		$queryres = mysqli_query($con,$query);
   		if($queryres)
   		{
   			$list = array(array());
			$i = 0;
				while($row = mysqli_fetch_assoc($queryres))
				{
					$list[$i]['name'] = $row['username'];
					$list[$i]['phoneno'] = $row['phoneno'];
				    $list[$i]['fooddesc'] =	$row['fooddescription'];
					$list[$i]['locationtext'] = $row['locationtext'];
					$list[$i]['longitude']=$row['longitude'];
					$list[$i]['latitude']=$row['latitude'];
					$i++;			
				}

			$output['list'] = $list;
			$output['error'] = false;
			$output['message'] = "list loaded successfully";
   		}
   		else 
   		{
   			$output['error']=true;
   			$output['message'] = "cannot execute query";
   		}	
   }
   else if($request_type == "removerows")
   {
   	  $list = json_decode($_REQUEST['removeditemslist']);
   	 /* for($i = 0; $i < count($list,COUNT_NORMAL); $i++)
   	  {
   	  	
   	  }*/
   	  foreach ($list as $k)
   	  {
   	  	$list1=json_decode($k,true);
   	  	$phoneno = $list1['phoneno'];
   	  	$fooddesc = $list1['fooddesc'];
   	  	$query="delete from `userrequesttable` where phoneno='$phoneno' and fooddescription='$fooddesc'";
   	  	$queryres = mysqli_query($con,$query);
   	  	if($queryres)
   	  	{
   	  		$output['error'] = false;
   	  	}
   	  	else 
   	  		{
   	  			$output['error'] = true;
   	  			break;
   	  		}
   	  }
   	  
   	  //$output["message"]="hello".$listi['phoneno'];
   }

}
else
{
	$output['error']=true;
	$output['message']='cannot connect to database';
}

echo json_encode($output);

?>