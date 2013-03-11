package org.analogweb;

/**
 * @author snowgoose
 */
public interface ModulesContainerAdaptorAware {

	/**
	 * モジュールをロード可能な{@link ContainerAdaptor}を設定します。
	 * @see Modules#getModulesContainerAdaptor()
	 * @param containerAdaptor {@link ContainerAdaptor}
	 */
	void setModulesContainerAdaptor(ContainerAdaptor containerAdaptor);

}
