<?php
session_start();
require_once 'global.php';

error_reporting(E_ERROR | E_PARSE);
header('Content-type: application/json; charset=utf-8');

header('Expires: Sun, 01 Jan 2014 00:00:00 GMT');
header('Cache-Control: no-store, no-cache, must-revalidate');
header('Cache-Control: post-check=0, pre-check=0', FALSE);
header('Pragma: no-cache');

$response = array();

$request = explode('/', trim($_SERVER['PATH_INFO'], '/'));
$action = strtolower(array_shift($request));
$id = array_shift($request);

require_once 'db_config.php';
$db = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);

if ($db->connect_errno) {
    $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
    $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการเชื่อมต่อฐานข้อมูล';
    $response[KEY_ERROR_MESSAGE_MORE] = $db->connect_error;
    echo json_encode($response);
    exit();
}
$db->set_charset("utf8");

//sleep(1); //todo:

switch ($action) {
    case 'login':
        doLogin();
        break;
    case 'logout':
        doLogout();
        break;
    case 'register':
        doRegister();
        break;
    case 'get_beverage':
        doGetBeverage();
        break;
    case 'add_pond':
        doAddPond();
        break;
    case 'update_pond':
        doUpdatePond();
        break;
    case 'delete_pond':
        doDeletePond();
        break;
    default:
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'No action specified or invalid action.';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
        break;
}

$db->close();
echo json_encode($response);
exit();

function doLogin()
{
    global $db, $response;

    $email = $db->real_escape_string($_POST['email']);
    $password = $db->real_escape_string($_POST['password']);

    $selectUserSql = "SELECT * FROM `bistro_user` WHERE `email` = '$email' AND `password` = '$password'";

    $selectUserResult = $db->query($selectUserSql);
    if ($selectUserResult) {
        if ($selectUserResult->num_rows > 0) {
            $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
            $response[KEY_ERROR_MESSAGE] = '';
            $response[KEY_ERROR_MESSAGE_MORE] = '';
            $response[KEY_LOGIN_SUCCESS] = TRUE;
            $user = fetchUser($selectUserResult);
            $response['user'] = $user;
        } else {
            $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
            $response[KEY_ERROR_MESSAGE] = 'ชื่อผู้ใช้ หรือรหัสผ่าน ไม่ถูกต้อง';
            $response[KEY_ERROR_MESSAGE_MORE] = '';
            $response[KEY_LOGIN_SUCCESS] = FALSE;
        }
        $selectUserResult->close();
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการอ่านข้อมูลบัญชีผู้ใช้';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $selectUserSql";
    }
}

function doLogout()
{
    global $response;

    $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
    $response[KEY_ERROR_MESSAGE] = 'ออกจากระบบสำเร็จ';
    $response[KEY_ERROR_MESSAGE_MORE] = '';
}

function fetchUser($selectUserResult)
{
    $row = $selectUserResult->fetch_assoc();

    $user = array();
    $user['id'] = (int)$row['id'];
    $user['name'] = $row['name'];
    $user['email'] = $row['email'];
    $user['created_at'] = $row['created_at'];

    return $user;
}

function doRegister()
{
    global $db, $response;

    $name = $db->real_escape_string($_POST['name']);
    $email = $db->real_escape_string($_POST['email']);
    $password = $db->real_escape_string($_POST['password']);

    $selectExistingUserSQL = "SELECT * FROM `bistro_user` WHERE `email` = '$email'";
    $selectExistingUserResult = $db->query($selectExistingUserSQL);
    if ($selectExistingUserResult->num_rows > 0) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = "สมัครสมาชิกไม่ได้ เนื่องจากมีอีเมล '$email' อยู่ในระบบแล้ว";
        $response[KEY_ERROR_MESSAGE_MORE] = '';
        $selectExistingUserResult->close();
        return;
    }
    $selectExistingUserResult->close();

    $insertUserSql = "INSERT INTO `bistro_user` (`email`, `password`, `name`) "
        . " VALUES ('$email', '$password', '$name')";
    $insertUserResult = $db->query($insertUserSql);
    if ($insertUserResult) {
        $insertId = $db->insert_id;
        $selectUserSql = "SELECT * FROM `bistro_user` WHERE `id` = $insertId";

        $selectUserResult = $db->query($selectUserSql);
        if ($selectUserResult) {
            $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
            $response[KEY_ERROR_MESSAGE] = '';
            $response[KEY_ERROR_MESSAGE_MORE] = '';
            $response['user'] = fetchUser($selectUserResult);
        }
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการบันทึกข้อมูล';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $insertUserSql";
    }
}

function doGetBeverage()
{
    global $db, $response;

    $sql = "SELECT * FROM `bistro_beverage_category` ORDER BY `id`";
    if ($categoryResult = $db->query($sql)) {
        $categoryList = array();
        while ($categoryRow = $categoryResult->fetch_assoc()) {
            $category = array();
            $categoryId = (int)$categoryRow['id'];
            $category['id'] = $categoryId;
            $category['name'] = $categoryRow['name'];
            $category['created_at'] = $categoryRow['created_at'];

            $beverageList = array();

            $sql = "SELECT * FROM `bistro_beverage` WHERE `category`=$categoryId";
            if ($beverageResult = $db->query($sql)) {
                $beverageList = array();
                while ($beverageRow = $beverageResult->fetch_assoc()) {
                    $beverage = array();
                    $beverage['id'] = (int)$beverageRow['id'];
                    $beverage['name'] = $beverageRow['name'];
                    $beverage['category_id'] = (int)$beverageRow['category'];
                    $beverage['price_hot'] = (int)$beverageRow['price_hot'];
                    $beverage['price_iced'] = (int)$beverageRow['price_iced'];
                    $beverage['price_frappe'] = (int)$beverageRow['price_frappe'];
                    $beverage['price_smoothie'] = (int)$beverageRow['price_smoothie'];
                    $beverage['created_at'] = $beverageRow['created_at'];
                    array_push($beverageList, $beverage);
                }
                $beverageResult->close();
            }

            $category[KEY_DATA_LIST] = $beverageList;
            array_push($categoryList, $category);
        }
        $categoryResult->close();

        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'อ่านข้อมูลสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
        $response[KEY_DATA_LIST] = $categoryList;

    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการอ่านข้อมูล';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}

function doAddPond()
{
    global $db, $response;

    $pondNumber = $db->real_escape_string($_POST['pondNumber']);
    $pondArea = $db->real_escape_string($_POST['pondArea']);
    $initialShrimpCount = $db->real_escape_string($_POST['initialShrimpCount']);

    if ($pondNumber <= 0) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'หมายเลขบ่อต้องเป็นเลขบวก (1, 2, 3, ...)';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
        return;
    }

    $selectExistingPondNumberSQL = "SELECT * FROM `pond` WHERE `number`=$pondNumber";
    $selectExistingPondNumberResult = $db->query($selectExistingPondNumberSQL);
    $count = $selectExistingPondNumberResult->num_rows;
    $selectExistingPondNumberResult->close();
    if ($count > 0) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'ไม่สามารถเพิ่มข้อมูลบ่อได้ เนื่องจากมีหมายเลขบ่อที่ระบุแล้ว (หมายเลขบ่อห้ามซ้ำ)';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
    } else {
        $sql = "INSERT INTO `pond` (number, area, initial_shrimp_count) VALUES ($pondNumber, $pondArea, $initialShrimpCount)";
        if ($db->query($sql)) {
            $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
            $response[KEY_ERROR_MESSAGE] = 'เพิ่มข้อมูลบ่อเลี้ยงสำเร็จ';
            $response[KEY_ERROR_MESSAGE_MORE] = '';
        } else {
            $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
            $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการบันทึกข้อมูลบ่อเลี้ยง';
            $errMessage = $db->error;
            $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
        }
    }
}

function doUpdatePond()
{
    global $db, $response;

    $pondId = (int)$db->real_escape_string($_POST['pondId']);
    $pondNumber = $db->real_escape_string($_POST['pondNumber']);
    $pondArea = $db->real_escape_string($_POST['pondArea']);
    $initialShrimpCount = $db->real_escape_string($_POST['initialShrimpCount']);

    $selectExistingPondNumberSQL = "SELECT * FROM `pond` WHERE `number`=$pondNumber";
    $selectExistingPondNumberResult = $db->query($selectExistingPondNumberSQL);
    if ($selectExistingPondNumberResult->num_rows > 0) {
        $row = $selectExistingPondNumberResult->fetch_assoc();
        if ($pondId !== (int)$row['id']) {
            $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
            $response[KEY_ERROR_MESSAGE] = 'ไม่สามารถบันทึกข้อมูลบ่อได้ เนื่องจากมีหมายเลขบ่อที่ระบุแล้ว (หมายเลขบ่อห้ามซ้ำ)';
            $response[KEY_ERROR_MESSAGE_MORE] = '';
            $selectExistingPondNumberResult->close();
            return;
        }
    }
    $selectExistingPondNumberResult->close();

    $sql = "UPDATE `pond` SET number=$pondNumber, area=$pondArea, initial_shrimp_count=$initialShrimpCount WHERE id=$pondId";
    if ($db->query($sql)) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'แก้ไขข้อมูลบ่อเลี้ยงสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการบันทึกข้อมูลบ่อเลี้ยง';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}

function doDeletePond()
{
    global $db, $response;

    $pondId = $db->real_escape_string($_POST['pondId']);

    $sql = "DELETE FROM `pond` WHERE id=$pondId";
    if ($db->query($sql)) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'ลบข้อมูลบ่อเลี้ยงสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการลบข้อมูลบ่อเลี้ยง';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}

function createRandomString($length)
{
    $key = '';
    $keys = array_merge(range(0, 9), range('a', 'z'));

    for ($i = 0; $i < $length; $i++) {
        $key .= $keys[array_rand($keys)];
    }

    return $key;
}

function moveUploadedFile($key, $dest)
{
    global $response;

    $response['name'] = $_FILES[$key]['name'];
    $response['type'] = $_FILES[$key]['type'];
    $response['size'] = $_FILES[$key]['size'];
    $response['tmp_name'] = $_FILES[$key]['tmp_name'];

    $src = $_FILES[$key]['tmp_name'];
    $response['upload_src'] = $src;

    $response['upload_dest'] = $dest;

    return move_uploaded_file($src, $dest);
}

function getUploadErrorMessage($errCode)
{
    $message = '';
    switch ($errCode) {
        case UPLOAD_ERR_OK:
            break;
        case UPLOAD_ERR_INI_SIZE:
        case UPLOAD_ERR_FORM_SIZE:
            $message .= 'File too large (limit of ' . get_max_upload() . ' bytes).';
            break;
        case UPLOAD_ERR_PARTIAL:
            $message .= 'File upload was not completed.';
            break;
        case UPLOAD_ERR_NO_FILE:
            $message .= 'Zero-length file uploaded.';
            break;
        default:
            $message .= 'Internal error #' . $errCode;
            break;
    }
    return $message;
}

?>
