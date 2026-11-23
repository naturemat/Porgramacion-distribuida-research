package ec.mentora.common.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String tenantId = request.getHeader("X-Tenant-Id");
        String correlationId = request.getHeader("X-Correlation-Id");
        if (tenantId != null && !tenantId.isBlank()) {
            TenantContext.set(tenantId);
        }
        if (correlationId != null && !correlationId.isBlank()) {
            CorrelationContext.set(correlationId);
        }
        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
            CorrelationContext.clear();
        }
    }
}
