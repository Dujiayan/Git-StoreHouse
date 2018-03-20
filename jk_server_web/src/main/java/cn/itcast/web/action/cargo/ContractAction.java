package cn.itcast.web.action.cargo;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.domain.Contract;
import cn.itcast.domain.User;
import cn.itcast.service.ContractService;
import cn.itcast.utils.Page;
import cn.itcast.utils.SysConstant;
import cn.itcast.web.action.BaseAction;
@Namespace(value="/cargo")

public class ContractAction extends BaseAction implements ModelDriven<Contract>{
	Contract model = new Contract();
	@Override
	public Contract getModel() {
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
	private ContractService contractService;
	
	public ContractService getContractService() {
		return contractService;
	}
	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}
	/**
	 * 细粒度权限管理的分页
	 */
	@Action(value="contractAction_list",results=@Result(name="list",location="/WEB-INF/pages/cargo/contract/jContractList.jsp"))
	public String list() throws Exception{
		//获取用户
		final User user = (User) ServletActionContext.getRequest().getSession().getAttribute(SysConstant.CURRENT_USER_INFO);
		//获取等级
		final Integer degree = user.getUserinfo().getDegree();
		//拼接查询条件
		Specification<Contract> spec = new Specification<Contract>() {
			
			@Override
			public Predicate toPredicate(Root<Contract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = null;
				if (degree == 4) {
						//普通员工
					p = cb.equal(root.get("createBy").as(String.class), user.getId());
				}else if(degree == 3){
					p = cb.equal(root.get("createDept").as(String.class),user.getDept().getId());
				}
				return p;
			}
		};
		//分页
		org.springframework.data.domain.Page<Contract> page2 = contractService.findPage(spec, new PageRequest(page.getPageNo()-1, page.getPageSize()));
		//拷贝数据
		super.copyPage(page, page2, "contractAction_list");
		super.push(page);
		return "list";
	}
	
	/**
	 * 分页查询
	 */
/*	@Action(value="contractAction_list",results={
			@Result(name="list",location="/WEB-INF/pages/cargo/contract/jContractList.jsp")
	})
	public String list(){
		//调用业务方法,实现分页查询
		org.springframework.data.domain.Page<Contract> page1 = contractService.findPage(null, new PageRequest(page.getPageNo()-1, page.getPageSize()));
		super.copyPage(page, page1, "contractAction_list");
		
		//将page放入值栈
		super.push(page1);
		//跳页面
		return "list";
	}
	*/
	
	/**
	 * 查询详情  <input type="checkbox" name="id" value="${dept.id }"/></td> model
	 * id属性：${dept.id }
	 */
	@Action(value = "contractAction_toview", results = {
			@Result(name = "toview", location = "/WEB-INF/pages/cargo/contract/jContractView.jsp") })
	public String toview (){
		//调用业务方法
		Contract obj = contractService.get(model.getId());
		//放入值栈
		super.push(obj);
		return "toview";
	}
	
	/**
	 *跳转到新增页面的方法 
	 */
	@Action(value="ContractAction_tocreate",results={
			@Result(name="tocreate",location="/WEB-INF/pages/cargo/contract/jContractCreate.jsp")
	})
	public String tocreate() throws Exception{
		return "tocreate";
	}
	
	/**
	 * 保存
	 */
	@Action(value="contractAction_insert",results = {
			@Result(name = "success", type="redirect", location = "contractAction_list") })
	public String insert() throws Exception{
		//获取当前登录用户
		User user = (User) ServletActionContext.getRequest().getSession().getAttribute(SysConstant.CURRENT_USER_INFO);
		//给购销合同添加标识
		user.setCreateBy(user.getId());
		user.setCreateDept(user.getDept().getId());
		//保存购销合同
		contractService.saveOrUpdate(model);
		
		return SUCCESS;
	}
	
	/**
	 * 跳转到修改页面
	 */
	@Action(value="contractAction_toupdate",results=@Result(name="toupdate",location="/WEB-INF/pages/cargo/contract/jContractUpdate.jsp"))
	public String toupdate() throws Exception {
		Contract contract = contractService.get(model.getId());
		super.push(contract);
		return "toupdate";
	}
	
	
	
	/**
	 * 更新
	 */
	@Action(value="contractAction_update",results={
			@Result(name="success",type="redirect",location="contractAction_list")
	})
	public String update(){
		//获取对象
		Contract contract = contractService.get(model.getId());
		//2.针对页面上要修改的属性进行修改
		contract.setCustomName(model.getCustomName());
		contract.setPrintStyle(model.getPrintStyle());
		contract.setContractNo(model.getContractNo());
		contract.setOffer(model.getOffer());
		contract.setInputBy(model.getInputBy());
		contract.setCheckBy(model.getCheckBy());
		contract.setInspector(model.getInspector());
		contract.setSigningDate(model.getSigningDate());
		contract.setImportNum(model.getImportNum());
		contract.setShipTime(model.getShipTime());
		contract.setTradeTerms(model.getTradeTerms());
		contract.setDeliveryPeriod(model.getDeliveryPeriod());
		contract.setCrequest(model.getCrequest());
		contract.setRemark(model.getRemark());
		
		//调用业务实现更新
		contractService.saveOrUpdate(contract);
		
		return SUCCESS;
		}
	/**
	 * 删除
	 */
	@Action(value="contractAction_delete",results={
			@Result(name="success",type="redirect",location="contractAction_list")
			})
	public String delete() throws Exception {
		contractService.deleteById(model.getId());
		return SUCCESS;
	}
	/**
	 * 取消
	 */
	@Action(value="contractAction_cancel",results=@Result(name="success",type="redirect",location="contractAction_list"))
	public String cancel() throws Exception{
		//获得合同状态
		Contract contract = contractService.get(model.getId());
		//修改状态
		contract.setState(0);
		//更新
		contractService.saveOrUpdate(contract);
		return SUCCESS;
		}
	/**
	 * 提交
	 */
	@Action(value="contractAction_submit",results=@Result(name="success",type="redirect",location="contractAction_list"))
	public String submit() throws Exception{
		//获得合同状态
		Contract contract = contractService.get(model.getId());
		//修改状态
		contract.setState(1);
		//更新
		contractService.saveOrUpdate(contract);
		return SUCCESS;
	
		
	}
}


