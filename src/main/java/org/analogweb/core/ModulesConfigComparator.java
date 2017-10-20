package org.analogweb.core;

import java.io.Serializable;
import java.util.Comparator;

import org.analogweb.ModulesConfig;
import org.analogweb.PluginModulesConfig;
import org.analogweb.UserModulesConfig;
import org.analogweb.util.Assertion;

/**
 * @author snowgoose
 */
public class ModulesConfigComparator
		implements
			Comparator<ModulesConfig>,
			Serializable {

	private static final long serialVersionUID = -8129615702089570460L;

	@Override
	public int compare(ModulesConfig modulesConfig1,
			ModulesConfig modulesConfig2) {
		Assertion.notNull(modulesConfig1, "ModuleConfig");
		Assertion.notNull(modulesConfig2, "ModuleConfig");
		int type1 = ModulesConfigType.valueIsOf(modulesConfig1).ordinal();
		int type2 = ModulesConfigType.valueIsOf(modulesConfig2).ordinal();
		if (type1 > type2) {
			return 1;
		} else if (type1 < type2) {
			return -1;
		} else {
			return 0;
		}
	}

	private interface IsOf {

		boolean isOf(ModulesConfig moduleConfig);
	}

	private enum ModulesConfigType implements IsOf {
		ROOT {

			@Override
			public boolean isOf(ModulesConfig moduleConfig) {
				return RootModulesConfig.class.isInstance(moduleConfig);
			}
		},
		PLUGIN {

			@Override
			public boolean isOf(ModulesConfig moduleConfig) {
				return PluginModulesConfig.class.isInstance(moduleConfig);
			}
		},
		USERDEF {

			@Override
			public boolean isOf(ModulesConfig moduleConfig) {
				return UserModulesConfig.class.isInstance(moduleConfig);
			}
		},
		OTHER {

			@Override
			public boolean isOf(ModulesConfig moduleConfig) {
				return false;
			}
		};

		static ModulesConfigType valueIsOf(ModulesConfig modulesConfig) {
			for (ModulesConfigType type : values()) {
				if (type.isOf(modulesConfig)) {
					return type;
				}
			}
			return OTHER;
		}
	}
}
