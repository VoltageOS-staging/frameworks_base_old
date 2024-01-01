package com.android.server.pm.ext;

import android.annotation.Nullable;
import android.content.pm.PackageManager;

import com.android.server.pm.pkg.component.ParsedPermission;
import com.android.server.pm.pkg.component.ParsedService;
import com.android.server.pm.pkg.component.ParsedServiceImpl;
import com.android.server.pm.pkg.component.ParsedUsesPermission;
import com.android.server.pm.pkg.component.ParsedUsesPermissionImpl;
import com.android.server.pm.pkg.parsing.ParsingPackage;

import java.util.ArrayList;
import java.util.List;

public class PackageParsingHooks {
    public static final PackageParsingHooks DEFAULT = new PackageParsingHooks();

    public boolean shouldSkipPermissionDefinition(ParsedPermission p) {
        return false;
    }

    public boolean shouldSkipUsesPermission(ParsedUsesPermission p) {
        return false;
    }

    @Nullable
    public List<ParsedUsesPermissionImpl> addUsesPermissions() {
        return null;
    }

    protected static List<ParsedUsesPermissionImpl> createUsesPerms(String... perms) {
        int l = perms.length;
        var res = new ArrayList<ParsedUsesPermissionImpl>(l);
        for (int i = 0; i < l; ++i) {
            res.add(new ParsedUsesPermissionImpl(perms[i], 0));
        }
        return res;
    }

    public void amendParsedService(ParsedServiceImpl s) {

    }

    public List<ParsedService> addServices(ParsingPackage pkg) {
        return null;
    }

    // supported return values:
    // PackageManager.COMPONENT_ENABLED_STATE_DISABLED
    // PackageManager.COMPONENT_ENABLED_STATE_ENABLED
    // PackageManager.COMPONENT_ENABLED_STATE_DEFAULT (skip override)
    public int overrideDefaultPackageEnabledState() {
        return PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
    }

    public static ParsedServiceImpl createService(ParsingPackage pkg, String className) {
        var s = new ParsedServiceImpl();
        s.setPackageName(pkg.getPackageName());
        s.setName(className);
        s.setProcessName(pkg.getProcessName());
        s.setDirectBootAware(pkg.isPartiallyDirectBootAware());
        s.setExported(true);
        return s;
    }
}
