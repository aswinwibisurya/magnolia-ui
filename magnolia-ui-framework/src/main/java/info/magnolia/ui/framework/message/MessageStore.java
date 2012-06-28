/**
 * This file Copyright (c) 2012 Magnolia International
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
package info.magnolia.ui.framework.message;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.commons.JcrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.repository.RepositoryConstants;

/**
 * Stores messages on behalf of {@link MessagesManager}.
 */
@Singleton
public class MessageStore {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String MESSAGE_STORE_PATH = "/modules/ui-admin-central/messageStore";

    public boolean saveMessage(final String userId, final Message message) {

        return MgnlContext.doInSystemContext(new MgnlContext.Op<Boolean, RuntimeException>() {

            @Override
            public Boolean exec() {
                try {
                    Session session = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);

                    if (message.getId() == null) {
                        message.setId(getUniqueMessageId(getOrCreateUserNode(session, userId)));
                    }

                    marshallMessage(message, getOrCreateMessageNode(session, userId, message));

                    session.save();

                    return true;

                } catch (RepositoryException e) {
                    logger.error("Saving message failed for user: " + userId, e);
                    return false;
                }
            }
        });
    }

    public int getNumberOfUnclearedMessagesForUser(final String userId) {

        return MgnlContext.doInSystemContext(new MgnlContext.Op<Integer, RuntimeException>() {

            @Override
            public Integer exec() throws RuntimeException {
                try {
                    Session session = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);

                    int n = 0;
                    for (Node messageNode : NodeUtil.getNodes(getOrCreateUserNode(session, userId), MgnlNodeType.NT_CONTENTNODE)) {
                        if (!messageNode.getProperty("cleared").getBoolean()) {
                            n++;
                        }
                    }
                    return n;

                } catch (RepositoryException e) {
                    logger.warn("Failed to find the number of uncleared messages for user: " + userId, e);
                    return 0;
                }
            }
        });
    }

    public List<Message> findAllMessagesForUser(final String userId) {
        return MgnlContext.doInSystemContext(new MgnlContext.Op<List<Message>, RuntimeException>() {

            @Override
            public List<Message> exec() throws RuntimeException {
                try {
                    Session session = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);

                    ArrayList<Message> messages = new ArrayList<Message>();

                    for (Node messageNode : NodeUtil.getNodes(getOrCreateUserNode(session, userId), MgnlNodeType.NT_CONTENTNODE)) {

                        Message message = unmarshallMessage(messageNode);

                        messages.add(message);
                    }
                    return messages;

                } catch (RepositoryException e) {
                    logger.error("Saving message failed for user: " + userId, e);
                    return new ArrayList<Message>();
                }
            }
        });
    }

    public Message findMessageById(final String userId, final String messageId) {

        return MgnlContext.doInSystemContext(new MgnlContext.Op<Message, RuntimeException>() {

            @Override
            public Message exec() {
                try {
                    Session session = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);

                    Node messageNode = getMessageNode(session, userId, messageId);

                    if (messageNode == null) {
                        return null;
                    }

                    return unmarshallMessage(messageNode);

                } catch (RepositoryException e) {
                    logger.error("Unable to read message: " + messageId + " for user: " + userId, e);
                    return null;
                }
            }
        });
    }

    private void marshallMessage(Message message, Node node) throws RepositoryException {
        node.setProperty("timestamp", message.getTimestamp());
        node.setProperty("type", message.getType().name());
        node.setProperty("subject", message.getSubject());
        node.setProperty("message", message.getMessage());
        node.setProperty("cleared", message.isCleared());
    }

    private Message unmarshallMessage(Node node) throws RepositoryException {
        Message message = new Message();
        message.setId(node.getName());
        message.setTimestamp(node.getProperty("timestamp").getLong());
        message.setType(MessageType.valueOf(node.getProperty("type").getString()));
        message.setSubject(node.getProperty("subject").getString());
        message.setMessage(node.getProperty("message").getString());
        message.setCleared(node.getProperty("cleared").getBoolean());
        return message;
    }

    private Node getOrCreateUserNode(Session session, String userId) throws RepositoryException {
        return JcrUtils.getOrCreateByPath(MESSAGE_STORE_PATH + "/" + userId, MgnlNodeType.NT_CONTENT, session);
    }

    private Node getOrCreateMessageNode(Session session, String userId, Message message) throws RepositoryException {
        return JcrUtils.getOrCreateByPath(MESSAGE_STORE_PATH + "/" + userId + "/" + message.getId(), false, MgnlNodeType.NT_CONTENT, MgnlNodeType.NT_CONTENTNODE, session, false);
    }

    private Node getMessageNode(Session session, String userId, String messageId) throws RepositoryException {
        String absolutePath = MESSAGE_STORE_PATH + "/" + userId + "/" + messageId;
        if (session.nodeExists(absolutePath)) {
            return session.getNode(absolutePath);
        }
        return null;
    }

    private String getUniqueMessageId(Node userNode) throws RepositoryException {
        int largestIdFound = -1;
        for (Node node : JcrUtils.getChildNodes(userNode)) {
            try {
                int nameAsInt = Integer.parseInt(node.getName());
                if (nameAsInt > largestIdFound) {
                    largestIdFound = nameAsInt;
                }
            } catch (NumberFormatException e) {
                logger.warn("Expected name of node " + userNode.getPath() + " to be numeric", e);
            }
        }
        return String.valueOf(largestIdFound + 1);
    }
}
