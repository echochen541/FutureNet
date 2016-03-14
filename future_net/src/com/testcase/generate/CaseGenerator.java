package com.testcase.generate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.filetool.util.FileUtil;

public class CaseGenerator {

	public static void generate(int VERTEX_NUM, int MAX_OUT_DEGREE,
			int MAX_WEIGHT, int MAX_CONDITION_NUM, String directory) {
		// System.out.println(FILE_PATH);
		File dir = new File(System.getProperty("user.dir").replaceAll("\\\\",
				"/")
				+ "/test/" + directory);  
		dir.mkdir();
		String FILE_PATH = System.getProperty("user.dir").replaceAll("\\\\",
				"/")
				+ "/test/" + directory + "/";
		
		int edgeIndex = 0, outDegree = 0, weight = 0, dst = 0;
		for (int i = 0; i < VERTEX_NUM; i++) {
			outDegree = (int) (Math.random() * (MAX_OUT_DEGREE + 1));
			for (int j = 0; j < outDegree; j++) {
				weight = (int) (Math.random() * MAX_WEIGHT + 1);
				dst = (int) (Math.random() * VERTEX_NUM + 1);
				if (dst == i) {
					dst++;
				}
				if (i == 0 && j == 0) {
					FileUtil.write(FILE_PATH + "topo.csv", edgeIndex + "," + i
							+ "," + dst + "," + weight + "\n", false);
				} else {
					FileUtil.write(FILE_PATH + "topo.csv", edgeIndex + "," + i
							+ "," + dst + "," + weight + "\n", true);
				}
				edgeIndex++;
			}
		}

		int conditionNum = (int) (Math.random() * MAX_CONDITION_NUM + 1);
		int start = (int) (Math.random() * VERTEX_NUM);
		int end = (int) (Math.random() * VERTEX_NUM);
		String condition = "";
		List<Integer> l = new ArrayList<Integer>();
		int n;
		for (int k = 0; k < conditionNum; k++) {
			if (k == 0) {
				n = (int) (Math.random() * VERTEX_NUM);
				l.add(n);
				condition += n;
			} else {
				do {
					n = (int) (Math.random() * VERTEX_NUM);
				} while (l.contains(n));
				l.add(n);
				condition = condition + "|" + n;
			}
		}
		FileUtil.write(FILE_PATH + "demand.csv", start + "," + end + ","
				+ condition + "\n", false);
	}
}
