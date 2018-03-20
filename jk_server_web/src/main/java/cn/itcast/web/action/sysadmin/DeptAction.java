package cn.itcast.web.action.sysadmin;

import java.util.List;

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

import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.domain.Dept;
import cn.itcast.service.DeptService;
import cn.itcast.utils.Page;
import cn.itcast.web.action.BaseAction;
@Namespace("/sysadmin")
public class DeptAction extends BaseAction implements ModelDriven<Dept>{
	private static final long serialVersionUID = 3169080203996142060L;
	
	private Dept model = new Dept();
	//用来进行属性封装的page.xxx  属性驱动   ${page.xoo}
	private Page page= new Page();
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	@Override
	public Dept getModel() {
		return model;
	}
	
	@Autowired
	private DeptService deptService ;
	@Action(value="deptAction_list",results={
			@Result(name="list",location="/WEB-INF/pages/sysadmin/dept/jDeptList.jsp")
	})
	public String list() throws Exception {
	
		//进行分页查询 
		
		/*PageRequest pageable = new PageRequest(page.getPageNo()-1, page.getPageSize());
		org.springframework.data.domain.Page<Dept> page2 = deptService.findPage(null, pageable);
		page.setResults(page2.getContent());
		page.setTotalPage(page2.getTotalPages());
		page.setTotalRecord(page2.getTotalElements());
		page.setUrl("deptAction_list");
		super.push(page);
		//跳页面
		return "list";*/
		
		//创建拼接条件的接口
		Specification<Dept> spec = new Specification<Dept>() {

			@Override
			public Predicate toPredicate(Root<Dept> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("state").as(Integer.class), 1);
			}
		};
		PageRequest pageable = new PageRequest(page.getPageNo()-1, page.getPageSize());
		org.springframework.data.domain.Page<Dept> page2 = deptService.findPage(spec, pageable);
		
		super.copyPage(page, page2, "deptAction_list");
		//把page对象压入值栈
		super.push(page);
		return "list";
	}
	/**
	 * 查看详细
	 */
	@Action(value="deptAction_toview",results=
			@Result(name="toview",location="/WEB-INF/pages/sysadmin/dept/jDeptView.jsp")
			)
	public String toview(){
		//model会封装id的属性
		Dept dept = deptService.get(model.getId());
		//压栈
		super.push(dept);
		return "toview";
	}
	/**
	 * 跳转到新增页面的方法
	 * 
	 */
	@Action(value="deptAction_tocreate",results={
		@Result(name="tocreate",location="/WEB-INF/pages/sysadmin/dept/jDeptCreate.jsp")	
			})
	public String tocreate(){
		Specification<Dept> spec = new Specification<Dept>() {

			@Override
			public Predicate toPredicate(Root<Dept> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				return 	cb.equal(root.get("state").as(Integer.class), 1);
			}
			};
			List<Dept> deptList = deptService.find(spec);
			super.put("deptList", deptList);
			return "tocreate";
	}
	/**
	 *保存
	 */
	@Action(value="deptAction_insert",results={
		@Result(name="success",type="redirect",location="deptAction_list")})
	public String insert (){
		// 数据封装
		//model封装了所有数据
		//保存或者更新
		deptService.saveOrUpdate(model);
		return SUCCESS;
	}
	/**
	 * 跳转到修改页面
	 */
	@Action(value="deptAction_toupdate",results={
			@Result(name="toupdate",location="/WEB-INF/pages/sysadmin/dept/jDeptUpdate.jsp")
	})
	public String toupdate(){
		//model封装id的值
		Dept dept = deptService.get(model.getId());
		//压栈
		super.push(dept);
		
		// 查询出所有的部门  唯一标识 oid
		Specification<Dept> spec = new Specification<Dept>() {

			@Override
			public Predicate toPredicate(Root<Dept> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("state").as(Integer.class), 1);
			}
		};
		List<Dept> deptList = deptService.find(spec);
		
		//从deptList	中移除部门的对象
		deptList.remove(dept);
		
		super.put("deptList", deptList);
		return "toupdate";
	}
	
	/**
	 * 修改
	 */
	@Action(value="deptAction_update",results={
			@Result(name="success",type="redirect",location="deptAction_list")
	})
	public String update(){
		//model把页面提交的数据全部封装了
		//通过id查询
		Dept dept = deptService.get(model.getId());
		//把medol中的数据设置到dept对象中
		dept.setDeptName(model.getDeptName());
		dept.setParent(model.getParent());
		//更新
		deptService.saveOrUpdate(dept);
		return SUCCESS;
	}
	/**
	 * 删除
	 */
	public String delete(){
		// 查看数据封装
		String[] ids = model.getId().split(",");
		//删除
		deptService.delete(ids);
		return SUCCESS;
		
	}
}
