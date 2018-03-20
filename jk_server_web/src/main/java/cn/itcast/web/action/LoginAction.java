package cn.itcast.web.action;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.domain.User;
import cn.itcast.utils.SysConstant;
import cn.itcast.utils.UtilFuns;

/**
 * @Description: 登录和退出类
 * @Author:		传智播客 java学院	传智.宋江
 * @Company:	http://java.itcast.cn
 * @CreateDate:	2014年10月31日
 * 
 * 继承BaseAction的作用
 * 1.可以与struts2的API解藕合
 * 2.还可以在BaseAction中提供公有的通用方法
 */
@Namespace("/")
@Results({
	@Result(name="login",location="/WEB-INF/pages/sysadmin/login/login.jsp"),
	@Result(name="success",location="/WEB-INF/pages/home/fmain.jsp"),
	@Result(name="logout",location="/index.jsp")})
public class LoginAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private String username;
	private String password;



	//SSH传统登录方式
	@Action("loginAction_login")
	public String login() throws Exception {
		
//		if(true){
//			String msg = "登录错误，请重新填写用户名密码!";
//			this.addActionError(msg);
//			throw new Exception(msg);
//		}
//		User user = new User(username, password);
//		User login = userService.login(user);
//		if (login != null) {
//			ActionContext.getContext().getValueStack().push(user);
//			session.put(SysConstant.CURRENT_USER_INFO, login);	//记录session
//			return SUCCESS;
//		}
//		return "login";
		//判断
		if (UtilFuns.isEmpty(username)) {
			return 	"login";	
		}
		try{
			// 先获取到subject的对象，绑定到当前线程的
			Subject subject = SecurityUtils.getSubject();			
			// 创建用户名和密码，password没有加密的
			AuthenticationToken token = new UsernamePasswordToken(username,password);
			System.out.println("aaaaa");
			// 访问安全管理器对象
			subject.login(token);
			// 认证通过了...
			// 从安全管理器中获取到认证通过后的user对象
			User user = (User)subject.getPrincipal();
			// 获取到认证成功后的用户的对象，存入到HttpSession对象中，一定使用常量做key
			ServletActionContext.getRequest().getSession().setAttribute(SysConstant.CURRENT_USER_INFO, user);
			// 跳转到首页
			return SUCCESS;
		}catch(Exception e){
			// 打印异常
			// 向值栈中存入错误的信息，给出提示
			
			// 跳转到登录页面
			return "login";
		}
	}
	
	
	//退出
	@Action("loginAction_logout")
	public String logout(){
		session.remove(SysConstant.CURRENT_USER_INFO);		//删除session
		SecurityUtils.getSubject().logout();   //登出
		return "logout";
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}

