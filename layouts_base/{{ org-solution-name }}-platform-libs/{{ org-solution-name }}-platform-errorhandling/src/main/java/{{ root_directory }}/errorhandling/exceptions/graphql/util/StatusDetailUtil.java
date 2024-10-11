package {{ root_package }}.errorhandling.exceptions.graphql.util;

import com.google.common.collect.ImmutableMap;
import com.google.protobuf.Any;
import com.google.rpc.*;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.lite.ProtoLiteUtils;

import java.util.*;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;
import static {{ root_package }}.errorhandling.exceptions.graphql.handlers.StatusRuntimeExceptionHandler.getStatusGivenStatusRuntimeException;

public class StatusDetailUtil {

    public static Map<String, Object> getDetails(StatusRuntimeException exception) {
        return getDetails(getStatusGivenStatusRuntimeException(exception), exception);
    }

    public static Map<String, Object> getDetails(Status status, StatusRuntimeException exception) {

        var detailMap = status.getDetailsList().stream()
                .map(StatusDetailUtil::extractDetails)
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(collectingAndThen(toMap(Map.Entry::getKey, Map.Entry::getValue), HashMap::new));

        // if no details map from Status
        // then get map from exception trailers
        if (detailMap.isEmpty()) {
            return getMapOfExceptionTrailer(exception);
        }

        detailMap.put("gRPC Code", Code.forNumber(status.getCode()).toString());
        detailMap.put("message", status.getMessage());

        return detailMap;
    }

    // ------------------ Status detail methods ------------------
    private static Map<String, Object> extractDetails(Any detail) {
        try {
            if (detail.is(BadRequest.class)) {
                return getMessageMap(detail.unpack(BadRequest.class));
            } else if (detail.is(ErrorInfo.class)) {
                return getMessageMap(detail.unpack(ErrorInfo.class));
            } else if (detail.is(DebugInfo.class)) {
                return getMessageMap(detail.unpack((DebugInfo.class)));
            } else if (detail.is(ResourceInfo.class)) {
                return getMessageMap(detail.unpack(ResourceInfo.class));
            } else if (detail.is(LocalizedMessage.class)) {
                return getMessageMap(detail.unpack(LocalizedMessage.class));
            } else {
                return Collections.emptyMap();
            }
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    protected static Map<String, Object> getMessageMap(BadRequest message) {
        return message.getFieldViolationsList().stream()
                .collect(toMap(BadRequest.FieldViolation::getField, BadRequest.FieldViolation::getDescription));
    }

    protected static Map<String, Object> getMessageMap(ErrorInfo message) {
        var base = new HashMap<String, Object>();
        base.put("reason", message.getReason());
        base.put("domain", message.getDomain());
        base.put("metadata", message.getMetadataMap());
        return base;
    }

    protected static Map<String, Object> getMessageMap(DebugInfo message) {
        var trace = String.join("\n", message.getStackEntriesList());
        return Map.of(
                "stacktrace", trace,
                "detail", message.getDetail()
        );
    }

    protected static Map<String, Object> getMessageMap(ResourceInfo message) {
        return Map.of(
                "resourceType", message.getResourceType(),
                "resourceName", message.getResourceName(),
                "owner", message.getOwner(),
                "description", message.getDescription()
        );
    }

    protected static Map<String, Object> getMessageMap(LocalizedMessage message) {
        return Map.of(
                "locale", message.getLocale(),
                "message", message.getMessage()
        );
    }


// ------------------ StatusRuntimeException trailer methods ------------------

    protected static Map<String, Object> getMapOfExceptionTrailer(StatusRuntimeException e) {
        ImmutableMap.Builder<String, Object> resultMapBuilder = ImmutableMap.builder();

        Metadata trailers = e.getTrailers();

        if (trailers != null) {
            var keys = trailers.keys();
            keys.forEach(key -> {
                if (key.equals("grpc-status-details-bin")) {
                    getGrpcStatusDetailTrailerItem(key, trailers).ifPresent(resultMapBuilder::put);
                } else if (key.endsWith(Metadata.BINARY_HEADER_SUFFIX)) {
                    getBinaryTrailerItem(key, trailers).ifPresent(resultMapBuilder::put);
                } else {
                    getAsciiTrailerItem(key, trailers).ifPresent(resultMapBuilder::put);
                }
            });
        }
        return resultMapBuilder.build();
    }

    private static Optional<Map.Entry<String, Object>> getGrpcStatusDetailTrailerItem(String key, Metadata trailers) {
        //This marshaller comes from how StatusProto marshals their protobuf
        //https://chromium.googlesource.com/external/github.com/grpc/grpc/+/HEAD/doc/PROTOCOL-HTTP2.md
        Metadata.Key<Status> k = Metadata.Key.of(key,
                ProtoLiteUtils.metadataMarshaller(Status.getDefaultInstance()));

        var trailerItem = trailers.get(k);

        return trailerItem != null ? Optional.of(Map.entry(k.name(), trailerItem.toString())) : Optional.empty();
    }

    private static Optional<Map.Entry<String, Object>> getBinaryTrailerItem(String key, Metadata trailers) {
        Metadata.Key<byte[]> k = Metadata.Key.of(key, Metadata.BINARY_BYTE_MARSHALLER);

        var trailerItem = trailers.get(k);

        return trailerItem != null ? Optional.of(Map.entry(k.name(), new String(trailerItem))) : Optional.empty();
    }

    private static Optional<Map.Entry<String, Object>> getAsciiTrailerItem(String key, Metadata trailers) {
        Metadata.Key<String> k = Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER);

        var trailerItem = trailers.get(k);

        return trailerItem != null ? Optional.of(Map.entry(k.name(), trailerItem)) : Optional.empty();
    }

}
