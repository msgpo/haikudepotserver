/*
 * Copyright 2018, Andrew Lindesay
 * Distributed under the terms of the MIT License.
 */

package org.haiku.haikudepotserver.api1;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.commons.lang3.StringUtils;
import org.haiku.haikudepotserver.api1.model.AbstractQueueJobResult;
import org.haiku.haikudepotserver.api1.model.pkg.job.*;
import org.haiku.haikudepotserver.api1.support.AuthorizationFailureException;
import org.haiku.haikudepotserver.api1.support.ObjectNotFoundException;
import org.haiku.haikudepotserver.dataobjects.User;
import org.haiku.haikudepotserver.dataobjects.auto._User;
import org.haiku.haikudepotserver.job.model.*;
import org.haiku.haikudepotserver.pkg.model.*;
import org.haiku.haikudepotserver.security.model.AuthorizationService;
import org.haiku.haikudepotserver.security.model.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Component
@AutoJsonRpcServiceImpl
public class PkgJobApiImpl extends AbstractApiImpl implements PkgJobApi {

    protected static Logger LOGGER = LoggerFactory.getLogger(PkgJobApiImpl.class);

    private final ServerRuntime serverRuntime;
    private final AuthorizationService authorizationService;
    private final JobService jobService;

    public PkgJobApiImpl(
            ServerRuntime serverRuntime,
            AuthorizationService authorizationService,
            JobService jobService) {
        this.serverRuntime = Preconditions.checkNotNull(serverRuntime);
        this.authorizationService = Preconditions.checkNotNull(authorizationService);
        this.jobService = Preconditions.checkNotNull(jobService);
    }

    @Override
    public QueuePkgCategoryCoverageExportSpreadsheetJobResult queuePkgCategoryCoverageExportSpreadsheetJob(QueuePkgCategoryCoverageExportSpreadsheetJobRequest request) {
        Preconditions.checkArgument(null != request);
        return queueSimplePkgJob(
                QueuePkgCategoryCoverageExportSpreadsheetJobResult.class,
                PkgCategoryCoverageExportSpreadsheetJobSpecification.class,
                Permission.BULK_PKGCATEGORYCOVERAGEEXPORTSPREADSHEET);
    }

    @Override
    public QueuePkgCategoryCoverageImportSpreadsheetJobResult queuePkgCategoryCoverageImportSpreadsheetJob(
            QueuePkgCategoryCoverageImportSpreadsheetJobRequest request) throws ObjectNotFoundException {
        Preconditions.checkArgument(null != request, "the request must be supplied");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(request.inputDataGuid), "the input data must be identified by guid");

        final ObjectContext context = serverRuntime.newContext();

        Optional<User> user = tryObtainAuthenticatedUser(context);

        if(!authorizationService.check(
                context,
                user.orElse(null),
                null,
                Permission.BULK_PKGCATEGORYCOVERAGEIMPORTSPREADSHEET)) {
            LOGGER.warn("attempt to import package categories, but was not authorized");
            throw new AuthorizationFailureException();
        }

        // now check that the data is present.

        jobService.tryGetData(request.inputDataGuid)
                .orElseThrow(() -> new ObjectNotFoundException(JobData.class.getSimpleName(), request.inputDataGuid));

        // setup and go

        PkgCategoryCoverageImportSpreadsheetJobSpecification spec = new PkgCategoryCoverageImportSpreadsheetJobSpecification();
        spec.setOwnerUserNickname(user.map(_User::getNickname).orElse(null));
        spec.setInputDataGuid(request.inputDataGuid);

        return new QueuePkgCategoryCoverageImportSpreadsheetJobResult(
                jobService.submit(spec, JobSnapshot.COALESCE_STATUSES_NONE));
    }

    @Override
    public QueuePkgScreenshotSpreadsheetJobResult queuePkgScreenshotSpreadsheetJob(QueuePkgScreenshotSpreadsheetJobRequest request) {
        Preconditions.checkArgument(null != request);
        return queueSimplePkgJob(
                QueuePkgScreenshotSpreadsheetJobResult.class,
                PkgScreenshotSpreadsheetJobSpecification.class,
                Permission.BULK_PKGICONSPREADSHEETREPORT);
    }

    @Override
    public QueuePkgIconSpreadsheetJobResult queuePkgIconSpreadsheetJob(QueuePkgIconSpreadsheetJobRequest request) {
        Preconditions.checkArgument(null != request);
        return queueSimplePkgJob(
                QueuePkgIconSpreadsheetJobResult.class,
                PkgIconSpreadsheetJobSpecification.class,
                Permission.BULK_PKGICONSPREADSHEETREPORT);
    }

    @Override
    public QueuePkgProminenceAndUserRatingSpreadsheetJobResult queuePkgProminenceAndUserRatingSpreadsheetJob(QueuePkgProminenceAndUserRatingSpreadsheetJobRequest request) {
        Preconditions.checkArgument(null!=request);
        return queueSimplePkgJob(
                QueuePkgProminenceAndUserRatingSpreadsheetJobResult.class,
                PkgProminenceAndUserRatingSpreadsheetJobSpecification.class,
                Permission.BULK_PKGPROMINENCEANDUSERRATINGSPREADSHEETREPORT);
    }

    @Override
    public QueuePkgIconExportArchiveJobResult queuePkgIconExportArchiveJob(QueuePkgIconExportArchiveJobRequest request) {
        Preconditions.checkArgument(null!=request);
        return queueSimplePkgJob(
                QueuePkgIconExportArchiveJobResult.class,
                PkgIconExportArchiveJobSpecification.class,
                Permission.BULK_PKGICONEXPORTARCHIVE);
    }

    @Override
    public QueuePkgIconArchiveImportJobResult queuePkgIconArchiveImportJob(QueuePkgIconArchiveImportJobRequest request)
        throws ObjectNotFoundException {

        Preconditions.checkArgument(null != request, "the request must be supplied");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(request.inputDataGuid), "the input data must be identified by guid");

        final ObjectContext context = serverRuntime.newContext();

        Optional<User> user = tryObtainAuthenticatedUser(context);

        if(!authorizationService.check(
                context,
                user.orElse(null),
                null,
                Permission.BULK_PKGICONIMPORTARCHIVE)) {
            LOGGER.warn("attempt to import package icons, but was not authorized");
            throw new AuthorizationFailureException();
        }

        // now check that the data is present.

        jobService.tryGetData(request.inputDataGuid)
                .orElseThrow(() -> new ObjectNotFoundException(JobData.class.getSimpleName(), request.inputDataGuid));

        // setup and go

        PkgIconImportArchiveJobSpecification spec = new PkgIconImportArchiveJobSpecification();
        spec.setOwnerUserNickname(user.map(_User::getNickname).orElse(null));
        spec.setInputDataGuid(request.inputDataGuid);

        return new QueuePkgIconArchiveImportJobResult(
                jobService.submit(spec, JobSnapshot.COALESCE_STATUSES_NONE));
    }

    @Override
    public QueuePkgVersionPayloadLengthPopulationJobResult queuePkgVersionPayloadLengthPopulationJob(QueuePkgVersionPayloadLengthPopulationJobRequest request) {
        Preconditions.checkArgument(null!=request, "a request objects is required");
        return queueSimplePkgJob(
                QueuePkgVersionPayloadLengthPopulationJobResult.class,
                PkgVersionPayloadLengthPopulationJobSpecification.class,
                Permission.BULK_PKGVERSIONPAYLOADLENGTHPOPULATION);
    }

    @Override
    public QueuePkgVersionLocalizationCoverageExportSpreadsheetJobResult queuePkgVersionLocalizationCoverageExportSpreadsheetJob(QueuePkgVersionLocalizationCoverageExportSpreadsheetJobRequest request) {
        Preconditions.checkArgument(null!=request, "a request objects is required");
        return queueSimplePkgJob(
                QueuePkgVersionLocalizationCoverageExportSpreadsheetJobResult.class,
                PkgVersionLocalizationCoverageExportSpreadsheetJobSpecification.class,
                Permission.BULK_PKGVERSIONLOCALIZATIONCOVERAGEEXPORTSPREADSHEET);
    }

    @Override
    public QueuePkgLocalizationCoverageExportSpreadsheetJobResult queuePkgLocalizationCoverageExportSpreadsheetJob(QueuePkgLocalizationCoverageExportSpreadsheetJobRequest request) {
        Preconditions.checkArgument(null!=request, "a request objects is required");
        return queueSimplePkgJob(
                QueuePkgLocalizationCoverageExportSpreadsheetJobResult.class,
                PkgLocalizationCoverageExportSpreadsheetJobSpecification.class,
                Permission.BULK_PKGLOCALIZATIONCOVERAGEEXPORTSPREADSHEET);
    }

    private <R extends AbstractQueueJobResult> R queueSimplePkgJob(
            Class<R> resultClass,
            Class<? extends AbstractJobSpecification> jobSpecificationClass,
            Permission permission) {

        final ObjectContext context = serverRuntime.newContext();

        Optional<User> user = tryObtainAuthenticatedUser(context);

        if (!user.isPresent()) {
            LOGGER.warn("attempt to queue {} without a user", jobSpecificationClass.getSimpleName());
            throw new AuthorizationFailureException();
        }

        if (!authorizationService.check(context, user.get(), null, permission)) {
            LOGGER.warn("attempt to queue {} without sufficient authorization", jobSpecificationClass.getSimpleName());
            throw new AuthorizationFailureException();
        }

        AbstractJobSpecification spec;

        try {
            spec = jobSpecificationClass.newInstance();
        }
        catch(InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("unable to create the job specification for class; " + jobSpecificationClass.getSimpleName(), e);
        }

        spec.setOwnerUserNickname(user.get().getNickname());

        R result;

        try {
            result = resultClass.newInstance();
        }
        catch(InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("unable to create the result; " + resultClass.getSimpleName(), e);
        }

        result.guid = jobService.submit(spec, JobSnapshot.COALESCE_STATUSES_QUEUED_STARTED);
        return result;
    }

    @Override
    public QueuePkgScreenshotExportArchiveJobResult queuePkgScreenshotExportArchiveJob(QueuePkgScreenshotExportArchiveJobRequest request) {
        Preconditions.checkArgument(null != request, "the request must be supplied");

        final ObjectContext context = serverRuntime.newContext();

        Optional<User> user = tryObtainAuthenticatedUser(context);

        if(!authorizationService.check(
                context,
                user.orElse(null),
                null,
                Permission.BULK_PKGSCREENSHOTEXPORTARCHIVE)) {
            LOGGER.warn("attempt to export pkg screenshots as an archive, but was not authorized");
            throw new AuthorizationFailureException();
        }

        PkgScreenshotExportArchiveJobSpecification specification = new PkgScreenshotExportArchiveJobSpecification();
        specification.setOwnerUserNickname(user.get().getNickname());
        specification.setPkgName(request.pkgName);

        return new QueuePkgScreenshotExportArchiveJobResult(
                jobService.submit(specification, JobSnapshot.COALESCE_STATUSES_NONE));
    }

    @Override
    public QueuePkgScreenshotArchiveImportJobResult queuePkgScreenshotArchiveImportJob(QueuePkgScreenshotArchiveImportJobRequest request) throws ObjectNotFoundException {
        Preconditions.checkArgument(null != request, "the request must be supplied");
        Preconditions.checkArgument(StringUtils.isNotBlank(request.inputDataGuid), "the data guid must be supplied");
        Preconditions.checkArgument(null != request.importStrategy, "the import strategy must be supplied");

        final ObjectContext context = serverRuntime.newContext();

        Optional<User> user = tryObtainAuthenticatedUser(context);

        if(!authorizationService.check(
                context,
                user.orElse(null),
                null,
                Permission.BULK_PKGSCREENSHOTIMPORTARCHIVE)) {
            LOGGER.warn("attempt to import package screenshots, but was not authorized");
            throw new AuthorizationFailureException();
        }

        // now check that the data is present.

        jobService.tryGetData(request.inputDataGuid)
                .orElseThrow(() -> new ObjectNotFoundException(JobData.class.getSimpleName(), request.inputDataGuid));

        // setup and go

        PkgScreenshotImportArchiveJobSpecification spec = new PkgScreenshotImportArchiveJobSpecification();
        spec.setOwnerUserNickname(user.map(_User::getNickname).orElse(null));
        spec.setInputDataGuid(request.inputDataGuid);
        spec.setImportStrategy(PkgScreenshotImportArchiveJobSpecification.ImportStrategy.valueOf(request.importStrategy.name()));

        return new QueuePkgScreenshotArchiveImportJobResult(
                jobService.submit(spec, JobSnapshot.COALESCE_STATUSES_NONE));
    }

}
