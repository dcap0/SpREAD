package org.ddmac.spread.repositorydata;

import org.ddmac.spread.enums.Serializer;

public record RepositoryData(
        String interfacePackage,
        String repoSimpleName,
        String entityName,
        String reqPath,
        Serializer serializer
) { }
