package org.haiku.haikudepotserver.dataobjects.auto;

import java.sql.Timestamp;

import org.apache.cayenne.exp.Property;
import org.haiku.haikudepotserver.dataobjects.PkgSupplement;
import org.haiku.haikudepotserver.dataobjects.support.AbstractDataObject;

/**
 * Class _PkgChangelog was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _PkgChangelog extends AbstractDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "id";

    public static final Property<String> CONTENT = Property.create("content", String.class);
    public static final Property<Timestamp> CREATE_TIMESTAMP = Property.create("createTimestamp", Timestamp.class);
    public static final Property<Timestamp> MODIFY_TIMESTAMP = Property.create("modifyTimestamp", Timestamp.class);
    public static final Property<PkgSupplement> PKG_SUPPLEMENT = Property.create("pkgSupplement", PkgSupplement.class);

    public void setContent(String content) {
        writeProperty("content", content);
    }
    public String getContent() {
        return (String)readProperty("content");
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
        writeProperty("createTimestamp", createTimestamp);
    }
    public Timestamp getCreateTimestamp() {
        return (Timestamp)readProperty("createTimestamp");
    }

    public void setModifyTimestamp(Timestamp modifyTimestamp) {
        writeProperty("modifyTimestamp", modifyTimestamp);
    }
    public Timestamp getModifyTimestamp() {
        return (Timestamp)readProperty("modifyTimestamp");
    }

    public void setPkgSupplement(PkgSupplement pkgSupplement) {
        setToOneTarget("pkgSupplement", pkgSupplement, true);
    }

    public PkgSupplement getPkgSupplement() {
        return (PkgSupplement)readProperty("pkgSupplement");
    }


}
