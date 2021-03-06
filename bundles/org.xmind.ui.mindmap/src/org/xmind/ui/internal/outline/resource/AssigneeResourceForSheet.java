package org.xmind.ui.internal.outline.resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.xmind.core.ISheet;
import org.xmind.core.ITopic;
import org.xmind.core.ITopicExtension;
import org.xmind.core.ITopicExtensionElement;

public class AssigneeResourceForSheet extends AbstractIndexResource
        implements IAssigneeResource {

    private ISheet sheet;

    private Set<String> assignees = new HashSet<String>();

    private Map<String, Set<ITopic>> assigneeToTopics = new HashMap<String, Set<ITopic>>();

    public AssigneeResourceForSheet(ISheet sheet) {
        Assert.isNotNull(sheet);
        this.sheet = sheet;
        init(false);
    }

    private void init(boolean update) {
        if (update) {
            assignees.clear();
            assigneeToTopics.clear();
        }

        collectResourceForSheet(sheet);
    }

    public Object getSource() {
        return sheet;
    }

    public void reset(Object source, boolean update) {
        Assert.isNotNull(source);
        this.sheet = (ISheet) source;
        init(update);
    }

    public Set<String> getAssignees() {
        return assignees;
    }

    public Set<ITopic> getTopics(String assignee) {
        return assigneeToTopics.get(assignee);
    }

    protected void collectResourceForTopic(ITopic topic) {
        ITopicExtension ext = topic.getExtension("org.xmind.ui.taskInfo"); //$NON-NLS-1$
        if (ext == null)
            return;

        ITopicExtensionElement content = ext.getContent();
        List<ITopicExtensionElement> children = content
                .getChildren("assigned-to"); //$NON-NLS-1$
        if (!children.isEmpty()) {
            for (ITopicExtensionElement element : children) {
                String assignee = element.getTextContent();

                if (assigneeToTopics.containsKey(assignee)) {
                    Set<ITopic> assignedTopics = assigneeToTopics.get(assignee);
                    if (assignedTopics == null) {
                        assignedTopics = new HashSet<ITopic>();
                        assigneeToTopics.put(assignee, assignedTopics);
                    }
                    assignedTopics.add(topic);
                } else {
                    assignees.add(assignee);
                    Set<ITopic> assignedTopics = new HashSet<ITopic>();
                    assignedTopics.add(topic);
                    assigneeToTopics.put(assignee, assignedTopics);
                }
            }
        }
    }
}
