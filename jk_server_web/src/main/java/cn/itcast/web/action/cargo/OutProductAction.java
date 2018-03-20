package cn.itcast.web.action.cargo;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.domain.ContractProduct;
import cn.itcast.service.ContractProductService;
import cn.itcast.utils.DownloadUtil;
import cn.itcast.utils.UtilFuns;
import cn.itcast.web.action.BaseAction;
/**
 * 出货表模块
 */
@Namespace("/cargo")
public class OutProductAction extends BaseAction{
	
	private static final long serialVersionUID = -6027192765170285343L;
	
	// 选择的船期
	private String inputDate;
	public String getInputDate() {
		return inputDate;
	}
	public void setInputDate(String inputDate) {
		this.inputDate = inputDate;
	}
	
	@Autowired
	private ContractProductService contractProductService;
	
	/**
	 * 跳转到出货表的页面
	 * @return
	 * @throws Exception
	 */
	@Action(value="outProductAction_toedit",results=@Result(name="toedit",location="/WEB-INF/pages/cargo/outproduct/jOutProduct.jsp"))
	public String toedit() throws Exception {
		return "toedit";
	}
	
	
	/**
	 * 
	 * 使用07版本的，使用XSSFWoorkbook对象，不去读取模板，手动创建的方式。
	 * 
	 * @throws Exception
	 */
	@Action(value="outProductAction_print")
	public void print() throws Exception {
		// ==================准备工作========================
		// 创建工作簿
		// Workbook wb = new HSSFWorkbook();
		
		// 使用07版本
		Workbook wb = new SXSSFWorkbook();
		
		// 创建工作表对象
		Sheet sheet = wb.createSheet();
		
		// 设置列宽
		sheet.setColumnWidth(0, 10*256);
		sheet.setColumnWidth(1, 30*256);
		sheet.setColumnWidth(2, 15*256);
		sheet.setColumnWidth(3, 30*256);
		sheet.setColumnWidth(4, 15*256);
		sheet.setColumnWidth(5, 15*256);
		sheet.setColumnWidth(6, 15*256);
		sheet.setColumnWidth(7, 15*256);
		sheet.setColumnWidth(8, 15*256);
		
		// 定义变量
		Row row = null;
		Cell cell = null;
		int rowNo = 0;
		// 默认从第二列开始的
		int cellNo = 1;
		
		// ==================处理大标题========================
		// 创建第一行对象
		row = sheet.createRow(rowNo++);
		// 设置行高
		row.setHeightInPoints(36f);
		// 创建1B单元格
		cell = row.createCell(cellNo);
		
		// 合并单元格
		// sheet.addMergedRegion(new CellRangeAddress(开始行，结束行，开始列，结束列));
		sheet.addMergedRegion(new CellRangeAddress(0,0,1,8));
		// 获取到大标题的样式
		// 设置样式
		cell.setCellStyle(bigTitle(wb));
		// 定义内容	2015-11月份出货表
		String content = inputDate.replace("-0", "-").replace("-", "年")+"月份出货表";
		// 设置内容
		cell.setCellValue(content);
		
		// ==================处理小标题========================
		// 创建第二行对象
		row = sheet.createRow(rowNo++);
		// 设置行高
		row.setHeightInPoints(27f);
		
		// 使用循环的方式
		String [] titles = {"客户","订单号","货号","数量","工厂","工厂交期","船期","贸易条款"};
		// 遍历数组
		for (String title : titles) {
			// 循环第一次，创建2B单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(title(wb));
			// 设置内容
			cell.setCellValue(title);
		}
		
		// ==================处理数据========================
		// HQL：from ContractProduct where contract.id = ?	合同下所有的货物
		// HQL：from ContractProduct where to_char(contract.shipTime,'yyyy-MM') = ?	合同下所有的货物
		// 查询数据
		List<ContractProduct> list = contractProductService.findByShiptime(inputDate);
		
		// 模拟循环
		for(int i=0;i<100000;i++){
			// 遍历list集合
			for (ContractProduct cp : list) {
				// 先把cellNo重置成1
				cellNo = 1;
				// 创建第三行对象
				row = sheet.createRow(rowNo++);
				row.setHeightInPoints(24f);
				// 创建单元格
				cell = row.createCell(cellNo++);
				// 设置样式
				cell.setCellStyle(text(wb));
				// 设置内容
				cell.setCellValue(cp.getContract().getCustomName());
				
				// 处理下一个单元格
				cell = row.createCell(cellNo++);
				// 设置样式
				cell.setCellStyle(text(wb));
				// 设置内容
				cell.setCellValue(cp.getContract().getContractNo());
				
				// 处理下一个单元格
				cell = row.createCell(cellNo++);
				// 设置样式
				cell.setCellStyle(text(wb));
				// 设置内容
				cell.setCellValue(cp.getProductNo());
				
				// 处理下一个单元格
				cell = row.createCell(cellNo++);
				// 设置样式
				cell.setCellStyle(text(wb));
				// 设置内容
				cell.setCellValue(cp.getCnumber());
				
				// 处理下一个单元格
				cell = row.createCell(cellNo++);
				// 设置样式
				cell.setCellStyle(text(wb));
				// 设置内容
				cell.setCellValue(cp.getFactoryName());
				
				// 处理下一个单元格
				cell = row.createCell(cellNo++);
				// 设置样式
				cell.setCellStyle(text(wb));
				// 设置内容
				cell.setCellValue(UtilFuns.dateTimeFormat(cp.getContract().getDeliveryPeriod()));
				
				// 处理下一个单元格
				cell = row.createCell(cellNo++);
				// 设置样式
				cell.setCellStyle(text(wb));
				// 设置内容
				cell.setCellValue(UtilFuns.dateTimeFormat(cp.getContract().getShipTime()));
				
				// 处理下一个单元格
				cell = row.createCell(cellNo++);
				// 设置样式
				cell.setCellStyle(text(wb));
				// 设置内容
				cell.setCellValue(cp.getContract().getTradeTerms());
			}
		}
		
		// ==================提供文件下载========================
		// 文件下载：2个头，一个流，使用response写出
		DownloadUtil downloadUtil = new DownloadUtil();
		HttpServletResponse response = ServletActionContext.getResponse();
		// 设置响应头
		// ServletOutputStream os = response.getOutputStream();
		// wb.write(os);
		// os.flush();
		
		// 把wb写入到byteArrayOutputStream流中
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		wb.write(byteArrayOutputStream);
		// 文件下载
		downloadUtil.download(byteArrayOutputStream, response, content+".xlsx");
	}
	
	
	
	/**
	 * 
	 * 使用的07版本的Excel，读取模板的方式
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action(value="outProductAction_print4")
	public void print4() throws Exception {
		// ==================准备工作========================
		// 获取到文件的路径，读取的是07版本的实现类
		String path = ServletActionContext.getServletContext().getRealPath("/make/xlsprint/tOUTPRODUCT.xlsx");
		// 提供文件的输入流
		InputStream in = new FileInputStream(path);
		// 读取工作簿
		// Workbook wb = new HSSFWorkbook(in);
		
		// 创建07版本的实现类
		Workbook wb = new XSSFWorkbook(in);
		
		// 获取工作表对象
		Sheet sheet = wb.getSheetAt(0);
		
		// 定义变量
		Row row = null;
		Cell cell = null;
		int rowNo = 0;
		// 默认从第二列开始的
		int cellNo = 1;
		
		// ==================处理大标题========================
		// 获取第一个对象
		row = sheet.getRow(rowNo++);
		// 获取第二个单元格
		cell = row.getCell(cellNo);
		// 定义内容	2015-11月份出货表
		String content = inputDate.replace("-0", "-").replace("-", "年")+"月份出货表";
		// 设置内容
		cell.setCellValue(content);
		
		// ==================处理小标题========================
		// 跳过第二行
		rowNo++;
		
		// ==================处理数据========================
		// 先获取到第三行对象，获取到每一个单元格的对象，获取到样式
		row = sheet.getRow(rowNo);
		// 获取到3B单元格
		cell = row.getCell(cellNo++);
		// 获取样式
		CellStyle style3B = cell.getCellStyle();
		
		cell = row.getCell(cellNo++);
		// 获取样式
		CellStyle style3C = cell.getCellStyle();

		cell = row.getCell(cellNo++);
		// 获取样式
		CellStyle style3D = cell.getCellStyle();
		
		cell = row.getCell(cellNo++);
		// 获取样式
		CellStyle style3E = cell.getCellStyle();
		
		cell = row.getCell(cellNo++);
		// 获取样式
		CellStyle style3F = cell.getCellStyle();
		
		cell = row.getCell(cellNo++);
		// 获取样式
		CellStyle style3G = cell.getCellStyle();
		
		cell = row.getCell(cellNo++);
		// 获取样式
		CellStyle style3H = cell.getCellStyle();
		
		cell = row.getCell(cellNo++);
		// 获取样式
		CellStyle style3I = cell.getCellStyle();
				
		// 查询数据
		List<ContractProduct> list = contractProductService.findByShiptime(inputDate);
		// 遍历list集合
		for (ContractProduct cp : list) {
			// 先把cellNo重置成1
			cellNo = 1;
			// 创建第三行对象
			row = sheet.createRow(rowNo++);
			row.setHeightInPoints(24f);
			// 创建单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(style3B);
			// 设置内容
			cell.setCellValue(cp.getContract().getCustomName());
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(style3C);
			// 设置内容
			cell.setCellValue(cp.getContract().getContractNo());
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(style3D);
			// 设置内容
			cell.setCellValue(cp.getProductNo());
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(style3E);
			// 设置内容
			cell.setCellValue(cp.getCnumber());
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(style3F);
			// 设置内容
			cell.setCellValue(cp.getFactoryName());
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(style3G);
			// 设置内容
			cell.setCellValue(UtilFuns.dateTimeFormat(cp.getContract().getDeliveryPeriod()));
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(style3H);
			// 设置内容
			cell.setCellValue(UtilFuns.dateTimeFormat(cp.getContract().getShipTime()));
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(style3I);
			// 设置内容
			cell.setCellValue(cp.getContract().getTradeTerms());
		}
		
		// ==================提供文件下载========================
		// 文件下载：2个头，一个流，使用response写出
		DownloadUtil downloadUtil = new DownloadUtil();
		HttpServletResponse response = ServletActionContext.getResponse();
		// 设置响应头
		// ServletOutputStream os = response.getOutputStream();
		// wb.write(os);
		// os.flush();
		
		// 把wb写入到byteArrayOutputStream流中
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		wb.write(byteArrayOutputStream);
		// 文件下载
		downloadUtil.download(byteArrayOutputStream, response, content+".xlsx");
	}
	
	
	/**
	 * 
	 * 使用的03版本的Excel，读取模板的方式
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action(value="outProductAction_print3")
	public void print3() throws Exception {
		// ==================准备工作========================
		// 获取到文件的路径
		String path = ServletActionContext.getServletContext().getRealPath("/make/xlsprint/tOUTPRODUCT.xls");
		// 提供文件的输入流
		InputStream in = new FileInputStream(path);
		// 读取工作簿
		Workbook wb = new HSSFWorkbook(in);
		// 获取工作表对象
		Sheet sheet = wb.getSheetAt(0);
		
		// 定义变量
		Row row = null;
		Cell cell = null;
		int rowNo = 0;
		// 默认从第二列开始的
		int cellNo = 1;
		
		// ==================处理大标题========================
		// 获取第一个对象
		row = sheet.getRow(rowNo++);
		// 获取第二个单元格
		cell = row.getCell(cellNo);
		// 定义内容	2015-11月份出货表
		String content = inputDate.replace("-0", "-").replace("-", "年")+"月份出货表";
		// 设置内容
		cell.setCellValue(content);
		
		// ==================处理小标题========================
		// 跳过第二行
		rowNo++;
		
		// ==================处理数据========================
		// 先获取到第三行对象，获取到每一个单元格的对象，获取到样式
		row = sheet.getRow(rowNo);
		// 获取到3B单元格
		cell = row.getCell(cellNo++);
		// 获取样式
		CellStyle style3B = cell.getCellStyle();
		
		cell = row.getCell(cellNo++);
		// 获取样式
		CellStyle style3C = cell.getCellStyle();

		cell = row.getCell(cellNo++);
		// 获取样式
		CellStyle style3D = cell.getCellStyle();
		
		cell = row.getCell(cellNo++);
		// 获取样式
		CellStyle style3E = cell.getCellStyle();
		
		cell = row.getCell(cellNo++);
		// 获取样式
		CellStyle style3F = cell.getCellStyle();
		
		cell = row.getCell(cellNo++);
		// 获取样式
		CellStyle style3G = cell.getCellStyle();
		
		cell = row.getCell(cellNo++);
		// 获取样式
		CellStyle style3H = cell.getCellStyle();
		
		cell = row.getCell(cellNo++);
		// 获取样式
		CellStyle style3I = cell.getCellStyle();
				
		// 查询数据
		List<ContractProduct> list = contractProductService.findByShiptime(inputDate);
		// 遍历list集合
		for (ContractProduct cp : list) {
			// 先把cellNo重置成1
			cellNo = 1;
			// 创建第三行对象
			row = sheet.createRow(rowNo++);
			row.setHeightInPoints(24f);
			// 创建单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(style3B);
			// 设置内容
			cell.setCellValue(cp.getContract().getCustomName());
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(style3C);
			// 设置内容
			cell.setCellValue(cp.getContract().getContractNo());
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(style3D);
			// 设置内容
			cell.setCellValue(cp.getProductNo());
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(style3E);
			// 设置内容
			cell.setCellValue(cp.getCnumber());
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(style3F);
			// 设置内容
			cell.setCellValue(cp.getFactoryName());
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(style3G);
			// 设置内容
			cell.setCellValue(UtilFuns.dateTimeFormat(cp.getContract().getDeliveryPeriod()));
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(style3H);
			// 设置内容
			cell.setCellValue(UtilFuns.dateTimeFormat(cp.getContract().getShipTime()));
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(style3I);
			// 设置内容
			cell.setCellValue(cp.getContract().getTradeTerms());
		}
		
		// ==================提供文件下载========================
		// 文件下载：2个头，一个流，使用response写出
		DownloadUtil downloadUtil = new DownloadUtil();
		HttpServletResponse response = ServletActionContext.getResponse();
		// 设置响应头
		// ServletOutputStream os = response.getOutputStream();
		// wb.write(os);
		// os.flush();
		
		// 把wb写入到byteArrayOutputStream流中
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		wb.write(byteArrayOutputStream);
		// 文件下载
		downloadUtil.download(byteArrayOutputStream, response, content+".xls");
	}
	
	
	/**
	 * 打印，提供文件下载
	 * 
	 * 使用的03版本的Excel，手动创建样式的方式
	 * 
	 * @return
	 * @throws Exception
	 */
	@Action(value="outProductAction_print2")
	public void print2() throws Exception {
		// ==================准备工作========================
		// 创建工作簿
		Workbook wb = new HSSFWorkbook();
		// 创建工作表对象
		Sheet sheet = wb.createSheet();
		
		// 设置列宽
		sheet.setColumnWidth(0, 10*256);
		sheet.setColumnWidth(1, 30*256);
		sheet.setColumnWidth(2, 15*256);
		sheet.setColumnWidth(3, 30*256);
		sheet.setColumnWidth(4, 15*256);
		sheet.setColumnWidth(5, 15*256);
		sheet.setColumnWidth(6, 15*256);
		sheet.setColumnWidth(7, 15*256);
		sheet.setColumnWidth(8, 15*256);
		
		// 定义变量
		Row row = null;
		Cell cell = null;
		int rowNo = 0;
		// 默认从第二列开始的
		int cellNo = 1;
		
		// ==================处理大标题========================
		// 创建第一行对象
		row = sheet.createRow(rowNo++);
		// 设置行高
		row.setHeightInPoints(36f);
		// 创建1B单元格
		cell = row.createCell(cellNo);
		
		// 合并单元格
		// sheet.addMergedRegion(new CellRangeAddress(开始行，结束行，开始列，结束列));
		sheet.addMergedRegion(new CellRangeAddress(0,0,1,8));
		// 获取到大标题的样式
		// 设置样式
		cell.setCellStyle(bigTitle(wb));
		// 定义内容	2015-11月份出货表
		String content = inputDate.replace("-0", "-").replace("-", "年")+"月份出货表";
		// 设置内容
		cell.setCellValue(content);
		
		// ==================处理小标题========================
		// 创建第二行对象
		row = sheet.createRow(rowNo++);
		// 设置行高
		row.setHeightInPoints(27f);
		
		// 使用循环的方式
		String [] titles = {"客户","订单号","货号","数量","工厂","工厂交期","船期","贸易条款"};
		// 遍历数组
		for (String title : titles) {
			// 循环第一次，创建2B单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(title(wb));
			// 设置内容
			cell.setCellValue(title);
		}
		
		// ==================处理数据========================
		// HQL：from ContractProduct where contract.id = ?	合同下所有的货物
		// HQL：from ContractProduct where to_char(contract.shipTime,'yyyy-MM') = ?	合同下所有的货物
		
		// 查询数据
		List<ContractProduct> list = contractProductService.findByShiptime(inputDate);
		// 遍历list集合
		for (ContractProduct cp : list) {
			// 先把cellNo重置成1
			cellNo = 1;
			// 创建第三行对象
			row = sheet.createRow(rowNo++);
			row.setHeightInPoints(24f);
			// 创建单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(text(wb));
			// 设置内容
			cell.setCellValue(cp.getContract().getCustomName());
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(text(wb));
			// 设置内容
			cell.setCellValue(cp.getContract().getContractNo());
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(text(wb));
			// 设置内容
			cell.setCellValue(cp.getProductNo());
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(text(wb));
			// 设置内容
			cell.setCellValue(cp.getCnumber());
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(text(wb));
			// 设置内容
			cell.setCellValue(cp.getFactoryName());
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(text(wb));
			// 设置内容
			cell.setCellValue(UtilFuns.dateTimeFormat(cp.getContract().getDeliveryPeriod()));
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(text(wb));
			// 设置内容
			cell.setCellValue(UtilFuns.dateTimeFormat(cp.getContract().getShipTime()));
			
			// 处理下一个单元格
			cell = row.createCell(cellNo++);
			// 设置样式
			cell.setCellStyle(text(wb));
			// 设置内容
			cell.setCellValue(cp.getContract().getTradeTerms());
		}
		
		// ==================提供文件下载========================
		// 文件下载：2个头，一个流，使用response写出
		DownloadUtil downloadUtil = new DownloadUtil();
		HttpServletResponse response = ServletActionContext.getResponse();
		// 设置响应头
		// ServletOutputStream os = response.getOutputStream();
		// wb.write(os);
		// os.flush();
		
		// 把wb写入到byteArrayOutputStream流中
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		wb.write(byteArrayOutputStream);
		// 文件下载
		downloadUtil.download(byteArrayOutputStream, response, content+".xls");
	}
	
	/**
	 * 大标题的样式
	 * @param wb
	 * @return
	 */
	public CellStyle bigTitle(Workbook wb){
		// 创建样式对象
		CellStyle style = wb.createCellStyle();
		// 创建字体对象
		Font font = wb.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short)16);
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);					//字体加粗
		// 设置字体
		style.setFont(font);
		
		style.setAlignment(CellStyle.ALIGN_CENTER);					//横向居中
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);		//纵向居中
		
		return style;
	}
	
	/**
	 * 小标题的样式
	 * @param wb
	 * @return
	 */
	public CellStyle title(Workbook wb){
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("黑体");
		font.setFontHeightInPoints((short)12);
		
		style.setFont(font);
		
		style.setAlignment(CellStyle.ALIGN_CENTER);					//横向居中
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);		//纵向居中
		
		style.setBorderTop(CellStyle.BORDER_THIN);					//上细线
		style.setBorderBottom(CellStyle.BORDER_THIN);				//下细线
		style.setBorderLeft(CellStyle.BORDER_THIN);					//左细线
		style.setBorderRight(CellStyle.BORDER_THIN);				//右细线
		
		return style;
	}

	/**
	 * 数据的样式
	 * @param wb
	 * @return
	 */
	public CellStyle text(Workbook wb){
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("Times New Roman");
		font.setFontHeightInPoints((short)10);
		
		style.setFont(font);
		
		style.setAlignment(CellStyle.ALIGN_LEFT);					//横向居左
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);		//纵向居中
		
		style.setBorderTop(CellStyle.BORDER_THIN);					//上细线
		style.setBorderBottom(CellStyle.BORDER_THIN);				//下细线
		style.setBorderLeft(CellStyle.BORDER_THIN);					//左细线
		style.setBorderRight(CellStyle.BORDER_THIN);				//右细线
		
		return style;
	}		
	
}
