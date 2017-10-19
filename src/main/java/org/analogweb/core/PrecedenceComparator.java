package org.analogweb.core;

import java.util.Comparator;

import org.analogweb.Precedence;

/**
 * @author snowgoose
 */
public class PrecedenceComparator<T extends Precedence>
		implements
			Comparator<T> {

	@Override
	public int compare(T arg0, T arg1) {
		if (arg0.getPrecedence() == arg1.getPrecedence()) {
			return 0;
		}
		if (arg0.getPrecedence() > arg1.getPrecedence()) {
			return 1;
		}
		return -1;
	}
}
