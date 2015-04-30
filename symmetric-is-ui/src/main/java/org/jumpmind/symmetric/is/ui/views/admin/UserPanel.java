package org.jumpmind.symmetric.is.ui.views.admin;

import java.util.Iterator;
import java.util.Set;

import org.jumpmind.symmetric.is.core.model.User;
import org.jumpmind.symmetric.is.ui.common.ApplicationContext;
import org.jumpmind.symmetric.is.ui.common.ButtonBar;
import org.jumpmind.symmetric.is.ui.common.TabbedPanel;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;


@SuppressWarnings("serial")
public class UserPanel extends NamedPanel {

    ApplicationContext context;
    
    TabbedPanel tabbedPanel;
    
    Button newButton;
    
    Button editButton;
    
    Button removeButton;

    BeanItemContainer<User> container;
    
    Table table;
    
    public UserPanel(ApplicationContext context, TabbedPanel tabbedPanel) {
        super("Users");
        this.context = context;
        this.tabbedPanel = tabbedPanel;
        
        ButtonBar buttonBar = new ButtonBar();
        addComponent(buttonBar);

        newButton = buttonBar.addButton("New", FontAwesome.PLUS);
        newButton.addClickListener(new NewClickListener());

        editButton = buttonBar.addButton("Edit", FontAwesome.EDIT);
        editButton.addClickListener(new EditClickListener());

        removeButton = buttonBar.addButton("Remove", FontAwesome.TRASH_O);
        removeButton.addClickListener(new RemoveClickListener());

        container = new BeanItemContainer<User>(User.class);

        table = new Table();
        table.setSizeFull();
        table.setCacheRate(100);
        table.setPageLength(100);
        table.setImmediate(true);
        table.setSelectable(true);
        table.setMultiSelect(true);

        table.setContainerDataSource(container);
        table.setVisibleColumns("loginId", "name", "lastLoginTime");
        table.setColumnHeaders("Login ID", "Full Name", "Last Login Time");
        table.addItemClickListener(new TableItemClickListener());
        table.addValueChangeListener(new TableValueChangeListener());
        table.setSortContainerPropertyId("loginId");
        table.setSortAscending(true);

        addComponent(table);
        setExpandRatio(table, 1.0f);
        refresh();
    }

    @Override
    public void selected() {
        refresh();
    }

    public void refresh() {
        container.removeAllItems();
        container.addAll(context.getConfigurationService().findUsers());
        table.sort();
        setButtonsEnabled();
    }

    protected void setButtonsEnabled() {
        Set<User> selectedIds = getSelectedItems();
        boolean enabled = selectedIds.size() > 0;
        editButton.setEnabled(enabled);
        removeButton.setEnabled(enabled);
    }

    @SuppressWarnings("unchecked")
    protected Set<User> getSelectedItems() {
        return (Set<User>) table.getValue();
    }

    @SuppressWarnings("unchecked")
    protected User getFirstSelectedItem() {
        Set<User> users = (Set<User>) table.getValue();
        Iterator<User> iter = users.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    class NewClickListener implements ClickListener {
        public void buttonClick(ClickEvent event) {
            User user = new User();
            UserEditPanel editPanel = new UserEditPanel(context, user);
            tabbedPanel.addCloseableTab(user.getId(), "Edit User", FontAwesome.USER, editPanel);
        }
    }

    class EditClickListener implements ClickListener {
        public void buttonClick(ClickEvent event) {
            User user = getFirstSelectedItem();
            context.getConfigurationService().refresh(user);
            UserEditPanel editPanel = new UserEditPanel(context, user);
            tabbedPanel.addCloseableTab(user.getId(), "Edit User", FontAwesome.USER, editPanel);
        }
    }

    class RemoveClickListener implements ClickListener {
        public void buttonClick(ClickEvent event) {
            for (User user : getSelectedItems()) {
                context.getConfigurationService().delete(user);
                container.removeItem(user);
            }
            table.setValue(null);
            setButtonsEnabled();
        }
    }

    class TableItemClickListener implements ItemClickListener {
        long lastClick;
        
        public void itemClick(ItemClickEvent event) {
            if (event.isDoubleClick()) {
                editButton.click();
            } else if (getSelectedItems().contains(event.getItemId()) &&
                System.currentTimeMillis()-lastClick > 500) {
                    table.setValue(null);
            }
            lastClick = System.currentTimeMillis();
        }
    }

    class TableValueChangeListener implements ValueChangeListener {
        public void valueChange(ValueChangeEvent event) {
            setButtonsEnabled();
        }
    }
}
