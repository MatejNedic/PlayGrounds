package com.mvp.mvp.security.evaluator;

import javax.validation.constraints.NotNull;

public interface RuleHandler {

    boolean checkRule(@NotNull String rule);
}
