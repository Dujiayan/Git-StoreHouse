package cn.itcast.web.shiro;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import cn.itcast.domain.Module;
import cn.itcast.domain.Role;
import cn.itcast.domain.User;
import cn.itcast.service.UserService;

/**
 *自定义realm类  给安全管理器提供数据 
 *
 */
public class AuthRealm extends AuthorizingRealm{
	@Autowired
	private UserService userService;
	/*
	 *	给认证功能提供数据的
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		//token是页面提交的数据
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		//调取获取用户名方法
		final String username = upToken.getUsername();
		//查询条件
		  Specification<User> spec = new Specification<User>() {

			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("userName").as(String.class), username);
				
			}
		};
		//使用用户名查询数据库 ,默认为用户名是唯一的
		List<User> list = userService.find(spec);
		//判断
		if (list !=null && list.size() >0) {
			//获取用户对象
			User user = list.get(0);
			//user从数据库中 查询 ,密码加密了
			/**
			 * principal 储存user对象
			 * credentials 当前用户密码(加密后)
			 * realname real域的名称  自定义名称
			 */
			SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user,user.getPassword(),"abc");
			//说明,查询到用户对象
			return info;
		}
		return null;
	}
	
	/*
	 * 给授权方法提供数据
	 * 访问安全管理器,让安全管理器帮忙做授权功能 ,安全管理器会访问realm域中doGetAuthorizationInfo方法,获取数据
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection pc) {
		//授权的程序
		List<String> list = new ArrayList<String>();
		
		//查询当前登录用户的所有菜单  ,对象导航查询
		//先查询出用户 ,可以从HttpSession对象中获取,从可以从realm域中获取中
		User user = (User)pc.fromRealm("abc").iterator().next();
		
		//对象导航查询 ,获取到用户所拥有的的所有角色
		Set<Role> roles = user.getRoles();
		//遍历角色集合
		for (Role role : roles) {
			//再获取到角色拥有的菜单集合
			Set<Module> modules = role.getModules();
			//遍历菜单
			for (Module module : modules) {
				list.add(module.getName());
			}
		}
		//遍历用户拥有的所有菜单,一定查询数据库
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.addStringPermissions(list);
		return info;
	}

}
