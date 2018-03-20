package cn.itcast.web.action.sysadmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.domain.Module;
import cn.itcast.domain.Role;
import cn.itcast.service.ModuleService;
import cn.itcast.service.RoleService;
import cn.itcast.utils.Page;
import cn.itcast.utils.SysConstant;
import cn.itcast.utils.UtilFuns;
import cn.itcast.web.action.BaseAction;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


/**
 *  
 */
//@Namespace(value = "/")
@Results({@Result(name="alist",type="redirectAction",location="roleAction_list")})
@Namespace(value = "/sysadmin")
public class RoleAction extends BaseAction<Role> implements ModelDriven<Role>{
	private Role model = new Role();
	@Autowired
	private ModuleService moduleService;
	
	
	private Page page = new Page();
	
	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	@Override
	public Role getModel() {
		return model;
	}
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private JedisPool jedispool;
	/**
	 * 分页查询
	 */
	@Action(value="roleAction_list",results={
			@Result(name="list",location="/WEB-INF/pages/sysadmin/role/jRoleList.jsp")
	})
	public  String list(){
		//调用业务 ,实现分页查询
		org.springframework.data.domain.Page<Role> page2 = roleService.findPage(null, new PageRequest(page.getPageNo()-1, page.getPageSize()));
		//给分页组件赋值
		super.copyPage(page, page2, "roleAction_list");
		//将page放入值栈
		super.push(page);
		//跳页面
		return "list";
	}

	/**
	 * 查看详情
	 */
	@Action(value="roleAction_toview",results={
		@Result(name="toview",location="/WEB-INF/pages/sysadmin/role/jRoleView.jsp")
	})
	public String toview(){
		//调用业务查询
		Role obj = roleService.get(model.getId());
		//放入值栈
		super.push(obj);
		//跳转页面
		return "toview";
	}
	
	/**
	 * 进入新增页面
	 */
	@Action(value="roleAction_tocreate",results={
			@Result(name="tocreate",location="/WEB-INF/pages/sysadmin/role/jRoleCreate.jsp")
	})
	public String tocreate(){
		return "tocreate";
		
	}
	/**
	 * 插入model parent id : deptName
	 */
	@Action(value="roleAction_insert")
	public String insert(){
		//调用业务,实现保存
		roleService.saveOrUpdate(model);
		return "alist";
		
	}
	/**
	 * 进入修改页面
	 */
	@Action(value="roleAction_toupdate",results={
			@Result(name="toupdate",location="/WEB-INF/pages/sysadmin/role/jRoleUpdate.jsp")
	})
	public String toupdate(){
		//加载原有部门对象
		Role dept = roleService.get(model.getId());
		//压栈
		super.push(dept);
		return "toupdate";
		
	}
	/**
	 * 更新
	 */
	@Action(value="roleAction_update")
	public String update(){
		//先查询原有对象
		Role obj = roleService.get(model.getId());
		//针对页面上要修改的数据进行修改
		obj.setName(model.getName());
		obj.setRemark(model.getRemark());
		//调用业务方法  ,实现更新
		roleService.saveOrUpdate(obj);
		//跳页面
		return "alist";
	}
	/*
	 * 删除
	 */
	@Action(value="roleAction_delete")
	public String delete(){
		//实现业务方法,实现删除
		roleService.deleteById(model.getId());
		//跳页面
		return "alist";
		
	}
	/**
	 * 跳转到给角色分配菜单的页面
	 * @return
	 * @throws Exception
	 */
	@Action(value = "roleAction_tomodule", results = {
			@Result(name = "tomodule", location = "/WEB-INF/pages/sysadmin/role/jRoleModule.jsp") })
	public String tomodule() throws Exception {
		Role role = roleService.get(model.getId());
		// 压栈
		super.push(role);
		return "tomodule";
	}
	
	/**
	 * 异步给zTree提供数据
	 * 
	 * 一个菜单对象 = {"id":"菜单id","pId":"菜单的父id","name":"菜单名称","checked":"true|false"}
	 * 
	 * [{"id":"值","pId":"值","name":"菜单名称","checked":"true|false"},{"id":"值","pId":"值","name":"菜单名称","checked":"true|false"}]
	 * 
	 * 怎么样生成json数据，FastJson工具类
	 * 
	 * List<Map<k,v>> list
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action(value = "roleAction_genzTreeNodes")
	public void getZtreeNodes() throws Exception {
		// 查询到数据，把数据转换成json的字符串，响应
		// 查询角色对象
		Role role = roleService.get(model.getId());
		
		//=============加入redis=============
		//先从连接池获取对象
		Jedis jedis = jedispool.getResource();
		//定义key值,key由字符串和角色id拼接
		String key = model.getId()+"Wang";
		//通过key从缓存中获取数据
		String val = jedis.get(key);
		//判断,如果为空,说明缓存中没有
		if (UtilFuns.isEmpty(val)) {
			//从数据库中查询
			//查询所有菜单
			//查询所有的菜单集合
			List<Module> mlist = moduleService.find(null);
			
			// 查询角色拥有的菜单，判断哪些是需要选中的
			Set<Module> modules = role.getModules();
			
			// 创建List集合，存入生成的数据
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			
			// 遍历mlist集合
			for(Module m : mlist){
				// 把m菜单对象的数据存入到map集合中，再把map存入到list集合中
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", m.getId());
				map.put("pId", m.getParentId());
				map.put("name", m.getName());
				if(modules.contains(m)){
					map.put("checked","true");
				}else{
					map.put("checked","false");
				}
				// 存入list
				list.add(map);
			}
			
			// 把list集合转换成Json的数据
			String str = JSON.toJSONString(list);
			val = str;
			System.out.println("=========查询数据库==========");
		}else{
			System.out.println("=========查询缓存============");
		}
		jedis.close();
		// 响应
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("application/json;charset=UTF-8");
		//response.getWriter().print(str);
		response.getWriter().print(val);
	}
	/**
	 * 给角色分配菜单的功能的实现
	 * @return
	 * @throws Exception
	 */
	private String moduleIds;
	public String getModuleIds() {
		return moduleIds;
	}

	public void setModuleIds(String moduleIds) {
		this.moduleIds = moduleIds;
	}
	
	@Action(value="roleAction_module")
	public String module() throws Exception {
		Role role = roleService.get(model.getId());
		// 清空
		role.getModules().clear();
		//切割moduleIds
		String[] ids = moduleIds.split(",");
		//遍历
		for (String mid : ids) {
			Module module = moduleService.get(mid);
			//存入role对象的set的集合中
			role.getModules().add(module);
			
		}
		//更新角色对象
		roleService.saveOrUpdate(role);
		
		//获得jedis,清空缓存
		Jedis jedis = jedispool.getResource();
		String key = role.getId()+"Wang";
		jedis.del(key);
		//关闭jedis
		jedis.close();
		
		return "alist";
	}
	
	
}
