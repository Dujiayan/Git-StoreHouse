package cn.itcast.web.action.stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.service.SqlService;
import cn.itcast.utils.FastJsonUtils;
import cn.itcast.web.action.BaseAction;
/**
 *统计分析模块 
 */
@Namespace(value="/stat")
public class StatChartAction extends BaseAction{

	private static final long serialVersionUID = -1530041287911740368L;
	
	@Autowired
	private SqlService sqlService;
	
	/**
	 * 生产厂家的销售情况
	 */
	@Action(value="statChartAction_factorysale",results={
			@Result(name="factorysale",location="/WEB-INF/pages/stat/chart/pie.jsp")
	})
	public String factorysale() throws Exception{
		
		return "factorysale";
	}
	/**
	 * 异步提供数据
	 */
	@Action(value="statChartAction_getFactorysale")
	public void getFactorysale() throws Exception{
		// 定义sql语句
				String sql = "select t.factory_name,sum(t.amount) from CONTRACT_PRODUCT_C t group by t.factory_name";
				// 使用service查询
				List<String> list = sqlService.executeSQL(sql);
				// 生成json	var data = [{key:val,key:val},{key:val,key:val}]
				List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
				// 向mapList中存入map的数据
				for(int i=0;i<list.size();i++){
					// 创建map对象
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("factoryName", list.get(i++));
					map.put("value", list.get(i));
					mapList.add(map);
				}
				// 响应
				FastJsonUtils.write_json(ServletActionContext.getResponse(), mapList);
	}
	
	/**
	 * 统计产品销售排行
	 * @return
	 * @throws Exception
	 */
	@Action(value="statChartAction_productsale",results=@Result(name="productsale",location="/WEB-INF/pages/stat/chart/column3D.jsp"))
	public String productsale() throws Exception {
		String sql = "select * from (select t.product_no,sum(t.amount) s from CONTRACT_PRODUCT_C t group by t.product_no order by s desc) where rownum < 5";
		// 使用service查询
		List<String> list = sqlService.executeSQL(sql);
		// 生成json	var data = [{key:val,key:val},{key:val,key:val}]
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		// 定义颜色的数组
		String [] colors = {"#0D8ECF","#2A0CD0","#CD0D74","#FCD202"};
		int j = 0;
		// 向mapList中存入map的数据
		for(int i=0;i<list.size();i++){
			// 创建map对象
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("productNo", list.get(i++));
			map.put("saleAmount", list.get(i));
			map.put("color", colors[j++]);
			mapList.add(map);
		}
		// 把mapList转换成json字符串
		String json = FastJsonUtils.toJSONString(mapList);
		// 压栈
		super.put("jsonData", json);
		return "productsale";
	}
	
	
	
	/**
	 * 统计在线人数
	 * @return
	 * @throws Exception
	 */
	@Action(value="statChartAction_onlineinfo",results=@Result(name="onlineinfo",location="/WEB-INF/pages/stat/chart/linSmooth.jsp"))
	public String onlineinfo() throws Exception {
		String sql = "select a.a1,nvl(b.c,0) from (select * from online_info_t) a left join (select to_char(t.login_time,'HH24') a1,count(*) c from LOGIN_LOG_P t group by to_char(t.login_time,'HH24') order by a1) b on a.a1 = b.a1 order by a.a1 ";
		// 执行SQL语句
		List<String> list = sqlService.executeSQL(sql);
		// 生成json	var data = [{key:val,key:val},{key:val,key:val}]
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		// 向mapList中存入map的数据
		for(int i=0;i<list.size();i++){
			// 创建map对象
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("hour", list.get(i++));
			map.put("value", list.get(i));
			mapList.add(map);
		}
		// 把mapList转换成json字符串
		String json = FastJsonUtils.toJSONString(mapList);
		// 压栈
		super.put("jsonData", json);
		
		return "onlineinfo";
	}
}

//jk_server_web/WEB-INF/pages/stat/chart/pie.jsp