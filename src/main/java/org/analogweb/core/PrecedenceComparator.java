package org.analogweb.core;

import java.util.Comparator;

import org.analogweb.Precedence;

/**
 * @author snowgoose
 */
public class PrecedenceComparator implements Comparator<Precedence> {

    @Override
    public int compare(Precedence arg0, Precedence arg1) {
        if(arg0.getPrecedence() == arg1.getPrecedence()){
            return 0;
        }
        if(arg0.getPrecedence() > arg1.getPrecedence()){
            return -1;
        }
        return 1;
    }

}
