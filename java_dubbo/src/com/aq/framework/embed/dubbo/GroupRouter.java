package com.aq.framework.embed.dubbo;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.cluster.Router;
import com.alibaba.dubbo.rpc.cluster.router.script.ScriptRouter;

public class GroupRouter implements Router {

	private static final Logger logger = LoggerFactory.getLogger(ScriptRouter.class);

	private final int priority;

	private final URL url;

	private final String rule;
	
	public URL getUrl() {
		return url;
	}

	public GroupRouter(URL url) {
		this.url = url;
		this.priority = url.getParameter(Constants.PRIORITY_KEY, 0);
		this.rule = url.getParameter(Constants.RULE_KEY);
	}

	public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
		Object firstParam = invocation.getArguments()[0];
		
		List<Invoker<T>> _invokers = new ArrayList<Invoker<T>>();
		
		//һ���ж϶Ժ���ķ����ṩ��
		for(Invoker<T> invoker : invokers)
		{
			URL providerUrl = invoker.getUrl();
			String path         = providerUrl.getPath();
			//�ж�provider��Path���������ݵĵ�һ�������Ƿ���ͬ�������ͬ���򷵻ص�ǰ��provider
			if(path.equals(firstParam)){
				_invokers.add(invoker);
			}
		}

		return _invokers;
	}

	public int compareTo(Router o) {
		GroupRouter c = (GroupRouter) o;
		return this.priority == c.priority ? 0 : (this.priority > c.priority ? 1 : -1);
	}

}
