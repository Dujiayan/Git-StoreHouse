package cn.itcast.web.action.sysadmin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.domain.Dept;
import cn.itcast.domain.Role;
import cn.itcast.domain.User;
import cn.itcast.service.DeptService;
import cn.itcast.service.RoleService;
import cn.itcast.service.UserService;
import cn.itcast.utils.Page;
import cn.itcast.web.action.BaseAction;

/*
 * 用户的Action类
 */
//@Namespace(value="/")
@Namespace(value="/sysadmin")
public class UserAction extends BaseAction implements ModelDriven<User>{
	private static final long serialVersionUID = 3169080203996142060L;
	private User model = new User();
	
	@Override
	public User getModel() {
		return model;
	}
	@Autowired
	private RoleService roleService;
	@Autowired
	private UserService userService;
	@Autowired
	private DeptService deptService;
	
	// 用来进行封装的 Page.xxx 属性驱动   ${Page.xxx}
	private Page page = new Page();
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	/**
	 * 分页查询
	 */
	@Action(value="userAction_list" ,results={
			@Result(name="list",location="/WEB-INF/pages/sysadmin/user/jUserList.jsp")
	})
	public String list(){
		//条件对象
		Specification<User> spec = new Specification<User>() {

			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("state").as(Integer.class),1 );
			}
		};
		//分页查询
		org.springframework.data.domain.Page<User> page2 = userService.findPage(spec, new PageRequest(page.getPageNo()-1, page.getPageSize()));
		//把数据转移
		super.copyPage(page, page2, "userAction_list");
		//压栈
		super.push(page);
		return "list";
	}
	/**
	 * 查看详细
	 */
	@Action(value="userAction_toview",results={
			@Result(name="toview",location="/WEB-INF/pages/sysadmin/user/jUserView.jsp")
	})
	public String toview(){
		User user = userService.get(model.getId());
		super.push(user);
		return "toview";
	}
	/**
	 * 	跳转到新增页面的方法
	 */
	@Action(value="userAction_tocreate",results={//  tocreate_Action
			@Result(name="tocreate",location="/WEB-INF/pages/sysadmin/user/jUserCreate.jsp")
	})
	public String tocreate(){
		//查询出所有部门
		List<Dept> deptList = deptService.find(null);
		//压栈
		super.put("deptList",deptList);
		//查询出所有用户
		List<User> userList = userService.find(null);
		super.put("userList", userList);
		return "tocreate";
	}
	/**
	 * 保存
	 */
	@Action(value="userAction_insert",results={
			@Result(name="success",type="redirect",location="userAction_list")
	})
	public String insert(){
		//后端接收参数 model封装数据
		userService.saveOrUpdate(model);
		return SUCCESS;
	}
	
	/**
	 * 跳转到修改的页面
	 * @return
	 * @throws Exception
	 */
	@Action(value="userAction_toupdate",results=@Result(name="toupdate",location="/WEB-INF/pages/sysadmin/user/jUserUpdate.jsp"))
	public String toupdate() throws Exception {
		User user = userService.get(model.getId());
		List<Dept> deptList = deptService.find(null);
		
		this.put("deptList", deptList);
		this.push(user);
		return "toupdate";
	}
	
	/**
	 * 修改
	 * @return
	 * @throws Exception
	 */
	@Action(value="userAction_update",results=@Result(name="success",type="redirect",location="deptAction_list"))
	public String update() throws Exception {
		
		return SUCCESS;
	}
	
	/**
	 * 删除
	 * @return
	 * @throws Exception
	 */
	@Action(value="deptAction_delete",results=@Result(name="success",type="redirect",location="deptAction_list"))
	public String delete() throws Exception {
		
		return SUCCESS;
	}

	/**
	 * 给用户分配角色页面
	 */
	@Action(value="UserAction_torole",results={
		@Result(name="torole",location="/WEB-INF/pages/sysadmin/user/jUserRole.jsp")
	})
	public String torole(){
		//通过id查询用户
		User user = userService.get(model.getId());
		//查询所有角色
		List<Role> rolelist = roleService.find(null);
		//查询用户拥有的角色
		Set<Role> roles = user.getRoles();
		//创建字符串拼接
		StringBuilder sb = new StringBuilder();
		//目的:把用户拥有的角色名称使用字符串拼接的方式:演员,导演,
		for (Role role : roles) {
			sb.append(role.getName());
			sb.append(",");
		}
		//生成字符串
		String roleStr = sb.toString();
		//压栈
		super.push(user);
		super.put("rolelist", rolelist);
		super.put("roleStr", roleStr);
		
		
		return "torole";
	}
	
	//可以封装,默认使用分隔
	//勾选多个角色的i的数组
	private String[] roleIds;
	public String[] getRoleIds() {
		return roleIds;
	}
	public void setRoleIds(String[] roleIds) {
		this.roleIds = roleIds;
	}
	/**
	 * 给用户分配角色的功能
	 */
	@Action(value="UserAction_role",results={
			@Result(name="success",type="redirect",location="userAction_list")
	})
	public String role(){
		//model会封装用户的id
		User user = userService.get(model.getId());
		//第一种方式:创建新的set集合,储存角色
		HashSet<Role> roles = new HashSet<Role>();
		//第二种方式:先清空
		//封装勾选的两个角色id
		for (String roleId : roleIds) {
			//可以查询到角色的对象
			Role role = roleService.get(roleId);
			//把role添加到user的set集合中
			roles.add(role);
			
		}
		//给用户设置的新的set集合
		user.setRoles(roles);
		//更新对象
		userService.saveOrUpdate(user);
		return "success";
		
	}
}	
