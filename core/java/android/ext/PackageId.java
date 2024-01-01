package android.ext;

import android.annotation.SystemApi;

/** @hide */
@SystemApi
// Int values that are assigned to packages in this interface can be retrieved at runtime from
// ApplicationInfo.ext().getPackageId() or from AndroidPackage.ext().getPackageId() (in system_server).
//
// PackageIds are assigned to parsed APKs only after they are verified, either by a certificate check
// or by a check that the APK is stored on an immutable OS partition.
public interface PackageId {
    int UNKNOWN = 0;
}
