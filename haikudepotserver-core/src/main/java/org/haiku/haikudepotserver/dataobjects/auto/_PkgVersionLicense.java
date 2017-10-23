package org.haiku.haikudepotserver.dataobjects.auto;

import org.apache.cayenne.exp.Property;
import org.haiku.haikudepotserver.dataobjects.PkgVersion;
import org.haiku.haikudepotserver.dataobjects.support.AbstractDataObject;

/**
 * Class _PkgVersionLicense was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _PkgVersionLicense extends AbstractDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "id";

    public static final Property<String> BODY = Property.create("body", String.class);
    public static final Property<PkgVersion> PKG_VERSION = Property.create("pkgVersion", PkgVersion.class);

    public void setBody(String body) {
        writeProperty("body", body);
    }
    public String getBody() {
        return (String)readProperty("body");
    }

    public void setPkgVersion(PkgVersion pkgVersion) {
        setToOneTarget("pkgVersion", pkgVersion, true);
    }

    public PkgVersion getPkgVersion() {
        return (PkgVersion)readProperty("pkgVersion");
    }


}
