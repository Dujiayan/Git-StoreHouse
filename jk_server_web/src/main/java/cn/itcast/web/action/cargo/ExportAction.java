package cn.itcast.web.action.cargo;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.domain.Contract;
import cn.itcast.domain.Export;
import cn.itcast.domain.ExportProduct;
import cn.itcast.service.ContractService;
import cn.itcast.service.ExportProductService;
import cn.itcast.service.ExportService;
import cn.itcast.utils.FastJsonUtils;
import cn.itcast.utils.Page;
import cn.itcast.vo.ExportProductVo;
import cn.itcast.vo.ExportResult;
import cn.itcast.vo.ExportVo;
import cn.itcast.web.action.BaseAction;

/**
 * 报运单模块 
 */
public class ExportAction extends BaseAction implements ModelDriven<Export>{
	
	private static final long serialVersionUID = 3169080203996142060L;
	
	private Export model = new Export();

	@Override
	public Export getModel() {
		
		return model;
	}
	@Autowired
	private ExportService exportService;
	
	@Autowired
	private ContractService contractService;
	
	@Autowired
	private ExportProductService exportProductService;
	
	private Page page = new Page();

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
	
	/**
	 * 查询已经提交的购销合同
	 */
	@Action(value="exportAction_contractList",results={
			@Result(name="contractList",location="/WEB-INF/pages/cargo/export/jContractList.jsp")
	})
	public String contractList() throws Exception{
		//拼接查询条件
		Specification<Contract> spec = new Specification<Contract>() {
			

			@Override
			public Predicate toPredicate(Root<Contract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("state").as(Integer.class), 1);
			}
		};
		
		org.springframework.data.domain.Page<Contract> page2 = contractService.findPage(spec,new PageRequest(page.getPageNo()-1, page.getPageSize()));
		super.copyPage(page, page2, "exportAction_contractList");
		super.push(page);
		return "contractList";
	}
	/**
	 * 跳转到新增页面的方法
	 */
	@Action(value="exportAction_tocreate",results={
				@Result(name="tocreate",location="/WEB-INF/pages/cargo/export/jExportCreate.jsp")
		})
	public String tocreate() throws Exception{
		return "tocreate";
	}
	/**
	 * 保存
	 */
	@Action(value="exportAction_insert",results=@Result(name="success",type="redirect",location="exportAction_list"))
	public String insert ()throws Exception{
		exportService.saveOrUpdate(model);
		return SUCCESS;
	}
	
	
	
	/**
	 * 细粒度权限管理的分页
	 */
	@Action(value="exportAction_list",results=@Result(name="list",location="/WEB-INF/pages/cargo/export/jExportList.jsp"))
	public String list() throws Exception {
		org.springframework.data.domain.Page<Export> page2 = exportService.findPage(null, new PageRequest(page.getPageNo()-1, page.getPageSize()));
		super.copyPage(page, page2, "exportAction_list");
		super.push(page);
		return "list";
	}
	
	
	/**
	 * 查看详细
	 */
	
	@Action(value="exportAction_toview",results=@Result(name="toview",location="/WEB-INF/pages/cargo/export/jExportView.jsp"))
	public String toview() throws Exception {
		Export export = exportService.get(model.getId());
		super.push(export);
		return "toview";
	}
	
	/**
	 * 跳转到修改页面内
	 */
	@Action(value="exportAction_toupdate",results=@Result(name="toupdate",location="/WEB-INF/pages/cargo/export/jExportUpdate.jsp"))
	public String toupdate() throws Exception {
		// 通过id查询
		Export export = exportService.get(model.getId());
		super.push(export);
		return "toupdate";
	}
	
	
	/**
	 * 异步生成商品数据
	 */
	@Action(value="exportAction_getExportProducts")
	public void getExportProducts() throws Exception {
		// 通过id查询到报运单的对象数据
		Export export = exportService.get(model.getId());
		// 查询报运单下的商品的数据
		Set<ExportProduct> products = export.getExportProducts();
		//直接使用工具类转json
		FastJsonUtils.write_json(ServletActionContext.getResponse(), products);
	}
	
	
	/**
	 * 修改
	 */
	
	// 接收商品的id
		private String[] mr_id;
		private Double[] mr_grossWeight;
		private Double[] mr_netWeight;
		private Double[] mr_sizeLength;
		private Double[] mr_sizeWidth;
		private Double[] mr_sizeHeight;
		private Double[] mr_exPrice;
		private Double[] mr_tax;

		public String[] getMr_id() {
			return mr_id;
		}
		public void setMr_id(String[] mr_id) {
			this.mr_id = mr_id;
		}
		public Double[] getMr_grossWeight() {
			return mr_grossWeight;
		}
		public void setMr_grossWeight(Double[] mr_grossWeight) {
			this.mr_grossWeight = mr_grossWeight;
		}
		public Double[] getMr_netWeight() {
			return mr_netWeight;
		}
		public void setMr_netWeight(Double[] mr_netWeight) {
			this.mr_netWeight = mr_netWeight;
		}
		public Double[] getMr_sizeLength() {
			return mr_sizeLength;
		}
		public void setMr_sizeLength(Double[] mr_sizeLength) {
			this.mr_sizeLength = mr_sizeLength;
		}
		public Double[] getMr_sizeWidth() {
			return mr_sizeWidth;
		}
		public void setMr_sizeWidth(Double[] mr_sizeWidth) {
			this.mr_sizeWidth = mr_sizeWidth;
		}
		public Double[] getMr_sizeHeight() {
			return mr_sizeHeight;
		}
		public void setMr_sizeHeight(Double[] mr_sizeHeight) {
			this.mr_sizeHeight = mr_sizeHeight;
		}
		public Double[] getMr_exPrice() {
			return mr_exPrice;
		}
		public void setMr_exPrice(Double[] mr_exPrice) {
			this.mr_exPrice = mr_exPrice;
		}
		public Double[] getMr_tax() {
			return mr_tax;
		}
		public void setMr_tax(Double[] mr_tax) {
			this.mr_tax = mr_tax;
		}
		
	@Action(value="exportAction_update",results=@Result(name="success",type="redirect",location="exportAction_list"))
	public String update() throws Exception {
		Export export = exportService.get(model.getId());
		export.setInputDate(model.getInputDate());
		export.setLcno(model.getLcno());
		export.setConsignee(model.getConsignee());
		export.setShipmentPort(model.getShipmentPort());
		export.setDestinationPort(model.getDestinationPort());
		export.setTransportMode(model.getTransportMode());
		export.setPriceCondition(model.getPriceCondition());
		export.setMarks(model.getMarks());
		export.setRemark(model.getRemark());
		
		//创建新的set集合
		Set<ExportProduct> 	eps = new HashSet<ExportProduct>();
		//使用数组接收毛重,净重等数据
		//把商品数据设置进去
		for (int i = 0; i < mr_id.length;i++) {
			//通过mr_id获得每一个商品主键
			String epId = mr_id[i];
			//获得商品对象
			ExportProduct ep = exportProductService.get(epId);
			//设置商品属性
			ep.setGrossWeight(mr_grossWeight[i]);
			ep.setNetWeight(mr_netWeight[i]);
			ep.setSizeLength(mr_sizeLength[i]);
			ep.setSizeWidth(mr_sizeWidth[i]);
			ep.setSizeHeight(mr_sizeHeight[i]);
			ep.setExPrice(mr_exPrice[i]);
			ep.setTax(mr_tax[i]);
			eps.add(ep);
		}
		export.setExportProducts(eps);
		return SUCCESS;
	}
	
	
	
	
	/**
	 * 删除
	 */
	
	@Action(value="exportAction_delete",results=@Result(name="success",type="redirect",location="exportAction_list"))
	public String delete() throws Exception {
		exportService.deleteById(model.getId());
		return SUCCESS;
	}
	
	
	
	/**
	 * 提交功能
	 */
	
	@Action(value="exportAction_submit",results=@Result(name="success",type="redirect",location="exportAction_list"))
	public String submit() throws Exception {
		Export export = exportService.get(model.getId());
		export.setState(1);
		exportService.saveOrUpdate(export);
		return SUCCESS;
	}
	
	
	/**
	 * 取消功能
	 */
	
	@Action(value="exportAction_cancel",results=@Result(name="success",type="redirect",location="exportAction_list"))
	public String cancel() throws Exception {
		Export export = exportService.get(model.getId());
		export.setState(0);
		exportService.saveOrUpdate(export);
		return SUCCESS;
	}
	/**
	 * 电子报运功能
	 * exportAction_exportE
	 */         
	@Action(value="exportAction_exportE",results={
			@Result(name="success",type="redirect",location="exportAction_list")
	})
	public String exportE() throws Exception {
		//通过id查询报运单
		Export export = exportService.get(model.getId());
		//把报运单数据封装到ExportVo对象中
		ExportVo exportVo = new ExportVo();
		//拷贝数据
		BeanUtils.copyProperties(export, exportVo);
		//没有的属性自己设置
		exportVo.setExportId(export.getId());
		//再准备商品的数据
		Set<ExportProduct> exportProducts = export.getExportProducts();
		//遍历报运单下所有的商品
		for (ExportProduct ep : exportProducts) {
			//把ExportProduct数据封装到ExportProductVo对象中
			ExportProductVo epvo = new ExportProductVo();
			//拷贝数据
			BeanUtils.copyProperties(ep, epvo);
			//没有的数据自己设置
			epvo.setEid(ep.getId());
			//商品的id值
			epvo.setExportProductId(ep.getId());
			//设置报运单的id
			epvo.setExportId(model.getId());
			epvo.setFactoryId(ep.getFactory().getId());
			//把epvo存入ExportVo对象的products集合中
			exportVo.getProducts().add(epvo);
		}
		
		//把ExportVo对象发送给海关平台
		WebClient.create("http://localhost:8080/jk_export/ws/export/user").type(MediaType.APPLICATION_JSON_TYPE).post(exportVo);
		
		
		//修改状态 ,待审批
		export.setState(2);
		//更新报运单
		exportService.saveOrUpdate(export);
		return SUCCESS;
	}
	/**
	 * 获取报运结果方法
	 */
	@Action(value="exportAction_getResult", results = {
				@Result(name = "success",type="redirect", location = "exportAction_list") 
		})
	public String getResult() throws Exception {
		ExportResult result = WebClient.create("http://localhost:8080/jk_export/ws/export/user/" + model.getId()).accept(MediaType.APPLICATION_JSON).get(ExportResult.class);
		//更新状态和税款
		exportService.updateByWs(result);
		return SUCCESS;
	}
	
	
}

