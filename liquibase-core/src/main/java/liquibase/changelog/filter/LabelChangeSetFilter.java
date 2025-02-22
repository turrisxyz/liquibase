package liquibase.changelog.filter;

import liquibase.ContextExpression;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Labels;
import liquibase.changelog.ChangeSet;
import liquibase.sql.visitor.SqlVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LabelChangeSetFilter implements ChangeSetFilter {
    private LabelExpression labelExpression;

    public LabelChangeSetFilter() {
        this(new LabelExpression());
    }

    public LabelChangeSetFilter(LabelExpression labels) {
        this.labelExpression = labels;
    }

    @Override
    public ChangeSetFilterResult accepts(ChangeSet changeSet) {
        List<SqlVisitor> visitorsToRemove = new ArrayList<>();
        for (SqlVisitor visitor : changeSet.getSqlVisitors()) {
            if ((visitor.getLabels() != null) && !labelExpression.matches(visitor.getLabels())) {
                visitorsToRemove.add(visitor);
            }
        }
        changeSet.getSqlVisitors().removeAll(visitorsToRemove);

        if ((labelExpression == null) || labelExpression.isEmpty()) {
            return new ChangeSetFilterResult(true, "No runtime labels specified, all labels will run", this.getClass());
        }

        Collection<Labels> inheritableLabels = changeSet.getInheritableLabels();
        if ((changeSet.getLabels() == null || changeSet.getLabels().isEmpty()) &&
            (inheritableLabels == null || inheritableLabels.isEmpty())) {
            return new ChangeSetFilterResult(true, "Change set runs under all labels", this.getClass());
        }

        if (labelExpression.matches(changeSet.getLabels()) && LabelExpression.matchesAll(inheritableLabels, labelExpression)) {
            return new ChangeSetFilterResult(true, "Labels matches '" + labelExpression.toString() + "'", this.getClass());
        }
        else {
            return new ChangeSetFilterResult(false, "Labels does not match '" + labelExpression.toString() + "'", this.getClass());
        }
    }
}
