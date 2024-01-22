package com.github.cd721.data_leakage_plugin.inspections;

import com.github.cd721.data_leakage_plugin.data.OverlapLeakageInstance;
import com.github.cd721.data_leakage_plugin.data.taints.Taint;
import com.github.cd721.data_leakage_plugin.enums.LeakageType;
import com.github.cd721.data_leakage_plugin.enums.OverlapLeakageSourceKeyword;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.python.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A type of {@link PyElementVisitor} that visits different types of elements within the PSI tree,
 * such as {@link PyReferenceExpression}s.
 */
public class OverlapLeakageElementVisitor extends ElementVisitor<OverlapLeakageInstance, OverlapLeakageSourceKeyword> {
    private final List<OverlapLeakageInstance> overlapLeakageInstances;
    private final PsiRecursiveElementVisitor recursiveElementVisitor;


    public OverlapLeakageElementVisitor(List<OverlapLeakageInstance> overlapLeakageInstances, @NotNull ProblemsHolder holder) {
        this.overlapLeakageInstances = overlapLeakageInstances;
        this.holder = holder;
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
              //  super.visitElement(element);//TODO:
                if (leakageIsAssociatedWithNode(overlapLeakageInstances, element)) {
                    holder.registerProblem(element, InspectionBundle.get(LeakageType.PreprocessingLeakage.getInspectionTextKey()));

                }
            }
        };
    }


    @Override
    public Predicate<OverlapLeakageInstance> leakageInstanceIsAssociatedWithNode(@NotNull PsiElement node) {
        var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);

        return instance -> (instance.lineNumber() == nodeLineNumber) && Objects.equals(instance.test(), node.getText()); //TODO: make sure it's ok to have text and not name
    }

    @Override
    public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {

        if (leakageIsAssociatedWithNode(overlapLeakageInstances, node)) {
            holder.registerProblem(node, InspectionBundle.get(LeakageType.OverlapLeakage.getInspectionTextKey()));

        }
    }
    @Override
    public void visitPyFunction(@NotNull PyFunction node){
        this.recursiveElementVisitor.visitElement(node);

    }

    //TODO: consider different making different visitors for performance
    @Override
    public void visitPyCallExpression(@NotNull PyCallExpression node) {//TODO: consider moving this into visitPyReferenceExpression.. will require some refactoring.

        //TODO: extract

        if (!overlapLeakageInstances.isEmpty()) {
            if (leakageSourceIsAssociatedWithNode(overlapLeakageInstances, node)) {

                renderInspectionOnLeakageSource(node, holder, overlapLeakageInstances);
            }

            renderInspectionOnTaints(node, holder, Arrays.stream(OverlapLeakageSourceKeyword.values()).toList());
        }
    }


    @Override
    public void renderInspectionOnLeakageSource(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, List<OverlapLeakageInstance> overlapLeakageInstances) {
//TODO: change name?
        OverlapLeakageInstance leakageInstance = overlapLeakageInstances.stream().filter(leakageSourceAssociatedWithNode(node)).findFirst().get();

        //TODO: what can we use besides getCallee?
        var taintAssociatedWithLeakageInstance = leakageInstance.getLeakageSource().findTaintThatMatchesText(node.getCallee().getText());

        holder.registerProblem(node, getInspectionMessageForLeakageSource(taintAssociatedWithLeakageInstance));
    }

    @NotNull
    private static String getInspectionMessageForLeakageSource(Taint taintAssociatedWithLeakageInstance) {
        StringBuilder inspectionMessage = new StringBuilder(InspectionBundle.get(LeakageType.OverlapLeakage.getInspectionTextKey()));
        inspectionMessage.append(" ");

        //get method keyword associated with taint
        Arrays.stream(OverlapLeakageSourceKeyword.values()).filter(value -> taintAssociatedWithLeakageInstance.containsText(value.toString()))//TODO: should just be the text on the right side of the period, not the whole thing
                .findFirst().ifPresent(keyword -> inspectionMessage.append(InspectionBundle.get(keyword.getPotentialCauses().get(0).getInspectionTextKey())));//TODO: refactor?

        return inspectionMessage.toString();
    }


}
