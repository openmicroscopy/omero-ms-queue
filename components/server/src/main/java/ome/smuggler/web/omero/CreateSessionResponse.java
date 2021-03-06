package ome.smuggler.web.omero;

/**
 * Web clients receive instances of this class in response to a request to
 * open a new OMERO session.
 * This is just a data transfer object whose sole purpose is to facilitate the
 * transfer of information from the client to the server.
 * @see SessionController#create(CreateSessionRequest)
 */
public class CreateSessionResponse {

    /**
     * The OMERO session key that identifies the newly opened session.
     */
    public String sessionKey;

}
