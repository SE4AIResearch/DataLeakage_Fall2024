package com.github.cd721.data_leakage_plugin.inspections.leakage_inspections.leakage_instances;

import com.github.cd721.data_leakage_plugin.data.OverlapLeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.inspections.leakage_inspections.LeakageInspection;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_instances.InstanceElementVisitor;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_instances.OverlapLeakageInstanceVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OverlapLeakageInstanceInspection extends InstanceInspection<OverlapLeakageInstance> {

    @Override
    public InstanceElementVisitor<OverlapLeakageInstance> instanceElementVisitor(List<OverlapLeakageInstance> leakageInstances
            , @NotNull ProblemsHolder holder) {
        return new OverlapLeakageInstanceVisitor(leakageInstances, holder);
    }

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.OverlapLeakage;
    }


}
