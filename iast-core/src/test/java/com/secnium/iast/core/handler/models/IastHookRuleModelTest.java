package com.secnium.iast.core.handler.models;

import com.secnium.iast.core.PropertyUtils;
import org.junit.Test;

public class IastHookRuleModelTest {
    @Test
    public void buildMoelFromServer() {
        PropertyUtils.getInstance(
                "/home/fine/BUG/DongTai-agent-java/iast-agent/src/main/resources/iast.properties");
        IastHookRuleModel.buildModel();
        System.out.println("2333");
    }
}