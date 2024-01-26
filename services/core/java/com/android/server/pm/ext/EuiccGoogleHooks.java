package com.android.server.pm.ext;

import android.Manifest;
import android.content.pm.PackageManager;

import com.android.server.pm.pkg.PackageStateInternal;
import com.android.server.pm.pkg.component.ParsedUsesPermission;

class EuiccGoogleHooks extends PackageHooks {

    static class ParsingHooks extends PackageParsingHooks {

        @Override
        public int overrideDefaultPackageEnabledState() {
            return PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        }

        @Override
        public boolean shouldSkipUsesPermission(ParsedUsesPermission p) {
            switch (p.getName()) {
                // Carrier apps aren't shipped on GrapheneOS, these permissions are needed only to
                // install/enable them
                case Manifest.permission.INSTALL_EXISTING_PACKAGES:
                case Manifest.permission.CHANGE_COMPONENT_ENABLED_STATE:
                    return true;
            }

            return false;
        }
    }

    @Override
    public boolean shouldBlockPackageVisibility(int userId, PackageStateInternal otherPkg) {
        // Block EuiccGoogle from interacting with GmsCore, which is used for feature flags, logging,
        // perf data reporting etc.
        //
        // Block visibility for the rest of non-system packages to reduce the attack surface.
        return !otherPkg.isSystem();
    }
}
