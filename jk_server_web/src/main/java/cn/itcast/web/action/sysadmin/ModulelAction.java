package cn.itcast.web.action.sysadmin;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.domain.Module;
import cn.itcast.service.ModuleService;
import cn.itcast.utils.Page;
import cn.itcast.web.action.BaseAction;
@Results({
	@Result(name="alist",type="redirectAction",location="ModulelAction_list")
})
public class ModulelAction extends BaseAction implements ModelDriven<Module>{
	private Module model = new Module();
	@Override
	public Module getModel() {
		return model;
	}
	
	private Page page = new Page();
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	@Autowired
	private  ModuleService	moduleService;	
	/**
	 * 分页查询
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action(value = "moduleAction_list", results = {
			@Result(name = "list", location = "/WEB-INF/pages/sysadmin/module/jModuleList.jsp") })
	public String list() throws Exception {
		// 1.调用业务方法，实现分页查询
		Specification<Module> spec = new Specification<Module>() {

			public Predicate toPredicate(Root<Module> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("state").as(Integer.class), 1);
			}
		};
		org.springframework.data.domain.Page<Module> page1 = moduleService.findPage(spec,
				new PageRequest(page.getPageNo() - 1, page.getPageSize()));

		// 给我们的分页组件赋值
		super.copyPage(page, page1,"moduleAction_list");

		// 设置url
		// page.setUrl("moduleAction_list");

		// 将page放入值栈中
		super.push(page);

		// 跳页面
		return "list";
	}

	/**
	 * 查看详情 <input type="checkbox" name="id" value="${dept.id }"/></td> model
	 * id属性：${dept.id }
	 */
	@Action(value = "moduleAction_toview", results = {
			@Result(name = "toview", location = "/WEB-INF/pages/sysadmin/module/jModuleView.jsp") })
	public String toview() throws Exception {
		// 1.调用业务方法
		Module obj = moduleService.get(model.getId());

		// 2.放入值栈中
		super.push(obj);
		// 3.跳页面
		return "toview";
	}

	/**
	 * 进入新增页面
	 */
	@Action(value = "moduleAction_tocreate", results = {
			@Result(name = "tocreate", location = "/WEB-INF/pages/sysadmin/module/jModuleCreate.jsp") })
	public String tocreate() throws Exception {

		return "tocreate";
	}

	/**
	 * 插入 model parent id : deptName:
	 */
	@Action("moduleAction_insert")
	public String insert() throws Exception {
		// 1.调用业务方法，实现保存
		moduleService.saveOrUpdate(model);
		return "alist";
	}

	/**
	 * 进入修改页面
	 */
	@Action(value = "moduleAction_toupdate", results = {
			@Result(name = "toupdate", location = "/WEB-INF/pages/sysadmin/module/jModuleUpdate.jsp") })
	public String toupdate() throws Exception {
		
		//3.加载原有部门对象
		Module dept = moduleService.get(model.getId());
		
		//放入值栈中
		super.push(dept);
		
		
		return "toupdate";
	}
	
	/**
	 * 更新
	 */
	@Action("moduleAction_update")
	public String update() throws Exception {
		//1.先查询原有的对象
		Module obj = moduleService.get(model.getId());
		//2.针对页面上要修改的属性进行修改
		obj.setName(model.getName());
		obj.setLayerNum(model.getLayerNum());
		obj.setCpermission(model.getCpermission());
		obj.setCurl(model.getCurl());
		obj.setCtype(model.getCtype());
         
		obj.setState(model.getState());
		obj.setBelong(model.getBelong());
		obj.setCwhich(model.getCwhich());
		obj.setRemark(model.getRemark());
		obj.setOrderNo(model.getOrderNo());
          
       
		//3.调用业务方法，实现更新
		moduleService.saveOrUpdate(obj);
		//4.跳页面
		return "alist";
	}
	
	/**
	 * 删除
	 */
	@Action("moduleAction_delete")
	public String delete() throws Exception {
		//1.调用业务方法，实现删除
		moduleService.deleteById(model.getId());
		//跳页面
		return "alist";
	}
}
