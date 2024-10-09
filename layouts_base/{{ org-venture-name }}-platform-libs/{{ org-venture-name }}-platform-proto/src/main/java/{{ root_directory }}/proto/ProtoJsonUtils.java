package {{ root_package }}.proto;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtoJsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(ProtoJsonUtils.class);

    /**
     * Converts a protobuf message into a json string
     * @param protoMessage message to convert
     * @return json string representing the message
     */
    public static String toJson(Message protoMessage) {
        try {
            return JsonFormat.printer().includingDefaultValueFields().print(protoMessage);
        } catch (InvalidProtocolBufferException e) {
            logger.error("Unable to print protobuf message", e);
            throw new IllegalArgumentException("Unable to print a protobuf message");
        }
    }

    /**
     * Parses a json string into a protobuf object
     * @param json string to create a protobuf object from
     * @param builder protobuf object builder that builds the result
     * @param <T> result type
     * @return resulting protobuf object
     */
    @SuppressWarnings("unchecked")
    public static <T> T toProto(String json, Message.Builder builder) {
        try {
            JsonFormat.parser().ignoringUnknownFields().merge(json, builder);
            return (T) builder.build();
        } catch (InvalidProtocolBufferException e) {
            logger.error("Unable to parse protobuf message", e);
            throw new IllegalArgumentException("Unable to parse a protobuf message");
        }
    }


}
