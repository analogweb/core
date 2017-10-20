package org.analogweb;

/**
 * Factory of {@link ContainerAdaptor}.
 * 
 * @param <T>
 *            type of {@link ContainerAdaptor}.
 * @author snowgoose
 */
public interface ContainerAdaptorFactory<T extends ContainerAdaptor>
		extends
			Module {

	/**
	 * Create new {@link ContainerAdaptor}.
	 * 
	 * @param resolver
	 *            {@link ApplicationContext}
	 * @return Created {@link ContainerAdaptor}
	 */
	T createContainerAdaptor(ApplicationContext resolver);
}
