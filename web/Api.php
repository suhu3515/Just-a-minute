<?php

require_once 'DbConnect.php';

$response = array();

if(isset($_GET['apicall']))
{
    switch($_GET['apicall'])
    {
        case 'signup':

            if (isTheseParametersAvailable(array('user_name','user_gender','user_mobile','user_password')))
            {
                $user_name = $_POST['user_name'];
                $user_gender = $_POST['user_gender'];
                $user_mobile = $_POST['user_mobile'];
                $user_pass = $_POST['user_password'];

                $stmt = $conn->prepare("SELECT * from users where user_mob=?");
                $stmt->bind_param("s",$user_mobile);
                $stmt->execute();
                $stmt->store_result();

                if ($stmt->num_rows > 0)
                {
                    $response['error'] = true;
                    $response['message'] = 'User Already Registered';
                    $stmt->close();
                }
                else
                {
                    $stmt = $conn->prepare("INSERT INTO users (user_name,user_gender,user_mob,user_password) VALUES (?,?,?,?)");
                    $stmt->bind_param("ssss",$user_name,$user_gender,$user_mobile,$user_pass);

                    if ($stmt->execute())
                    {
                        $stmt = $conn->prepare("SELECT user_id, user_name, user_gender, user_mob, user_password from users where user_mob=?");
                        $stmt->bind_param("s",$user_mobile);
                        $stmt->execute();
                        $stmt->bind_result($user_id,$user_name, $user_gender, $user_mobile, $user_pass);
                        $stmt->fetch();

                        $user = array
                        (
                            'user_id'=>$user_id,
                            'user_name'=>$user_name,
                            'user_gender'=>$user_gender,
                            'user_mobile'=>$user_mobile,
                            'user_pass'=>$user_pass
                        );

                        $stmt->close();

                        $response['error']= false;
                        $response['message'] = 'User registered Successfully';
                        $response['user'] = $user;
                    }
                }
            }
            else
            {
                $response['error'] = true;
                $response['message'] = 'required parameters are not available';

            }
            break;

        case "user_login":

            if (isTheseParametersAvailable(array('login_mobile','login_pass')))
            {
                $mob = $_POST['login_mobile'];
                $pass = $_POST['login_pass'];
                $stmt1 = $conn->prepare("SELECT * FROM users where user_mob=? and user_password=?");
                $stmt1->bind_param("ss", $mob, $pass);
                $stmt1->execute();
                $stmt1->store_result();
                if ($stmt1->num_rows > 0)
                {
                    $stmt2 = $conn->prepare("select user_id,user_name,user_gender,user_mob,user_password from users where user_mob=?");
                    $stmt2->bind_param("s", $mob);
                    $stmt2->execute();
                    $stmt2->bind_result($user_id_1,$user_name_1,$user_gender_1, $user_mobile_1, $user_pass_1);
                    $stmt2->fetch();

                    $USER = array
                    (
                        'user_id'=>$user_id_1,
                        'user_name'=>$user_name_1,
                        'user_gender'=>$user_gender_1,
                        'user_mobile'=>$user_mobile_1,
                        'user_pass'=>$user_pass_1
                    );

                    $response['error'] = false;
                    $response['message'] = 'Succesfully logged in';
                    $response['USER'] = $USER;
                    $stmt2->close();
                    $stmt1->close();
                }
                else
                {
                    $response['error'] = true;
                    $response['message'] = 'No user found';
                    $stmt1->close();
                }
            }
            else
            {
                $response['error'] = true;
                $response['message'] = 'required parameters are not available';
            }

            break;


        default:
            $response['error'] = true;
            $response['message'] = 'Invalid operation Called';
    }
}
else
{
    $response['error'] = true;
    $response['message'] = 'Invalid API Call';
}

echo json_encode($response);

function isTheseParametersAvailable($params)
{
    foreach($params as $param)
    {
        if (!isset($_POST[$param]))
        {
            return false;
        }
    }
    return true;
}
