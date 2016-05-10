package com.filetool.main;

import com.filetool.util.FileUtil;
import com.filetool.util.LogUtil;
import com.routesearch.route.AdvancedRoute;
import com.routesearch.route.Route;

/**
 * 工具入口
 * 
 * @author echochen
 * @since 2016-3-4
 * @version v1.0
 */
public class Main {
	public static void main(String[] args) {
		// String graphFilePath = args[0];
		// String conditionFilePath = args[1];
		// String resultFilePath = args[2];

		// String graphFilePath = "test/case2/topo.csv";
		// String conditionFilePath = "test/case2/demand.csv";
		// String resultFilePath = "test/case2/result.csv";

		String graphFilePath = "D:/Eclipse/workspace/future_net/future_net/test/case4/topo.csv";
		String conditionFilePath = "D:/Eclipse/workspace/future_net/future_net/test/case4/demand.csv";
		String resultFilePath = "D:/Eclipse/workspace/future_net/future_net/test/case4/result.csv";

		String graphContent = FileUtil.read(graphFilePath, null);
		String conditionContent = FileUtil.read(conditionFilePath, null);

		LogUtil.printLog("begin");
//		 Route.searchRoute(graphContent, conditionContent, resultFilePath);
		AdvancedRoute.searchRoute(graphContent, conditionContent);
		LogUtil.printLog("end");
	}
}
