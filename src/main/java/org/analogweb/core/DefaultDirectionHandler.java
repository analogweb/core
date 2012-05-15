package org.analogweb.core;

import java.io.IOException;

import javax.servlet.ServletException;

import org.analogweb.Direction;
import org.analogweb.DirectionFormatter;
import org.analogweb.DirectionFormatterAware;
import org.analogweb.DirectionHandler;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
import org.analogweb.exception.DirectionEvaluationException;


/**
 * @author snowgoose
 */
public class DefaultDirectionHandler implements DirectionHandler {

    public void handleResult(Direction result, DirectionFormatter resultFormatter,
            RequestContext context, RequestAttributes attributes) throws IOException,
            ServletException {
        try {
            if(result instanceof DirectionFormatterAware<?>){
                ((DirectionFormatterAware<?>)result).attach(resultFormatter);
            }
            result.render(context);
        } catch (Exception e) {
            throw new DirectionEvaluationException(e, result);
        }

    }

}
