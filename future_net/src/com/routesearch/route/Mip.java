package com.routesearch.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.GlpkTerminal;
import org.gnu.glpk.GlpkTerminalListener;
import org.gnu.glpk.glp_iocp;
import org.gnu.glpk.glp_prob;
import org.gnu.glpk.glp_tran;

public class Mip implements GlpkTerminalListener {
	private String fname;
	private String fdata;

	public Mip(String fname, String fdata) {
		this.fname = fname;
		this.fdata = fdata;
	}

	public List<CostV> mipSolver(int numOfEdges) {
		GLPK.glp_java_set_numeric_locale("C");
		glp_prob lp;
		glp_tran tran;
		glp_iocp iocp;
		int skip = 0;
		int ret;

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

			// solve model
			iocp = new glp_iocp();
			GLPK.glp_init_iocp(iocp);
			iocp.setPresolve(GLPKConstants.GLP_ON);
			ret = GLPK.glp_intopt(lp, iocp);
			// retrieve result
			if (ret == 0) {
				return write_mip_solution(lp, numOfEdges);
			}

			// free memory
			GLPK.glp_mpl_free_wksp(tran);
			GLPK.glp_delete_prob(lp);

		} catch (org.gnu.glpk.GlpkException e) {
			System.err.println("An error inside the GLPK library occured.");
			System.err.println(e.getMessage());
		} catch (RuntimeException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	private static List<CostV> write_mip_solution(glp_prob lp, int numOfEdges) {
		int i;
		int n;
		String name;
		double val;
		double cost;
		List<CostV> cvs = new ArrayList<CostV>();

		name = GLPK.glp_get_obj_name(lp);
		val = GLPK.glp_mip_obj_val(lp);
		cost = val;
		n = GLPK.glp_get_num_cols(lp);

		for (i = numOfEdges + 1; i <= n; i++) {
			name = GLPK.glp_get_col_name(lp, i);
			val = GLPK.glp_mip_col_val(lp, i);

			if (val > 0.0 && val <= cost + 0.5) {
				int v = Integer.parseInt(name.substring(name.indexOf("[") + 1, name.indexOf("]")));
				cvs.add(new CostV(v, val));
			}
		}

		Collections.sort(cvs);
		return cvs;
	}

	@Override
	public boolean output(String arg0) {
		// do nothing
		return false;
	}
}