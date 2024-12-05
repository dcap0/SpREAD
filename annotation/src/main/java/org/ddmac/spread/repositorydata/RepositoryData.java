package org.ddmac.spread.repositorydata;

import org.ddmac.spread.enums.Serializer;

/**
 * Record holding metadata about annotated interface.
 *
 * @param interfacePackage Package the interface resides in.
 * @param repoSimpleName Simple name of the repository class.
 * @param entityName Simple name of the entity associated with the repository.
 * @param reqPath User provided path for the api endpoint.
 * @param serializer User provided enum for the serializer they use.
 */

public record RepositoryData(
        String interfacePackage,
        String repoSimpleName,
        String entityName,
        String reqPath,
        Serializer serializer
) { }
