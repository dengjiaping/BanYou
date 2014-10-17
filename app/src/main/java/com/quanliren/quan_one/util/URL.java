package com.quanliren.quan_one.util;

public class URL {
//	public static final String URL="http://115.28.134.163";
//	public static final String IP="115.28.134.163";
//	public static final String URL="http://192.168.1.21:8084";
//	public static final String URL="http://192.168.1.15:8080";
//	public static final String SOCKET_URL="http://192.168.1.21:8082";
//	public static final String IP="192.168.1.21";
//	public static final String URL="http://192.168.1.10:8080";
//	public static final String IP="192.168.1.15";
	public static final String URL="http://www.bjqlr.com";
	public static final String IP="www.bjqlr.com";
	public static final Integer PORT=30003;
	
	
	public static final String STATUS="status";
	public static final String RESPONSE="responses";
	public static final String INFO="info";
	public static final String PAGEINDEX="p";
	public static final String TOTAL="total";
	public static final String COUNT="count";
	public static final String LIST="list";
	
	/**城市列表**/
	public static final String CITYS=URL+"/client/get_citys.php";
	/**首页轮播**/
	public static final String AD=URL+"/client/get_activity_imgs.php";
	/**首页列表**/
	public static final String HOME_USERLIST=URL+"/client/user/home_list.php";
	/**注册第一步填写手机号**/
	public static final String REG_FIRST=URL+"/client/send_user_mobile.php";
	/**注册第二步填写验证码**/
	public static final String REG_SENDCODE=URL+"/client/send_user_auth.php";
	/**注册第三步填写基本信息**/
	public static final String REG_THIRD=URL+"/client/send_reg_info.php";
	/**找回密码第一步**/
	public static final String FINDPASSWORD_FIRST=URL+"/client/forget_pwd_one.php";
	/**找回密码第二部**/
	public static final String FINDPASSWORD_SECOND=URL+"/client/forget_pwd_two.php";
	/**修改密码**/
	public static final String MODIFYPASSWORD=URL+"/client/user/alert_pwd.php";
	/**退出**/
	public static final String LOGOUT=URL+"/client/logout.php";
	/**登陆**/
	public static final String LOGIN=URL+"/client/user_login.php";
	/**编辑用户信息**/
	public static final String EDIT_USER_INFO=URL+"/client/user/edit_info.php";
	/**上传用户头像**/
	public static final String UPLOAD_USER_LOGO=URL+"/client/user/img/avatar/upload.php";
	/**获取用户详细信息**/
	public static final String GET_USER_INFO=URL+"/client/user/get_user_detail.php";
	/**删除动态**/
	public static final String DELETE_DONGTAI=URL+"/client/user/dynamic/update_dy.php";
	/**获取用户详细信息**/
	public static final String SET_USERLOGO=URL+"/client/user/setting_avatar.php";
	/**删除头像**/
	public static final String DELETE_USERLOGO=URL+"/client/user/img/avatar/update_num.php";
	/**发表动态**/
	public static final String PUBLISH_TXT=URL+"/client/user/dynamic/pub_text.php";
	/**发表群动态**/
	public static final String PUBLISH_GROUP_TXT=URL+"/client/user/dynamic/crowd_pub_text.php";
	/**发表动态图片**/
	public static final String PUBLISH_IMG=URL+"/client/user/dynamic/pub_img.php";
	/**关注**/
	public static final String CONCERN=URL+"/client/user/atten/concern_he.php";
	/**取消关注**/
	public static final String CANCLECONCERN=URL+"/client/user/atten/cancel_atten.php";
	/**关注列表**/
	public static final String CONCERNLIST=URL+"/client/user/atten/concern_list.php";
	/**获取联系方式**/
	public static final String GETCONTACT=URL+"/client/user/contact_he.php";
	/**获取附近的人列表**/
	public static final String NearUserList=URL+"/client/user/nearby_user_list.php";
	/**动态**/
	public static final String DONGTAI=URL+"/client/user/dynamic/dy_list.php";
	/**个人动态**/
	public static final String PERSONALDONGTAI=URL+"/client/user/dynamic/user_dy_list.php";
	/**删除动态**/
	public static final String DELETEDONGTAI=URL+"/client/user/dynamic/update_dy.php";
	/**留言**/
	public static final String LEAVEMESSAGE=URL+"/client/user/prv/send_general_msg.php";
	/**创建群**/
	public static final String CREATEGROUP=URL+"/client/user/crowd/create_info.php";
	/**群头像**/
	public static final String CREATEGROUP_IMG=URL+"/client/user/crowd/create_img.php";
	/**获取群列表**/
	public static final String GETGROUPLIST=URL+"/client/user/crowd/crowd_list.php";
	/**获取群资料**/
	public static final String GETGROUPDETAIL=URL+"/client/user/crowd/crowd_detail.php";
	/**获取群成员**/
	public static final String GETGROUPMEMBERLIST=URL+"/client/user/crowd/member_list.php";
	/**设置或删除成员**/
	public static final String AMENTMEMBER=URL+"/client/user/crowd/amend_member.php";
	/**申请加入群**/
	public static final String JOINGROUP=URL+"/client/user/crowd/apply_crowd.php";
	/**邀请加入群**/
	public static final String INVITEGROUP=URL+"/client/user/crowd/invite_crowd.php";
	/**私信列表**/
	public static final String MESSAGELIST=URL+"/client/user/prv/msg_list.php";
	/**群消息列表**/
	public static final String GROUPMESSAGELIST=URL+"/client/user/prv/crowd_msgs.php";
	/**同意拒绝**/
	public static final String AGREEORNOT=URL+"/client/user/prv/dispose_info.php";
	/**删除消息**/
	public static final String DELETEMESSAGE=URL+"/client/user/prv/del_msg.php";
	/**删除群消息**/
	public static final String DELETEGROUPMESSAGE=URL+"/client/user/prv/del_crowd_msg.php";
	/**与某人私信列表**/
	public static final String CHATLISTWITHSOMEONE=URL+"/client/user/prv/member_msgs.php";
	/**群相册**/
	public static final String GROUPPHOTOLIST=URL+"/client/user/crowd/photo_list.php";
	/**上传群图片**/
	public static final String UPLOADGROUPPHOTO=URL+"/client/user/crowd/upload_photo.php";
	/**退出群**/
	public static final String EXITGROUP=URL+"/client/user/crowd/logout_crowd.php";
	/**删除群图片**/
	public static final String DELETEGROUPPHOTO=URL+"/client/user/crowd/del_photo.php";
	/**获取动态详情**/
	public static final String GETDONGTAI_DETAIL=URL+"/client/user/dynamic/get_dy_detail.php";
	/**评论**/
	public static final String REPLY_DONGTAI=URL+"/client/user/dynamic/reply_dy.php";
	/**获取支付宝单号**/
	public static final String GETALIPAY=URL+"/client/pay/build_alipay.php";
	/**举报并拉黑**/
	public static final String JUBAOANDBLACK=URL+"/client/user/black/report_and_black.php";
	/**加入黑名单**/
	public static final String ADDTOBLACK=URL+"/client/user/black/add_black.php";
	/**取消黑名单**/
	public static final String CANCLEBLACK=URL+"/client/user/black/cancel_black.php";
	/**黑名单列表**/
	public static final String BLACKLIST=URL+"/client/user/black/black_list.php";
	/**发送语音图片**/
	public static final String SENDFILE=URL+"/client/msg/send_file_msg.php";
	/**获取广告图片**/
	public static final String ADBANNER=URL+"/client/get_act_banner.php";
	/**赠送体力**/
	public static final String GIVETILI=URL+"/client/user/to_give_power.php";
	/**我的物品**/
	public static final String MY_PRO=URL+"/client/user/get_my_goods.php";
	/**退还**/
	public static final String EXCHANGE_MONEY=URL+"/client/user/exchange_apply.php";
	/**抽奖号码**/
	public static final String PRONUM=URL+"/client/user/get_lottery_codes.php";
	/**删除单号**/
	public static final String DELETENUM=URL+"/client/user/activity/del_code.php";
	/**统计**/
	public static final String TONGJI=URL+"/reg_channel.php";
}
