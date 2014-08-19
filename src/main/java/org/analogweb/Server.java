package org.analogweb;

/**
 * @author snowgooseyk
 */
public interface Server {

	void run();
	
	void shutdown(int mode);
}
