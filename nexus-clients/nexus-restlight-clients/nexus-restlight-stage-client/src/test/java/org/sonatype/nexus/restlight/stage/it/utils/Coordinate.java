package org.sonatype.nexus.restlight.stage.it.utils;

import org.sonatype.nexus.artifact.Gav;
import org.sonatype.nexus.artifact.IllegalArtifactCoordinateException;

public class Coordinate
{

    private String groupId;

    private String artifactId;

    private String version;

    private String classifier;

    private String extension;

    public Coordinate( final String groupId, final String artifactId, final String version, final String classifier,
                       final String extension )
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.classifier = classifier;
        this.extension = extension;
    }

    public Coordinate( final String groupId, final String artifactId, final String version, final String extension )
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.classifier = null;
        this.extension = extension;
    }

    public Gav toGav()
        throws IllegalArtifactCoordinateException
    {
        return new Gav( groupId, artifactId, version, classifier, extension, null, null, null, false, false, null,
                        false, null );
    }

    public String getGroupId()
    {
        return groupId;
    }

    public Coordinate setGroupId( final String groupId )
    {
        this.groupId = groupId;
        return this;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public Coordinate setArtifactId( final String artifactId )
    {
        this.artifactId = artifactId;
        return this;
    }

    public String getVersion()
    {
        return version;
    }

    public Coordinate setVersion( final String version )
    {
        this.version = version;
        return this;
    }

    public String getClassifier()
    {
        return classifier;
    }

    public Coordinate setClassifier( final String classifier )
    {
        this.classifier = classifier;
        return this;
    }

    public String getExtension()
    {
        return extension;
    }

    public Coordinate setExtension( final String extension )
    {
        this.extension = extension;
        return this;
    }

    @Override
    public String toString()
    {
        return "Coordinate [\n artifactId=" + artifactId + "\n classifier=" + classifier + "\n extension=" + extension
            + "\n groupId=" + groupId + "\n version=" + version + "\n]";
    }

}
