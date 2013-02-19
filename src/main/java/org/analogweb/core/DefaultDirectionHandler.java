package org.analogweb.core;

import java.io.IOException;

import org.analogweb.Direction;
import org.analogweb.DirectionFormatter;
import org.analogweb.DirectionFormatterAware;
import org.analogweb.DirectionHandler;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.WebApplicationException;

/**
 * @author snowgoose
 */
public class DefaultDirectionHandler implements DirectionHandler {

    public void handleResult(Direction result, DirectionFormatter resultFormatter,
            RequestContext context, ResponseContext response) throws IOException,
            WebApplicationException {
        try {
            if (result instanceof DirectionFormatterAware<?>) {
                ((DirectionFormatterAware<?>) result).attach(resultFormatter);
            }
            result.render(context, response);
        } catch (Exception e) {
            throw new DirectionEvaluationException(e, result);
        }

    }

}
