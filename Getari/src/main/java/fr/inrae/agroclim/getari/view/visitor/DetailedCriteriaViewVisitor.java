/**
 * This file is part of GETARI.
 *
 * GETARI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GETARI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GETARI. If not, see <https://www.gnu.org/licenses/>.
 */
package fr.inrae.agroclim.getari.view.visitor;

import static fr.inrae.agroclim.getari.util.ComponentUtil.bindSpinnerToDouble;
import static fr.inrae.agroclim.getari.util.ComponentUtil.newDoubleSpinner;
import static fr.inrae.agroclim.getari.util.ComponentUtil.newLabel;

import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.indicators.model.criteria.CompositeCriteria;
import fr.inrae.agroclim.indicators.model.criteria.Criteria;
import fr.inrae.agroclim.indicators.model.criteria.FormulaCriteria;
import fr.inrae.agroclim.indicators.model.criteria.NoCriteria;
import fr.inrae.agroclim.indicators.model.criteria.SimpleCriteria;
import fr.inrae.agroclim.indicators.model.criteria.visitor.CriteriaVisitor;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import lombok.extern.log4j.Log4j2;

/**
 * Visiteur d'un critère. Génère le formulaire d'édition du critère concerné.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class DetailedCriteriaViewVisitor extends DetailedViewVisitor implements CriteriaVisitor {

    /**
     * Constructor.
     *
     * @param g grid pane to add form into
     * @param c position
     */
    public DetailedCriteriaViewVisitor(final GridPane g, final Cpt c) {
        super(g, c);
    }

    @Override
    public final void visit(final Criteria c) {
        if (c == null) {
            LOGGER.info("Strange, visited criteria is null!");
            return;
        }
        if (c instanceof CompositeCriteria) {
            visitCompositeCriteria((CompositeCriteria) c);
            addSeparator();
        } else if (c instanceof NoCriteria) {
            visitNoCriteria((NoCriteria) c);
            addSeparator();
        } else if (c instanceof SimpleCriteria) {
            visitSimpleCriteria((SimpleCriteria) c);
            addSeparator();
        } else if (c instanceof FormulaCriteria) {
            visitFormulaCriteria((FormulaCriteria) c);
        } else {
            LOGGER.error("Not handled: {}", c.getClass());
        }
    }

    /**
     * Draw details of simple criteria.
     *
     * @param cc simple criteria to draw.
     */
    private void visitCompositeCriteria(final CompositeCriteria cc) {
        if (cc.getCriteria() == null || cc.getCriteria().isEmpty()) {
            return;
        }
        for (int i = 0; i < cc.getCriteria().size(); i++) {
            final Criteria c = cc.getCriteria().get(i);
            if (c instanceof CompositeCriteria) {
                visitCompositeCriteria((CompositeCriteria) c);
            } else if (c instanceof NoCriteria) {
                visitNoCriteria((NoCriteria) c);
                addSeparator();
            } else if (c instanceof SimpleCriteria) {
                visitSimpleCriteria((SimpleCriteria) c);
            }
            if (i < cc.getCriteria().size() - 1) {
                final Label operatorValue = new Label(
                        Messages.getString("detail.operator."
                                + cc.getLogicalOperator().name())
                        );
                getGridPane().add(operatorValue, 0, getCpt().nextRow());
            }
        }
    }

    /**
     * Draw details of formula criteria.
     *
     * @param c formula criteria to draw.
     */
    private void visitFormulaCriteria(final FormulaCriteria c) {
        final Label expressionLabel = newLabel("detail.formula.expression.label");
        final Label expression = new Label(c.getExpression());
        expression.setWrapText(true);
        final Label variablesLabel = newLabel("detail.formula.variables.label");
        final Label variables = new Label(c.getVariables().toString());
        getGridPane().add(expressionLabel, 0, getCpt().nextRow());
        getGridPane().add(expression, 1, getCpt().getRow());
        getGridPane().add(variablesLabel, 0, getCpt().nextRow());
        getGridPane().add(variables, 1, getCpt().getRow());
    }

    /**
     * Draw details of criteria defining variable.
     *
     * @param sc simple criteria to draw.
     */
    private void visitNoCriteria(final NoCriteria sc) {
        final Label variable = new Label(
                Messages.getString("detail.criteria.variable",
                        sc.getVariable().toString()));
        getGridPane().add(variable, 0, getCpt().nextRow());
    }

    /**
     * Draw details of simple criteria.
     *
     * @param sc simple criteria to draw.
     */
    private void visitSimpleCriteria(final SimpleCriteria sc) {
        final String operator = sc.getOperator().getRepr();
        final Label criteria = newLabel("detail.criteria.label");
        final Label criteriaValue = new Label(
                Messages.getString("detail.threshold.variable",
                        sc.getVariable().toString(),
                        operator));
        final Label threshold = newLabel("detail.threshold.label");
        final Spinner<Double> textThreshold = newDoubleSpinner(true);
        bindSpinnerToDouble(textThreshold, sc,
                Messages.getString("detail.threshold.attribute"));
        getGridPane().add(criteria, 0, getCpt().nextRow());
        getGridPane().add(criteriaValue, 1, getCpt().getRow());
        getGridPane().add(threshold, 0, getCpt().nextRow());
        getGridPane().add(textThreshold, 1, getCpt().getRow());
    }

}
