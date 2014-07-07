/**
 * This file Copyright (c) 2014 Magnolia International
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
package info.magnolia.ui.admincentral.shellapp.pulse.task.action;

import static org.mockito.Mockito.mock;

import info.magnolia.task.Task;
import info.magnolia.task.Task.Status;
import info.magnolia.task.TasksManager;
import info.magnolia.ui.api.shell.Shell;

import org.junit.Test;

/**
 * CompleteHumanTaskActionTest.
 */
public class RejectHumanTaskActionTest extends BaseHumanTaskActionTest {

    private RejectTaskAction action;

    @Override
    public void setUp() {
        super.setUp();
        action = new RejectTaskAction(mock(RejectTaskActionDefinition.class), null, mock(TasksManager.class), null, null, null, mock(Shell.class));
    }

    @Test
    public void rejectActionExecutesIfTaskStatusIsInProgressAndAssignedToCurrentUser() throws Exception {
        // GIVEN
        Task task = new Task();
        task.setStatus(Status.InProgress);
        task.setActorId(BaseHumanTaskActionTest.CURRENT_USER);

        // WHEN
        action.canExecuteTask(task);

        // THEN no exception
    }

    @Test(expected = IllegalStateException.class)
    public void rejectActionFailsIfTaskStatusIsInProgressButIsNotAssignedToCurrentUser() throws Exception {
        // GIVEN
        Task task = new Task();
        task.setStatus(Status.InProgress);
        task.setActorId("anotherUser");

        // WHEN
        action.canExecuteTask(task);

        // THEN throw exception
    }

    @Test(expected = IllegalStateException.class)
    public void rejectActionFailsIfTaskStatusIsNotInProgress() throws Exception {
        // GIVEN
        Task task = new Task();
        task.setActorId(BaseHumanTaskActionTest.CURRENT_USER);
        task.setStatus(Status.Resolved);

        // WHEN
        action.canExecuteTask(task);

        // GIVEN
        task.setStatus(Status.Failed);

        // WHEN
        action.canExecuteTask(task);

        // GIVEN
        task.setStatus(Status.Created);

        // WHEN
        action.canExecuteTask(task);
    }

}