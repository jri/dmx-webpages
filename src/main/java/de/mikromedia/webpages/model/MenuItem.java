package de.mikromedia.webpages.model;

import de.deepamehta.core.JSONEnabled;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.service.CoreService;
import static de.mikromedia.webpages.WebpageService.ASSOCIATION;
import static de.mikromedia.webpages.WebpageService.MENU_ITEM;
import static de.mikromedia.webpages.WebpageService.MENU_ITEM_ACTIVE;
import static de.mikromedia.webpages.WebpageService.MENU_ITEM_HREF;
import static de.mikromedia.webpages.WebpageService.MENU_ITEM_NAME;
import static de.mikromedia.webpages.WebpageService.ROLE_CHILD;
import static de.mikromedia.webpages.WebpageService.ROLE_PARENT;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class MenuItem implements JSONEnabled {

    public Topic menuItem;
    public Website website;
    private List<RelatedTopic> relatedItems;
    private List<MenuItem> childItems = new ArrayList<MenuItem>();
    public boolean hasChildItems = false;

    public MenuItem(Topic menuItem, Website website) {
        this.menuItem = menuItem;
        this.website = website;
        if (!isWebpageMenuItemTopic()) {
            throw new IllegalArgumentException("Given topic is not of type Webpage Menu Item");
        }
        this.menuItem.loadChildTopics();
        loadRelatedMenuItems();
    }

    public MenuItem(long topicId, Website website, CoreService dms) {
        this.menuItem = dms.getTopic(topicId);
        this.website = website;
        if (!isWebpageMenuItemTopic()) {
            throw new IllegalArgumentException("Given topic is not of type Webpage Menu Item");
        }
        this.menuItem.loadChildTopics();
        loadRelatedMenuItems();
    }

    public long getId() {
        return menuItem.getId();
    }

    public String getLabel() {
        return menuItem.getChildTopics().getStringOrNull(MENU_ITEM_NAME);
    }

    public String getHref() { // TODO: Migrate to "Link"
        return menuItem.getChildTopics().getStringOrNull(MENU_ITEM_HREF);
    }

    public String getFullHref() {
        String sitePrefix = this.website.getSitePrefix();
        String fullHref = "";
        if (sitePrefix != null) {
            fullHref += "/" + sitePrefix;
            if (sitePrefix.equals("admin") || sitePrefix.equals("standard")) {
                fullHref = "";
            }
        }
        fullHref += "/" + menuItem.getChildTopics().getStringOrNull(MENU_ITEM_HREF);
        return fullHref;
    }

    public boolean isActive() {
        return menuItem.getChildTopics().getBooleanOrNull(MENU_ITEM_ACTIVE);
    }

    public List<MenuItem> getChildMenuItems() {
        if (relatedItems == null) loadRelatedMenuItems();
        childItems =  new ArrayList<MenuItem>();
        for (Topic relatedItem : relatedItems) {
            childItems.add(new MenuItem(relatedItem, this.website));
        }
        return childItems;
    }

    public JSONObject toJSON() {
        try {
            return new JSONObject()
                .put("label", getLabel())
                .put("href", getHref())
                .put("items", getChildMenuItems());
        } catch (JSONException ex) {
            Logger.getLogger(MenuItem.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void loadRelatedMenuItems() {
        relatedItems = menuItem.getRelatedTopics(ASSOCIATION, ROLE_PARENT,
                ROLE_CHILD, MENU_ITEM);
        hasChildItems = (relatedItems.size() > 0);
    }

    private boolean isWebpageMenuItemTopic() {
        if (this.menuItem == null) return false;
        return (menuItem.getTypeUri().equals(MENU_ITEM));
    }

}
