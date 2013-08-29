/**
 * This file Copyright (c) 2012-2013 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This file is dual-licensed under both the Magnolia
 * Network Agreement and the GNU General Public License.
 * You may elect to use one or the other of these licenses.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or MNA you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the Magnolia Network Agreement (MNA), this file
 * and the accompanying materials are made available under the
 * terms of the MNA which accompanies this distribution, and
 * is available at http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.ui.workbench.search;

import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.ui.workbench.container.OrderBy;
import info.magnolia.ui.workbench.definition.WorkbenchDefinition;
import info.magnolia.ui.workbench.list.FlatJcrContainer;

import javax.jcr.nodetype.NodeType;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The jcr container backing the search view. It provides the subset of items returned by the current search. It will include <code>mgnl:folder</code> nodes if the latter are defined as "searchable".
 * 
 * @see #findSearchableNodeTypes()
 */
public class SearchJcrContainer extends FlatJcrContainer {

    private static final Logger log = LoggerFactory.getLogger(SearchJcrContainer.class);

    protected static final String WHERE_TEMPLATE_FOR_SEARCH = "localname() LIKE '%1$s%%' or " + SELECTOR_NAME + ".[%2$s] IS NOT NULL %3$s";

    protected static final String CONTAINS_TEMPLATE_FOR_SEARCH = "contains(" + SELECTOR_NAME + ".*, '%1$s')";

    protected static final String JCR_SCORE_FUNCTION = "score(" + SELECTOR_NAME + ")";

    private String fullTextExpression;

    private String whereCauseNodeTypes;

    public SearchJcrContainer(WorkbenchDefinition workbenchDefinition) {
        super(workbenchDefinition);
        whereCauseNodeTypes = super.getQueryWhereClauseNodeTypes();

        for (NodeType nt : getSearchableNodeTypes()) {
            // include mgnl:folder if searchable
            if (NodeTypes.Folder.NAME.equals(nt.getName())) {
                whereCauseNodeTypes += " or [jcr:primaryType] = '" + NodeTypes.Folder.NAME + "'";
                break;
            }
        }
    }

    /**
     * Overrides its default implementation to take further constraints from {@link #getQueryWhereClauseSearch()} into account.
     */
    @Override
    protected String getQueryWhereClause() {
        final String clauseWorkspacePath = getQueryWhereClauseWorkspacePath();
        final String whereClauseSearch = getQueryWhereClauseSearch();

        String whereClause = "(" + getQueryWhereClauseNodeTypes() + ")";

        if (!"".equals(whereClauseSearch)) {
            whereClause += " and (" + whereClauseSearch + ") ";
        }

        if (!"".equals(clauseWorkspacePath)) {
            if (!"".equals(whereClause)) {
                whereClause = clauseWorkspacePath + " and " + whereClause;
            } else {
                whereClause += clauseWorkspacePath;
            }
        }

        if (!"".equals(whereClause)) {
            whereClause = " where (" + whereClause + ")";
        }

        log.debug("JCR query WHERE clause is {}", whereClause);
        return whereClause;
    }

    @Override
    protected String getQueryWhereClauseNodeTypes() {
        return whereCauseNodeTypes;
    }

    /**
     * Builds a string representing the constraints to be applied for this search. Used by the overridden {@link #getQueryWhereClause()} to augment the WHERE clause for this query.
     * It basically adds constraints on node names, property names and full-text search on all <code>searchable</code> properties/columns declared in the workbench configuration.
     */
    protected String getQueryWhereClauseSearch() {
        if (StringUtils.isBlank(getFullTextExpression())) {
            return "";
        }
        final String unescapedFullTextExpression = getFullTextExpression();
        // See http://wiki.apache.org/jackrabbit/EncodingAndEscaping
        final String escapedFullTextExpression = unescapedFullTextExpression.replaceAll("'", "''").trim();

        final String escapedSearch = Text.escapeIllegalJcrChars(unescapedFullTextExpression);
        final String stmt = String.format(WHERE_TEMPLATE_FOR_SEARCH, escapedSearch, escapedSearch, String.format("or " + CONTAINS_TEMPLATE_FOR_SEARCH, escapedFullTextExpression));

        log.debug("Search where-clause is {}", stmt);
        return stmt;
    }

    public void setFullTextExpression(String fullTextExpression) {
        this.fullTextExpression = fullTextExpression;
    }

    public String getFullTextExpression() {
        return fullTextExpression;
    }

    @Override
    protected String getJcrNameOrderByFunction() {
        return JCR_SCORE_FUNCTION;
    }

    @Override
    /**
     * Order by jcr score descending.
     */
    protected OrderBy getDefaultOrderBy(String property) {
        return new OrderBy(property, false);
    }
}
