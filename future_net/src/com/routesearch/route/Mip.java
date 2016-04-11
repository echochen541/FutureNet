package com.routesearch.route;

import org.gnu.glpk.*;

import com.filetool.util.FileUtil;

public class Mip implements GlpkCallbackListener, GlpkTerminalListener {
	private boolean hookUsed = false;
	private String fname;
	private String fdata;
	private int numOfEdges;
	private glp_prob lp;
	private static int[][] edges;
	private static double minCost = Double.MAX_VALUE;

	public Mip(String fname, String fdata, int numOfEdges) {
		this.fname = fname;
		this.fdata = fdata;
		this.numOfEdges = numOfEdges;
	}

	public void mipSolver() {
		GLPK.glp_java_set_numeric_locale("C");
		glp_tran tran;
		glp_iocp iocp;
		int skip = 0;
		int ret;

		// listen to callbacks
		GlpkCallback.addListener(this);

		// listen to terminal output
		GlpkTerminal.addListener(this);
		try {
			// create problem
			lp = GLPK.glp_create_prob();
			// allocate workspace
			tran = GLPK.glp_mpl_alloc_wksp();
			// read model
			ret = GLPK.glp_mpl_read_model(tran, fname, skip);
			// read data
			ret = GLPK.glp_mpl_read_data(tran, fdata);
			// generate model
			ret = GLPK.glp_mpl_generate(tran, null);
			// build model
			GLPK.glp_mpl_build_prob(tran, lp);

			// set solver parameters
			iocp = new glp_iocp();
			GLPK.glp_init_iocp(iocp);
			iocp.setPresolve(GLPKConstants.GLP_ON);

			// solve model
			ret = GLPK.glp_intopt(lp, iocp);

			// retrieve result
			if (ret == 0)
				return;

			// free memory
			GLPK.glp_mpl_free_wksp(tran);
			GLPK.glp_delete_prob(lp);

		} catch (org.gnu.glpk.GlpkException e) {
			System.err.println("An error inside the GLPK library occured.");
			System.err.println(e.getMessage());
		} catch (RuntimeException e) {
			System.err.println(e.getMessage());
		}
		// do not listen to output anymore
		GlpkTerminal.removeListener(this);
		// do not listen for callbacks anymore
		GlpkCallback.removeListener(this);

		// check that the terminal hook function has been used
		if (!hookUsed) {
			throw new RuntimeException("The terminal output hook was not used.");
		}
		return;
	}

	private static void write_mip_solution(glp_prob lp, int numOfEdges) {
		edges = new int[Route.numOfVertices][Route.numOfVertices];
		// int n;
		String name;
		double val;
		double cost;
		// List<CostV> cvs = new ArrayList<CostV>();

		name = GLPK.glp_get_obj_name(lp);
		val = GLPK.glp_mip_obj_val(lp);
		cost = val;

		if (cost == minCost)
			System.exit(0);

		if (cost < minCost)
			minCost = cost;

		// n = GLPK.glp_get_num_cols(lp);
		// System.out.println(cost);

		// System.out.println(Route.sourceIndex + "--------------->" +
		// Route.destinationIndex);

		for (int i = 1; i <= numOfEdges; i++) {
			name = GLPK.glp_get_col_name(lp, i);
			val = GLPK.glp_mip_col_val(lp, i);
			int start = Integer.parseInt(name.substring(name.indexOf("[") + 1, name.indexOf(",")));
			int end = Integer.parseInt(name.substring(name.indexOf(",") + 1, name.indexOf("]")));

			if (val == 1.0) {
				edges[start][end] = 1;
			}
		}
		return;
	}

	@Override
	public boolean output(String str) {
		hookUsed = true;
		// System.out.print(str);
		return false;
	}

	@Override
	public void callback(glp_tree tree) {
		int reason = GLPK.glp_ios_reason(tree);
		if (reason == GLPKConstants.GLP_IBINGO) {
			// System.out.println("Better solution found");
			// write answer to result.csv
			write_mip_solution(lp, numOfEdges);

			StringBuffer resultSb = new StringBuffer();

			int pre = Route.sourceIndex;

			while (pre != Route.destinationIndex) {
				for (int i = 0; i < Route.numOfVertices; i++) {
					if (edges[pre][i] == 1) {
						resultSb.append(Route.edgeIDs[pre][i] + "|");
						pre = i;
						break;
					}
				}
			}
			resultSb.deleteCharAt(resultSb.length() - 1);
			// System.out.println(resultSb.toString());
			FileUtil.write(Route.resultFilePath, resultSb.toString(), false);
		}
	}
}