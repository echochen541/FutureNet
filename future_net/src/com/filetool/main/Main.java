package com.filetool.main;

import com.filetool.util.FileUtil;
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
//		String graphFilePath = args[0];
//		String conditionFilePath = args[1];
//		String resultFilePath = args[2];

		 String graphFilePath = "test/case9/topo.csv";
		 String conditionFilePath = "test/case9/demand.csv";
		 String resultFilePath = "test/case9/result.csv";

		// 读取输入文件
		String graphContent = FileUtil.read(graphFilePath, null);
		String conditionContent = FileUtil.read(conditionFilePath, null);

		// 功能实现入口
		String resultStr = Route.searchRoute(graphContent, conditionContent, resultFilePath);

		// 写入输出文件
		FileUtil.write(resultFilePath, resultStr, false);
	}
}
