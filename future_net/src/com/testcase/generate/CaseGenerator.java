package com.testcase.generate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.filetool.util.FileUtil;

public class CaseGenerator {

	public static void generate(int numOfvertices, int maxOutDegree,
			int maxWeight, int numOfCondition, String directory) {
		// System.out.println(FILE_PATH);
		File dir = new File(System.getProperty("user.dir").replaceAll("\\\\",
				"/")
				+ "/test/" + directory);  
		dir.mkdir();
		String FILE_PATH = System.getProperty("user.dir").replaceAll("\\\\",
				"/")
				+ "/test/" + directory + "/";
		
		int edgeIndex = 0, outDegree = 0, weight = 0, dst = 0;
		for (int i = 0; i < numOfvertices; i++) {
			outDegree = (int) (Math.random() * (maxOutDegree + 1));
			for (int j = 0; j < outDegree; j++) {
				weight = (int) (Math.random() * maxWeight + 1);
				dst = (int) (Math.random() * numOfvertices + 1);
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

		int start = (int) (Math.random() * numOfvertices);
		int end = (int) (Math.random() * numOfvertices);
		String condition = "";
		List<Integer> l = new ArrayList<Integer>();
		int n;
		for (int k = 0; k < numOfCondition; k++) {
			if (k == 0) {
				n = (int) (Math.random() * numOfvertices);
				l.add(n);
				condition += n;
			} else {
				do {
					n = (int) (Math.random() * numOfvertices);
				} while (l.contains(n));
				l.add(n);
				condition = condition + "|" + n;
			}
		}
		FileUtil.write(FILE_PATH + "demand.csv", start + "," + end + ","
				+ condition + "\n", false);
	}
}
