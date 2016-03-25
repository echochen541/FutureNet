package com.filetool.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class FileUtil {
	/**
	 * 读取文件并按行输出
	 * 
	 * @param filePath
	 * @param spec
	 *            允许解析的最大行数， spec==null时，解析所有行
	 * @return
	 * @author
	 * @since 2016-3-1
	 */
	public static String read(final String filePath, final Integer spec) {
		File file = new File(filePath);
		// 当文件不存在或者不可读时
		if ((!isFileExists(file)) || (!file.canRead())) {
			System.out.println("file [" + filePath + "] is not exist or cannot read!!!");
			return null;
		}

		BufferedReader br = null;
		FileReader fb = null;
		StringBuffer sb = new StringBuffer();
		try {
			fb = new FileReader(file);
			br = new BufferedReader(fb);

			String str = null;
			int index = 0;
			while (((spec == null) || index++ < spec) && (str = br.readLine()) != null) {
				sb.append(str + "\n");
				// System.out.println(str);

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeQuietly(br);
			closeQuietly(fb);
		}

		return sb.toString();
	}

	/**
	 * 写文件
	 * 
	 * @param filePath
	 *            输出文件路径
	 * @param content
	 *            要写入的内容
	 * @param append
	 *            是否追加
	 * @return
	 * @author s00274007
	 * @since 2016-3-1
	 */
	public static int write(final String filePath, final String content, final boolean append) {
		File file = new File(filePath);
		if (content == null) {
			System.out.println("file [" + filePath + "] invalid!!!");
			return 0;
		}

		// 当文件存在但不可写时
		if (isFileExists(file) && (!file.canRead())) {
			return 0;
		}

		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			if (!isFileExists(file)) {
				file.createNewFile();
			}

			fw = new FileWriter(file, append);
			bw = new BufferedWriter(fw);

			bw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		} finally {
			closeQuietly(bw);
			closeQuietly(fw);
		}

		return 1;
	}

	private static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException e) {
		}
	}

	private static boolean isFileExists(final File file) {
		if (file.exists() && file.isFile()) {
			return true;
		}

		return false;
	}

	/**
	 * -----------------------------------------------------------------------
	 * getAppPath需要一个当前程序使用的Java类的class属性参数，它可以返回打包过的
	 * Java可执行文件（jar，war）所处的系统目录名或非打包Java程序所处的目录
	 * 
	 * @param cls为Class类型
	 * @return 返回值为该类所在的Java程序运行的目录
	 *         -----------------------------------------------------------------
	 *         --------
	 */
	public static String getAppPath(Class cls) {
		// 检查用户传入的参数是否为空
		if (cls == null)
			throw new java.lang.IllegalArgumentException("参数不能为空！");
		ClassLoader loader = cls.getClassLoader();
		// 获得类的全名，包括包名
		String clsName = cls.getName() + ".class";
		// 获得传入参数所在的包
		Package pack = cls.getPackage();
		String path = "";
		// 如果不是匿名包，将包名转化为路径
		if (pack != null) {
			String packName = pack.getName();
			// 此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
			if (packName.startsWith("java.") || packName.startsWith("javax."))
				throw new java.lang.IllegalArgumentException("不要传送系统类！");
			// 在类的名称中，去掉包名的部分，获得类的文件名
			clsName = clsName.substring(packName.length() + 1);
			// 判定包名是否是简单包名，如果是，则直接将包名转换为路径，
			if (packName.indexOf(".") < 0)
				path = packName + "/";
			else {// 否则按照包名的组成部分，将包名转换为路径
				int start = 0, end = 0;
				end = packName.indexOf(".");
				while (end != -1) {
					path = path + packName.substring(start, end) + "/";
					start = end + 1;
					end = packName.indexOf(".", start);
				}
				path = path + packName.substring(start) + "/";
			}
		}
		// 调用ClassLoader的getResource方法，传入包含路径信息的类文件名
		java.net.URL url = loader.getResource(path + clsName);
		// 从URL对象中获取路径信息
		String realPath = url.getPath();
		// 去掉路径信息中的协议名"file:"
		int pos = realPath.indexOf("file:");
		if (pos > -1)
			realPath = realPath.substring(pos + 5);
		// 去掉路径信息最后包含类文件信息的部分，得到类所在的路径
		pos = realPath.indexOf(path + clsName);
		realPath = realPath.substring(0, pos - 1);
		// 如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
		if (realPath.endsWith("!"))
			realPath = realPath.substring(0, realPath.lastIndexOf("/"));
		/*------------------------------------------------------------  
		ClassLoader的getResource方法使用了utf-8对路径信息进行了编码，当路径  
		 中存在中文和空格时，他会对这些字符进行转换，这样，得到的往往不是我们想要  
		 的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的  
		 中文及空格路径  
		-------------------------------------------------------------*/
		try {
			realPath = java.net.URLDecoder.decode(realPath, "utf-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return realPath;
	}// getAppPath定义结束
}
