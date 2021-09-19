package com.mvp.mvp.security.evaluator;

import com.mvp.mvp.model.security.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component("RuleHandler")
public class RuleHandlerImpl implements RuleHandler {
    @Override
    public boolean checkRule(@NotNull String rule) {
        return ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getRoles().stream().anyMatch(n -> n.getName().equals(rule));
    }
}
