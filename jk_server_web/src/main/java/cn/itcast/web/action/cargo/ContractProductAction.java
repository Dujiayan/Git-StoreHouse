package cn.itcast.web.action.cargo;

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

import cn.itcast.domain.Contract;
import cn.itcast.domain.ContractProduct;
import cn.itcast.domain.Factory;
import cn.itcast.service.ContractProductService;
import cn.itcast.service.FactoryService;
import cn.itcast.utils.Page;
import cn.itcast.web.action.BaseAction;
@Namespace(value="/cargo")
public class ContractProductAction extends BaseAction implements ModelDriven<ContractProduct>{
	private static final long serialVersionUID = 3169080203996142060L;
	
	private ContractProduct model = new ContractProduct();
	@Override
	public ContractProduct getModel() {
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
	private ContractProductService contractProductService ;
	@Autowired
	private FactoryService factoryService; 
	
	/**
	 * 进入新增页面
	 */
	@Action(value="contractProductAction_tocreate",results={
			@Result(name="tocreate",location="/WEB-INF/pages/cargo/contract/jContractProductCreate.jsp")
	})
	public String tocreate(){
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
		
		
		/**
		 * 分页查寻合同下的所有货物
		 */
		Specification<ContractProduct> spec1 = new Specification<ContractProduct>() {
			@Override
			public Predicate toPredicate(Root<ContractProduct> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Join<ContractProduct, Contract> join = root.join("contract");
				
				return cb.equal(join.get("id").as(String.class),model.getContract().getId());	
			}
		};
		//分页查询
		org.springframework.data.domain.Page<ContractProduct> page2 = contractProductService.findPage(spec1, new PageRequest(page.getPageNo()-1, page.getPageSize()));
		super.copyPage(page, page2, "contractProductAction_tocreate");
		super.push(page);
		return "tocreate";
	}
	/**
	 * 保存
	 */
	@Action(value="contractProductAction_insert",results=
			{@Result(name="success",type="redirect",location="contractProductAction_tocreate",params={"contract.id","${contract.id}"})})
	public String insert(){
		contractProductService.saveOrUpdate(model);
		
		return SUCCESS;
	}
	/**
	 * 跳转到修改的页面
	 */
	@Action(value="contractProductAction_toupdate",results=@Result(name="toupdate",location="/WEB-INF/pages/cargo/contract/jContractProductUpdate.jsp"))
	public String toupdate() throws Exception {
		// model会封装数据
		ContractProduct contractProduct = contractProductService.get(model.getId());
		super.push(contractProduct);
		
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
	@Action(value="contractProductAction_update",results=@Result(name="success",type="redirect",location="contractProductAction_tocreate",params={"contract.id","${contract.id}"}))
	public String update()  {
		// 通过id查询
		ContractProduct contractProduct = contractProductService.get(model.getId());
		// 设置
		contractProduct.setFactory(model.getFactory());
		contractProduct.setFactoryName(model.getFactoryName());
		contractProduct.setProductNo(model.getProductNo());
		contractProduct.setProductImage(model.getProductImage());
		contractProduct.setCnumber(model.getCnumber());
		contractProduct.setAmount(model.getAmount());
		contractProduct.setPackingUnit(model.getPackingUnit());
		contractProduct.setLoadingRate(model.getLoadingRate());
		contractProduct.setBoxNum(model.getBoxNum());
		contractProduct.setPrice(model.getPrice());
		contractProduct.setOrderNo(model.getOrderNo());
		contractProduct.setProductDesc(model.getProductDesc());
		contractProduct.setProductRequest(model.getProductRequest());
		
		// 更新货物
		contractProductService.saveOrUpdate(contractProduct);
		
		return SUCCESS;
	}
	
	/**
	 * 删除
	 * @return
	 * @throws Exception
	 */
	@Action(value="contractProductAction_delete",results=@Result(name="success",type="redirect",location="contractProductAction_tocreate",params={"contract.id","${contract.id}"}))
	public String delete()  {
		contractProductService.deleteById(model.getId());
		return SUCCESS;
	}
	
}
