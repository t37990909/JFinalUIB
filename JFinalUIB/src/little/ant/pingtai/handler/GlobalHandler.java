package little.ant.pingtai.handler;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import little.ant.pingtai.beetl.MyBeetlRender;
import little.ant.pingtai.common.ContextBase;
import little.ant.pingtai.model.Syslog;
import little.ant.pingtai.thread.ThreadSysLog;
import little.ant.pingtai.tools.ToolUtils;
import little.ant.pingtai.tools.ToolWeb;

import org.apache.log4j.Logger;

import com.jfinal.handler.Handler;

public class GlobalHandler extends Handler {
	
	private static Logger log = Logger.getLogger(GlobalHandler.class);
	
	public static String reqSysLogKey = "reqSysLog";
	
	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		log.info("初始化访问系统功能日志");
		Syslog reqSysLog = getSysLog(request);
		long starttime = new Date().getTime();
		reqSysLog.set("startdate", new java.sql.Timestamp(starttime));//开始时间
		request.setAttribute(reqSysLogKey, reqSysLog);
		
		log.info("设置 web 路径");
		String cxt = ContextBase.getContextAllPath(request);
		request.setAttribute("cxt", cxt);

		log.info("生成 request id");
		request.setAttribute("requesId", ToolUtils.getUuidByJdk(false));
		
		log.debug("beetl cookie处理");
		Map<String, Cookie> cookieMap = ToolWeb.readCookieMap(request);
		request.setAttribute("cookieMap", cookieMap);

		log.debug("beetl cookie处理");
		request.setAttribute("paramMap", ToolWeb.getParamMap(request));

		log.info("设置Header");
		request.setAttribute("decorator", "none");
		response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
		
		nextHandler.handle(target, request, response, isHandled);
		
		log.info("请求处理完毕，计算耗时");
		
		// 结束时间
		long endtime = new Date().getTime();
		reqSysLog.set("enddate", new java.sql.Timestamp(endtime));
		
		// 总耗时
		Long haoshi = endtime - starttime;
		reqSysLog.set("haoshi", haoshi);
		
		// 视图耗时
		long renderTime = 0;
		if(null != request.getAttribute(MyBeetlRender.renderTimeKey)){
			renderTime = (long) request.getAttribute(MyBeetlRender.renderTimeKey);
		}
		reqSysLog.set("viewhaoshi", renderTime);
		
		// action耗时
		reqSysLog.set("actionhaoshi", haoshi - renderTime);
		
		log.info("日志添加到入库队列");
		ThreadSysLog.add(reqSysLog);
	}
	
	/**
	 * 创建日志对象,并初始化一些属性值
	 * @param request
	 * @return
	 */
	public Syslog getSysLog(HttpServletRequest request){
		String requestPath = ToolWeb.getRequestURIWithParam(request); 
		String ip = ToolWeb.getIpAddr(request);
		String referer = request.getHeader("Referer"); 
		String userAgent = request.getHeader("User-Agent");
		String cookie = request.getHeader("Cookie");
		String method = request.getMethod();
		String xRequestedWith = request.getHeader("X-Requested-With");
		String host = request.getHeader("Host");
		String acceptLanguage = request.getHeader("Accept-Language");
		String acceptEncoding = request.getHeader("Accept-Encoding");
		String accept = request.getHeader("Accept");
		String connection = request.getHeader("Connection");

		Syslog reqSysLog = new Syslog();
		
		reqSysLog.set("ips", ip);
		reqSysLog.set("requestpath", requestPath);
		reqSysLog.set("referer", referer);
		reqSysLog.set("useragent", userAgent);
		reqSysLog.set("cookie", cookie);
		reqSysLog.set("method", method);
		reqSysLog.set("xrequestedwith", xRequestedWith);
		reqSysLog.set("host", host);
		reqSysLog.set("acceptlanguage", acceptLanguage);
		reqSysLog.set("acceptencoding", acceptEncoding);
		reqSysLog.set("accept", accept);
		reqSysLog.set("connection", connection);

		return reqSysLog;
	}
	
}
