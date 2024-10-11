package {{ root_package }}.aws.v2.model;

public class ClientInteraction<Request, Response> {

    private final Request request;

    private final Response response;

    public ClientInteraction(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }
}
