package org.analogweb;

/**
 * A precedence of modules.
 * 
 * @author snowgoose
 */
public interface Precedence {

	int HIGHEST = Integer.MIN_VALUE;
	int LOWEST = Integer.MAX_VALUE;

	int getPrecedence();
}
