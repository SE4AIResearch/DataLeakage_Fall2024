package com.github.cd721.data_leakage_plugin.inspections.leakage_inspections.leakage_instances;

import com.github.cd721.data_leakage_plugin.data.PreprocessingLeakageInstance;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.inspections.leakage_inspections.LeakageInspection;
import com.github.cd721.data_leakage_plugin.inspections.visitors.leakage_instances.PreprocessingLeakageInstanceVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

public class PreprocessingLeakageInstanceInspection extends LeakageInspection<PreprocessingLeakageInstance> {

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.PreprocessingLeakage     ;
    }

    @Override
    public PyElementVisitor getElementVisitor(@NotNull ProblemsHolder holder) {
        var leakageInstances = leakageAnalysisParser.LeakageInstances();

        var preprocessingLeakageInstances = getLeakageInstancesForType(leakageInstances);
        return new PreprocessingLeakageInstanceVisitor(preprocessingLeakageInstances, holder);
    }



}
