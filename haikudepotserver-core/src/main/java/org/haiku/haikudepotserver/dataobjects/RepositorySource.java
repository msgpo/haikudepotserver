/*
 * Copyright 2015-2017, Andrew Lindesay
 * Distributed under the terms of the MIT License.
 */

package org.haiku.haikudepotserver.dataobjects;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.apache.cayenne.validation.BeanValidationFailure;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.haiku.haikudepotserver.dataobjects.auto._RepositorySource;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class RepositorySource extends _RepositorySource {

    private final static Pattern CODE_PATTERN = Pattern.compile("^[a-z0-9]{2,16}_[a-z0-9_]+$");

    public static RepositorySource get(ObjectContext context, ObjectId objectId) {
        Preconditions.checkArgument(null != context, "the context must be supplied");
        Preconditions.checkArgument(null != objectId, "the objectId must be supplied");
        Preconditions.checkArgument(objectId.getEntityName().equals(RepositorySource.class.getSimpleName()),
                "the objectId must be targetting RepositorySource");
        return SelectById.query(RepositorySource.class, objectId)
                .sharedCache()
                .selectOne(context);
    }

    public static Optional<RepositorySource> getByCode(ObjectContext context, String code) {
        Preconditions.checkArgument(null != context, "the context must be supplied");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(code), "the code must be supplied");
        return Optional.ofNullable(ObjectSelect.query(RepositorySource.class).where(CODE.eq(code)).selectOne(context));
    }

    public static List<RepositorySource> findActiveByRepository(ObjectContext context, Repository repository) {
        Preconditions.checkArgument(null != context, "the context must be supplied");
        Preconditions.checkArgument(null != repository, "the repository must be supplied");
        return ObjectSelect.query(RepositorySource.class)
                .where(REPOSITORY.eq(repository))
                .and(ACTIVE.isTrue())
                .select(context);
    }

    @Override
    public void validateForInsert(ValidationResult validationResult) {
        if(null==getActive()) {
            setActive(Boolean.TRUE);
        }

        super.validateForInsert(validationResult);
    }

    @Override
    protected void validateForSave(ValidationResult validationResult) {
        super.validateForSave(validationResult);

        if(null != getCode()) {
            if(!RepositorySource.CODE_PATTERN.matcher(getCode()).matches()) {
                validationResult.addFailure(new BeanValidationFailure(this, CODE.getName(), "malformed"));
            }
        }

        if(null != getUrl()) {
            try {
                new URL(getUrl());
            }
            catch(MalformedURLException mue) {
                validationResult.addFailure(new BeanValidationFailure(this, URL.getName(), "malformed"));
            }
        }

        if(null != getUrl()) {
            if(getUrl().endsWith("/")) {
                validationResult.addFailure(new BeanValidationFailure(this, URL.getName(), "trailingslash"));
            }
        }

    }

    /**
     * <p>This is the URL at which one might find the packages for this repository.</p>
     */

    public URL getPackagesBaseURL() {
        try {
            return UriComponentsBuilder.fromUriString(getBaseURL().toString())
                    .pathSegment("packages")
                    .build()
                    .toUri()
                    .toURL();
        }
        catch(MalformedURLException mue) {
            throw new IllegalStateException("unable to reform a url for obtaining packages",mue);
        }
    }

    /**
     * <p>This URL can be used to access the HPKR data for the repository source.</p>
     */

    public URL getHpkrURL() {
        try {
            return UriComponentsBuilder.fromUriString(getBaseURL().toString())
                    .pathSegment("repo")
                    .build()
                    .toUri()
                    .toURL();
        }
        catch(MalformedURLException mue) {
            throw new IllegalStateException("unable to reform a url for obtaining packages",mue);
        }
    }

    /**
     * <p>Other files are able to be found in the repository.  This method provides a root for these
     * files.</p>
     */

    private URL getBaseURL() {
        try {
            return new URL(getUrl());
        }
        catch(MalformedURLException mue) {
            throw new IllegalStateException("malformed url should not be stored in a repository");
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("code", getCode())
                .build();
    }

}
