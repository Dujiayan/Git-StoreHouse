package cn.itcast.web.action.cargo;
/**
 * 购销合同下的货物下的附件的Action类
 */


import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.domain.ContractProduct;
import cn.itcast.domain.ExtCproduct;
import cn.itcast.domain.Factory;
import cn.itcast.service.ExtCproductService;
import cn.itcast.service.FactoryService;
import cn.itcast.utils.Page;
import cn.itcast.web.action.BaseAction;
@Namespace(value="/cargo")
public class ExtCProductAction extends BaseAction implements ModelDriven<ExtCproduct>{
	private static final long serialVersionUID = 3169080203996142060L;
	
	private ExtCproduct model = new ExtCproduct();
	@Override
	public ExtCproduct getModel() {
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
	private ExtCproductService extCproductService ;
	@Autowired
	private FactoryService factoryService; 
	
	/**
	 * 进入新增页面
	 */
	@Action(value="extCproductAction_tocreate",results={
			@Result(name="tocreate",location="/WEB-INF/pages/cargo/contract/jExtCproductCreate.jsp")
	})
	public String tocreate() throws Exception{
		//查询生产 附加的工厂
		Specification<Factory> spec = new Specification<Factory>() {
			@Override
			public Predicate toPredicate(Root<Factory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get("state").as(String.class), "1");
				Predicate p2 = cb.equal(root.get("ctype").as(String.class),"货物" );
				return cb.and(p,p2);
			}
		};
		//查询所有的生产厂家
		List<Factory> factorylist = factoryService.find(spec);
		super.put("factorylist", factorylist);
		
		Specification<ExtCproduct>spec2 = new Specification<ExtCproduct>() {
			//查询货物下所有的附件
			public Predicate toPredicate(Root<ExtCproduct> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				//获取关联对象
				Join<ExtCproduct, ContractProduct> join = root.join("ContractProduct");
				return cb.equal(join.get("id").as(String.class),model.getContractProduct().getId() );
			}
		};
		
		//查询货物下的附件的功能,分页显示
		org.springframework.data.domain.Page<ExtCproduct> page2 = extCproductService.findPage(spec2, new PageRequest(page.getPageNo()-1, page.getPageSize()));
		//拷贝数据
		super.copyPage(page, page2, "extCproductAction_tocreate");
		//压栈
		super.push(page);
		return "tocreate";
	}
	/**
	 * 保存
	 */
	@Action(value="extCproductAction_insert",results=
			{@Result(name="success",type="redirect",location="extCproductAction_tocreate",params={"contractProduct.id","${ontractProduct.id}"})})
	public String insert(){
		extCproductService.saveOrUpdate(model);
		
		return SUCCESS;
	}
	/**
	 * 跳转到修改的页面
	 */
	@Action(value="extCproductAction_toupdate",results=@Result(name="toupdate",location="/WEB-INF/pages/cargo/contract/jExtCproductUpdate.jsp"))
	public String toupdate() throws Exception {
		// model会封装数据
		ExtCproduct ExtCproduct = extCproductService.get(model.getId());
		super.push(ExtCproduct);
		
		// 查询工厂
		Specification<Factory> spec1 = new Specification<Factory>() {
			public Predicate toPredicate(Root<Factory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// 拼接2个查询条件
				Predicate p = cb.equal(root.get("state").as(String.class), "1");
				// 拼接查询条件
				Predicate p2 = cb.equal(root.get("ctype").as(String.class), "货物");
				// 拼接条件
				return cb.and(p,p2);
			}
		};
		// 查询所有的生成厂家
		List<Factory> factoryList = factoryService.find(spec1);
		// 压栈
		super.put("factoryList", factoryList);
		
		return "toupdate";
	}
	
	
	/**
	 * 修改
	 * @return
	 * @throws Exception
	 */
	@Action(value="extCproductAction_update",results=@Result(name="success",type="redirect",location="extCproductAction_tocreate",params={"contractProduct.id","${contractProduct.id}"}))
	public String update()  {
		// 通过id查询
		ExtCproduct ExtCproduct = extCproductService.get(model.getId());
		// 设置
		ExtCproduct.setFactory(model.getFactory());
		ExtCproduct.setFactoryName(model.getFactoryName());
		ExtCproduct.setProductNo(model.getProductNo());
		ExtCproduct.setProductImage(model.getProductImage());
		ExtCproduct.setCnumber(model.getCnumber());
		ExtCproduct.setAmount(model.getAmount());
		ExtCproduct.setPackingUnit(model.getPackingUnit());
		ExtCproduct.setPrice(model.getPrice());
		ExtCproduct.setOrderNo(model.getOrderNo());
		ExtCproduct.setProductDesc(model.getProductDesc());
		ExtCproduct.setProductRequest(model.getProductRequest());
		
		// 更新附件
		extCproductService.saveOrUpdate(ExtCproduct);
		
		return SUCCESS;
	}
	
	/**
	 * 删除
	 * @return
	 * @throws Exception
	 */
	@Action(value="ExtCproductAction_delete",results=@Result(name="success",type="redirect",location="extCproductAction_tocreate",params={"contract.id","${contract.id}"}))
	public String delete()  {
		extCproductService.deleteById(model.getId());
		return SUCCESS;
	}
	
}















