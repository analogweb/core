package org.analogweb.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.util.CollectionUtils;
import org.analogweb.util.StringUtils;

/**
 * @author snowgooseyk
 */
public class AcceptLanguages {

    private final RequestContext request;
    private List<Locale> locales;

    public AcceptLanguages(RequestContext context) {
        this.request = context;
    }

    protected List<String> getHeaderValues(RequestContext context) {
        Headers headers = context.getRequestHeaders();
        if (headers == null) {
            return Collections.emptyList();
        }
        return headers.getValues("Accept-Language");
    }

    public static class AcceptLanguage {

        private float q;
        private Locale locale;

        private AcceptLanguage(Locale locale, float q) {
            this.locale = locale;
            this.q = q;
        }

        public static AcceptLanguage valueOf(Locale locale, float q) {
            return new AcceptLanguage(locale, q);
        }

        public Locale getLocale() {
            return this.locale;
        }

        public float getQualityFactor() {
            return this.q;
        }
    }

    public List<Locale> getLocales() {
        if (this.locales == null) {
            List<String> headerValues = getHeaderValues(this.request);
            if (CollectionUtils.isEmpty(headerValues)) {
                this.locales = Collections.emptyList();
                return this.locales;
            }
            List<AcceptLanguage> ac = new ArrayList<AcceptLanguage>();
            for (String acceptLanguage : headerValues) {
                for (String aLanguage : StringUtils.split(acceptLanguage, ',')) {
                    List<String> codeAndParam = StringUtils.split(
                            StringUtils.trimToEmpty(aLanguage), ';');
                    if (CollectionUtils.isNotEmpty(codeAndParam)) {
                        String code = codeAndParam.get(0);
                        float quality = 1.0f;
                        if (codeAndParam.size() > 1) {
                            for (int i = 1; i < codeAndParam.size(); i++) {
                                String param = StringUtils.trimToEmpty(codeAndParam.get(i));
                                if (param.startsWith("q=")) {
                                    quality = Float.valueOf(StringUtils.substring(param, 2));
                                    break;
                                }
                            }
                        }
                        List<String> codes = StringUtils.split(code.replace('-', '_'), '_');
                        Locale locale;
                        switch (codes.size()) {
                        case 2:
                            locale = new Locale(codes.get(0), codes.get(1));
                            break;
                        case 3:
                            locale = new Locale(codes.get(0), codes.get(1), codes.get(2));
                            break;
                        default:
                            locale = new Locale(codes.get(0));
                        }
                        ac.add(AcceptLanguage.valueOf(locale, quality));
                    }
                }
            }
            Collections.sort(ac, getComparator());
            locales = new ArrayList<Locale>();
            for (AcceptLanguage a : ac) {
                locales.add(a.getLocale());
            }
            locales = Collections.unmodifiableList(locales);
        }
        return this.locales;
    }

    protected final Comparator<AcceptLanguage> DEFAULT_COMPARATOR = new Comparator<AcceptLanguage>() {

        @Override
        public int compare(AcceptLanguage o1, AcceptLanguage o2) {
            float q1 = o1.getQualityFactor();
            float q2 = o2.getQualityFactor();
            if (q1 == q2) {
                return 0;
            } else if (q1 < q2) {
                return 1;
            } else {
                return -1;
            }
        }
    };

    protected Comparator<AcceptLanguage> getComparator() {
        return DEFAULT_COMPARATOR;
    }

    public Locale getLocale() {
        return CollectionUtils.indexOf(getLocales(), 0);
    }
}
