package com.AgeniusAgent.Agenius.intercepteur;


import com.AgeniusAgent.Agenius.context.JobOfferContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JobOfferNameInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI(); // Ex: /CandidatsSelected/JavaDeveloper
        String[] segments = path.split("/");

        if (segments.length >= 3 && "CandidatsSelected".equals(segments[1])) {
            String joboffrename = segments[2];
            System.out.println("🔍 Intercepted job offer name: " + joboffrename);
            JobOfferContext.setJobOfferName(joboffrename);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        JobOfferContext.clear(); // Nettoyage après la requête
    }
}
