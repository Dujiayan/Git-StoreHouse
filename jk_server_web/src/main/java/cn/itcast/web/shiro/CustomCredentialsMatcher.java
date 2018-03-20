package cn.itcast.web.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

import cn.itcast.utils.Encrypt;

public class CustomCredentialsMatcher extends SimpleCredentialsMatcher{
	/**
	 * 密码对比的方法
	 * token	登陆页面传的,密码没有加密
	 * info		从数据库中查询数据,密码是加密后的
	 */
	public boolean doCredentialsMatch(AuthenticationToken token,AuthenticationInfo info){
		// 先获取页面的密码
		UsernamePasswordToken uptoken = (UsernamePasswordToken) token;
		//获取用户名
		String username = uptoken.getUsername();
		//获取密码,没有加密
		String fromPwd = new String (uptoken.getPassword());
		//对象fromPwd进行加密
		String md5 = Encrypt.md5(fromPwd,username);
		//在获取到数据库中的密码
		String dbPwd = (String) info.getCredentials();
		//对比
		return super.equals(md5,dbPwd);
		
	}
}
