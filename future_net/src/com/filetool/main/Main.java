package com.filetool.main;

import java.io.IOException;
import java.lang.reflect.Field;

import com.filetool.util.FileUtil;
import com.filetool.util.LogUtil;
import com.routesearch.route.Route;

/**
 * 工具入口
 * 
 * @author echochen
 * @since 2016-3-4
 * @version v1.0
 */
public class Main {
	// link .ddl of glpk
	static {
		try {
			addDir("link");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("please input args: graphFilePath,conditionFilePath, resultFilePath");
			return;
		}

		String graphFilePath = args[0];
		String conditionFilePath = args[1];
		String resultFilePath = args[2];

		// String graphFilePath = "test/case1/topo.csv";
		// String conditionFilePath = "test/case1/demand.csv";
		// String resultFilePath = "test/case1/result.csv";

		// LogUtil.printLog("Begin");

		// 读取输入文件
		String graphContent = FileUtil.read(graphFilePath, null);
		String conditionContent = FileUtil.read(conditionFilePath, null);

		// 功能实现入口
		String resultStr = Route.searchRoute(graphContent, conditionContent, resultFilePath);

		// 写入输出文件
		FileUtil.write(resultFilePath, resultStr, false);

		// LogUtil.printLog("End");
	}

	public static void addDir(String s) throws IOException {
		try {
			Field field = ClassLoader.class.getDeclaredField("usr_paths");
			field.setAccessible(true);
			String[] paths = (String[]) field.get(null);

			for (int i = 0; i < paths.length; i++) {
				if (s.equals(paths[i])) {
					return;
				}
			}
			String[] tmp = new String[paths.length + 1];
			System.arraycopy(paths, 0, tmp, 0, paths.length);
			tmp[paths.length] = s;
			field.set(null, tmp);
		} catch (IllegalAccessException e) {
			throw new IOException("Failed to get permissions to set library path");
		} catch (NoSuchFieldException e) {
			throw new IOException("Failed to get field handle to set library path");
		}
	}
}
