package tn.amin.mpro2;

import android.content.res.Resources;
import android.content.res.XModuleResources;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import tn.amin.mpro2.constants.OrcaInfo;
import tn.amin.mpro2.debug.OrcaExplorer;
import tn.amin.mpro2.orca.OrcaGateway;

public class MainHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private String modulePath = null;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        this.modulePath = startupParam.modulePath;
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        switch (lpparam.packageName) {
            case OrcaInfo.ORCA_PACKAGE_NAME:
                orcaHook(lpparam);
                break;
        }
    }

    /**
     * Initialize gateway with Messenger package and implement all features.
     * @param lpparam information about messenger package (version, dir...)
     */
    private void orcaHook(XC_LoadPackage.LoadPackageParam lpparam) {
        OrcaGateway gateway = new OrcaGateway(lpparam.appInfo.sourceDir, lpparam.classLoader, getResources());

        MProPatcher featuresBox = new MProPatcher(gateway);
        featuresBox.init();

        OrcaExplorer.exploreEarly(lpparam.classLoader);
    }

    private Resources getResources() {
        return XModuleResources.createInstance(modulePath, null);
    }
}
