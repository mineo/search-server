package org.musicbrainz.search.servlet;

/**
 * Defines the name of the webservice resources as defined at http://wiki.musicbrainz.org/XML_Web_Service#The_URL_Schema
 */
public enum ResourceType {

    ARTIST("artist"),
    LABEL("label"),
    RELEASE("release"),
    RELEASE_GROUP("release-group"),
    RECORDING("recording"), 
    CDSTUB("cdstub"),
    FREEDB("freedb"),
    ANNOTATION("annotation"),
    WORK("work"),
    ;

    private String name;

    ResourceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ResourceType getValue(String value) {
        for ( ResourceType candidateEnum : ResourceType.values() ) {
            if(candidateEnum.getName().equals(value)) return candidateEnum;
        }
        return null;
    }

}
